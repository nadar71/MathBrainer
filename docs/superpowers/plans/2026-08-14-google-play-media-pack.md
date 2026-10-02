# MathBrainer Google Play Media Pack Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Produce a complete, visually consistent Google Play media pack for MathBrainer using its modernized split-brain identity.

**Architecture:** Final assets live under `store-media/google-play/`, grouped by Play Console surface. Generated brand artwork is created non-destructively, while verified app screenshots are copied or newly captured and then cropped to compliant dimensions without changing application code.

**Tech Stack:** Built-in image generation, Android emulator/ADB capture, macOS image processing, PNG/JPEG metadata inspection.

## Global Constraints

- Preserve the split brain and arithmetic-symbol concept from the existing icon.
- Use matte black/charcoal, white, and amber `#F5A623`.
- Store icon is exactly `512 x 512`, full bleed, with no baked rounded corners or text.
- Feature graphic is exactly `1024 x 500`.
- Feature graphic title is exactly `Math Brainer`.
- Feature graphic supporting line is exactly `Train your mind. Master the numbers.`
- Phone screenshots remain authentic, ad-free `1080 x 2160` captures without device frames.
- Tablet captures must show native app rendering and contain no test-ad labels.
- Do not modify app code, runtime behavior, or launcher resources.
- Do not upload or publish assets externally.

---

### Task 1: Modernized Store Icon

**Files:**
- Reference: `app/src/main/res/mipmap-xxxhdpi/ic_launcher.png`
- Create: `store-media/google-play/icon/mathbrainer-store-icon-512.png`

**Interfaces:**
- Consumes: Existing split-brain launcher identity and global palette.
- Produces: Final square brand mark used as the visual reference for Task 2.

- [ ] **Step 1: Generate the modernized mark**

Use the existing icon as a reference image. Generate a flat, symmetrical split-brain mark with crisp white brain contours, restrained amber arithmetic symbols, and a matte black full-bleed background. Require no text, watermark, gradients, shadows, border, transparency, or rounded-corner mask.

- [ ] **Step 2: Inspect visual fidelity**

Confirm the result retains both brain hemispheres, has recognizable arithmetic symbols, uses no unintended text, and remains readable when previewed at `64 x 64`.

- [ ] **Step 3: Export exact dimensions**

Export the selected artwork as `store-media/google-play/icon/mathbrainer-store-icon-512.png` at exactly `512 x 512` pixels.

- [ ] **Step 4: Verify encoding and dimensions**

Run:

```bash
sips -g pixelWidth -g pixelHeight -g format store-media/google-play/icon/mathbrainer-store-icon-512.png
```

Expected: width `512`, height `512`, format `png`.

### Task 2: Feature Graphic

**Files:**
- Reference: `store-media/google-play/icon/mathbrainer-store-icon-512.png`
- Create: `store-media/google-play/feature-graphic/mathbrainer-feature-graphic-1024x500.png`

**Interfaces:**
- Consumes: Final Task 1 brain mark and exact approved copy.
- Produces: Final Play Store feature graphic.

- [ ] **Step 1: Generate the wide composition**

Create a `1024 x 500` landscape composition with a charcoal-black backdrop, subtle mathematical patterning, and the modernized brain mark. Keep the central region uncluttered and preserve strong contrast.

- [ ] **Step 2: Add exact approved copy**

Render these two lines exactly:

```text
Math Brainer
Train your mind. Master the numbers.
```

Use clean white typography with amber accent details. Do not add ratings, awards, prices, download claims, or calls to action.

- [ ] **Step 3: Inspect text and composition**

Visually verify spelling, punctuation, safe margins, logo fidelity, and absence of malformed or duplicate text.

- [ ] **Step 4: Export and verify dimensions**

Run:

```bash
sips -g pixelWidth -g pixelHeight -g format store-media/google-play/feature-graphic/mathbrainer-feature-graphic-1024x500.png
```

Expected: width `1024`, height `500`, format `png`.

### Task 3: Package Phone Screenshots

**Files:**
- Consume: `store-screenshots/play-store/*.png`
- Create: `store-media/google-play/phone-screenshots/*.png`

**Interfaces:**
- Consumes: The 19 previously verified phone screenshots.
- Produces: A complete, numbered phone screenshot selection pool.

- [ ] **Step 1: Copy the verified screenshot set**

Copy all 19 screenshots into `store-media/google-play/phone-screenshots/` while preserving their sequential filenames.

- [ ] **Step 2: Verify every file dimension**

Run `sips -g pixelWidth -g pixelHeight` for every PNG. Expected for each file: `1080 x 2160`.

- [ ] **Step 3: Audit screenshot content**

Visually inspect the complete set and reject any image containing consent dialogs, launcher screens, white loading screens, debug overlays, or `Test Ad` labels.

- [ ] **Step 4: Write selection guidance**

Create `store-media/google-play/phone-screenshots/SELECTION.md` ranking the strongest 8 screenshots and explaining the recommended listing order in one sentence per image.

### Task 4: Tablet Screenshot Sets

**Files:**
- Create: `store-media/google-play/tablet-7-inch/*.png`
- Create: `store-media/google-play/tablet-10-inch/*.png`

**Interfaces:**
- Consumes: Current debug APK and approved representative screen list.
- Produces: Separate 7-inch and 10-inch screenshot pools.

- [ ] **Step 1: Discover or create tablet emulator profiles**

List available AVDs and create one 7-inch and one 10-inch portrait tablet profile only when equivalent profiles do not already exist.

- [ ] **Step 2: Install and launch the current APK**

Install `app/build/outputs/apk/debug/app-debug.apk`, complete consent once, and verify the home catalog renders without clipping on each tablet.

- [ ] **Step 3: Capture representative screens**

Capture home, Memory Flash, Quick Count, Enigma, Random Operations, and Falling Ops on each tablet profile. Wait for challenge content to become visible before each capture.

- [ ] **Step 4: Remove only test-ad regions**

Crop each image without altering app content. Preserve the native tablet aspect ratio and system status area while excluding the bottom test-ad region.

- [ ] **Step 5: Verify tablet outputs**

Visually inspect all 12 tablet images for authentic app UI, readable content, no malformed layout, and no `Test Ad` labels.

### Task 5: Promotional Video Support Pack

**Files:**
- Create: `store-media/google-play/promo-video/SHOT-LIST.md`
- Create: `store-media/google-play/promo-video/YOUTUBE-COPY.md`

**Interfaces:**
- Consumes: Final screenshot selection and approved brand language.
- Produces: A practical recording plan and YouTube metadata; no video is uploaded or generated.

- [ ] **Step 1: Write the shot list**

Create a 30-second portrait recording sequence covering home discovery, one choose-result challenge, Memory Flash, Quick Count, Falling Ops, and the statistics screen. Specify approximate duration and transition for each shot.

- [ ] **Step 2: Write YouTube copy**

Provide one concise video title, one short description, and one privacy-safe recording checklist. Do not claim outcomes that the app does not guarantee.

### Task 6: Final Compliance Audit

**Files:**
- Create: `store-media/google-play/README.md`

**Interfaces:**
- Consumes: Every artifact from Tasks 1-5.
- Produces: Final inventory and validation report.

- [ ] **Step 1: Inventory final assets**

List all deliverables by Play Console category, path, pixel dimensions, format, and count.

- [ ] **Step 2: Run metadata validation**

Use `sips` to verify dimensions and formats for every final PNG. Flag any mismatch instead of silently resizing an incorrect source.

- [ ] **Step 3: Perform final visual review**

Inspect the icon, feature graphic, all phone screenshots, and all tablet screenshots for clipping, bad text, debug artifacts, and inconsistent branding.

- [ ] **Step 4: Record optional items clearly**

Document that a promotional video remains optional and requires a public or unlisted YouTube URL in Play Console.

- [ ] **Step 5: Commit the completed media pack**

Stage only `store-media/google-play/` and the implementation plan, then commit with:

```bash
git commit -m "assets: add Google Play media pack"
```
