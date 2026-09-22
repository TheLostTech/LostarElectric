---
name: Lostar Electric / House Core
description: Implemented House Core surface and shared Lostar identity, September 2026.
colors:
  hc-ink: '#0b2545'
  blue-900: '#071a31'
  hc-yellow: '#f4b500'
  hc-teal: '#276466'
  white: '#ffffff'
  anthracite: '#20252b'
  hc-muted: '#526170'
  hc-paper: '#f4f6f6'
  hc-line: '#d5dfe2'
  gray-300: '#c8d1dc'
  blue-050: '#eef4fa'
  field-border: '#a7b5be'
  dark-copy: '#d0dce5'
  dark-divider: '#3a5069'
  yellow-hover: '#dfaa10'
  gray-700: '#53606f'
typography:
  display:
    fontFamily: Rockstar, Inter, "Helvetica Neue", Arial, sans-serif
    fontSize: clamp(56px, 5.65vw, 82px)
    fontWeight: 800
    lineHeight: 1.02
    letterSpacing: '0'
  headline:
    fontFamily: Rockstar, Inter, "Helvetica Neue", Arial, sans-serif
    fontSize: clamp(38px, 4vw, 58px)
    fontWeight: 800
    lineHeight: 1.05
    letterSpacing: '0'
  title:
    fontFamily: Rockstar, Inter, "Helvetica Neue", Arial, sans-serif
    fontSize: 28px
    fontWeight: 800
    lineHeight: 1.15
    letterSpacing: '0'
  body:
    fontFamily: Inter, "Helvetica Neue", Arial, sans-serif
    fontSize: 16px
    fontWeight: 400
    lineHeight: 1.65
    letterSpacing: normal
  lead:
    fontFamily: Inter, "Helvetica Neue", Arial, sans-serif
    fontSize: 18px
    fontWeight: 400
    lineHeight: 1.65
    letterSpacing: normal
  label:
    fontFamily: Inter, "Helvetica Neue", Arial, sans-serif
    fontSize: 14px
    fontWeight: 600
    lineHeight: 1.5
    letterSpacing: normal
  navigation:
    fontFamily: Inter, "Helvetica Neue", Arial, sans-serif
    fontSize: 14px
    fontWeight: 700
    lineHeight: 1.5
    letterSpacing: normal
  button:
    fontFamily: Inter, "Helvetica Neue", Arial, sans-serif
    fontSize: 15px
    fontWeight: 800
    lineHeight: 1.5
    letterSpacing: normal
  field:
    fontFamily: Inter, "Helvetica Neue", Arial, sans-serif
    fontSize: 16px
    fontWeight: 400
    lineHeight: 1.5
    letterSpacing: normal
  small:
    fontFamily: Inter, "Helvetica Neue", Arial, sans-serif
    fontSize: 13px
    fontWeight: 400
    lineHeight: 1.6
    letterSpacing: normal
rounded:
  surface: '0'
  field: 2px
  control: 4px
spacing:
  inline: 8px
  compact: 12px
  row: 16px
  control: 22px
  panel: 24px
  request: 32px
  split: 64px
  wide: 80px
  section: clamp(72px, 8vw, 120px)
components:
  button-download:
    backgroundColor: '{colors.hc-yellow}'
    textColor: '{colors.hc-ink}'
    typography: '{typography.button}'
    rounded: '{rounded.control}'
    padding: 12px 22px
  button-download-hover:
    backgroundColor: '{colors.yellow-hover}'
  button-primary:
    backgroundColor: '{colors.hc-ink}'
    textColor: '{colors.white}'
    typography: '{typography.button}'
    rounded: '{rounded.control}'
    padding: 12px 22px
  button-primary-hover:
    backgroundColor: '{colors.blue-900}'
  button-secondary:
    backgroundColor: '{colors.white}'
    textColor: '{colors.hc-ink}'
    typography: '{typography.button}'
    rounded: '{rounded.control}'
    padding: 12px 22px
  button-secondary-hover:
    backgroundColor: '{colors.blue-050}'
  input:
    backgroundColor: '{colors.white}'
    textColor: '{colors.hc-ink}'
    typography: '{typography.field}'
    rounded: '{rounded.field}'
    padding: 12px
  navigation:
    textColor: '{colors.gray-700}'
    typography: '{typography.navigation}'
  layer-selector:
    backgroundColor: '{colors.hc-paper}'
    rounded: '{rounded.surface}'
    padding: 16px 22px
  download-row:
    backgroundColor: '{colors.white}'
    textColor: '{colors.hc-ink}'
    rounded: '{rounded.surface}'
    padding: 24px
  request-surface:
    backgroundColor: '{colors.hc-paper}'
    rounded: '{rounded.surface}'
    padding: 32px
  faq-summary:
    textColor: '{colors.hc-ink}'
    padding: 0 20px 0 0
---

# Design System: Lostar Electric / House Core

## Overview

**Creative North Star: "Tavolo luminoso dei livelli"**

Lostar’s existing logo, navy and yellow palette, and Rockstar display face remain the identity anchors. The implemented House Core surface places crisp technical drawings and real exported documents on white and pale paper, with generous space and short, direct Italian copy. Its strong display titles, fine rules and functional controls keep the presentation concrete.

This extraction covers the replacement House Core page, the House Core entry on the root home page, and the shared navigation, buttons and footer they use. “Tavolo luminoso dei livelli” names the House Core composition; it is not a replacement identity for Lostar’s industrial services. Those pages retain their earlier visual system. Page strategy and the surface direction remain in `docs/house-core-direction.md`; product commitments remain in `PRODUCT.md`.

**Key Characteristics:**

- Existing Lostar logo, navy/yellow identity and Rockstar titles.
- White and pale paper surfaces, square technical panels and thin dividers.
- A legible plan and exported PDF provide the main visual evidence.
- Visible controls, text states and keyboard focus explain interaction.

Extracted from `site/styles.css`, `site/house-core.css`, `site/house-core.html`, `site/index.html`, and the desktop/mobile review captures in `.impeccable/review/`. The frontmatter owns the extracted token values. The sidecar owns motion, elevation, breakpoints and isolated component examples.

## Colors

The palette retains Lostar’s dark blue and yellow and adds pale drafting surfaces with a restrained teal functional accent.

### Primary

- **Lostar Navy** (`hc-ink`): section headings, active navigation, navy buttons, dark journey/download bands and diagram/control ink; it matches the shared `--blue-800`.
- **Lostar Yellow** (`hc-yellow`): the main House Core download action, journey numbers, selected navigation underline and focus on dark bands; it matches the shared `--yellow`.
- **Deep Navy** (`blue-900`): the hero headline and hover state of navy buttons.
- **Download Hover** (`yellow-hover`): the yellow CTA’s hover state.

### Secondary

- **Technical Teal** (`hc-teal`): thin capability icons, offer labels and focus on light backgrounds. It supports the existing brand colors.

### Neutral

- **White** (`white`): base page, drawing stage, fields and download rows.
- **Anthracite** (`anthracite`): inherited body text.
- **Technical Slate** (`hc-muted`): House Core explanatory copy, captions and field notes.
- **Shared Slate** (`gray-700`): incumbent navigation and the home entry’s supporting copy.
- **Light Paper** (`hc-paper`): plan framing, document section and request surface.
- **Paper Rule** (`hc-line`): section rules, rows and panel divisions.
- **Control Border** (`gray-300`): shared secondary-button border; **Field Border** (`field-border`): House Core input border.
- **Pale Blue** (`blue-050`): secondary-button hover fill.
- **Dark-surface Copy** (`dark-copy`) and **Dark Divider** (`dark-divider`): readable supporting text and separators within navy bands.

**The Identity Continuity Rule.** Keep the existing logo assets, navy/yellow identity and Rockstar display family across the new surface and its home-page entry.

**The Focus Contrast Rule.** Use teal focus outlines on light surfaces and yellow outlines inside the navy journey, download section and shared footer.

## Typography

**Display Font:** Rockstar, with Inter, Helvetica Neue, Arial and sans-serif fallbacks. The bundled font face is `site/fonts/Rockstar-ExtraBold.otf`, registered at weight 800. Its compact uppercase appearance comes from the font; do not add unnecessary letter spacing or convert all prose to uppercase.

**Body Font:** Inter, Helvetica Neue, Arial, sans-serif. Inter is requested by the stack but is not separately bundled or imported by these pages, so system fallbacks are part of the implemented behavior. There is no separate mono family; installation commands inherit the body face.

The wide difference between heavy titles and quiet supporting text carries the hierarchy. Use the frontmatter’s `display`, `headline` and `title` roles for the House Core headings, `body` for normal paragraphs, `lead` for hero/download introductions, and `label`, `navigation`, `button`, `field` and `small` for controls. Major headings have zero tracking. Capability and form headings use the observed 27px title variant; the default title is 28px.

The body is the browser’s 16px default with paragraph line-height 1.65; inherited non-paragraph UI text keeps the shared 1.5 line-height. Section introductions commonly use 17px. Hero copy is constrained to a 510px column; FAQ paragraphs are limited to 65ch. The frontmatter records desktop heading roles; the responsive sizes are specified below.

## Layout

The shared header is fixed at 72px high, with a 136px logo, centered navigation, a right-hand CTA and a menu control when space narrows. It uses a translucent white fill, a bottom border and a 12px backdrop blur. Main content clears the fixed header. The House Core subnavigation is a separate normal-flow row with a 66px minimum height.

House Core content uses a maximum 1280px width and 48px side gutters. Section vertical padding follows the fluid `section` spacing token. The hero pairs a 0.9fr copy column with a 1.15fr plan column; its gap scales from 32px to 64px, with 64px top and 72px bottom padding. Most following sections use two unequal columns, generous 64–80px gaps, and thin ruled rows. The three-step navy journey is an equal three-column band. The home entry uses the incumbent section container with a two-column split and 64px gap; it does not replace the industrial home layout.

Responsive behavior is deliberate and scoped:

- At 1600px and above, the hero gap becomes 80px and its title 88px.
- At 1060px and below, the shared main navigation becomes a toggleable full-width panel below the header. The menu button is 44px square.
- At 1050px and below, House Core gutters become 28px, hero columns become 0.9fr / 1fr with a 30px gap, H1 uses `clamp(46px, 6vw, 66px)`, H2 is 44px, and major section gaps become 40px. “di Lostar” leaves the subnavigation.
- At 760px and below, House Core gutters become 20px and its main grids stack. The hero uses 40px / 44px vertical padding, a 36px gap and `clamp(48px, 9vw, 68px)` H1; H2 is 40px. The document copy comes before its image. Form fields stack, request padding becomes 24px, and the journey becomes three ruled rows. Subnavigation is 60px high and omits “Il progetto.” The home entry stacks with a 28px gap.
- At 640px and below, the shared header uses `1fr auto`, hides its CTA and right-aligns the menu button; its logo is 120px wide. The shared footer stacks. Shared buttons become full-width unless a House Core component explicitly keeps an auto width, as the hero, document and closing CTAs do.

The incumbent industrial layout also has a 900px breakpoint; preserve its existing behavior instead of interpreting it as a new House Core rule. Anchor scroll padding is 140px on House Core, reduced to 90px below 760px.

## Elevation & Depth

The House Core surface is predominantly flat. White, paper and navy zones, fine rules and the drawing itself create separation. The exported document preview alone uses a soft paper shadow (`0 18px 36px #0b25451a`). The shared stylesheet retains its earlier visual shadow (`0 16px 40px rgba(11, 37, 69, 0.12)`) for incumbent imagery; it is not the default for new House Core panels. Header translucency is the only blurred layer in this surface.

**The Surface Scope Rule.** Use the light-table composition for House Core; do not apply its page structure to the industrial service pages by default.

## Shapes

Technical surfaces and download rows have square corners. Fields use the `field` radius and shared buttons/menu controls use `control`. Dividers are generally one pixel, while the first hardware offer has a two-pixel navy top rule and the shared footer has a four-pixel yellow top border. Line icons use open strokes; capability/download icons are 26px with a 1.5px stroke, while shared button icons are 17px with a 2px stroke. Keep the existing logo artwork intact.

## Components

### Buttons

Compact, substantial rectangular controls. House Core buttons use the frontmatter padding, a 52px minimum height, a 10px label/icon gap, centered wrapping text and the `control` radius. The shared header/home button baseline remains 44px minimum height with 14px text and horizontal 18px padding. Yellow signals the main House Core download; navy is used for document downloads and request actions; white with a gray outline is secondary. Hover uses the recorded sibling variants. Pressing a button shifts it down one pixel.

Button transitions are 140ms ease for transform, background and border color. Focus is a three-pixel outline offset by five pixels; use the surface-appropriate color described above. Reduced-motion preference removes transitions and animations and restores automatic scrolling.

### Inputs / Fields

Plain white fields on the paper request surface. Use a one-pixel field border, `field` radius, 12px padding, 48px minimum height and 16px text. Labels are 14px semibold with an 8px gap. Textareas have a 106px minimum height and resize vertically. The request surface uses 32px padding, a 22px row gap and 18px gaps within the two-field row; it becomes one column on mobile. Validation is native browser validation; this page does not define custom error or disabled visual variants. Form status is text in a live status region.

### Navigation

Shared navigation uses compact bold sans-serif links, with a two-pixel yellow underline on the current page and navy text on hover/current state. The underline transform has a 160ms ease transition. Mobile expansion is controlled by the menu button’s `aria-expanded`; its panel begins below the fixed header. The separate House Core subnavigation uses 14px semibold links with a visible hover underline, reducing to 13px below 760px. Preserve the focus-revealed skip link before the header.

### Layered plan

A square paper-framed figure containing a white drawing stage, a labelled heading, a fieldset and a caption. The real plan remains the main subject. The layer selector uses native 17px navy-accent checkboxes, 14px labels and 8px label gaps. A visible text status with `aria-live="polite"` states which layers are shown. The controls are revealed after the interactive SVG is available; a descriptive static image remains the fallback. Checkbox changes control the existing geometry without decorative animation.

### Download rows / Containers

Operating-system downloads are square white rows on navy, with three columns: a 44px icon track, flexible copy and a trailing download icon. They use 24px padding and an 18px gap; mobile uses 22px padding and a 12px gap. The primary label is 18px bold, the note 13px muted. Hover changes the surface to the observed pale neutral `#f0f3f5`; focus stays yellow because these rows belong to the navy download section. There is no pill/chip system in the new surface.

### Disclosure rows

FAQ and installation help use native `details` / `summary`. FAQ rows have a fine bottom rule, 22px bottom padding and margin, an 18px semibold summary, and a 15px answer. Installation help on navy uses a smaller 15px summary and light supporting text. Keep native expansion behavior, keyboard interaction and visible focus; there is no custom opening animation.

## Do's and Don'ts

### Do:

- Do preserve the existing Lostar logo and the Rockstar / sans-serif hierarchy.
- Do show project geometry and exported documents with a visible description of their demonstrative status.
- Do use the current container widths and stacked mobile reading order before adding new layout rules.
- Do preserve native controls, visible focus, text status and reduced-motion behavior.
- Do keep House Core composition rules scoped to House Core and its home entry.

### Don't:

- Don’t treat the House Core light-table composition as a new identity for the industrial services.
- Don’t replace the plan or real exported document with generic decorative feature cards.
- Don’t remove layer labels, the live visibility status or the static plan fallback.
- Don’t use color alone to communicate a selected layer or an interaction state.
- Don’t describe illustrative imagery as a photograph of a completed Lostar installation.
