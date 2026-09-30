# Verifiche Lostar Electric · 23 settembre 2026

## Intero sito

- Sei pagine verificate in Chrome: Home, Quadri, Automazione, Collaudo, Contatti e House Core. Nessun overflow a 320, 640, 859, 861, 1024 e 1440px. Un H1 per pagina, ID univoci e navigazione corrente corretta.
- Catture complete desktop 1440×1000 e mobile 390×844, aperte e controllate in due passaggi: prima ispezione e conferma dopo le correzioni. Nessuna immagine rotta o errore JS/HTTP.
- 38 link/risorse locali e ancore verificati; risposta 200 e header ZIP dei download. Cinque vecchi hash conservati con redirect alle nuove pagine.
- Menu mobile: apertura, Escape con restituzione del focus, chiusura cliccando fuori e al cambio di breakpoint.
- Contatti: cinque servizi preselezionabili, elenchi dei documenti pertinenti, comune obbligatorio per installazione, query sconosciute ignorate, campi obbligatori ed email verificati dal browser.
- Copia richiesta funzionante, fallback con testo selezionato quando il browser nega la clipboard. Nessuna email di prova inviata. La configurazione di un destinatario è stata simulata solo nel browser per verificare la comparsa del pulsante bozza, senza apertura/invio.
- Senza JavaScript: menu leggibile, contenuti e download disponibili, moduli disabilitati con spiegazione. I dati non vengono inviati per GET.
- Detector Impeccable eseguito una sola volta sulle sei pagine: 20 avvisi `cramped-padding` sui wrapper. Il codice usa `padding-block` e contenitori interni con margini; gli spazi effettivi sono visibili nelle catture. Nessuna soppressione o seconda scansione. Risultato completo conservato nella cartella locale di revisione.

## House Core e documenti

- I checkbox modificano i gruppi SVG reali e aggiornano il testo dei livelli visibili. Il disegno statico resta disponibile senza JavaScript.
- Focus via Tab/Shift+Tab sui download: giallo #F4B500 su blu #0B2545.
- Windows e Mac: pacchetti del 22 settembre, già verificati nel progetto House Core. Hash e controllo esclusione dati locali in `docs/download-verification.json`. Non eseguita una nuova installazione Windows nativa su questo Mac.
- PDF dimostrativi: 4 documenti / 23 pagine generati dal renderer reale; quantità ed esame visivo già verificati nella fase House Core. SVG e PDF condividono i simboli della planimetria 2D. Nessuna rigenerazione dei pacchetti necessaria per il solo rifacimento del sito.
- Il raster `general-preview.png` conserva metadata e sidecar di provenienza con dataset sintetico. I nuovi disegni industriali sono SVG geometrici originali.

## Limiti di rilascio

`contact-config.json` ha email nulla. Nell’anteprima il limite è visibile, l’azione email è nascosta e il testo si può copiare. Per il canale commerciale occorre un indirizzo aziendale confermato.

Vercel ha bloccato i precedenti deployment con `TEAM_ACCESS_REQUIRED`, anche dopo il push del commit riconosciuto da GitHub come TheLostTech. Gli URL dei tentativi non sono preview funzionanti. Nessun nuovo tentativo senza cambiamenti di accesso e nessuna modifica alla produzione. Usare l’anteprima locale.

## Esito della revisione Impeccable

Revisione indipendente delle dodici catture delle sei pagine, copy e codice. Primo esito `fix`: anticipare il limite del recapito nei due moduli, integrare il nome House Core nel titolo della home e aggiornare la documentazione dal precedente perimetro House-only all’intero sito.

Correzioni applicate in un solo gruppo, dodici catture aggiornate e verificate. Il verdict pass ha valutato tutti e tre i punti `resolved`, senza regressioni introdotte dalle correzioni; disposizione finale `ship`, limitata ai tre punti riesaminati e non alla prontezza del lancio commerciale. DESIGN.md e `.impeccable/design.json` aggiornati dal documenter all’implementazione completa. Il percorso estende il marchio esistente: non è stato usato né certificato un comp o un QUALITY BAR esterno.

Dopo lo spostamento degli avvisi, verificati nuovamente l’ordine del DOM e la copia del testo in entrambi i moduli. Nessun nuovo invio o deployment.
