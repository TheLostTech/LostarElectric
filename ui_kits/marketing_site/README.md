# Marketing site — UI kit

High-fidelity click-through recreation of the Lostar marketing site.

## Screens
- **Home** — hero, cosa facciamo, servizi, numeri (30 anni), CTA preventivo
- **Servizi** — progettazione / cablaggio / collaudo / spedizione
- **Chi siamo** — heritage 30 anni, officina, certificazioni
- **Contatti** — form richiesta preventivo

## Structure
Components split small and composable:

- `Header.jsx` — sticky top nav, logo, menu, language switch IT/EN, CTA
- `Footer.jsx` — address placeholder, nav, certifications strip
- `Hero.jsx` — photo hero with overlay + yellow top bar + eyebrow + CTA
- `SectionHeader.jsx` — eyebrow + title + optional kicker, left-aligned
- `ServiceCard.jsx` — service tile with icon, title, spec line
- `StatsBar.jsx` — big numbers row (30 anni, 1.200 quadri/anno, etc.)
- `CertStrip.jsx` — certification logos placeholder
- `QuoteForm.jsx` — request-a-quote form
- `Button.jsx` / `Badge.jsx` / `Input.jsx` — primitives

## How to use
Open `index.html`. Use the top nav to switch screens (client-side state, no routing). Language toggle flips IT/EN copy via a simple object lookup.

## Placeholders to replace
- Real cabinet / workshop photos (currently SVG placeholders in `assets/`)
- Certification marks (currently text-only)
- Real address, VAT, company details
