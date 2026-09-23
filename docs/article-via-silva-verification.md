# Primo articolo Lostar — verifica e pubblicazione

23 settembre 2026. Articolo: https://lostartechnology.com/articoli/strisce-led-soffitto-torino. Raccolta: https://lostartechnology.com/articoli.

## Contenuti e struttura

- Letto il Capitolato Master Rev0.6 dell’8 settembre 2026; fatti, quantità aperte e candidati hardware controllati sul testo estratto dal documento.
- Revisore indipendente Impeccable: nessun difetto materiale nel perimetro fonte/copy, collegamenti e sorgenti. Nessuna verifica visiva indipendente dichiarata.
- Documenter indipendente: estensione coerente con il mondo visivo esistente; DESIGN.md e sidecar preservati. Dettagli in article-via-silva-design-record.md.
- Otto pagine: H1 unico, canonical, nessun ID duplicato; collegamenti interni, ancore e JSON-LD verificati. La sitemap contiene otto URL, inclusi raccolta e articolo.
- Un solo articolo canonico per le varianti LED a Torino: una serie successiva dovrà distinguere i bisogni informativi, senza pagine duplicate per variazioni minime della query.

## Browser e pubblicazione

- CUA Chrome: articolo e raccolta verificati a 1440px e 390px effettivi; nessuno scorrimento orizzontale dell’intero documento. Verificati indice, caricamento immagini e menu mobile con chiusura tramite Escape e ripristino del focus.
- Una correzione di impaginazione: ripristinato il centraggio della copertina. Screenshot osservati direttamente nel tool; nessun file screenshot o revisione visiva indipendente è dichiarato.
- Anteprima Vercel: /articoli e /articoli/strisce-led-soffitto-torino rispondono HTTP 200 con i contenuti attesi e cleanUrls.
- Pubblicazione produzione READY, deployment dpl_FMGuQbQLrMv1jMre6Wpq4cpNUSh5, https://lostar-electric-o3td60ik4-alessandrolostumbo-9141s-projects.vercel.app.
- Verificati sul dominio definitivo articolo, raccolta, sitemap, robots e immagini con HTTP 200. Canonical e dati strutturati coerenti; nessun noindex nel markup e nessun blocco globale nel robots.
- Il resolver locale e Chrome conservano ancora il parcheggio Squarespace. Google DNS e Cloudflare DNS restituiscono invece 216.198.79.1. Contenuti HTTPS del dominio controllati con curl --resolve verso Vercel, senza disabilitare TLS. La pubblicazione Vercel è stata anche aperta e verificata nel browser.

## Google

La procedura di proprietà dominio Search Console è preparata nell’account aziendale, ma non completata: manca la conferma richiesta dalla policy del controllo del browser per attribuire accesso persistente. Nessun TXT di verifica Google è stato aggiunto, nessuna sitemap inviata da Search Console e nessuna richiesta di indicizzazione completata in questa fase. La sitemap pubblica è già aggiornata e indicata nel robots. Non viene dichiarata né garantita l’indicizzazione o una posizione.

Riferimenti consultati: [policy anti-spam Google, keyword stuffing e doorway abuse](https://developers.google.com/search/docs/essentials/spam-policies), [verifica proprietà Search Console](https://support.google.com/webmasters/answer/9008080?hl=it).
