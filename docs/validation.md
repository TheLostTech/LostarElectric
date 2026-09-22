# Verifiche della pagina House Core

- Browser Chrome headless, viewport 1440×1000, 390×844 e 320×740: nessun overflow orizzontale e nessun errore JS/HTTP.
- Checkbox modificano la visibilità dei gruppi SVG reali; la didascalia aggiorna i livelli visibili.
- Ancore progetta/download/documenti/invia raggiungono sezioni leggibili sotto la navigazione fissa.
- Windows, Mac e PDF d'esempio: risposta 200, header ZIP, conteggio byte verificato. Hash e controllo esclusione dati locali in download-verification.json. Pacchetti già validati nel progetto House Core, non eseguita una nuova installazione nativa Windows su questo Mac.
- Menu mobile apre/chiude, Escape richiude. Vecchio #house-core raggiunge la pagina dedicata. Cinque route industriali/contatti conservate.
- Copia richiesta verificata nel form House Core e nel form tecnico. Con Clipboard API negata viene mostrato un testo selezionabile. Nessun messaggio di prova inviato.
- Focus via Tab/Shift+Tab sui download: giallo #F4B500 su sfondo blu #0B2545. Corretto l'allineamento destro del menu mobile.
- Revisione Impeccable indipendente: due correzioni richieste, entrambe valutate resolved; disposizione finale ship, limitata ai punti riesaminati. Il recapito non configurato rimane il limite esplicito alla pubblicazione commerciale.
- Detector eseguito una volta. Segnalazioni: top border footer 4px (marca ereditata, non accento laterale); padding su wrapper (figli hanno margini/padding reali, verificati nelle catture); Inter (solo corpo ereditato, display Rockstar). Nessuna seconda esecuzione.
- PDF: 4 documenti / 23 pagine generati dal renderer reale, quantità verificate ed esame visivo di tutte le pagine. SVG e PDF condividono i simboli della planimetria 2D.
- Raster general-preview.png: origine e dataset sintetico registrati in metadata PNG e sidecar di provenienza.

## Limite per il rilascio

contact-config.json ha email null. Nell'anteprima il limite è visibile e l'azione email è nascosta; il testo si può copiare. Impostare un indirizzo confermato, verificare la bozza mailto senza inviarla e rilasciare la versione approvata. Nessuna modifica della produzione Vercel effettuata.

## Esito Vercel

Upload completato ma deployment bloccato da Vercel per autorizzazione dell’autore Git (TEAM_ACCESS_REQUIRED), anche dopo il push del commit riconosciuto da GitHub come TheLostTech. Non considerare i link dei tentativi una preview funzionante. La produzione originaria conserva ETag fe997b67bb6637f758a6810a348df4ff. Usare l’anteprima locale finché l’accesso Vercel non viene risolto.
