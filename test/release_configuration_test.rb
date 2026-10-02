# frozen_string_literal: true

require "minitest/autorun"
require "open3"
require "yaml"

class ReleaseConfigurationTest < Minitest::Test
  ROOT = File.expand_path("..", __dir__)
  FASTFILE = File.read(File.join(ROOT, "fastlane", "Fastfile"))
  WORKFLOW = File.read(File.join(ROOT, ".github", "workflows", "release-internal.yml"))
  WORKFLOW_CONFIG = YAML.safe_load(WORKFLOW, aliases: true)
  RELEASE_DOCUMENTATION = %w[
    fastlane/README.md
    docs/release/RELEASE_RUNBOOK.md
    docs/release/RELEASE_CHECKLIST.md
  ].map { |path| File.read(File.join(ROOT, path)) }.join("\n")
  REQUIRED_SECRETS = %w[
    PLAY_STORE_JSON_KEY
    ANDROID_KEYSTORE_BASE64
    KEYSTORE_PASSWORD
    KEY_ALIAS
    KEY_PASSWORD
    ADMOB_APP_ID
    ADMOB_BANNER_ID
    RELEASE_EVIDENCE_PASSWORD
  ].freeze
  SIGNING_AND_BUILD_SECRETS = %w[
    KEYSTORE_PASSWORD
    KEY_ALIAS
    KEY_PASSWORD
    ADMOB_APP_ID
    ADMOB_BANNER_ID
  ].freeze
  RELEASE_AAB_PATH = "app/build/outputs/bundle/release/app-release.aab"

  def test_fastlane_preserves_the_operator_maintained_readme
    assert_match(/^skip_docs$/, FASTFILE, "Fastlane must not regenerate fastlane/README.md")
  end

  def test_internal_lane_has_fixed_destination_and_bundle_only_upload
    lane = FASTFILE.match(/lane :internal_release do\n(?<body>.*?)\n  end/m)
    refute_nil lane, "internal_release lane is missing or accepts options"

    body = lane[:body]
    assert_includes body, 'track: "internal"'
    assert_includes body, 'release_status: "completed"'
    %w[apk changelogs metadata images screenshots].each do |kind|
      assert_includes body, "skip_upload_#{kind}: true"
    end
    assert_includes body, "Digest::SHA256.file(absolute_aab_path).hexdigest"
    refute_includes body, "build_release_bundle", "upload lane must not rebuild the verified AAB"
  end

  def test_internal_release_workflow_is_manual_protected_and_valid_yaml
    refute_nil WORKFLOW_CONFIG, "workflow must parse as YAML"

    assert_includes WORKFLOW, "workflow_dispatch:"
    assert_includes WORKFLOW, "environment: google-play-internal"
    assert_includes WORKFLOW, "cancel-in-progress: false"
    assert_includes WORKFLOW, "${{ runner.temp }}/mathbrainer-upload.jks"
    assert_includes WORKFLOW, "jarsigner -verify"
    assert_includes WORKFLOW, "sha256sum"
    assert_includes WORKFLOW, "if: always()"
    assert_includes WORKFLOW, "retention-days: 30"

    REQUIRED_SECRETS.each do |secret|
      assert_includes WORKFLOW, "${{ secrets.#{secret} }}"
    end

    uses_lines = WORKFLOW.lines.grep(/^\s*-?\s*uses:/)
    refute_empty uses_lines, "workflow must use pinned actions"
    uses_lines.each do |line|
      assert_match(/@[0-9a-f]{40}\s+# v\d/, line, "action reference must use a SHA and release tag comment: #{line}")
    end

    %w[push: pull_request: schedule: track:\ production].each do |forbidden|
      refute_includes WORKFLOW, forbidden
    end
    refute_match(/inputs:\s*\n(?:.*\n)*?\s+track:/, WORKFLOW, "workflow must not allow a track input")
  end

  def test_workflow_pins_ruby_3_3
    ruby_step = workflow_action_step("ruby/setup-ruby")
    inputs = ruby_step.fetch("with", {})

    assert_equal "3.3", inputs["ruby-version"]
    assert_equal true, inputs["bundler-cache"]
  end

  def test_trusted_ref_policy_runs_before_secrets_and_rejects_other_refs
    trusted_ref_index = workflow_step_index("Enforce trusted release ref")
    first_secret_index = WORKFLOW.index("${{ secrets.")
    assert_operator workflow_step_source_index("Enforce trusted release ref"), :<, first_secret_index

    %w[refs/heads/master refs/tags/v1.2.3].each do |release_ref|
      _stdout, stderr, status = run_workflow_step("Enforce trusted release ref", "RELEASE_REF" => release_ref)
      assert status.success?, "expected trusted ref #{release_ref} to pass: #{stderr}"
    end

    %w[refs/heads/main refs/heads/release refs/tags/1.2.3 refs/pull/12/merge].each do |release_ref|
      _stdout, _stderr, status = run_workflow_step("Enforce trusted release ref", "RELEASE_REF" => release_ref)
      refute status.success?, "expected untrusted ref #{release_ref} to fail"
    end

    assert_operator trusted_ref_index, :<, workflow_step_index("Reconstruct upload keystore")
  end

  def test_release_secrets_are_scoped_to_the_steps_that_consume_them
    refute_match(/^    env:/, WORKFLOW, "release secrets must not be available to every job step")

    assert_equal ["ANDROID_KEYSTORE_BASE64"], secret_names(workflow_step("Reconstruct upload keystore"))
    assert_equal SIGNING_AND_BUILD_SECRETS.sort,
                 secret_names(workflow_step("Build signed release AAB")).sort
    assert_equal ["RELEASE_EVIDENCE_PASSWORD"],
                 secret_names(workflow_step("Encrypt private release evidence"))
    assert_equal ["PLAY_STORE_JSON_KEY"],
                 secret_names(workflow_step("Upload verified AAB to Play internal testing"))

    assert_equal REQUIRED_SECRETS.sort, secret_names(WORKFLOW).sort
  end

  def test_build_evidence_play_upload_and_post_upload_hash_check_are_strictly_ordered
    ordered_steps = [
      "Build signed release AAB",
      "Verify release evidence",
      "Encrypt private release evidence",
      "Upload verified AAB to Play internal testing",
      "Confirm uploaded AAB hash is unchanged",
      "Upload internal release evidence"
    ]
    positions = ordered_steps.map { |name| workflow_step_index(name) }

    assert_equal positions.sort, positions, "release steps must preserve build/evidence/upload ordering"
    ordered_steps.each do |name|
      assert_equal 1, workflow_steps.count { |candidate| candidate["name"] == name },
                   "release step must occur exactly once: #{name}"
    end
    assert_equal "bundle exec fastlane android build_release_bundle",
                 workflow_step_config("Build signed release AAB").fetch("run")
    assert_equal "bundle exec fastlane android internal_release",
                 workflow_step_config("Upload verified AAB to Play internal testing").fetch("run")

    evidence_step = workflow_step("Verify release evidence")
    hash_step = workflow_step("Confirm uploaded AAB hash is unchanged")

    assert_includes evidence_step, "sha256sum \"$aab_path\" > \"$checksum_path\""
    assert_includes evidence_step, RELEASE_AAB_PATH
    assert_includes hash_step, "sha256sum --check"
    assert_includes hash_step, "app-release.aab.sha256"
  end

  def test_evidence_verification_requires_a_fully_signed_aab
    evidence_step = workflow_step("Verify release evidence")

    artifact_step = workflow_step("Upload internal release evidence")

    assert_includes evidence_step, 'verification_output="$(jarsigner -verify -verbose -certs "$aab_path" 2>&1)"'
    assert_includes evidence_step, %q(grep -Eq '^jar verified\.$')
    assert_includes evidence_step, %q(grep -Eiq 'jar is unsigned|unsigned entr(y|ies)')
    assert_match(
      /^        if: always\(\) && steps\.encrypt_evidence\.outcome == 'success'$/,
      artifact_step,
      "encrypted evidence must upload after a failed Play publish but never after failed encryption"
    )
  end

  def test_public_artifact_contains_only_encrypted_private_evidence_and_public_metadata
    encryption_step = workflow_step("Encrypt private release evidence")
    artifact_path = workflow_step_config("Upload internal release evidence").fetch("with").fetch("path")

    assert_includes encryption_step, "tar -czf"
    assert_includes encryption_step, "openssl enc -aes-256-cbc"
    assert_includes encryption_step, "-salt"
    assert_includes encryption_step, "-pbkdf2"
    assert_includes encryption_step, "-iter 100000"
    assert_includes encryption_step, "-pass env:RELEASE_EVIDENCE_PASSWORD"
    assert_includes encryption_step, "rm -f \"$plaintext_archive\""
    refute_match(/(?:echo|printf).*RELEASE_EVIDENCE_PASSWORD/, encryption_step)

    assert_includes artifact_path, "release-evidence.tar.gz.enc"
    assert_includes artifact_path, "app-release.aab.sha256"
    assert_includes artifact_path, "release-manifest.txt"
    refute_includes artifact_path, RELEASE_AAB_PATH
    refute_includes artifact_path, "mapping.txt"
    refute_includes artifact_path, "resources.txt"
  end

  def test_release_documentation_covers_internal_play_operations
    assert_includes RELEASE_DOCUMENTATION, "google-play-internal"
    REQUIRED_SECRETS.each do |secret|
      assert_includes RELEASE_DOCUMENTATION, secret
    end
    assert_includes RELEASE_DOCUMENTATION, "internal_release"
    assert_match(/manual dispatch/i, RELEASE_DOCUMENTATION)
    assert_match(/increment.*versionCode|versionCode.*increment/i, RELEASE_DOCUMENTATION)
    assert_includes RELEASE_DOCUMENTATION, "mathbrainer-internal-release-"
    assert_match(/closed.*production.*separate owner action/i, RELEASE_DOCUMENTATION)
    assert_includes RELEASE_DOCUMENTATION, "refs/heads/master"
    assert_includes RELEASE_DOCUMENTATION, "refs/tags/v*"
    assert_includes RELEASE_DOCUMENTATION, "Selected branches and tags"
    assert_includes RELEASE_DOCUMENTATION, "eu.indiewalkabout.mathbrainer"
    assert_includes RELEASE_DOCUMENTATION, "Release apps to testing tracks"
    assert_match(/AES-256-CBC/i, RELEASE_DOCUMENTATION)
    assert_match(/PBKDF2/i, RELEASE_DOCUMENTATION)
    assert_match(/decrypt/i, RELEASE_DOCUMENTATION)
    assert_match(/private password manager/i, RELEASE_DOCUMENTATION)
    assert_match(/workflow-produced.*AAB/i, RELEASE_DOCUMENTATION)
    refute_match(/pass\s+through\s+Play\s+Internal\s+App\s+Sharing/i, RELEASE_DOCUMENTATION)
  end

  private

  def workflow_step(name)
    step = WORKFLOW.match(/^      - name: #{Regexp.escape(name)}\n(?<body>.*?)(?=^      - |\z)/m)
    refute_nil step, "workflow step is missing: #{name}"

    step[:body]
  end

  def workflow_step_config(name)
    step = workflow_steps.find { |candidate| candidate["name"] == name }
    refute_nil step, "workflow step is missing: #{name}"

    step
  end

  def workflow_action_step(action_name)
    step = workflow_steps.find { |candidate| candidate.fetch("uses", "").start_with?("#{action_name}@") }
    refute_nil step, "workflow action step is missing: #{action_name}"

    step
  end

  def workflow_steps
    WORKFLOW_CONFIG.fetch("jobs").fetch("publish").fetch("steps")
  end

  def workflow_step_index(name)
    index = workflow_steps.index { |candidate| candidate["name"] == name }
    refute_nil index, "workflow step is missing: #{name}"

    index
  end

  def workflow_step_source_index(name)
    index = WORKFLOW.index("      - name: #{name}\n")
    refute_nil index, "workflow step is missing: #{name}"

    index
  end

  def run_workflow_step(name, environment)
    script = workflow_step_config(name).fetch("run")
    Open3.capture3(environment, "bash", "-s", stdin_data: script, chdir: ROOT)
  end

  def secret_names(text)
    text.scan(/\$\{\{ secrets\.([A-Z0-9_]+) \}\}/).flatten
  end
end
