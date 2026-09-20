# frozen_string_literal: true

require "minitest/autorun"
require "open3"
require "yaml"

class ProductionReleaseTest < Minitest::Test
  ROOT = File.expand_path("..", __dir__)
  WORKFLOW_PATH = File.join(ROOT, ".github", "workflows", "release-production.yml")
  FASTFILE_PATH = File.join(ROOT, "fastlane", "Fastfile")
  GEMFILE_PATH = File.join(ROOT, "Gemfile")
  APP_GRADLE_PATH = File.join(ROOT, "app", "build.gradle.kts")
  REQUIRED_SECRETS = %w[
    ADMOB_APP_ID
    ADMOB_BANNER_ID
    ANDROID_KEYSTORE_BASE64
    FIREBASE_APP_DISTRIBUTION_JSON_KEY
    KEYSTORE_PASSWORD
    KEY_ALIAS
    KEY_PASSWORD
    PLAY_PRODUCTION_JSON_KEY
  ].freeze

  def test_production_workflow_exists
    assert_path_exists WORKFLOW_PATH
  end

  def test_workflow_is_manual_protected_and_uses_no_release_artifact
    require_workflow!

    assert_includes workflow_source, "workflow_dispatch:"
    assert_equal "production-release", publish_job.fetch("environment")
    assert_equal({ "contents" => "read" }, publish_job.fetch("permissions"))
    assert_equal false, workflow_config.fetch("concurrency").fetch("cancel-in-progress")
    refute_includes workflow_source, "actions/upload-artifact"
    refute_includes workflow_source, "RELEASE_EVIDENCE_PASSWORD"
    refute_includes workflow_source, "release-evidence"

    uses_lines = workflow_source.lines.grep(/^\s*-?\s*uses:/)
    refute_empty uses_lines
    uses_lines.each do |line|
      assert_match(/@[0-9a-f]{40}\s+# v\d/, line)
    end
  end

  def test_workflow_validates_release_identity_before_reading_secrets
    require_workflow!

    identity_index = step_index("Validate production release identity")
    first_secret_index = workflow_source.index("${{ secrets.")
    assert_operator step_source_index("Validate production release identity"), :<, first_secret_index
    assert_operator identity_index, :<, step_index("Reconstruct upload keystore")

    valid_env = {
      "RELEASE_REF" => "refs/heads/release/3.0.2_13",
      "EXPECTED_VERSION_CODE" => "13",
      "EXPECTED_VERSION_NAME" => "3.0.2"
    }
    _stdout, stderr, status = run_step("Validate production release identity", valid_env)
    assert status.success?, stderr

    [
      valid_env.merge("RELEASE_REF" => "refs/heads/develop"),
      valid_env.merge("EXPECTED_VERSION_CODE" => "12"),
      valid_env.merge("EXPECTED_VERSION_NAME" => "3.0.1")
    ].each do |invalid_env|
      _stdout, _stderr, invalid_status = run_step("Validate production release identity", invalid_env)
      refute invalid_status.success?, "invalid release identity unexpectedly passed: #{invalid_env}"
    end
  end

  def test_secrets_are_step_scoped_and_complete
    require_workflow!

    refute publish_job.key?("env"), "release secrets must not be job-scoped"
    assert_equal ["ANDROID_KEYSTORE_BASE64"], secret_names(step("Reconstruct upload keystore"))
    assert_equal %w[ADMOB_APP_ID ADMOB_BANNER_ID KEYSTORE_PASSWORD KEY_ALIAS KEY_PASSWORD].sort,
                 secret_names(step("Build signed production AAB")).sort
    assert_equal ["FIREBASE_APP_DISTRIBUTION_JSON_KEY"],
                 secret_names(step("Write Firebase credentials"))
    assert_equal ["PLAY_PRODUCTION_JSON_KEY"],
                 secret_names(step("Publish production release to Google Play"))
    assert_equal REQUIRED_SECRETS.sort, secret_names(workflow_source).uniq.sort
  end

  def test_one_build_is_verified_then_reused_for_firebase_and_play
    require_workflow!

    ordered = [
      "Build signed production AAB",
      "Verify production AAB",
      "Write Firebase credentials",
      "Distribute production AAB to Firebase",
      "Publish production release to Google Play",
      "Confirm production AAB hash is unchanged",
      "Remove temporary release credentials"
    ]
    positions = ordered.map { |name| step_index(name) }
    assert_equal positions.sort, positions

    assert_equal 1, workflow_source.scan("bundle exec fastlane android build_release_bundle").length
    assert_equal "bundle exec fastlane android distribute_firebase",
                 step_config("Distribute production AAB to Firebase").fetch("run")
    assert_equal "bundle exec fastlane android production_release",
                 step_config("Publish production release to Google Play").fetch("run")
    assert_includes step("Verify production AAB"), "jarsigner -verify"
    assert_includes step("Verify production AAB"), 'grep -Eq \'^jar verified\\.$\' "$verification_log"'
    refute_match(/printf.*\|\s*grep/, step("Verify production AAB"))
    assert_includes step("Verify production AAB"), "sha256sum"
    assert_includes step("Confirm production AAB hash is unchanged"), "sha256sum --check"
    assert_equal "always()", step_config("Remove temporary release credentials").fetch("if")
  end

  def test_firebase_destination_is_fixed_to_approved_app_and_group
    require_workflow!

    firebase_step = step_config("Distribute production AAB to Firebase")
    assert_equal "${{ vars.FIREBASE_APP_ID }}", firebase_step.fetch("env").fetch("FIREBASE_APP_ID")
    assert_equal "${{ vars.FIREBASE_TESTER_GROUPS }}",
                 firebase_step.fetch("env").fetch("FIREBASE_TESTER_GROUPS")

    validation = step("Validate Firebase destination")
    assert_includes validation, "1:632111455840:android:952c21d10fcd75073aed05"
    assert_includes validation, "owner-testers"
  end

  def test_fastlane_has_fixed_production_and_firebase_upload_lanes
    fastfile = File.read(FASTFILE_PATH, encoding: Encoding::UTF_8)
    gemfile = File.read(GEMFILE_PATH, encoding: Encoding::UTF_8)

    production_lane = lane_body(fastfile, "production_release")
    firebase_lane = lane_body(fastfile, "distribute_firebase")

    assert_includes production_lane, 'track: "production"'
    assert_includes production_lane, 'release_status: "completed"'
    assert_includes production_lane, 'rollout: "1.0"'
    assert_includes production_lane, 'ENV.fetch("PLAY_PRODUCTION_JSON_KEY")'
    refute_includes production_lane, "build_release_bundle"

    assert_includes firebase_lane, "firebase_app_distribution("
    assert_includes firebase_lane, 'android_artifact_type: "AAB"'
    assert_includes firebase_lane, 'ENV.fetch("FIREBASE_APP_ID")'
    assert_includes firebase_lane, 'ENV.fetch("FIREBASE_TESTER_GROUPS")'
    refute_includes firebase_lane, "build_release_bundle"

    assert_includes gemfile, 'gem "fastlane-plugin-firebase_app_distribution", "1.0.0"'
  end

  def test_store_sync_defines_both_locales_and_approved_release_notes
    fastfile = File.read(FASTFILE_PATH, encoding: Encoding::UTF_8)

    %w[en-GB it-IT phoneScreenshots sevenInchScreenshots tenInchScreenshots].each do |value|
      assert_includes fastfile, value
    end
    refute_includes fastfile, '"en-US" =>'
    assert_includes fastfile, "Improved layouts across phones and tablets."
    assert_includes fastfile, "Layout migliorati su smartphone e tablet."
    assert_includes fastfile, 'File.join(project, locale_root, "changelogs", "13.txt")'
  end

  private

  def require_workflow!
    skip "production workflow has not been implemented yet" unless File.file?(WORKFLOW_PATH)
  end

  def workflow_source
    @workflow_source ||= File.read(WORKFLOW_PATH)
  end

  def workflow_config
    @workflow_config ||= YAML.safe_load(workflow_source, aliases: true)
  end

  def publish_job
    workflow_config.fetch("jobs").fetch("publish-production")
  end

  def steps
    publish_job.fetch("steps")
  end

  def step_config(name)
    steps.find { |candidate| candidate["name"] == name } || flunk("missing workflow step: #{name}")
  end

  def step(name)
    match = workflow_source.match(/^      - name: #{Regexp.escape(name)}\n(?<body>.*?)(?=^      - |\z)/m)
    refute_nil match, "missing workflow step: #{name}"
    match[:body]
  end

  def step_index(name)
    steps.index { |candidate| candidate["name"] == name } || flunk("missing workflow step: #{name}")
  end

  def step_source_index(name)
    workflow_source.index("      - name: #{name}\n") || flunk("missing workflow step: #{name}")
  end

  def run_step(name, environment)
    Open3.capture3(environment, "bash", "-s", stdin_data: step_config(name).fetch("run"), chdir: ROOT)
  end

  def secret_names(value)
    value.to_s.scan(/\$\{\{ secrets\.([A-Z0-9_]+) \}\}/).flatten
  end

  def lane_body(source, name)
    match = source.match(/lane :#{Regexp.escape(name)} do(?: \|[^|]+\|)?\n(?<body>.*?)\n  end/m)
    refute_nil match, "missing Fastlane lane: #{name}"
    match[:body]
  end
end
