---
name: lostar-design
description: Use this skill to generate well-branded interfaces and assets for Lostar (quadri e armadi elettrici industriali — industrial electrical panels, ~30 years experience, Italian B2B), either for production or throwaway prototypes/mocks/etc. Contains essential design guidelines, colors, type, fonts, assets, and UI kit components for prototyping.
user-invocable: true
---

Read the `README.md` file within this skill, and explore the other available files — it has full content + visual foundations, iconography, and a manifest.

Key files:
- `README.md` — voice, colors, type, spacing, shadows, motion, imagery, iconography
- `colors_and_type.css` — all design tokens as CSS vars, plus semantic type classes
- `fonts/Rockstar-ExtraBold.otf` — brand display face
- `assets/` — logos (primary/reverse/mark/tagline), photo placeholders, blueprint pattern
- `preview/` — small HTML preview cards per token group
- `ui_kits/marketing_site/` — hi-fi marketing site recreation (Home, Servizi, Chi siamo, Contatti — IT/EN toggle)

If creating visual artifacts (slides, mocks, throwaway prototypes, etc), copy assets out of this skill into your project and create static HTML files for the user to view. Import `colors_and_type.css` and use the CSS variables — don't re-derive colors or type scales.

If working on production code, copy the assets and read the rules here to become an expert in designing with the Lostar brand.

**Brand quick rules**
- Primary blue `#0B2545`, yellow accent `#F4B500` (≤10% of view), cool grey neutrals.
- Rockstar ExtraBold for display, Inter for UI/body, IBM Plex Mono for technical specs.
- Small radii (0/2/4/8). Subtle blue-tinted shadows. No bouncy motion. No emoji. No bluish-purple gradients. No left-border accent cards.
- Italian-first, "tu" al cliente, no exclamation marks, units with non-breaking space ("400 A").
- Lucide icons (CDN), 1.5px stroke, 24×24.

If the user invokes this skill without any other guidance, ask them what they want to build or design, ask some clarifying questions, and act as an expert designer who outputs HTML artifacts _or_ production code, depending on the need.
