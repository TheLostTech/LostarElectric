---
name: Lostar Electric
description: Approved modern industrial theme for the Lostar Electric and House Core website.
colors:
  blue-800: '#0b2545'
  blue-900: '#071a31'
  yellow: '#f4b500'
  yellow-hover-bright: '#ffca27'
  focus: '#276466'
  white: '#fff'
  anthracite: '#202b35'
  gray-700: '#526170'
  gray-300: '#a7b5be'
  gray-200: '#d5dfe2'
  gray-050: '#f4f6f6'
  blue-050: '#eef4fa'
  dark-copy: '#d0dce5'
  nav-copy: '#e4ebf1'
  dark-divider: '#3a5069'
  yellow-hover: '#dfaa10'
  download-hover: '#f0f3f5'
typography:
  display:
    fontFamily: Archivo, "Helvetica Neue", Arial, sans-serif
    fontSize: clamp(56px, 6vw, 90px)
    fontWeight: 800
    lineHeight: 1.04
    letterSpacing: '-0.035em'
  display-home:
    fontFamily: Archivo, "Helvetica Neue", Arial, sans-serif
    fontSize: clamp(68px, 7.25vw, 112px)
    fontWeight: 900
    lineHeight: 0.9
    letterSpacing: '-0.04em'
  display-contact:
    fontFamily: Archivo, "Helvetica Neue", Arial, sans-serif
    fontSize: clamp(52px, 6vw, 88px)
    fontWeight: 800
    lineHeight: 1.04
    letterSpacing: '-0.035em'
  display-house-core:
    fontFamily: Archivo, "Helvetica Neue", Arial, sans-serif
    fontSize: clamp(52px, 5.2vw, 74px)
    fontWeight: 800
    lineHeight: 1.02
    letterSpacing: '0'
  headline:
    fontFamily: Archivo, "Helvetica Neue", Arial, sans-serif
    fontSize: clamp(36px, 4vw, 54px)
    fontWeight: 800
    lineHeight: 1.04
    letterSpacing: '-0.035em'
  headline-house-core:
    fontFamily: Archivo, "Helvetica Neue", Arial, sans-serif
    fontSize: clamp(38px, 4vw, 58px)
    fontWeight: 800
    lineHeight: 1.05
    letterSpacing: '0'
  title:
    fontFamily: Archivo, "Helvetica Neue", Arial, sans-serif
    fontSize: 28px
    fontWeight: 800
    lineHeight: 1.04
    letterSpacing: '-0.035em'
  service-title:
    fontFamily: Archivo, "Helvetica Neue", Arial, sans-serif
    fontSize: 34px
    fontWeight: 800
    lineHeight: 1.04
    letterSpacing: '-0.035em'
  body:
    fontFamily: 'Archivo, "Helvetica Neue", Arial, sans-serif'
    fontSize: 16px
    fontWeight: 400
    lineHeight: 1.6
    letterSpacing: normal
  lead:
    fontFamily: 'Archivo, "Helvetica Neue", Arial, sans-serif'
    fontSize: 19px
    fontWeight: 400
    lineHeight: 1.65
    letterSpacing: normal
  label:
    fontFamily: 'Archivo, "Helvetica Neue", Arial, sans-serif'
    fontSize: 14px
    fontWeight: 600
    lineHeight: 1.5
    letterSpacing: normal
  button:
    fontFamily: 'Archivo, "Helvetica Neue", Arial, sans-serif'
    fontSize: 15px
    fontWeight: 700
    lineHeight: 1.4
    letterSpacing: normal
  field:
    fontFamily: 'Archivo, "Helvetica Neue", Arial, sans-serif'
    fontSize: 16px
    fontWeight: 600
    lineHeight: 1.5
    letterSpacing: normal
  caption:
    fontFamily: 'Archivo, "Helvetica Neue", Arial, sans-serif'
    fontSize: 12px
    fontWeight: 400
    lineHeight: 1.6
    letterSpacing: normal
  navigation:
    fontFamily: 'Archivo, "Helvetica Neue", Arial, sans-serif'
    fontSize: 14px
    fontWeight: 600
    lineHeight: 1.6
    letterSpacing: normal
rounded:
  surface: '0'
  field: 2px
  control: 3px
spacing:
  label-gap: 8px
  control-gap: 12px
  compact: 16px
  field-row: 18px
  form-gap: 22px
  panel-compact: 24px
  panel: 32px
  section-gap: 80px
  section-space: clamp(72px, 8vw, 112px)
  hc-space: clamp(72px, 8vw, 120px)
components:
  button-primary:
    backgroundColor: '{colors.blue-800}'
    textColor: '{colors.white}'
    typography: '{typography.button}'
    rounded: '{rounded.control}'
    padding: 12px 22px
  button-primary-hover:
    backgroundColor: '{colors.blue-900}'
  button-yellow:
    backgroundColor: '{colors.yellow}'
    textColor: '{colors.blue-800}'
    typography: '{typography.button}'
    rounded: '{rounded.control}'
    padding: 12px 22px
  button-yellow-hover:
    backgroundColor: '{colors.yellow-hover-bright}'
  button-secondary:
    backgroundColor: '{colors.white}'
    textColor: '{colors.blue-800}'
    typography: '{typography.button}'
    rounded: '{rounded.control}'
    padding: 12px 22px
  button-secondary-hover:
    backgroundColor: '{colors.blue-050}'
  input:
    backgroundColor: '{colors.white}'
    textColor: '{colors.blue-800}'
    typography: '{typography.field}'
    rounded: '{rounded.field}'
    padding: 12px
  navigation:
    textColor: '{colors.nav-copy}'
    typography: '{typography.navigation}'
  service-row:
    textColor: '{colors.gray-700}'
    rounded: '{rounded.surface}'
    padding: 34px 0
  request-surface:
    backgroundColor: '{colors.gray-050}'
    rounded: '{rounded.surface}'
    padding: 32px
  recipient-notice:
    backgroundColor: '{colors.white}'
    textColor: '{colors.gray-700}'
    rounded: '{rounded.surface}'
    padding: 16px
  faq-summary:
    textColor: '{colors.blue-800}'
    padding: 0 16px 0 0
  layer-selector:
    backgroundColor: '{colors.gray-050}'
    rounded: '{rounded.surface}'
    padding: 16px 22px
  download-row:
    backgroundColor: '{colors.white}'
    textColor: '{colors.blue-800}'
    rounded: '{rounded.surface}'
    padding: 24px
---

# Design System: Lostar Electric

## Overview

**Creative North Star: "Energia sotto controllo"**

The approved theme keeps Lostar's logo and navy/yellow palette while giving the site a stronger industrial presence. A dark shared header, variable Archivo type, a full-bleed illustrative panel image on home and bold white service heroes create the visual entry. The ruled service rows and pale technical drawing grounds carry the detail below the first viewport.

This system covers home, industrial services, contacts, House Core, articles and utility pages. Shared navigation, typography, footer, controls, focus treatment and palette keep them coherent. The home hero image is explicitly illustrative, not a photographed Lostar installation. Industrial service pages retain schematic figures; House Core retains its specialized plan, document and download layouts. Surface strategy remains in `docs/lostar-site-direction.md` and `.impeccable/surfaces/site-index-html.md`; product commitments remain in `PRODUCT.md`.

**Key Characteristics:**

- Existing Lostar logo and navy/yellow palette, with Archivo variable type throughout the active theme.
- Dark shared header, full-bleed illustrative home image and bold navy service heroes.
- Illustrative SVG diagrams for industrial services; interactive plan and PDF evidence for House Core.
- Unified request controls, visible recipient status and contextual document lists.
- Responsive reading order, native disclosures and high-contrast keyboard focus.

Updated from the current `site/*.html` pages, `site/styles.css`, `site/modern-theme.css` and `site/house-core.css`, with the approved preview as direction. This supersedes the September technical-catalogue visual choices while retaining their verified content and House Core flow. Frontmatter owns primitive token values; `.impeccable/design.json` adds motion, breakpoints, elevation and isolated component examples. The named north star describes the active theme, not a new brand identity.

## Colors

The shared palette combines Lostar navy and yellow with white, pale paper and restrained technical slate.

### Primary

- **Lostar Navy** (`blue-800`): display headings, primary actions, dark sections, diagram ink and footer.
- **Lostar Yellow** (`yellow`): important actions on navy, current-page navigation markers, small diagram accents and focus on dark surfaces.
- **Deep Navy** (`blue-900`): shared header, deepest fields and footer. **Bright Yellow Hover** (`yellow-hover-bright`) is the active shared action hover; the earlier `yellow-hover` remains on House Core's local action.

### Secondary

- **Technical Teal** (`focus`): light-surface focus rings, checklist strokes and House Core capability icons. It remains a functional supporting accent.

### Neutral

- **White** (`white`): page ground, text on dark heroes, fields and download rows.
- **Anthracite** (`anthracite`): normal body text.
- **Technical Slate** (`gray-700`): descriptions, navigation, captions and notes.
- **Light Paper** (`gray-050`): diagram grounds, alternating sections and request containers.
- **Control Border** (`gray-300`) and **Paper Rule** (`gray-200`): field/button outlines and section/row dividers.
- **Pale Blue** (`blue-050`): outlined-button hover fill.
- **Dark Copy** (`dark-copy`) and **Dark Divider** (`dark-divider`): secondary text and rules on navy. **Nav Copy** (`nav-copy`) keeps links readable in the dark header.
- **Download Hover** (`download-hover`): House Core’s white operating-system rows on hover.

House Core’s existing `hc-ink`, `hc-yellow`, `hc-muted`, `hc-paper`, `hc-line` and `hc-teal` CSS variables are local aliases of the shared palette represented here; they do not establish a separate palette. Unused stylesheet declarations are not promoted into normative tokens.

**The Identity Continuity Rule.** Keep the existing logo assets and navy/yellow palette across industrial and House Core pages; use Archivo in the active theme.

**The Focus Contrast Rule.** Use teal focus outlines on light surfaces and yellow outlines in the dark header, navy sections and the footer.

## Typography

**Display Font:** Archivo variable, then Helvetica Neue, Arial, sans-serif. The local WOFF2 face is registered for weights 400–900 with `font-display: swap`. Shared headings use 800 weight and tight tracking; the home H1 uses 900, uppercase styling and tighter tracking. House Core headings keep their own line-height and zero tracking while inheriting Archivo.

**Body Font:** Archivo variable, then Helvetica Neue, Arial, sans-serif. Body text is 16px with 1.6 line-height; paragraphs are limited to 70ch. There is no separate mono family.

The frontmatter records the effective desktop hierarchy: service display, larger home display, contact display and House Core display; shared and House Core headlines; titles and service-row titles; body, lead, labels, controls and captions. Shared lead text is 19px/1.65 except where the visual hero overrides it. Supporting section introductions use 17–18px. Labels and navigation are semibold; shared action labels are bold.

Specialized roles remain limited to their contexts. Home service-row headings are 34px Archivo. Work-sequence titles are 18px/1.3 semibold body type. House Core capability headings use 22px/1.35 semibold body type and hardware-offer headings use 24px/1.3; its request title remains 27px Archivo. House Core paragraphs use 1.65 line-height, while controls inherit the shared system. Request values inherit the labels’ semibold weight through `font: inherit`; no regular-weight field override is implemented.

Responsive heading sizes are described with their layouts below. Do not restore earlier House Core sizes that are superseded by the final overrides in its stylesheet.

## Layout

The site is a set of six static pages with common navigation and footer. Shared content uses a maximum 1280px width and 48px side gutters. Section padding follows `section-space`, ranging from 72px to 112px; House Core uses `hc-space`, capped at 120px. Generous gaps separate large content groups; fine ruled rows handle denser information.

The fixed shared header is deep navy, 86px high on desktop, with a subtle bottom rule, reverse logo, light links and a yellow right-hand request CTA. It has no blur or translucency. Main content clears the header through the shared height variable. The footer uses three columns (1.2fr / 0.8fr / 0.8fr), 64px gaps and a lower ruled row; it remains deep navy.

Home uses a full-width illustrative industrial image with a navy overlay, white headline, yellow action and visible caption. Three navy paths beneath it lead to Quadri elettrici, Automazione and House Core; they remain visible and stack on mobile. Service heroes retain the copy/diagram grid but now sit on navy, with white type and yellow actions. Below the heroes, ruled service rows, explanation grids, definition rows, pale supply sections, request bands and native FAQ rows preserve the technical reading path. The home House Core entry still pairs its title with the plan.

The contact page uses a 0.85fr / 1.15fr checklist/form split. Forms share a square paper container, a 22px content gap and paired field rows where room permits. Recipient status precedes editable fields. The service selector changes the supporting document checklist; incoming service links can preselect the corresponding request type. These are functional controls, not decorative filters.

Shared responsive behavior:

- At 1200px and below, the shared header tightens its navigation gaps, and the home hero copy and H1 scale down.
- At 1120px and below, content gutters become 32px, the header CTA is hidden, service hero gaps become 40px and large section splits reduce to 44px gaps.
- At 860px and below, the header becomes 72px high and its 46px menu button appears at the right. Navigation becomes a dark vertical panel below the header. Home keeps its image background and three visible paths; service hero grids retain two columns until mobile. Section intros and service explanations stack. The work sequence becomes two columns, contact field pairs stack, and request padding becomes 24px.
- At 640px and below, shared content has 20px gutters, the logo is 138px and the main content grids stack with 34px gaps. The home image shifts right under a stronger navy overlay; its H1 uses `clamp(48px, 13vw, 70px)` and the three paths stack. Service H1 uses `clamp(44px, 11vw, 64px)`, while shared H2 remains 38px. Service rows become title/description plus a trailing arrow. The work sequence becomes one column. Contact form precedes the checklist and uses 24px vertical / 20px horizontal padding. Action-group buttons fill the available width and form actions stack. Footer brand spans both columns of the two-column link grid; its lower row stacks.

House Core retains its plan-specific structure: a 0.9fr / 1.15fr hero, a three-step navy journey, two-column capability rows and separate document, hardware, download and request sections. The subnavigation remains in normal flow below the shared header. At 1050px its gutters become 28px and major gaps narrow; at 760px gutters become 20px, its grids stack, document copy precedes the preview, field pairs stack and the journey becomes three ruled rows. The subnavigation reduces from 66px to 60px and drops its first link. Its effective H1 is the frontmatter’s House Core display role at all wider sizes, then `clamp(46px, 10vw, 62px)` at 760px and below. H2 is 44px below 1050px and 40px below 760px. Above 1600px its hero gap increases to 80px; the later H1 override still governs typography.

Shared anchor scroll padding is 104px. House Core overrides it to 140px, reduced to 90px below 760px. Native drawing proportions and visible captions are preserved across the breakpoints.

## Elevation & Depth

The full site is flat: white, paper and navy fields, one-pixel rules and drawing geometry supply structure. The sole implemented shadow is the House Core PDF-preview shadow (`0 18px 36px #0b25451a`), which suggests a document resting on paper. Shared service rows, form surfaces, diagrams, header and footer have no box shadow. There is no backdrop blur.

**The Flat Surface Rule.** Separate sections with white, paper, navy and fine rules; reserve the observed paper shadow for the House Core document preview.

## Shapes

Diagram grounds, service rows, request panels and download rows are square. Native fields use the `field` radius; shared buttons and the header CTA use the three-pixel `control` radius. The mobile menu keeps its existing four-pixel radius. Most dividers are one pixel; the current navigation marker is two pixels in the modern header, while House Core’s first hardware offer uses a two-pixel top rule. Avoid turning ruled structures into floating rounded cards.

Shared button icons are 18px with a 1.5px stroke, service arrows 24px with a 1.5px stroke and text-link arrows 19px. House Core capability/download icons are 26px with a 1.5px stroke. Icons remain outline SVGs. Keep the supplied logo artwork intact.

## Components

### Buttons and text links

Shared buttons are 50px minimum height with 12px/22px padding, a 12px label/icon gap, bold 15px/1.4 text and three-pixel corners. At the smallest shared breakpoint horizontal padding becomes 18px. The home hero's yellow action is larger: 58px minimum height on desktop and 54px on mobile. The header CTA is 44px minimum height with 10px/16px padding and 14px text. House Core buttons explicitly retain 52px minimum height and 12px/22px padding.

Navy remains the standard primary action on light surfaces; yellow is dominant in the header and dark heroes, with a brighter shared hover state. White with a control-border outline is secondary. Button colors transition in 160ms; modern shared buttons and header CTA move one pixel down on active press. Text links keep an underline, with a thicker underline on hover.

### Shared navigation

Navigation links use semibold 14px light text, 48px minimum height and a responsive gap. Hover and current-page text become white; the current page keeps a two-pixel yellow marker. At the mobile-menu breakpoint the dark panel sits below the 72px header, uses 16px links and 46px targets; hover and current text become yellow. The menu button exposes its expanded state and controls the same link set. A focus-revealed skip link precedes the header; main targets can receive focus.

### Service rows and technical figures

The three immediate home paths are large links on a navy strip below the photograph. Later home service links are full-width ruled rows with a 34px Archivo title, muted description and arrow. Hover subtly fills the row, underlines the title and shifts the arrow four pixels. Desktop rows use 34px vertical padding; mobile rows use 26px and preserve the arrow beside the text. Interior service pages use definition rows and unboxed checklists, with body-type labels instead of extra display headings.

The home hero uses a local illustrative raster, shown full-bleed under a navy contrast overlay. Its visible caption says the pictured panel does not document a Lostar realization. Industrial service drawings remain illustrative SVGs, with captions explaining that they are schematic rather than supplied configurations or completed work. House Core’s home entry has a visible demo caption. Keep all captions visible when reusing a figure.

### Request forms and recipient status

Both request forms use white fields on paper, one-pixel control borders, two-pixel corners, 12px input padding, 48px minimum field height, 16px values and labels separated by 8px. Shared field pairs have an 18px gap. Contact textarea minimum height is 140px; House Core’s is 106px. Both resize vertically.

The recipient notice is a square bordered block before the first editable field. On the contact page it has a white fill; House Core’s pending notice inherits the paper surface. The notice explains the current channel before the user enters information. When a recipient is unavailable, the interface supports copying text and does not expose an email-send claim. Preserve the neutral introduction and contextual document list. Fieldsets start disabled until initialization, and a noscript explanation remains available; validation and disabled appearance use native browser behavior rather than invented error tokens. Status feedback is textual in a live status region.

### Disclosures

Service and House Core FAQs use native `details` / `summary`, an 18px semibold summary, a fine bottom rule and 22px row separation. Answers use 15px muted body text. House Core installation help uses a smaller summary within the navy download section. There is no custom open/close animation.

### House Core plan and downloads

The layered plan is a square paper-framed figure with a white drawing stage, labelled heading, native checkbox fieldset and visible text status. Checkboxes are 17px, navy-accented and labelled in 14px body text. Controls appear only once the interactive SVG is ready; a descriptive static plan is the fallback. Toggling layers updates existing geometry and the live text state without decorative motion.

Operating-system downloads are square white rows on navy, with a 44px icon track, flexible copy and trailing download icon. They use 24px padding / 18px gaps, reduced to 22px / 12px on the House Core mobile breakpoint. The focus outline stays yellow. There is no chip/tag component system in the implemented website.

### Focus and motion

Interactive controls use a three-pixel focus outline offset by five pixels. Teal is used on light surfaces and yellow in the dark header, navy bands and the footer. Navigation/current states, labels and status text complement color. Reduced-motion preference removes transitions and animations and uses automatic scrolling. Motion stays limited to button state, the one-pixel shared active press, subtle path/row hover feedback, service arrows and smooth anchor scrolling.

## Do's and Don'ts

### Do:

- Do preserve the shared logo, Archivo type stacks, dark header and footer on every page.
- Do keep the illustrative home image caption visible and legible against the image.
- Do use ruled rows and clear service headings to organize industrial content.
- Do keep diagram/demo captions visible and accurate about what the image represents.
- Do show recipient availability before editable request fields and keep action labels consistent with the available channel.
- Do preserve the mobile reading order, keyboard focus and reduced-motion behavior.
- Do use House Core’s plan and document evidence within its specialized layouts.

### Don't:

- Don’t retain the superseded House-only scope or a separate earlier industrial style.
- Don’t present the illustrative home image or service diagrams as photographed Lostar work, or add stock metrics or unverified ratings.
- Don’t use decorative standalone kickers above the main content headings.
- Don’t introduce a translucent or blurred header, raised service cards or ornamental motion.
- Don’t hide a request limitation below the effort it qualifies or imply an upload/send operation that the interface does not perform.
