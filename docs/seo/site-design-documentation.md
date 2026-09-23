# Documentazione design · rifinitura SEO del sito

Data: 23 settembre 2026. Esito della revisione indipendente: **SHIP per il perimetro SEO**, senza difetti visivi o di interazione materiali nelle superfici controllate. Riferimenti: `site-seo-direction.md`, `site-finish-review.md` e `site-seo-review-2026-09-23.md` in questa cartella.

## Perimetro e fonti di autorità

La rifinitura riguarda le nove pagine HTML pubbliche: home, quadri, automazione, collaudo, contatti, House Core, indice articoli e i due articoli LED/quadri. Home e prodotto mantengono modalità Persuade; i contenuti editoriali mantengono modalità Read. Metadati, dati strutturati, collegamenti puliti, sitemap e indice opzionale `site/llms.txt` rendono più espliciti servizi, identità e percorso commerciale.

`DESIGN.md` resta la fonte dei token e delle regole visive; `.impeccable/design.json` conserva estensioni ed esempi dei componenti. Entrambi sono stati letti e **preservati integralmente**. Questo documento registra la rifinitura senza creare un secondo sistema di token. `PRODUCT.md` governa le affermazioni di prodotto; la direzione specifica di questo intervento è in `site-seo-direction.md`.

Il confronto dei sorgenti non rileva modifiche a `site/styles.css`, `site/house-core.css` o `site/articoli.css`. Il catalogo tecnico Lostar mantiene logo, palette navy/giallo, Rockstar, testo sans-serif, superfici piane, regole sottili, spaziatura e breakpoint esistenti. Nessuna nuova famiglia di componenti o animazione è stata introdotta.

## Riutilizzo dei componenti

| Superficie | Modifica e continuità |
| --- | --- |
| Home | Titoli e copy precisano servizi e prodotto; la banda editoriale esistente collega entrambi gli articoli. La nuova sezione di quattro domande riusa il layout FAQ dei servizi e i controlli nativi `details`/`summary`, incluse regole, focus e comportamento responsive. |
| House Core | H1, CTA, descrizione hardware, download e FAQ distinguono House Core da House Studio. Hero, pianta interattiva, selettori dei layer, anteprima PDF, righe download e form restano quelli esistenti. |
| Servizi e contatti | Metadati e collegamenti rifiniti entro header, footer, diagrammi, CTA, checklist e richiesta esistenti. Le query dei link continuano a preselezionare il servizio. |
| Articoli | Layout di lettura, indice, figure, tabelle e bande CTA esistenti. Terminologia e collegamenti aggiornati senza una nuova struttura editoriale. |
| Navigazione | URL HTML interni puliti; ancore e query conservate. `site/script.js` aggiorna la destinazione del reindirizzamento dei vecchi hash, senza modificare il menu. |

Le FAQ restano contenuto visibile e interattivo. La loro aggiunta non costituisce una promessa di rich result. Le nuove immagini social sono riferimenti nei metadati e non aggiungono immagini al contenuto caricato nella pagina.

## Terminologia e responsabilità

- **Lostar Electric / Lostar** identifica l'organizzazione editoriale e commerciale. I dati strutturati delle pagine condividono i riferimenti Organization e WebSite sul dominio `lostartechnology.com`.
- **House Core** indica il sistema locale della casa, con software e computer locale e collegamento ai dispositivi compatibili. **House Studio** indica l'applicazione gratuita per il progetto virtuale; i download Windows/macOS correnti restano anteprime ZIP.
- Layer PDF e quantità preparano il confronto tecnico. Hardware, configurazione dei dispositivi reali e installazione hanno valutazione dedicata. Il progetto virtuale non comanda dispositivi reali; i PDF non sostituiscono il completamento tecnico necessario ai lavori e il software non sostituisce le protezioni elettriche.
- Lostar resta responsabile della correttezza delle offerte, dei contenuti e dei futuri dati aziendali. Email e telefono commerciali, sede e copertura operativa non vengono dedotti dal targeting SEO. La località dell'intervento serve alla valutazione della proposta.
- Il form prepara/copia il testo e non invia richieste o file. Lo stato del destinatario precede i campi. Le funzioni AI dipendono dal servizio configurato e non sono presentate come universalmente incluse senza costi.
- Il progetto dimostrativo resta anonimo. Nei sottotitoli degli articoli non compare Torino; l'articolo quadri conserva la pubblicazione del 1 luglio 2026 e distingue l'aggiornamento del 23 settembre 2026.

## Provenienza delle immagini

| File | Origine e impiego |
| --- | --- |
| `site/assets/social/logo.png` | Raster 600 × 168 del logo di progetto `site/assets/logo-lostar.svg`, su fondo bianco. Composizione sorgente `site/assets/social/logo.svg`; usato nell'identità Organization. |
| `site/assets/social/lostar.png` | Anteprima 1200 × 630 composta da `site/assets/logo-lostar.svg` e `site/assets/industrial/panel.svg`. Sorgente `site/assets/social/lostar.svg`; schema illustrativo, non fotografia o prova di un'installazione fornita. |
| `site/assets/stories/house-core-progetto-illuminazione-3d.png` | Visualizzazione esistente 2560 × 980, riutilizzata per House Core, indice articoli e articolo LED. Il sidecar rimanda a `output/house-core-3d/view-iso.png`; studio dimostrativo, non fotografia di lavori realizzati. Nessuna modifica visiva in questa rifinitura. |

I due nuovi PNG sono esportazioni locali tramite macOS `sips`, senza immagini generate da AI. Ognuno contiene il campo testuale PNG `Origin` e ha un sidecar `.provenance.json` con origine, sorgente, data, `ai_generated: false` e SHA-256. La lettura dei chunk PNG ha confermato origine e dimensioni; entrambi gli hash corrispondono ai file. Il raster 3D preesistente conserva il proprio sidecar e il campo incorporato `impeccable:prompt` con dichiarazione `Origin`. Questa registrazione descrive la provenienza disponibile nel progetto, senza aggiungere licenze o attribuzioni non documentate.

## Evidenza e limiti della verifica

Il documenter ha confrontato design, sorgenti, diff, sidecar e metadati PNG. L'evidenza visiva appartiene al reviewer indipendente e si trova in `site-finish-review.md`; non è stata prodotta una seconda sessione browser per documentare il lavoro.

La revisione ha controllato nel preview locale home, House Core, contatti, collaudo, indice articoli e articolo LED a **1440 × 1000 e 390 × 844 CSS pixel**. Sono stati verificati avvolgimento dei titoli, assenza di overflow orizzontale sulle sei pagine, menu mobile, apertura FAQ con mouse e tastiera, stato del destinatario e selezione del servizio dalla CTA collaudo. Il reviewer non ha rilevato difetti materiali e ha giudicato le 26 segnalazioni del detector rispetto a CSS ed evidenza effettiva, senza richiedere modifiche cosmetiche.

Le catture CUA fresche sono inline: nessun nuovo PNG di revisione è stato salvato e le immagini storiche in `.impeccable/review/` non sono prova di questo intervento. Alcune catture scorse erano incomplete; il reviewer ha usato una cattura mobile completa per la FAQ home e geometria DOM/calcolata per la CTA finale LED. Le pagine quadri e automazione e la CTA dell'altro articolo non hanno una nuova cattura integrale indipendente.

L'audit statico `site-audit-after-2026-09-23.json` registra nove pagine, 346 riferimenti interni, zero errori e zero avvisi. `site-metadata-comparison.json` registra il confronto prima/dopo dei metadati. Questi controlli non misurano indicizzazione, ranking, citazioni AI, idoneità completa ai rich result o un nuovo punteggio SEOptimer.

Pubblicazione, risposta HTTP pubblica, redirect di produzione, vecchi hash e configurazione del dominio restano responsabilità della verifica tecnica del rilascio, da registrare in `site-seo-review-2026-09-23.md`. Questa documentazione non certifica tali esiti. Non sono stati verificati altri browser, larghezze intermedie, l'intero percorso con screen reader o l'installazione dei pacchetti scaricabili.

## Disallineamenti documentali segnalati

Nessuna deriva materiale dei token o del linguaggio visivo risulta dal perimetro esaminato. Esistono disallineamenti di copertura e cronologia: `DESIGN.md` e la narrativa di `.impeccable/design.json` descrivono ancora l'estrazione delle sei pagine originarie, mentre il sito comprende nove pagine; le tre superfici editoriali non sono enumerate in quell'estrazione. Le descrizioni più vecchie usano inoltre House Core come nome sintetico del percorso gratuito, mentre l'ultima distinzione esplicita in `PRODUCT.md` e il copy corrente nominano House Studio.

I brief precedenti conservano lo scope e gli stati di consegna della loro fase, inclusi limiti di deployment storici. Le note SEO aggiunte ai due brief delimitano l'intervento attuale; stato della pubblicazione e verifiche del rilascio vanno letti nel report SEO corrente. Non è stato eseguito un aggiornamento globale dei documenti né promosso un cambiamento di copy a nuova regola visiva.

## Integrazione asset · 23 settembre 2026

La successiva correzione del touch icon aggiunge `site/assets/apple-touch-icon.png` (180 × 180) e il sorgente `site/assets/apple-touch-icon.svg`: il marchio di `site/assets/favicon.svg` conserva geometria e colori, con fondo bianco. Tutte le nove pagine HTML dichiarano `rel="apple-touch-icon"` e `sizes="180x180"` verso questo PNG. Il campo incorporato `Origin` e `site/assets/apple-touch-icon.provenance.json` registrano l'esportazione locale con macOS `sips`, senza immagini generate da AI; dimensioni, origine e corrispondenza SHA-256 sono state verificate. L'immagine è stata ispezionata dal responsabile del rilascio; il documenter ha verificato sorgenti e metadati. Nessun contenuto visibile, CSS o file del sistema globale di design è cambiato. Questa verifica non aggiunge un test su dispositivo Apple né una nuova misura di punteggio; pubblicazione e riscontri esterni restano nel report SEO del rilascio.
