# Lostar Electric — sito statico

Il sito comprende sei pagine: `index.html`, `quadri.html`, `automazione.html`, `collaudo.html`, `contatti.html` e `house-core.html`. I vecchi hash dei servizi reindirizzano alle pagine corrispondenti. Ogni pagina ha titolo e metadati propri.

Dalla radice del repository: `python3 -m http.server 4178 --bind 127.0.0.1 --directory site`.

La selezione del servizio nei contatti usa `?servizio=quadri|automazione|collaudo|house-hardware|house-installazione`. Il modulo aggiorna i documenti suggeriti; il comune diventa obbligatorio per l’installazione House Core. I moduli preparano il testo di un’email nel client locale; non caricano file né inviano messaggi. Allegare manualmente i documenti nella bozza. La copia del testo funziona anche senza recapito configurato, con fallback selezionabile se il browser nega l’accesso agli appunti.

Prima della pubblicazione commerciale, impostare in `contact-config.json` l’email aziendale confermata. Non usare l’indirizzo di esempio del vecchio kit. Con email nulla i pulsanti email sono nascosti e il limite è visibile. Senza JavaScript la navigazione e i download restano disponibili; i moduli sono disabilitati con una spiegazione.

I pacchetti Mac e Windows del 22 settembre includono l’export PDF e la selezione dei livelli. I download precedenti sono conservati per i link condivisi. Le tavole dimostrative usano dati sintetici; generazione e verifiche in `tools/generate-house-demo.mts` e `assets/house-demo/verification.json`. I tre schemi industriali sono generati da `tools/generate-industrial-diagrams.mjs`, senza fotografie o caratteristiche di prodotti reali.

Vercel: progetto esistente `lostar-electric`, radice di deploy questa cartella; nessun framework/build. Eseguire il deploy dalla radice del repository collegato, perché Vercel ha già `site` come Root Directory. L’accesso CLI è funzionante e il sito è pubblicato su `lostartechnology.com`; `lostar-electric.vercel.app` reindirizza al dominio definitivo. Audit e verifiche sono nella cartella `docs/` del repository.

Il dominio definitivo è `lostartechnology.com`, registrato su Squarespace e collegato a Vercel. Le sei pagine hanno canonical e `og:url` sul dominio definitivo; `sitemap.xml` elenca gli stessi URL e `robots.txt` indica la sitemap. `www` reindirizza al dominio senza prefisso. Configurazione e record DNS sono documentati in `docs/domain-migration-2026-09-23.md`.
