# Lostar — Design System

> Quadri e armadi elettrici industriali. Esperienza trentennale.
> _Il tuo impianto, la nostra cura._

This design system defines the visual + content language for **Lostar**, an Italian industrial electrical panel builder ("quadri e armadi elettrici industriali") with ~30 years of experience. It's built for B2B audiences: installers, contractors, manufacturing end-clients, and design engineers — in Italian and English.

**Status:** built from scratch. No pre-existing codebase, no brand guidelines, no logo files were provided. Everything here is a fresh proposal, flagged where assumptions were made, ready to iterate on.

---

## Sources & provenance

| Item | Source | Notes |
| --- | --- | --- |
| Codebase / Figma | None provided | `LostarElettric/` folder was empty when attached. User confirmed: "create everything from scratch." |
| Company brief | User chat | 30 years in industrial electrical panels. Italian. B2B mixed audience. Italian + English. |
| Fonts | Google Fonts (substitution) | **Inter** (UI/body) and **IBM Plex Mono** (specs/codes). See caveat below. |
| Logo | Designed here | Three-bar mark evokes bus bars / distribution panel columns. Two blue + one yellow. |
| Photos | SVG placeholders in `assets/` | Schematic drawings standing in for real photos of cabinets, workshop, and site installations. **Must be replaced** with real photography. |

### Font substitution flag ⚠
### Font status
**Rockstar ExtraBold** is the brand display face (user-provided, in `fonts/`). We use it for all display / heading sizes. **Inter** and **IBM Plex Mono** are Google Fonts substitutions for body and mono — replace if Lostar licenses a text face.

---

## Index / Manifest

```
Lostar Design System/
├── README.md                     ← you are here
├── SKILL.md                      ← Agent Skill entry point
├── colors_and_type.css           ← all design tokens (CSS vars) + semantic classes
├── assets/
│   ├── logo-lostar.svg               primary horizontal logo
│   ├── logo-lostar-mark.svg          mark-only (square, app icons, favicons)
│   ├── logo-lostar-reverse.svg       for dark / photographic backgrounds
│   ├── logo-lostar-tagline.svg       with descriptor line
│   ├── pattern-blueprint.svg         repeating grid / blueprint texture
│   ├── placeholder-quadro.svg        stand-in for cabinet interior photo
│   ├── placeholder-officina.svg      stand-in for workshop photo
│   └── placeholder-cantiere.svg      stand-in for on-site installation photo
├── preview/                      ← design-system preview cards (Colors / Type / …)
└── ui_kits/
    └── marketing_site/           ← hi-fi marketing site recreation
        ├── README.md
        ├── index.html
        └── *.jsx
```

---

## Content fundamentals

### Voice & tone
Professionale ma **caldo**. Si dà del **tu** al cliente. Serietà ingegneristica (sigle, norme, numeri) bilanciata da frasi brevi e umane. Niente marketing-speak vuoto, niente superlativi.

| Do | Don't |
| --- | --- |
| "Il tuo impianto, la nostra cura." | "Soluzioni all'avanguardia leader di settore." |
| "Progettiamo, cabliamo e collaudiamo in officina." | "Unleash your industrial potential!" |
| "Quadro di distribuzione BT — fino a 4000 A, forma 4b." | "Amazing power distribution solutions." |
| "Trent'anni di quadri, fatti bene." | "Siamo orgogliosi di annunciare che…" |

### Copy rules
- **Tu** al cliente, sempre (non "Lei", non "Voi").
- **Noi** per l'azienda ("Realizziamo…", "Progettiamo…").
- **Italiano di default.** English versions should keep the same directness — address the reader as "you", never "the client".
- **Numeri concreti > aggettivi.** "1.200 quadri l'anno" batte "produzione elevata".
- **Norme citate per intero la prima volta, poi in sigla.** "conforme alla IEC 61439-1/2" → poi "IEC 61439".
- **Maiuscole:** solo per sigle tecniche (IP65, AC, MCCB, PLC) e nomi propri. Niente Title Case nelle headline — sentence case o ALL CAPS corti per eyebrow labels.
- **Nessun emoji.** Mai. Neanche nelle email. Questo è un brand industriale.
- **Nessun punto esclamativo.** Mai.
- **Unità di misura:** sempre con spazio non-interrompente. "400 A", non "400A". "IP 65" lo scriviamo "IP65" (convenzione tecnica).

### Examples — headlines
- **Hero site:** _"Quadri elettrici che non ti lasciano a terra. Da trent'anni."_
- **Servizi:** _"Dal disegno al collaudo. In officina. Con le tue norme addosso."_
- **Chi siamo:** _"Trent'anni di quadri, fatti bene."_
- **Contatti:** _"Mandaci lo schema. Ti rispondiamo in 48 ore."_

### Examples — product description
> **Quadro di distribuzione BT — serie QDB**
> Distribuzione bassa tensione fino a 4.000 A. Forma di segregazione fino a 4b, grado di protezione fino a IP55. Conforme a IEC 61439-1/2. Carpenteria in lamiera d'acciaio zincata, verniciatura a polveri RAL 7035.

Technical. Terse. No fluff. Norms front-and-center.

### Examples — CTA copy
- Primary: "Richiedi un preventivo" / "Get a quote"
- Secondary: "Scarica la scheda" / "Download spec sheet"
- Tertiary: "Parliamone →" / "Let's talk →"

---

## Visual foundations

### Color — philosophy
**Deep institutional blue + white + one yellow accent.** The blue does all the serious work (headers, type, structure). The yellow is reserved for the brand bar, CTAs, and highlights — it's electrical-signage yellow (think DIN rail terminal blocks, warning placards), not decorative. Neutrals are a proper engineering grey scale, slightly cool.

- **Primary:** `--lostar-blue-800` `#0B2545` — logo, headers, buttons, institutional
- **Accent:** `--lostar-yellow-500` `#F4B500` — brand bar, key CTAs, highlights only
- **Neutrals:** `--gray-050` → `--gray-900`, a 10-step cool grey scale
- **Status:** green `#1E9E57` (live / OK), amber `#E8A100` (warn), red `#C42826` (fault), grey `#6C7583` (off)

**Rules**
- Yellow covers **≤ 10%** of any given view. It's a highlight, not a theme.
- No blue-to-purple gradients. No sunset gradients. Gradients only inside photographic hero blocks (blue-900 → blue-800, very subtle).
- Dark backgrounds allowed but sparingly — use `--lostar-blue-900` or `--gray-900`, never pure black.

### Type
- **Sans:** Inter — 400/500/600/700/800. Used for everything UI + body.
- **Mono:** IBM Plex Mono — 500/600. Used for technical codes, IP ratings, product SKUs, phone/email metadata, fine-print metrics. _"IP65 / IK10"_ reads mono, not sans.
- **Display:** same Inter at 800 weight, tight tracking (`-0.02em`), 1.1 line-height.
- **Eyebrow labels:** 12px, 600, uppercase, letter-spacing `0.08em`, colored `--lostar-blue-800`. Pattern: `SERVIZI · 01`, `QUADRI BT`, `CERTIFICAZIONI`.
- **Body line-height:** 1.5. Reading is long-form and technical; tight body kills it.

### Spacing & layout
- **Base unit:** 4 px. Scale: 4 / 8 / 12 / 16 / 24 / 32 / 48 / 64 / 96 / 128.
- **Container:** `--container-max: 1280px`. Narrow text column: `880px`.
- **Section rhythm:** ~96 px vertical padding between sections (`--section-py`). Generous but not floaty.
- **Grid:** 12-column. Gutters 24 px.
- **Alignment:** strictly left-aligned body copy. Center only for hero + stats rows.

### Corner radii — small, engineered
- `0` — hero blocks, full-bleed bands, technical spec boxes
- `2 px` — inputs, chips
- `4 px` — buttons, cards (default)
- `8 px` — large cards, modals (max)
- `999 px` — status pills only

_No generous 16/20 px rounded-everywhere. This isn't a lifestyle app._

### Elevation / shadow
Subtle. Three levels max. Shadows are tinted with the brand blue (`rgba(11, 37, 69, …)`) rather than pure black — keeps them cohesive.

- Level 1: card hover lift
- Level 2: floating elements (dropdowns, tooltips)
- Level 3: modals only

### Borders
- Default `1 px solid --border-subtle` (`#DFE3E8`)
- Strong `1 px solid --border-strong` for data tables / spec sheets
- Brand `2 px solid --border-brand` for featured cards
- Accent `3 px solid --border-accent` — **only** as a 3 px top border on a hero block or CTA panel. Never as a left-side accent on a card (AI-slop trope).

### Backgrounds
- **Default:** white `#FFFFFF`.
- **Subtle section break:** `--gray-050` (`#F7F8FA`).
- **Brand section:** `--lostar-blue-800` (`#0B2545`) with white type.
- **Photographic hero:** real photo with a 30% dark-blue overlay for legibility. Add a 3 px top or bottom brand-bar (yellow) to separate.
- **Blueprint pattern:** `pattern-blueprint.svg` — repeating grid on dark blue. Use _only_ in hero / section-break contexts, at low opacity.

### Imagery — photography direction
- **Subject:** real cabinets, bus bars, DIN rails, wiring, workshop interiors, technicians at work.
- **Color vibe:** cool and neutral. Slight desaturation. Avoid warm orange-tinted shots.
- **Lighting:** overhead industrial neutral white (4000-5000 K), soft but directional. Visible machined surfaces.
- **Composition:** real equipment in real use. Close-ups of terminations and labeling are powerful hero shots. Avoid stock "businessman shaking hands" imagery entirely.
- **No B&W.** No heavy grain. No cinematic blue-orange color grading.
- Until real photography is shot, use the SVG placeholders in `assets/`.

### Motion
**Fast, mechanical, minimal.** This is not a springy consumer app.
- Durations: 120 ms (micro), 180 ms (default), 260 ms (larger reveals).
- Easing: `cubic-bezier(0.2, 0, 0, 1)` — a precise material-style standard curve. No bouncy overshoot.
- Fades + small translations only. No flashy parallax, no 3D tilts.

### Interactive states
- **Hover** (buttons): background darkens ~10% or shifts to next step up in the blue scale. Underline links on hover.
- **Active / press:** darker still + `transform: translateY(1px)` + `--shadow-inset-1`. Feels like a real button depressing.
- **Focus:** 3 px ring `rgba(47, 106, 179, 0.35)` (`--shadow-focus`). Always visible.
- **Disabled:** 40% opacity + `cursor: not-allowed`. No color-shifting.

### Transparency & blur
Use sparingly.
- Overlays on hero photos: solid navy at 30-45% alpha, no blur.
- Sticky header when scrolled: white at 92% with a 12 px backdrop-blur. Only when scrolled past hero.

### Cards
- White background, 1 px subtle border, 4 px radius, no shadow at rest.
- On hover: shadow level 1, border shifts to `--border-default`.
- Featured card: 2 px brand-blue border, still no fill color.

### Fixed elements
- Header: fixed to top, 72 px tall. Becomes semi-transparent + blurred after 80 px scroll.
- "Richiedi preventivo" floating CTA: optional, bottom-right on mobile only, 56 px circular, brand yellow.

---

## Iconography

### Approach
**Line icons. 1.5 px stroke. 24 × 24 grid. Rounded caps, rounded joins.** This matches the engineering/institutional register — not filled, not overly geometric. Think measured engineering drawings.

We use **[Lucide](https://lucide.dev)** via CDN as the primary icon set:

```html
<script src="https://unpkg.com/lucide@latest/dist/umd/lucide.min.js"></script>
```

Why Lucide: open-source, MIT, 1.5 px stroke, consistent 24 × 24 grid, has all the utility icons (mail, phone, download, chevrons, settings, etc.) plus enough technical ones (zap, cpu, shield, factory, wrench).

### Substitution flag ⚠
Lostar has no proprietary icon set. Lucide is the closest free match to the ABB/Siemens engineering-line aesthetic. If a proprietary icon set is later commissioned, the CSS will need one variable update (stroke width) and icons can be swapped per name.

### What we handle with custom SVGs (`assets/icons/`)
- **Domain-specific icons** Lucide doesn't cover: `cabinet`, `din-rail`, `bus-bar`, `terminal-block`, `circuit-breaker`. Drawn to match Lucide's 1.5 px stroke + 24 grid so they slot in without looking foreign.

### No emoji. Ever.
Not in UI, not in email, not in slides. Emoji are off-brand for an industrial manufacturer. The only glyphs we allow outside the icon set:
- `→` (right arrow) in CTAs and breadcrumbs.
- `·` (middle dot) as a separator in eyebrow labels.
- `—` (em dash) in copy.

### Logos
All logo variants live in `assets/`:
- `logo-lostar.svg` — default, on white / light
- `logo-lostar-reverse.svg` — on dark blue or photographic backgrounds
- `logo-lostar-mark.svg` — square mark, for favicons / social avatars / app icons
- `logo-lostar-tagline.svg` — with descriptor for formal contexts (PDFs, quotes)

Minimum logo size: 120 px wide (horizontal) / 32 px (mark). Clear space: 0.5 × the mark's width on all sides.

---

## UI kits

- `ui_kits/marketing_site/` — marketing site: home, servizi, chi siamo, contatti. See its own README for details.

---

## Caveats (read me)

1. **All photography is placeholder SVG.** Real photos of Lostar's cabinets and workshop are needed before production use.
2. **Fonts are substitutions** (Inter + IBM Plex Mono via Google Fonts). Replace if a licensed typeface exists.
3. **Logo is a proposal** — three-bar mark is a starting point. Happy to iterate on marks that lean more into the name (a stylized "L", a filled geometric mark) or a purely typographic route.
4. **Company history is thin** ("trent'anni") — certifications, exact product families, key clients, and sede/geography were not supplied. Copy uses plausible placeholders.
5. **English copy is minimal.** I've noted where it should appear but didn't translate every string yet.
