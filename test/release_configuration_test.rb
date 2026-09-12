# frozen_string_literal: true

require "minitest/autorun"

class ReleaseConfigurationTest < Minitest::Test
  ROOT = File.expand_path("..", __dir__)
  FASTFILE = File.read(File.join(ROOT, "fastlane", "Fastfile"))

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
end
