# Quadri elettrici — independent finish review

Reviewed 23 September 2026. Review-only pass; no product code edited.

## Disposition

**ship** — at the scope of the local article, its listing teaser, related-link integration, and the supplied editorial/design contract. No material finish issue was found in the inspected desktop/mobile render or article copy. This is not a production-deployment or electrical-compliance verdict.

## Material findings

None requiring correction.

The supplied detector findings do not establish a material regression. The article CTA has real computed padding of 46px on desktop and 26px on mobile, and the listing band has deliberate vertical spacing. The tight display leading and most reported type sizes belong to the incumbent article stylesheet; the small panel-caption extension remains consistent with that visual language. The focusable table region uses a visible native focus outline rather than the site's custom control outline; keyboard scrolling works, so this is a minor finish observation, not a shipping blocker.

## Verification evidence

- Read `PRODUCT.md`, `DESIGN.md`, `docs/article-quadri-direction.md`, the Impeccable new-work/craft-floor references, the supplied detector output, the target HTML/CSS and the relevant master excerpt, lines 407–767. Compared the CSS delta with the established article styling. No second detector run.
- Independently inspected Chrome at `http://127.0.0.1:4182/articoli/quadri-elettrici` and `/articoli.html`. Confirmed actual CSS viewports through the DOM: 1440 × 1000 and 390 × 844. The browser had a 0.9 scale, so override dimensions were compensated; the override was reset afterward.
- Inspected the desktop title, introduction, illustration, reading/table area, complete page composition and closing CTA; inspected the mobile title, circuit section/table, source/CTA close, and complete article listing. The article teaser has clear image/title/body/action hierarchy at both widths.
- Article document width equaled the viewport at 1440 and 390px; the mobile listing also measured 390px. All three article images were loaded. The desktop reading column measured 760px with 18px/32.4px body type; mobile body type measured 17px/29.75px.
- The mobile table is 510px wide inside a 350px scrolling region. It is labelled and focusable; two ArrowRight presses moved its scroll position to 88.89px. The table remains contained in the reading column. Article index links successfully reached their sections.
- The source note and both closing actions are readable and separated. CTA padding was verified from computed styles. Palette contrast calculations gave 14.40:1 for body text on white, 6.36:1 for secondary text on white, 11.03:1 for CTA copy on navy and 8.39:1 for navy on yellow.
- Source inspection confirmed related article links in `site/quadri.html` and the LED article, the canonical/BlogPosting metadata, the labelled table, and the article's primary-source links. The new page and listing were reviewed live; the two related-link host pages were checked in source.

Screenshots were rendered inline by CUA and inspected; no screenshot files were persisted. Scrolled viewport captures at the bottom of the long article returned blank images and were discarded. Valid full-page and top-origin cropped captures supplied the lower-page evidence instead; the blank images were not used to judge the page.

## Remaining limits

The public domain, deployment, DNS and external source availability were outside this review. No user form was submitted, account touched or publication performed. This pass checks the article against the supplied master and confirmed brief, not the technical sufficiency of an actual installation. Search ranking, rich-result eligibility and AI citations have not been measured. Visual coverage used the two stated widths, not every device or browser.

## Design/context compliance

The work extends the established Read layout: Lostar logo, navy/yellow palette, Rockstar headings, plain body typography, ruled table, article index and closing actions. It does not introduce a replacement identity or unrequested concept exercise. Strong repetition of “quadri elettrici” follows the explicit editorial direction; headings and index entries omit Torino.

The copy preserves the important distinctions in the supplied master: seven preliminary force circuits, six light circuits serving twelve commanded lines, further services still to assign, separate measurement and actuation, and device/circuit-specific checks. It distinguishes magnetothermal protection, differential protection and contactors without publishing universal cable sections or breaker ratings. The reserve percentage is explicitly a prototype choice. The example is anonymous and openly unfinished; the illustration is labelled as schematic rather than evidence of a completed system. The House Core/export/quote path retains the limits on executive design, automatic document submission and AI service costs. No private home address appears in the reviewed article or additions.

---

# Addendum — publication date and House Core product context

23 September 2026. Bounded review after additional user steering; only the revised content, dates and affected section were rechecked. No UI code edited and no detector rerun.

## Disposition

**ship** — the requested July publication dating, anonymized device references and expanded House Core section pass this scoped addendum. The earlier whole-surface verdict is not broadened by this check.

## Material findings

None in the changed content or inspected render. Publication and update dates are distinguished clearly. The longer House Core section preserves readable hierarchy and paragraph spacing at both inspected widths.

## Verification evidence

- Compared the updated article with the supplied `Sistema operativo casa/docs/prodotto/house-core.md`, including its House Core/House Studio distinction, declared electrical dependencies, export scope, current preview packages and limits on autonomous sensitive actions.
- Verified date parity in source and live page: publication `2026-07-01` / “1° luglio 2026”; update `2026-09-23` / “23 settembre 2026”. BlogPosting and Open Graph use the same calendar dates. The live article-list teaser also displays `2026-07-01` / “1° luglio 2026”. The source note explicitly identifies the September update and source consultation.
- Independently inspected the revised date block and complete expanded House Core section at actual 1440px and 390px CSS widths on localhost. Headings, emphasis, paragraphs and metadata wrap without document overflow: document widths remained 1440px and 390px respectively. Inline captures were valid and readable. The viewport override was reset afterward.
- Confirmed that the panel article no longer names the prototype meter brand/model; its four two-channel meters/eight-channel architecture and separation from actuation remain intact. Source review of the LED changes confirms that prototype controller/firmware attribution was generalized, while the generic WLED documentation explanation remains explicitly a reference.
- Confirmed the closing article copy and download label now name House Studio. The electrical-model discussion states that relationships are declared, does not infer hidden wiring, and does not certify the real installation. PDF exports remain a technical discussion/computation input; real device connections are distinct from virtual design and depend on supported adapters. Cloud AI transmission and potential service costs remain disclosed.

## Remaining limits

This is a content/date/affected-section addendum, not a fresh broad visual audit. The requested July date is treated as user-supplied editorial dating, not independently established publication history. Deployment and external source checks remain outside scope. Screenshots were inspected inline through CUA; no screenshot files were persisted. No form, account or publication action was performed.

## Design/context compliance

The extension remains inside the existing Read system and uses the established heading hierarchy and paragraph typography. It incorporates the newly supplied product document without adding universal device compatibility, automatic wiring discovery, electrical certification, autonomous sensitive actions or unrestricted free AI claims. The anonymous prototype and generic external references remain distinguishable. The earlier source description of the named prototype meter is superseded by this anonymized version.
