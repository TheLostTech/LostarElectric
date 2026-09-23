---
name: Lostar Electric
description: Implemented design system for the complete Lostar Electric and House Core website.
colors:
  blue-800: '#0b2545'
  blue-900: '#071a31'
  yellow: '#f4b500'
  focus: '#276466'
  white: '#fff'
  anthracite: '#202b35'
  gray-700: '#526170'
  gray-300: '#a7b5be'
  gray-200: '#d5dfe2'
  gray-050: '#f4f6f6'
  blue-050: '#eef4fa'
  dark-copy: '#d0dce5'
  dark-divider: '#3a5069'
  yellow-hover: '#dfaa10'
  download-hover: '#f0f3f5'
typography:
  display:
    fontFamily: Rockstar, "Helvetica Neue", Arial, sans-serif
    fontSize: clamp(52px, 5.8vw, 84px)
    fontWeight: 800
    lineHeight: 1.08
    letterSpacing: '0'
  display-home:
    fontFamily: Rockstar, "Helvetica Neue", Arial, sans-serif
    fontSize: clamp(56px, 6.6vw, 94px)
    fontWeight: 800
    lineHeight: 1.04
    letterSpacing: '0'
  display-contact:
    fontFamily: Rockstar, "Helvetica Neue", Arial, sans-serif
    fontSize: clamp(50px, 5.5vw, 76px)
    fontWeight: 800
    lineHeight: 1.08
    letterSpacing: '0'
  display-house-core:
    fontFamily: Rockstar, "Helvetica Neue", Arial, sans-serif
    fontSize: clamp(52px, 5.2vw, 74px)
    fontWeight: 800
    lineHeight: 1.02
    letterSpacing: '0'
  headline:
    fontFamily: Rockstar, "Helvetica Neue", Arial, sans-serif
    fontSize: clamp(36px, 4vw, 54px)
    fontWeight: 800
    lineHeight: 1.08
    letterSpacing: '0'
  headline-house-core:
    fontFamily: Rockstar, "Helvetica Neue", Arial, sans-serif
    fontSize: clamp(38px, 4vw, 58px)
    fontWeight: 800
    lineHeight: 1.05
    letterSpacing: '0'
  title:
    fontFamily: Rockstar, "Helvetica Neue", Arial, sans-serif
    fontSize: 28px
    fontWeight: 800
    lineHeight: 1.08
    letterSpacing: '0'
  service-title:
    fontFamily: Rockstar, "Helvetica Neue", Arial, sans-serif
    fontSize: 34px
    fontWeight: 800
    lineHeight: 1.08
    letterSpacing: '0'
  body:
    fontFamily: '"Helvetica Neue", Arial, sans-serif'
    fontSize: 16px
    fontWeight: 400
    lineHeight: 1.6
    letterSpacing: normal
  lead:
    fontFamily: '"Helvetica Neue", Arial, sans-serif'
    fontSize: 19px
    fontWeight: 400
    lineHeight: 1.65
    letterSpacing: normal
  label:
    fontFamily: '"Helvetica Neue", Arial, sans-serif'
    fontSize: 14px
    fontWeight: 600
    lineHeight: 1.5
    letterSpacing: normal
  button:
    fontFamily: '"Helvetica Neue", Arial, sans-serif'
    fontSize: 15px
    fontWeight: 600
    lineHeight: 1.4
    letterSpacing: normal
  field:
    fontFamily: '"Helvetica Neue", Arial, sans-serif'
    fontSize: 16px
    fontWeight: 600
    lineHeight: 1.5
    letterSpacing: normal
  caption:
    fontFamily: '"Helvetica Neue", Arial, sans-serif'
    fontSize: 12px
    fontWeight: 400
    lineHeight: 1.6
    letterSpacing: normal
  navigation:
    fontFamily: '"Helvetica Neue", Arial, sans-serif'
    fontSize: 14px
    fontWeight: 600
    lineHeight: 1.6
    letterSpacing: normal
rounded:
  surface: '0'
  field: 2px
  control: 4px
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
    backgroundColor: '{colors.yellow-hover}'
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
    textColor: '{colors.gray-700}'
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

**Creative North Star: "Catalogo tecnico Lostar"**

The complete Lostar Electric website extends the existing logo, navy/yellow palette and Rockstar display face. Large direct titles, pale drawing grounds, crisp schematic illustrations and ruled service rows give the site the character of a technical catalogue. Helvetica Neue / Arial body copy, generous section spacing and visible controls support a concrete, readable presentation.

This extraction covers all six static pages: home, quadri, automazione, collaudo, contatti and House Core. Their navigation, footer, controls, focus treatment and palette are shared. Industrial pages use consistent service and request structures; House Core retains specialized plan, document and download layouts within the same identity. Surface strategy remains in `docs/lostar-site-direction.md` and `.impeccable/surfaces/site-index-html.md`; product commitments remain in `PRODUCT.md`.

**Key Characteristics:**

- Existing Lostar logo, navy/yellow palette and Rockstar headings across all six pages.
- Solid white navigation, square drawing grounds and flat ruled service structures.
- Illustrative SVG diagrams for industrial services; interactive plan and exported PDF evidence for House Core.
- Unified request controls, visible recipient status and contextual document lists.
- Responsive reading order, native disclosures and high-contrast keyboard focus.

Extracted from the six current `site/*.html` pages, `site/styles.css`, `site/house-core.css` and the full-site review captures under `.impeccable/review/site/`. This document supersedes the earlier House-only extraction. Frontmatter owns primitive token values; `.impeccable/design.json` adds motion, breakpoints, elevation and isolated component examples. The named north star describes the implemented catalogue composition, not a new brand identity.

## Colors

The shared palette combines Lostar navy and yellow with white, pale paper and restrained technical slate.

### Primary

- **Lostar Navy** (`blue-800`): display headings, primary actions, dark sections, diagram ink and footer.
- **Lostar Yellow** (`yellow`): important actions on navy, current-page navigation markers, small diagram accents and focus on dark surfaces.
- **Deep Navy** (`blue-900`) and **Yellow Hover** (`yellow-hover`): the corresponding button hover colors.

### Secondary

- **Technical Teal** (`focus`): light-surface focus rings, checklist strokes and House Core capability icons. It remains a functional supporting accent.

### Neutral

- **White** (`white`): page ground, solid header, fields and download rows.
- **Anthracite** (`anthracite`): normal body text.
- **Technical Slate** (`gray-700`): descriptions, navigation, captions and notes.
- **Light Paper** (`gray-050`): diagram grounds, alternating sections and request containers.
- **Control Border** (`gray-300`) and **Paper Rule** (`gray-200`): field/button outlines and section/row dividers.
- **Pale Blue** (`blue-050`): outlined-button hover fill.
- **Dark Copy** (`dark-copy`) and **Dark Divider** (`dark-divider`): secondary text and rules on navy.
- **Download Hover** (`download-hover`): House Core’s white operating-system rows on hover.

House Core’s existing `hc-ink`, `hc-yellow`, `hc-muted`, `hc-paper`, `hc-line` and `hc-teal` CSS variables are local aliases of the shared palette represented here; they do not establish a separate palette. Unused stylesheet declarations are not promoted into normative tokens.

**The Identity Continuity Rule.** Keep the existing logo assets, navy/yellow palette and Rockstar display face across industrial and House Core pages.

**The Focus Contrast Rule.** Use teal focus outlines on light surfaces and yellow outlines inside navy sections and the footer.

## Typography

**Display Font:** Rockstar, then Helvetica Neue, Arial, sans-serif. The local ExtraBold face is registered at weight 800 with `font-display: swap`. Its condensed, uppercase appearance supplies the display character; headings use zero tracking and balanced wrapping.

**Body Font:** Helvetica Neue, Arial, sans-serif. Inter is no longer in the implemented font stack. Body text is explicitly 16px with 1.6 line-height; paragraphs are limited to 70ch. There is no separate mono family.

The frontmatter records the actual desktop hierarchy: shared display, larger home display, contact display and House Core display; shared and House Core headlines; display titles and service-row titles; body, lead, labels, controls and captions. Shared lead text is 19px/1.65. Supporting section introductions use 17–18px. Body and interface text are normal weight except labels, navigation and actions, which are semibold.

Specialized roles remain limited to their contexts. Home service-row headings are 34px Rockstar. Work-sequence titles are 18px/1.3 semibold body type. House Core capability headings use 22px/1.35 semibold body type and hardware-offer headings use 24px/1.3; its request title remains 27px Rockstar. House Core paragraphs use 1.65 line-height, while controls inherit the shared system. Request values inherit the labels’ semibold weight through `font: inherit`; no regular-weight field override is implemented.

Responsive heading sizes are described with their layouts below. Do not restore earlier House Core sizes that are superseded by the final overrides in its stylesheet.

## Layout

The site is a set of six static pages with common navigation and footer. Shared content uses a maximum 1280px width and 48px side gutters. Section padding follows `section-space`, ranging from 72px to 112px; House Core uses `hc-space`, capped at 120px. Generous gaps separate large content groups; fine ruled rows handle denser information.

The fixed shared header is solid white, 80px high on desktop, with a one-pixel bottom rule, a 144px logo, centered links and a right-hand request CTA. It has no blur or translucency. Main content clears the header through the shared height variable. The footer uses three columns (1.2fr / 0.8fr / 0.8fr), 64px gaps and a lower ruled row; it has no yellow top border.

Home and service heroes use a 1.1fr / 0.9fr grid, pairing direct copy with a relevant diagram. The home hero is navy with white type; service heroes are white with pale diagram grounds. Home service links are ruled rows with title, description and arrow columns. Industrial pages then use a 0.72fr / 1.28fr explanation grid, definition rows, a pale supply section, a navy request band and native FAQ rows. The home House Core entry integrates the product name in its H2 beside the plan, without a standalone kicker.

The contact page uses a 0.85fr / 1.15fr checklist/form split. Forms share a square paper container, a 22px content gap and paired field rows where room permits. Recipient status precedes editable fields. The service selector changes the supporting document checklist; incoming service links can preselect the corresponding request type. These are functional controls, not decorative filters.

Shared responsive behavior:

- At 1120px and below, content gutters become 32px, the header CTA is hidden, hero gaps become 40px, the home H1 becomes 76px and large section splits reduce to 44px gaps.
- At 860px and below, the header becomes 72px high and its 46px menu button appears at the right. Navigation becomes a vertical panel below the header. Home/service heroes retain two columns (1fr / 0.8fr) with a 30px gap; their H1 sizes become 60px / 52px and leads 17px. Section intros and service explanations stack. The work sequence becomes two columns, contact field pairs stack, and request padding becomes 24px.
- At 640px and below, shared content has 20px gutters, the logo is 128px and the main content grids stack with 34px gaps. Home H1 uses `clamp(48px, 12vw, 66px)`; service/contact H1 is 46px and shared H2 is 38px. Service rows become title/description plus a trailing arrow. The work sequence becomes one column. Contact form precedes the checklist and uses 24px vertical / 20px horizontal padding. Action-group buttons fill the available width and form actions stack. Footer brand spans both columns of the two-column link grid; its lower row stacks.

House Core retains its plan-specific structure: a 0.9fr / 1.15fr hero, a three-step navy journey, two-column capability rows and separate document, hardware, download and request sections. The subnavigation remains in normal flow below the shared header. At 1050px its gutters become 28px and major gaps narrow; at 760px gutters become 20px, its grids stack, document copy precedes the preview, field pairs stack and the journey becomes three ruled rows. The subnavigation reduces from 66px to 60px and drops its first link. Its effective H1 is the frontmatter’s House Core display role at all wider sizes, then `clamp(46px, 10vw, 62px)` at 760px and below. H2 is 44px below 1050px and 40px below 760px. Above 1600px its hero gap increases to 80px; the later H1 override still governs typography.

Shared anchor scroll padding is 104px. House Core overrides it to 140px, reduced to 90px below 760px. Native drawing proportions and visible captions are preserved across the breakpoints.

## Elevation & Depth

The full site is flat: white, paper and navy fields, one-pixel rules and drawing geometry supply structure. The sole implemented shadow is the House Core PDF-preview shadow (`0 18px 36px #0b25451a`), which suggests a document resting on paper. Shared service rows, form surfaces, diagrams, header and footer have no box shadow. There is no backdrop blur.

**The Flat Surface Rule.** Separate sections with white, paper, navy and fine rules; reserve the observed paper shadow for the House Core document preview.

## Shapes

Diagram grounds, service rows, request panels and download rows are square. Native fields use the `field` radius; shared buttons and the mobile menu use `control`. Most dividers are one pixel; the current navigation marker is three pixels and House Core’s first hardware offer uses a two-pixel top rule. Avoid turning ruled structures into floating rounded cards.

Shared button icons are 18px with a 1.5px stroke, service arrows 24px with a 1.5px stroke and text-link arrows 19px. House Core capability/download icons are 26px with a 1.5px stroke. Icons remain outline SVGs. Keep the supplied logo artwork intact.

## Components

### Buttons and text links

Shared buttons are 50px minimum height with 12px/22px padding, a 12px label/icon gap, semibold 15px/1.4 text and four-pixel corners. At the smallest shared breakpoint horizontal padding becomes 18px. The compact header CTA is 44px minimum height with 10px/16px padding and 14px text. House Core buttons explicitly retain 52px minimum height and 12px/22px padding.

Navy is the standard primary action; yellow provides the dominant action on dark sections and House Core downloads; white with a control-border outline is secondary. Hover variants are recorded in the frontmatter. The only button transition is 160ms ease on background, color and border-color. There is no pressed translation or separate custom active style. Text links keep an underline, with a thicker underline on hover.

### Shared navigation

Navigation links use semibold 14px body text, 48px minimum height and a 26px gap at full width. The current page is navy with a three-pixel yellow marker; hover changes text to navy. At the mobile-menu breakpoint the panel sits below the 72px header and uses 46px link targets, with a shorter current marker. The menu button exposes its expanded state and controls the same link set. A focus-revealed skip link precedes the header; main targets can receive focus.

### Service rows and technical figures

Home service links are full-width ruled rows with a 34px display title, muted description and arrow. Hover underlines the title and shifts the arrow four pixels in 160ms ease. Desktop rows use 34px vertical padding; mobile rows use 26px and preserve the arrow beside the text. Interior service pages use definition rows and unboxed checklists, with body-type labels instead of extra display headings.

Industrial drawings are clean illustrative SVGs, with visible captions explaining that they are schematic rather than supplied configurations or completed work. House Core’s home entry has the same visible demo caption. Caption text sits outside the drawing ground; do not crop it away when reusing a figure.

### Request forms and recipient status

Both request forms use white fields on paper, one-pixel control borders, two-pixel corners, 12px input padding, 48px minimum field height, 16px values and labels separated by 8px. Shared field pairs have an 18px gap. Contact textarea minimum height is 140px; House Core’s is 106px. Both resize vertically.

The recipient notice is a square bordered block before the first editable field. On the contact page it has a white fill; House Core’s pending notice inherits the paper surface. The notice explains the current channel before the user enters information. When a recipient is unavailable, the interface supports copying text and does not expose an email-send claim. Preserve the neutral introduction and contextual document list. Fieldsets start disabled until initialization, and a noscript explanation remains available; validation and disabled appearance use native browser behavior rather than invented error tokens. Status feedback is textual in a live status region.

### Disclosures

Service and House Core FAQs use native `details` / `summary`, an 18px semibold summary, a fine bottom rule and 22px row separation. Answers use 15px muted body text. House Core installation help uses a smaller summary within the navy download section. There is no custom open/close animation.

### House Core plan and downloads

The layered plan is a square paper-framed figure with a white drawing stage, labelled heading, native checkbox fieldset and visible text status. Checkboxes are 17px, navy-accented and labelled in 14px body text. Controls appear only once the interactive SVG is ready; a descriptive static plan is the fallback. Toggling layers updates existing geometry and the live text state without decorative motion.

Operating-system downloads are square white rows on navy, with a 44px icon track, flexible copy and trailing download icon. They use 24px padding / 18px gaps, reduced to 22px / 12px on the House Core mobile breakpoint. The focus outline stays yellow. There is no chip/tag component system in the implemented website.

### Focus and motion

Interactive controls use a three-pixel focus outline offset by five pixels. Teal is used on light surfaces and yellow within navy bands and the footer. Navigation/current states, labels and status text complement color. Reduced-motion preference removes transitions and animations and uses automatic scrolling. Beyond button colors, service-arrow hover and smooth anchor scrolling, the site has no decorative motion system.

## Do's and Don'ts

### Do:

- Do preserve the shared logo, type stacks, header and footer on every page.
- Do use ruled rows and clear service headings to organize industrial content.
- Do keep diagram/demo captions visible and accurate about what the image represents.
- Do show recipient availability before editable request fields and keep action labels consistent with the available channel.
- Do preserve the mobile reading order, keyboard focus and reduced-motion behavior.
- Do use House Core’s plan and document evidence within its specialized layouts.

### Don't:

- Don’t retain the superseded House-only scope or a separate earlier industrial style.
- Don’t add stock metrics, invented project photography or unverified ratings to technical diagrams.
- Don’t use decorative standalone kickers above the main content headings.
- Don’t introduce a translucent or blurred header, raised service cards or ornamental motion.
- Don’t hide a request limitation below the effort it qualifies or imply an upload/send operation that the interface does not perform.
