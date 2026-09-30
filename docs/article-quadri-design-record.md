# Quadri elettrici — implementation record

Recorded 23 September 2026. Surface mode: **Read**. Scope: `/articoli/quadri-elettrici`, its article-index entry and related links. This is an ordinary extension of the corrected LED article flow within the existing “Catalogo tecnico Lostar” identity. The explicit user brief supplies the direction; no new visual world or concept selection is implied.

This record supplements [the article direction](article-quadri-direction.md) and [independent finish review](article-quadri-finish-review.md). [DESIGN.md](../DESIGN.md) and [its sidecar](../.impeccable/design.json) remain the global design authority and were preserved. No UI file, global design record, commit or deployment was changed by this documentation pass.

## Evidence checked

Source inspection covered [PRODUCT.md](../PRODUCT.md), the global design document and sidecar, the article direction, the complete finish review including its later addendum, [article markup](../site/articoli/quadri-elettrici.html), [article CSS](../site/articoli.css), [shared CSS](../site/styles.css), [article-index markup](../site/articoli.html), the corrected [LED article](../site/articoli/strisce-led-soffitto-torino.html) and its [earlier implementation record](article-via-silva-design-record.md). The Impeccable document reference informed this bounded record; it does not authorize refreshing the existing global files.

The source values below describe the implementation. Browser evidence is attributed to the independent reviewer, not a new inspection by this documentation pass. No context command, detector or browser session was rerun. The reviewer inspected captures inline through CUA; no screenshot files were persisted, so this record provides no screenshot-file links.

## Reused visual system and reading flow

The article inherits the Lostar logo, Rockstar ExtraBold display face, Helvetica Neue / Arial body stack, navy `--blue-800`, yellow `--yellow`, anthracite body text and slate secondary text. White, pale paper and one-pixel rules organize the content. Shared navigation, footer, breadcrumbs, buttons, skip link, anchor behavior and reduced-motion handling remain in use. The page adds no palette, font, shadow, radius or motion system.

The opening sequence remains breadcrumbs, one descriptive H1, deck, author/publication/update metadata, then a visibly captioned figure. The H1 uses the incumbent article scale (`clamp(44px, 5.7vw, 78px)`, maximum width 1040px); the deck uses 21px/1.6 within 760px. The reading layout pairs a 220px sticky index with a body capped at 760px. Paragraphs retain the 70ch maximum and 18px/1.8 body type. Rockstar H2s use `clamp(30px, 3vw, 42px)` at 1.16 line height; H3s use semibold body type at 22px/1.35. Sections are separated by 60px.

The eleven-link index follows the article's subject progression: household use, circuits, loads, protection, contactors, measurement, routes, documents, quantities, House Core and questions. It is sticky at 112px on wide screens. At 900px and below, the reading layout becomes one column and the index enters normal flow with two link columns. At 640px and below, index links stack; body type becomes 17px/1.75, H1 44px, H2 31px and section spacing 44px. Shared gutters become 20px.

The circuit table reuses the captioned, ruled article table and labelled, keyboard-focusable horizontal scrolling region. Its mobile minimum width is 510px. Links remain underlined with a stronger hover underline. The closing navy section uses the established yellow primary action and white secondary action, with yellow keyboard focus. The article-index entry reuses the ruled image/text feature and its mobile stack. These are established Read-surface roles, not new global token definitions.

## Scoped panel figure

The panel article's specific addition is an equal two-column cover combining the existing 640 × 560 panel SVG with an explanatory caption. The figure uses a pale paper ground, a 44px gap and 28px/44px padding; the image keeps its proportions and is capped at 510px. The caption uses 17px/1.65 body type, a 28px introductory label, a definition list for protection/measurement/control and a ruled 13px qualification note.

At 640px and below, the figure stacks image before caption with a 12px gap and 20px padding. The image cap becomes 350px and the introductory label 25px. The listing variant contains the same SVG within a 370px maximum height. A 34px related-link margin completes the small article stylesheet extension. These local variations are recorded here rather than promoted into the global design document or sidecar.

The caption explicitly identifies the SVG as an illustration already belonging to the Lostar site. It is neither the prototype's executive panel nor a wiring diagram. It introduces the distinction between protection, measurement and command without claiming completed work or verified electrical performance.

## Content authority and dates

The direction and review identify the anonymous prototype's source as the master specification, revision 0.6 of 8 September 2026, electrical chapter. The article preserves the preliminary seven force circuits, six lighting circuits serving twelve commanded lines, separate measurement and actuation, and services still requiring assignments. Its four two-channel meters remain anonymous. The reserve percentage is a prototype choice rather than a universal prescription. The visible source note preserves the unfinished technical status.

The user-supplied House Core product document is recorded in the direction and addendum as `Sistema operativo casa/docs/prodotto/house-core.md`. The reviewer compared that source with the revised section. The article distinguishes House Core, the local home system, from House Studio, the desktop virtual-design application. It describes declared relationships between appliance, connection point, circuit, protection and measurement; those relationships do not infer hidden wiring or certify the installation. PDF quantities and routes remain inputs to technical work. Preview downloads, supported adapters, manual sharing, configured AI costs and cloud transmission limits remain explicit. Sensitive electrical actions are not delegated to the assistant's autonomous initiative.

Public copy presents an anonymous House Core demonstration project. Subheadings and index entries omit Torino; the body uses geography only in the context of preparing a service request. The article's linked technical references and source note remain visible. This documentation pass records those references without independently rechecking electrical or legal claims.

Publication is **1 July 2026**, as explicitly requested by the user. Actual update and source-consultation dating is **23 September 2026**. Visible article metadata, BlogPosting and Open Graph agree on those calendar dates; the listing teaser displays the July publication date. The September source note explains the later product-document update. The July editorial date is not independent evidence that the page was online in July.

## Recorded finish result and limits

The independent review records **ship** for the article, listing teaser, related-link integration and supplied contract. Its later addendum separately records **ship** for the revised dates, anonymized device references and expanded House Core section. The addendum is a bounded content and affected-section check, not a renewed whole-site review.

The review records actual desktop/mobile CSS widths of 1440px and 390px, matching document widths with no page overflow. It verified the 760px desktop reading column, mobile reading type, loaded article images, anchor navigation and contained horizontal table scrolling by keyboard. The addendum inspected the revised metadata and complete House Core section at the same widths. Inline captures supplied the visual evidence; blank lower-page captures were discarded and were not used to judge the page.

The table region's native focus outline differs from the custom site control outline; the reviewer recorded this as a minor observation, with successful keyboard scrolling and no shipping blocker. Deployment, public-domain behavior, external source availability, search ranking and AI citations are outside these review verdicts. The record does not certify an electrical installation or claim coverage of every device/browser.

## Existing documentation drift reported

The global design document and sidecar still describe the earlier six-page extraction. Their component and breakpoint inventories do not include the established article reading roles or its 900px index breakpoint. This is existing coverage drift, not authority to normalize the article back to the service-page typography.

The global navigation documentation records a 26px full-width link gap and a 1120px header-CTA cutoff. The existing editorial overrides in shared CSS now use a 20px desktop link gap and hide that CTA at 1200px. The earlier LED implementation record also retains a historical addendum claiming Torino in section headings and index entries; the corrected LED markup and current product brief supersede that claim.

These discrepancies were reported only. The global design files, sidecar and prior article record were not repaired or regenerated as a side effect of this ordinary extension.
