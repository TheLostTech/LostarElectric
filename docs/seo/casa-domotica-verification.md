# Verifica articolo «Casa domotica»

23 settembre 2026. Superficie: `/articoli/casa-domotica`, nuovo articolo informativo collegato alla pagina commerciale `/house-core`. Skill SEO per struttura e verifiche; Impeccable per estensione della superficie Read e revisione indipendente.

## Contenuto e fonti

- Documentazione House Core fornita dall’utente, stato del 23 settembre: modello digitale, impianto dichiarato, assistente, regole, registri, adattatori, PDF e anteprima. Master brief per visione e sezioni impianti/esportazione.
- Funzioni disponibili e future distinte; nessun prezzo di mercato, risparmio percentuale, volume di ricerca o ranking inventato. Il collegamento reale resta distinto dalla progettazione gratuita. Casa prototipo anonima; niente indirizzi, persone o marche installate.
- Keyword nel title, H1, apertura, H2 pertinenti, risposte e collegamenti. FAQ visibili, senza promessa di risultati avanzati. Torino/Piemonte nel contesto della richiesta di preventivo, non nei sottotitoli.
- Il racconto comprende progetto 2D/3D, giornata domestica, luci, dipendenze elettriche, assistente, rete locale e cloud, ristrutturazione, costi, computo e disponibilità. Tabelle separano quantità di progetto e verifiche esecutive.
- Data reale del nuovo articolo: 23 settembre 2026, coerente tra testo, indice, metadati e JSON-LD. La data editoriale luglio del precedente articolo quadri è conservata.

## Controlli riproducibili

`python3 scripts/seo-audit.py --output docs/seo/casa-domotica-site-audit.json`

Risultato: 10 pagine, 407 riferimenti interni verificati, zero errori e zero avvisi. Include canonical, titoli e descrizioni, gerarchia dei titoli, immagini, frammenti, JSON-LD e corrispondenza sitemap/HTML.

Audit statico AEO della skill sul nuovo HTML: nessun rilievo, un H1, JSON-LD valido, sette domande FAQ. Risultato in `casa-domotica-aeo-audit.json`, con percorso privato rimosso dal campo relativo al file locale. Non è un test di indicizzazione o citazione.

Detector Impeccable eseguito una volta sui sei HTML modificati. Risultato in `casa-domotica-impeccable-detect.json`: avvisi statici su padding e interlinea da confrontare con le misure reali e il sistema ereditato, non una prova di difetto visivo. Nessuna modifica globale dei CSS.

Asset: riuso dello studio 3D dimostrativo, della tavola PDF e della pianta SVG esistente. Nessuna nuova immagine generata. La scansione della provenienza ha individuato due PNG social con sidecar e metadato Origin ma senza il campo letto dalla skill: aggiunta la stessa origine nel metadato atteso, senza modificare i pixel, e aggiornati gli hash dei sidecar. Scansione finale: quattro raster, nessuna provenienza mancante.

`git diff --check`: nessun errore di whitespace.

## Browser e perimetro della revisione

Ispezione iniziale Chrome via CUA a 1440×1000 e 390×844 CSS pixel. Apertura leggibile, immagini principali caricate, nessun overflow orizzontale della pagina. Testo desktop 18px, colonna 760px, separazione fra sezioni 60px. Tabella mobile confinata a un contenitore di 350px con larghezza interna 510px; CTA mobile con 26px di padding. Indice articoli contiene le tre guide con la nuova per prima.

Gli screenshot sono visualizzabili inline, ma l’API disponibile non espone un percorso di salvataggio: il reviewer riceve un’eccezione esplicita per una verifica live indipendente. Le catture vuote ottenute durante salti di scorrimento non sono usate come evidenza. Il verdetto finale e le prove del reviewer sono nel report `docs/article-casa-domotica-finish-review.md`.

## Distribuzione e misurazione

Rimandi aggiunti da homepage, pagina House Core, indice e articoli LED/quadri. Aggiornati ItemList, sitemap e indice facoltativo `llms.txt`. Il contenuto sostanziale è già nell’HTML, senza dipendere dall’esecuzione JavaScript.

Matrice di dieci domande in `casa-domotica-prompt-matrix.json` per una futura misurazione delle superfici di ricerca. Stato: non eseguita. La pubblicazione, il codice HTTP e il markup non dimostrano presenza in prima pagina o citazioni AI.

Riferimenti primari Google consultati: [Article](https://developers.google.com/search/docs/appearance/structured-data/article), [AI features](https://developers.google.com/search/docs/appearance/ai-features), [contenuti utili](https://developers.google.com/search/docs/fundamentals/creating-helpful-content). Il markup descrive contenuto, autore, data e immagine visibili; le risposte autonome favoriscono comprensione e consultazione senza promettere selezione da parte di un motore.

## Release pubblica

23 settembre 2026. Commit `965c698` pubblicato in produzione, deployment `dpl_HnZQsvEBuTmCBjGuC46ue85C21Cy`, alias `lostartechnology.com`. Verificati con `curl --resolve lostartechnology.com:443:216.198.79.1`: `/articoli/casa-domotica`, `/articoli`, `/house-core` e `/sitemap.xml` rispondono HTTP 200; titolo e canonical dell'articolo corretti; sitemap con l'URL nuovo. Google DNS restituisce `216.198.79.1`; il resolver della rete locale restituiva ancora il parcheggio Squarespace dal TTL precedente.

La revisione visiva indipendente non è stata completata: il report `docs/article-casa-domotica-finish-review.md` non esiste.
