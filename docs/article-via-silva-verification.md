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

Dopo la conferma esplicita dell’utente, il 23 settembre 2026 è stato aggiunto il TXT Google alla radice del dominio su Squarespace. Search Console ha confermato «Proprietà verificata» nell’account aziendale Lostar. Il record è stato controllato anche sui DNS autoritativi; i record web, email e Domain Connect sono invariati. Il TXT deve restare presente per mantenere la verifica.

La sitemap https://lostartechnology.com/sitemap.xml è stata inviata e il dettaglio finale riporta «Sitemap elaborata correttamente», ultima lettura 23/09/26, otto pagine rilevate e zero video. Il riepilogo aveva inizialmente mostrato «Impossibile recuperare», poi superato dal risultato di elaborazione nel dettaglio.

Il test in tempo reale dell’articolo, eseguito il 23 settembre 2026 alle 15:11, riporta «L’URL è disponibile per Google», «La pagina può essere indicizzata» e un elemento Breadcrumb valido. È stata inviata la richiesta di indicizzazione: Google ha mostrato «Indicizzazione richiesta» e ha confermato l’inserimento dell’URL nella coda di scansione prioritaria. Lo stato dell’indice precedente alla richiesta era «Rilevata, ma attualmente non indicizzata»: la richiesta accettata non è una prova di indicizzazione già avvenuta e non garantisce posizioni.

Proprietà: https://search.google.com/u/2/search-console?resource_id=sc-domain%3Alostartechnology.com. Verifica, invio sitemap ed esiti sono stati letti direttamente nell’interfaccia Search Console dell’account Lostar.

Riferimenti consultati: [policy anti-spam Google, keyword stuffing e doorway abuse](https://developers.google.com/search/docs/essentials/spam-policies), [verifica proprietà Search Console](https://support.google.com/webmasters/answer/9008080?hl=it).


## Correzione privacy e keyword del 23 settembre 2026

Richiesta successiva dell’utente: rimuovere i riferimenti pubblici all’indirizzo reale e mettere al centro «strisce LED Torino». Titolo SEO, H1, H2, anteprime social, JSON-LD e richiami da home, raccolta e House Core aggiornati. H3 dedicati ai temi correlati: domotica, velette, cartongesso, profili LED, controller, alimentatori e materiali. URL canonico invariato.

- Indirizzo eliminato da testo, attributi, ancore, metadati PNG, provenienza e nomi degli asset pubblici. Il progetto è presentato in forma anonima. I pixel dell’immagine restano identici: conservati byte per byte i chunk IDAT, modificate soltanto le informazioni testuali.
- Ripulite anche le quattro distribuzioni software pubbliche, incluse le due precedenti ancora servite: commenti, nomi visibili, README e riferimenti di fallback. Questi ultimi ora usano i file generici effettivamente inclusi nel pacchetto; le variabili dei launcher non cambiano. Percorsi dei membri ZIP e permessi conservati. Il pacchetto PDF dimostrativo non richiedeva modifiche.
- Revisore indipendente: ship nel perimetro sorgenti/copy/privacy. Tutti i 47 file del sito e 2.465 membri ZIP controllati senza corrispondenze dei termini identificativi; CRC dei cinque ZIP valido, 273 link/asset interni risolti, JSON-LD coerente. Non sono state dichiarate scansioni OCR o una revisione visiva indipendente.
- Verifica parent CUA a 1440 e 390 px: nessun overflow del documento, titolo leggibile e indice utilizzabile. Ventisei membri JavaScript modificati controllati con node --check. Documentazione del design aggiornata senza cambiare CSS, DESIGN.md o sidecar.
- Pubblicazione produzione READY: dpl_m9toa5XJFXTP2u46LaYiqjczGKfR, https://lostar-electric-y6la5ie3j-alessandrolostumbo-9141s-projects.vercel.app.
- Home, raccolta, articolo, House Core e nuovi asset restituiscono HTTP 200 sul dominio definitivo, senza riferimenti all’indirizzo. I due vecchi asset con nome identificativo restituiscono 404. Scaricate da produzione tutte e quattro le distribuzioni software e confrontati gli SHA-256 con gli archivi ripuliti: corrispondono.
- HTTPS verificato con curl --resolve verso il target Vercel corrente durante la propagazione DNS locale; il nuovo deployment è stato aperto anche nel browser, confermando titolo, copertina e assenza dell’indirizzo nel DOM.

Questa verifica riguarda l’albero pubblico attuale e il nuovo deployment. La cronologia Git e gli eventuali deployment storici non sono stati cancellati. Nessuna nuova promessa di ranking o di indicizzazione: la precedente richiesta Google riguarda lo stesso URL canonico.
