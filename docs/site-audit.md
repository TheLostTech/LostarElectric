# Ricognizione Lostar · 23 settembre 2026

Esaminate le sei pagine visibili di lostar-electric.vercel.app (Home, Quadri, Automazione, Collaudo, House Core, Contatti), desktop e House Core mobile. Screenshot di partenza raccolti in /tmp/lostar-house-redesign.

- House Core: priorità sull'assistente vocale invece che sulla progettazione; nessuna immagine dell'output; link a pacchetti 11 settembre, senza selezione layer recente; processo a quattro voci dentro griglia a cinque colonne. Sostituzione completa autorizzata.
- Home: collega House Core solo come sistema operativo per appartamenti; manca il percorso software gratuito → hardware. Aggiornare punto d'ingresso.
- Contatti: form senza invio, senza campi name, pulsante type=button senza handler. Upload seleziona file ma non li trasmette. Non usarlo come canale di lead generation finché non è reale. Indirizzo info@lostar.it del kit non verificato, chiesto all'utente.
- Sito intero: navigazione hash, un solo titolo/metadati per pagine diverse, nessuno skip link/gestione focus route. Migliorare raccordi e accessibilità condivisa; House Core diventa pagina statica dedicata indicizzabile con compatibilità dei vecchi hash.
- Servizi industriali: palette e marca coerenti, ma immagini schematiche e numeri/SLA ereditati dal prototipo. Non inventare foto/casi/prove; mantenere fuori dalla nuova pagina i numeri non verificati. Eventuale riscrittura commerciale di questi servizi richiede materiali reali.
- GitHub TheLostTech/LostarElectric contieneva soltanto il design kit. La copia del sito era pubblicata via Vercel CLI da cartella non Git. Inserita in site/ in branch dedicato per versionarla senza riscrivere il kit.

## Collegamento Git / Vercel

Il primo tentativo di preview è stato bloccato da Vercel con TEAM_ACCESS_REQUIRED: l’ultimo commit del repository aveva autore `Ale Lost <alelost@MacBook-Pro-di-Ale.local>`, senza identità GitHub verificata. Nessuna identità Git era configurata localmente. Il nuovo lavoro va attribuito all’account GitHub autenticato TheLostTech, con email noreply del suo ID verificato via API GitHub (200366680), senza riscrivere il commit storico né cambiare impostazioni globali.

Il commit nuovo è correttamente riconosciuto da GitHub come autore TheLostTech (3c62f5a), ma Vercel continua a restituire BLOCKED / TEAM_ACCESS_REQUIRED. La preview cloud non è utilizzabile: occorre risolvere l’accesso dell’account GitHub nel team Vercel. Nessun bypass dei controlli sul team è stato tentato. Anteprima locale disponibile su http://127.0.0.1:4178/house-core.html.
