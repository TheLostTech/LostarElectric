# Lostar Electric — sito statico

Pagina House Core: `house-core.html`. Navigazione storica: `index.html` con hash per i servizi industriali; `#house-core` reindirizza alla pagina dedicata.

Servire la cartella con `python3 -m http.server 4178 --bind 127.0.0.1 --directory site` dalla radice del repository.

Prima della pubblicazione in produzione impostare in `contact-config.json` l'email aziendale confermata. Non usare l'indirizzo di esempio del vecchio kit. I moduli preparano un'email nel client locale; non caricano file né inviano messaggi. Allegare manualmente lo ZIP dopo aver aperto la bozza. Copia richiesta funziona anche senza email configurata.

I pacchetti Mac e Windows del 22 settembre includono l'export PDF e la selezione dei livelli. I download precedenti sono conservati per i link già condivisi. Le tavole dimostrative usano solo dati sintetici; generazione e verifiche in `tools/generate-house-demo.mts` e `assets/house-demo/verification.json`.

Vercel: progetto esistente `lostar-electric`, radice di deploy questa cartella; nessun framework/build. Preview: `vercel deploy --cwd site`. Produzione: eseguire il comando di promozione solo dopo approvazione della pagina e del canale di contatto.
