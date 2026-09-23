# Revisione SEO e GEO dell’articolo LED — 23 settembre 2026

## Perimetro e risultato atteso

Skill applicata: `seo` (magnus919/agent-skills, installazione locale del 23 settembre 2026). Superfici: Google Search e, per GEO, Google AI Overviews/AI Mode. Pubblico: proprietari e progettisti che preparano illuminazione e domotica per una ristrutturazione a Torino/Piemonte. Entità: Lostar Electric e il suo software House Core. Percorso commerciale: ricerca → articolo → House Core gratuito → layer → valutazione hardware ed eventuale installazione concordata.

Il risultato verificabile di questa revisione è una pagina accessibile, comprensibile e con fonti pertinenti. Posizioni, citazioni AI, traffico e conversioni sono risultati distinti, non ancora misurati. Nessuna garanzia di prima pagina.

## Decisioni implementate

- Ultima richiesta dell’utente: niente «Torino» nei sottotitoli, nel sommario sotto l’H1 o nell’indice. Rimane in H1, title, descrizione e contesto geografico nel testo. Aggiornati anche gli H2 delle anteprime in homepage, House Core e archivio articoli.
- Una sola pagina canonica: `/articoli/strisce-led-soffitto-torino`. Nessuna pagina duplicata per varianti della keyword.
- Introduzione con risposta diretta, definizione di House Core e rapporto con Lostar Electric; spiegazione delle scene e confronto analogico/indirizzabile; fonti WLED accanto alle affermazioni pertinenti.
- Elenco dei dati per il preventivo, risposta sui fattori di costo senza prezzi inventati, tabella del computo distinta dalla verifica tecnica esecutiva.
- Autore visibile collegato alla homepage; BlogPosting e BreadcrumbList conservati, con `citation` coerenti con i due link tecnici visibili. Nessuno schema FAQ, indirizzo commerciale o dato di recensione inventato.
- Nessun indirizzo privato reintrodotto. Esempio anonimo, quantità ancora aperte dichiarate, rendering presentato come studio di progetto. File scaricabili non modificati.

## Domande e risposta canonica

| Domanda / intento | Sezione | Prova e limite |
| --- | --- | --- |
| Come progettare le strisce prima dell’acquisto? | `#progetto-luce`, `#house-core` | Funzioni documentate del prodotto; non certificazione dell’impianto |
| Velette, profili o arredi? | `#soffitto` | Scelte del capitolato anonimo, non lavori ultimati |
| Scene e strisce indirizzabili | `#scene` | Intenti del progetto e documentazione WLED |
| Metri di LED, cavi e alimentazione | `#materiali` | Tabella dei dati da raccogliere; niente lunghezze massime universali |
| Quanto costa e cosa serve per il preventivo? | `#preventivo`, `#domande` | Fattori di costo e perimetro da concordare, nessun listino inventato |

## Registro delle fonti

Accesso: 23 settembre 2026. Fonti primarie; confidenza alta sul contenuto descritto, nessuna inferenza di ranking.

| Fonte | Ambito e supporto | Non dimostra | Quando ricontrollare |
| --- | --- | --- | --- |
| [Google: AI features](https://developers.google.com/search/docs/appearance/ai-features) | Google Search: fondamentali SEO, contenuti testuali, dati strutturati coerenti, indicizzazione e idoneità agli snippet | Inclusione o citazione dell’articolo; comportamento di altri provider AI | Cambiamenti della documentazione o della policy crawler |
| [Google: Article](https://developers.google.com/search/docs/appearance/structured-data/article) | Dati Article/BlogPosting, autore, date, immagine e corrispondenza con il contenuto | Rich result garantiti | Variazione autore, immagine o struttura della pagina |
| [Google: spam policies](https://developers.google.com/search/docs/essentials/spam-policies#keyword-stuffing) | Ripetizioni innaturali di keyword possono costituire keyword stuffing | Densità ideale o posizione raggiungibile | Nuovo brief editoriale o variazione della policy |
| [WLED: strisce compatibili](https://kno.wled.ge/basics/compatible-led-strips/) | Differenza analogico/indirizzabile, gruppi di LED, compatibilità hardware | Compatibilità di ogni componente del futuro impianto | Scelta dei modelli e aggiornamenti WLED |
| [WLED: cablaggio](https://kno.wled.ge/advanced/wiring/) | Alimentazione su più punti, corrente dei cavi, caduta di tensione | Dimensionamento esecutivo della casa | Distinta componenti e verifica elettrica |

Fonte interna: capitolato del progetto dimostrativo rev. 0.6, 8 settembre 2026, già letto nella preparazione dell’articolo. Non pubblicare la fonte privata. Responsabile dell’aggiornamento editoriale: Lostar Electric, con verifica tecnica quando cambiano i componenti o le capacità di House Core.

## Verifica e misurazione

**Osservato — prima/dopo:** gli audit della skill sono conservati in `led-audit-before.json` e `led-audit-after.json`, con il percorso assoluto locale omesso. In entrambi: un H1, nessun errore di parsing JSON-LD e nessuna segnalazione dello script. Le domande nelle intestazioni passano da quattro a sei: è una modifica strutturale, non un punteggio di efficacia SEO.

**Osservato — controlli locali:** tutti gli H2–H6 e l’indice senza Torino; H1 e title conservano la keyword; ancore esistenti, ID univoci, headline dello schema uguale all’H1, fonti dello schema presenti anche nel testo. Nessun indirizzo privato nell’HTML aggiornato. Lettura nel browser desktop e mobile a 390 CSS px: nessun overflow orizzontale dell’intera pagina.

**Osservato — stato Google precedente alla revisione:** dominio verificato, sitemap elaborata con otto pagine e richiesta di indicizzazione dell’articolo già accettata il 23 settembre. Il test live precedente risultava accessibile a Google; la presenza nell’indice non era ancora confermata. Non ripetere l’invio per ogni modifica al copy e non presentare una richiesta accettata come indicizzazione avvenuta.

**Non misurato:** citazioni, ranking, impression, click e conversioni dopo questa revisione. `led-prompt-matrix.json` congela sei domande in italiano con hash; l’ultima è un controllo negativo sulla certificazione elettrica. Per una successiva rilevazione registrare data, lingua/località, provider/superficie, modello se visibile, risposta integrale, URL citati e correttezza del supporto. Ripetere lo stesso insieme senza cambiare le domande a posteriori. Distinguere menzione, citazione e visita; Search Console aggrega il traffico delle funzioni AI nella ricerca Web. Nessuna automazione di monitoraggio creata.

**Limiti commerciali invariati:** la pagina House Core prepara e copia il testo della richiesta; il recapito di ricezione è ancora da configurare. I layer non vengono inviati automaticamente e la revisione non attiva un servizio di ricezione allegati.
