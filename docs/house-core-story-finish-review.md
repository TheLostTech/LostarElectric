# House Core · revisione finale del racconto

Data: 30 settembre 2026. Ambito: pagina House Core, raccordo dalla home, schema concettuale, anteprima social e documentazione di prodotto/design.

## Fonte e limiti verificati

Il racconto segue `Sistema operativo casa/docs/prodotto/house-core.md`, confrontato con gli ZIP House Studio pubblicati il 23 settembre. I due download contengono l'assistente, comandi semplici, domande sullo stato virtuale, registri di manutenzione e oggetti e memoria. Nessun modello AI è preconfigurato: le conversazioni più ampie richiedono una configurazione locale o un fornitore esterno. Senza licenza gli stati e i consumi sono simulati; il controllo di una casa reale richiede dispositivi compatibili, configurazione e attivazione concordata. Storico energetico e diagnosi delle anomalie non sono promessi. Nessun dato della casa prototipo è stato usato nei nuovi asset.

## Risultato visivo e funzionale

La prima schermata presenta il sistema operativo e l'assistente; lo schema rende leggibile il flusso da ambienti, impianti e misure disponibili al modello locale. Il dialogo nella sezione assistente è marcato come illustrativo. House Studio, planimetria interattiva, PDF dimostrativi, download e richiesta rimangono disponibili più avanti. Titolo, descrizioni, dati strutturati e anteprima social raccontano lo stesso prodotto. La palette Lostar blu/giallo è preservata.

Revisione browser a 1440, 768, 390 e 320 px: nessun overflow globale; la hero si impila prima che diagramma e titolo diventino illeggibili. A 320 px il titolo resta su tre righe e tutte le voci della sott navigazione sono visibili. Lo schema mobile è scorrevole e raggiungibile da tastiera con un focus visibile; l'alt ne descrive la relazione completa. Menu mobile, ancore, controlli dei livelli della pianta e caricamento PDF sono stati verificati. Il test della API contatti passa (7 casi); HTML, JSON-LD, SVG e JavaScript sono sintatticamente validi; nessuna ancora interna manca.

Il rilevatore Impeccable ha segnalato avvisi generici di padding su sezioni a tutta larghezza, ma i contenuti hanno gutter interni e le schermate non mostrano collisioni. Ha segnalato il bordo superiore giallo del blocco domande come side-tab: è un accento orizzontale, non un bordo laterale. I testi chiari sul blu sono tinti dalla palette e mantengono contrasto leggibile. Nessuna modifica automatica è stata applicata in base a falsi positivi.
