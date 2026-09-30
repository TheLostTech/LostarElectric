# Ricognizione e rifacimento Lostar · 23 settembre 2026

Il sito precedente presentava servizi diversi dentro una sola pagina a navigazione hash. I contenuti ripetevano slogan, tempi di risposta non verificati e illustrazioni marcate come placeholder. Il modulo contatti non trasmetteva le richieste pur mostrando un selettore di allegati. House Core era presentato soprattutto come assistente, senza mostrare il risultato della progettazione.

Il rifacimento comprende ora tutte le sei pagine: Home, Quadri elettrici, Automazione, Collaudo e modifiche, Contatti e House Core. Ciascuna ha un titolo, metadati, una funzione e contenuti propri. I vecchi hash reindirizzano alle nuove pagine. La richiesta esplicita dell’utente estende il lavoro iniziale su House Core all’intero sito.

## Contenuti e percorso commerciale

- I servizi industriali descrivono realizzazione e cablaggio, informazioni necessarie e confini della fornitura. Rimossi numeri, SLA e caratteristiche nominali non confermati.
- Tre disegni tecnici originali sostituiscono i vecchi segnaposto. Sono presentati come schemi illustrativi, senza attribuirli a lavori realmente consegnati.
- House Core propone progettazione virtuale gratuita, esportazione dei livelli e delle quantità, richiesta hardware ed eventuale installazione. I download Windows/Mac e i PDF dimostrativi provengono dal prodotto reale.
- Il modulo contatti seleziona il servizio dal collegamento di provenienza e aggiorna l’elenco dei documenti utili. Gli allegati si aggiungono alla bozza nel client email; il sito non simula upload o invii.
- Navigazione, footer, spaziatura, tipografia e controlli sono condivisi. Sezioni ampie e righe separate sostituiscono le griglie ripetitive del prototipo.

## Provenienza e distribuzione

Il repository GitHub TheLostTech/LostarElectric conteneva inizialmente solo il kit del marchio. Il sito originario era distribuito da una cartella non Git. La nuova implementazione è versionata in `site/`, mantenendo il kit storico alla radice.

L’account GitHub autenticato è TheLostTech. Il nuovo commit viene riconosciuto da GitHub, ma i tentativi Vercel della sessione restituiscono BLOCKED / TEAM_ACCESS_REQUIRED: l’autore non è autorizzato a creare deployment nel progetto. Nessun bypass del controllo e nessuna modifica della produzione. Non presentare gli URL dei tentativi come anteprime funzionanti; usare `http://127.0.0.1:4178/` finché l’accesso non cambia.

Il recapito aziendale non è confermato. `contact-config.json` conserva `email: null`; la copia del testo funziona e il limite è dichiarato nell’anteprima. Nessun indirizzo del vecchio kit è stato assunto come valido.
