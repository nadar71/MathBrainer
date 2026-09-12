# frozen_string_literal: true

require "minitest/autorun"
require "yaml"

class ReleaseConfigurationTest < Minitest::Test
  ROOT = File.expand_path("..", __dir__)
  FASTFILE = File.read(File.join(ROOT, "fastlane", "Fastfile"))
  WORKFLOW = File.read(File.join(ROOT, ".github", "workflows", "release-internal.yml"))
  REQUIRED_SECRETS = %w[
    PLAY_STORE_JSON_KEY
    ANDROID_KEYSTORE_BASE64
    KEYSTORE_PASSWORD
    KEY_ALIAS
    KEY_PASSWORD
    ADMOB_APP_ID
    ADMOB_BANNER_ID
  ].freeze

  def test_internal_lane_has_fixed_destination_and_bundle_only_upload
    lane = FASTFILE.match(/lane :internal_release do\n(?<body>.*?)\n  end/m)
    refute_nil lane, "internal_release lane is missing or accepts options"

    body = lane[:body]
    assert_includes body, 'track: "internal"'
    assert_includes body, 'release_status: "completed"'
    %w[apk changelogs metadata images screenshots].each do |kind|
      assert_includes body, "skip_upload_#{kind}: true"
    end
  end

  def test_internal_release_workflow_is_manual_protected_and_valid_yaml
    refute_nil YAML.safe_load(WORKFLOW, aliases: true), "workflow must parse as YAML"

    assert_includes WORKFLOW, "workflow_dispatch:"
    assert_includes WORKFLOW, "environment: google-play-internal"
    assert_includes WORKFLOW, "cancel-in-progress: false"
    assert_includes WORKFLOW, "${{ runner.temp }}/mathbrainer-upload.jks"
    assert_includes WORKFLOW, "bundle exec fastlane android internal_release"
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
end
