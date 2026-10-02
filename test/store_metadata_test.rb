# frozen_string_literal: true

require "minitest/autorun"

class StoreMetadataTest < Minitest::Test
  ROOT = File.expand_path("..", __dir__)
  FASTFILE_PATH = File.join(ROOT, "fastlane", "Fastfile")
  LISTING_ROOT = File.join(ROOT, "store-media", "google-play", "listing")
  LOCALES = %w[en-GB it-IT].freeze

  def test_every_store_locale_has_valid_localized_copy
    LOCALES.each do |locale|
      short_path = File.join(LISTING_ROOT, locale, "short_description.txt")
      full_path = File.join(LISTING_ROOT, locale, "full_description.txt")

      assert_path_exists short_path
      assert_path_exists full_path

      short_description = File.read(short_path, encoding: Encoding::UTF_8).strip
      full_description = File.read(full_path, encoding: Encoding::UTF_8).strip

      assert_operator short_description.length, :<=, 80
      assert_operator full_description.length, :<=, 4_000
      assert_includes full_description, "18"
      assert_match(/Memory Flash/i, full_description)
    end
  end

  def test_store_copy_highlights_the_major_14_to_18_game_redesign
    expected_copy = {
      "en-GB" => {
        short: "18 completely redesigned maths, memory and logic games for your mind.",
        full: ["expanded from 14 to 18", "completely redesigned"]
      },
      "it-IT" => {
        short: "18 giochi di matematica, memoria e logica completamente ridisegnati.",
        full: ["da 14 a 18", "completamente ridisegnata"]
      }
    }

    expected_copy.each do |locale, expected|
      short_description = File.read(
        File.join(LISTING_ROOT, locale, "short_description.txt"),
        encoding: Encoding::UTF_8
      ).strip
      full_description = File.read(
        File.join(LISTING_ROOT, locale, "full_description.txt"),
        encoding: Encoding::UTF_8
      ).strip

      assert_equal expected[:short], short_description
      expected[:full].each { |highlight| assert_includes full_description.downcase, highlight }
    end
  end

  def test_fastlane_syncs_listing_copy_and_has_metadata_only_publish_lane
    fastfile = File.read(FASTFILE_PATH, encoding: Encoding::UTF_8)

    assert_includes fastfile, 'STORE_LISTING_ROOT = File.join(STORE_MEDIA_ROOT, "listing")'
    assert_includes fastfile, 'File.join(STORE_LISTING_ROOT, play_locale, "short_description.txt")'
    assert_includes fastfile, 'File.join(STORE_LISTING_ROOT, play_locale, "full_description.txt")'

    lane = lane_body(fastfile, "publish_store_metadata")
    assert_includes lane, 'track: "production"'
    assert_includes lane, "skip_upload_metadata: false"
    assert_includes lane, "skip_upload_changelogs: true"
    assert_includes lane, "skip_upload_images: true"
    assert_includes lane, "skip_upload_screenshots: true"
    assert_includes lane, "skip_upload_apk: true"
    refute_includes lane, "aab:"
  end

  private

  def lane_body(source, name)
    match = source.match(/lane :#{Regexp.escape(name)} do(?: \|[^|]+\|)?\n(?<body>.*?)\n  end/m)
    refute_nil match, "missing Fastlane lane: #{name}"
    match[:body]
  end
end
