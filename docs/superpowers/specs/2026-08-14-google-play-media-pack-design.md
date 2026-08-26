# MathBrainer Google Play Media Pack Design

## Objective

Create a cohesive Google Play media pack that modernizes MathBrainer's existing split-brain identity while remaining visually consistent with the app's dark interface and amber accent color.

## Visual Direction

- Preserve the split brain and arithmetic-symbol concept from the current launcher icon.
- Simplify the mark so it remains legible at small store and launcher sizes.
- Use matte black and charcoal backgrounds, white structure and typography, and amber `#F5A623` accents.
- Favor flat, crisp geometry over cartoon, photorealistic, or dimensional styling.
- Do not bake rounded corners into the store icon; Google Play applies its own mask.
- Avoid watermarks, unrelated imagery, excessive text, and promotional claims.

## Deliverables

### Store Icon

- One `512 x 512` PNG.
- Modernized split-brain mark with arithmetic symbols.
- Full-bleed square artwork without transparency or pre-rendered corner radius.
- No text.

### Feature Graphic

- One `1024 x 500` PNG or JPEG.
- Dark charcoal mathematical backdrop with restrained patterning.
- Modernized brain mark as the focal element.
- Exact title: `Math Brainer`.
- Exact supporting line: `Train your mind. Master the numbers.`
- Strong central safe area so cropping and store overlays do not obscure essential content.

### Phone Screenshots

- Reuse the verified `1080 x 2160` ad-free gameplay captures.
- Preserve authentic application UI without decorative device frames.
- Keep all 19 variants available so the final listing set can be selected later.

### Tablet Screenshots

- Capture separate 7-inch and 10-inch tablet sets when the app renders correctly on those emulator profiles.
- Use representative screens rather than duplicating every phone screenshot.
- Prioritize home, Memory Flash, Quick Count, Enigma, Random Operations, and Falling Ops.
- Preserve the native tablet layout and remove test-ad areas from final exports.

### Promotional Video

- No video file is generated in this scope.
- Provide a short recommended shot list and title/description copy because Play Console uses a YouTube URL for the promotional video.

## Delivery Structure

Store final artifacts under `store-media/google-play/`:

- `icon/`
- `feature-graphic/`
- `phone-screenshots/`
- `tablet-7-inch/`
- `tablet-10-inch/`
- `promo-video/`

Keep intermediate generation outputs outside the final folders. Existing raw screenshots remain unchanged.

## Validation

- Verify exact pixel dimensions and PNG/JPEG encoding.
- Visually inspect every final image for clipping, malformed text, test-ad labels, and unintended artifacts.
- Confirm the icon is recognizable at small preview size.
- Confirm title and supporting line are spelled exactly in the feature graphic.
- Confirm screenshots show only MathBrainer content and normal system chrome.

## Scope Boundaries

- Do not change application code or runtime behavior solely for marketing assets.
- Do not replace in-app launcher resources unless requested separately.
- Do not publish or upload assets to Play Console.
