# Via Silva article — implementation record

Recorded 23 September 2026. Surface mode: **Read**. Scope: the article index and `/articoli/strisce-led-soffitto-torino`, extending the existing “Catalogo tecnico Lostar” identity. This record supplements [the surface direction](article-via-silva-direction.md); it does not replace [DESIGN.md](../DESIGN.md) or regenerate [its sidecar](../.impeccable/design.json).

## Evidence checked

The extraction was checked against [PRODUCT.md](../PRODUCT.md), the existing design document and sidecar, the surface direction, [shared CSS](../site/styles.css), [article CSS](../site/articoli.css), [index markup](../site/articoli.html) and [article markup](../site/articoli/strisce-led-soffitto-torino.html). The values below describe the source implementation. Independent screenshot, computed-style and deployment verification are outside this documentation pass; no visual verification is claimed here.

## Reused principles

The article keeps the supplied logo, Rockstar ExtraBold display face, Helvetica Neue / Arial body stack, Lostar navy `#0b2545` and yellow `#f4b500`. White and pale paper grounds, slate supporting copy and one-pixel rules provide hierarchy without raised cards or new shadows. Shared header, footer, breadcrumbs, buttons, focus treatments and reduced-motion behavior remain the foundation. Article styling introduces no new global palette or motion system.

## Reading composition

The opening sequence is breadcrumbs, one descriptive H1, deck, author/date/topic, then the native-proportion 2560 × 980 project image and visible caption. The H1 uses `clamp(44px, 5.7vw, 78px)` with a 1040px maximum width; the deck is 21px/1.6 within 760px. The reading grid pairs a 220px index with a flexible body capped at 760px. Paragraphs retain the shared 70ch maximum; body text is 18px/1.8. Rockstar H2s use `clamp(30px, 3vw, 42px)` at 1.16 line height, while H3s use semibold body type at 22px/1.35. Sections are separated by 60px.

The seven-link index is sticky at 112px on wide screens. At 900px and below it enters normal flow above the article, with two columns of links; below 640px those links form one column. Small-screen body text is 17px/1.75, the H1 is 44px, H2s are 31px and section spacing is 44px. Shared gutters become 20px at this breakpoint.

The materials table has a caption, column headers and a labelled, keyboard-focusable horizontal scroll region; below 640px its 510px minimum width keeps the columns readable. Inline links are underlined, with a thicker hover underline. The page retains the skip link and native fragment navigation. The closing navy section uses the shared yellow download button, white secondary action and yellow focus outline. The index presents a ruled image/text feature that stacks below 640px.

## Content and reuse limits

The cover caption identifies a project visualization, not completed work. The PDF preview is explicitly a separate synthetic demonstration. Preserve both distinctions, the visible source note, and open quantities/component choices when reusing this surface. The article moves from the prototype and lighting intentions to materials, House Core exports and a scoped quotation; it does not establish measured lighting performance or final electrical sizing.

Free software, separately quoted hardware/installation and potentially chargeable configured AI services remain distinct. The canonical article covers both local LED search variants; future stories need a distinct subject and intent. These reading dimensions and narrative components are surface-specific additions, not new global design tokens.
