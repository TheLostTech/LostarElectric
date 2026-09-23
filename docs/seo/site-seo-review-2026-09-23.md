# Lostar · SEO e GEO del sito

Data: 23 settembre 2026. Superficie: le nove pagine pubbliche di `https://lostartechnology.com/`. Pubblico: progettisti, installatori, costruttori di macchine e committenti in Italia, con attenzione a Torino, Piemonte e Nord Italia. Obiettivo commerciale: richieste per quadri/cablaggio e percorso House Studio gratuito → progetto esportato → valutazione hardware/installazione.

## Riscontro iniziale osservato

Report SEOptimer del 23 settembre 2026, ore 14:24 UTC, letto nel browser: voto complessivo **B**, On-Page **A**, GEO **B−**, Links **F**, Usability **A+**, Performance **A+**; 17 raccomandazioni. Il report ha raggiunto Vercel sul nuovo dominio, non la pagina di parcheggio. Ha rilevato zero backlink e zero domini referenti nel proprio indice; questo non prova l'assenza assoluta di link sul web. I Core Web Vitals non hanno abbastanza dati reali. Tempi sintetici e grade di performance non sono misure di esperienza di tutti i visitatori.

Fonte vendor: [report SEOptimer](https://www.seoptimer.com/lostartechnology.com#recommendation). Il limite giornaliero del piano gratuito era già esaurito prima della pubblicazione di queste modifiche.

## Interventi e verifiche riproducibili

| Controllo | Prima | Dopo |
|---|---:|---:|
| Pagine con JSON-LD | 2/9 | 9/9 |
| Pagine con immagine Open Graph | 3/9 | 9/9 |
| Pagine con Twitter/X card | 3/9 | 9/9 |
| Link HTML interni che richiedono redirect `.html` | 156 | 0 |
| Indice testuale opzionale `llms.txt` | assente | presente |

Il confronto deriva dai sorgenti Git prima/dopo, non dal ricalcolo di SEOptimer. Evidenza: `site-metadata-comparison.json`. Audit statico: `site-audit-after-2026-09-23.json`; 9 pagine e 346 riferimenti interni controllati, nessun errore o salto di livello dei titoli. Comando: `python3 scripts/seo-audit.py --output docs/seo/site-audit-after-2026-09-23.json`.

Implementati:

- Identità Organization e WebSite coerente, logo raster e riferimenti stabili fra pagine, servizi e autori. Nessuna ragione sociale, sede, certificazione o recensione inventata.
- WebPage, ContactPage, Service e CollectionPage/ItemList secondo il contenuto effettivo. BreadcrumbList sulle pagine con percorso visibile. BlogPosting degli articoli conservati e collegati alla stessa organizzazione.
- Titoli e descrizioni distinti e pertinenti; canonical e URL social assoluti sul dominio definitivo; anteprime PNG con dimensioni e alternative testuali, lingua italiana, `max-image-preview:large`.
- Immagini social composte dal logo e dallo schema illustrativo esistenti, con origine incorporata nei PNG e sidecar. Nessun aumento degli asset caricati dalla pagina per il visitatore: sono riferimenti metadata.
- Link puliti nei menu, footer, CTA e contenuti; query di servizio e ancore conservate. Redirect delle vecchie sezioni hash aggiornato alle route pulite. Gli URL `.html` pubblici continuano a essere gestiti da Vercel.
- Homepage con risposte concrete su destinatari, documenti per preventivo, percorso gratuito e valutazione della località. Collegamenti contestuali ai due articoli. Proposta per Piemonte/Nord Italia formulata senza promettere una sede o copertura non confermate.
- Distinzione coerente: House Studio è il progetto virtuale gratuito; House Core è il sistema locale della casa. Preview Windows/macOS, hardware e installazione separati, funzioni AI dipendenti dal servizio configurato.
- Sitemap delle nove pagine aggiornata alla modifica effettiva; robots già aperto conservato. `llms.txt` è un indice fattuale opzionale, non un requisito Google o una garanzia di citazione.

Conservati i vincoli editoriali: nessun indirizzo reale della casa nel sito, nessun Torino nei sottotitoli degli articoli, data di pubblicazione dell'articolo quadri al 1 luglio 2026 e data di aggiornamento al 23 settembre 2026.

## Mappa intento → pagina canonica

| Intento principale | Pagina |
|---|---|
| Lostar, quadri elettrici, automazione e domotica | `/` |
| Assemblaggio/cablaggio di quadri elettrici su progetto | `/quadri` |
| Cablaggio bordo macchina e quadri di comando | `/automazione` |
| Collaudo, ampliamenti e modifiche dei quadri | `/collaudo` |
| Preparazione richiesta di preventivo | `/contatti` |
| House Core, House Studio, progetto casa domotica e layer PDF | `/house-core` |
| Indice editoriale sugli impianti | `/articoli` |
| Strisce LED Torino, soffitti e illuminazione domotica | `/articoli/strisce-led-soffitto-torino` |
| Quadri elettrici, carichi, protezioni e casa domotica | `/articoli/quadri-elettrici` |

La pagina servizio quadri risponde all'intento commerciale; l'articolo approfondisce il caso narrativo. I link reciproci rendono esplicita questa differenza. Nessuna pagina territoriale duplicata è stata creata.

## Confini della misura GEO

L'intervento migliora leggibilità, identità, struttura delle risposte e collegamenti verificabili. Non sono stati misurati nuovi posizionamenti, citazioni in AI Overview, AI Mode o risposte di altri assistenti. JSON valido e HTTP 200 provano soltanto la consegna e la struttura controllate. Non sono un test completo dei rich result.

Set iniziale di domande per una futura misura, da mantenere invariato fra rilevazioni:

1. Che cosa fa Lostar Electric?
2. Quali documenti servono per un preventivo di quadri elettrici?
3. Qual è la differenza fra House Core e House Studio?
4. Posso progettare gratis una casa domotica ed esportare i layer PDF?
5. Come preparare un progetto di strisce LED a soffitto a Torino?
6. Come organizzare carichi e protezioni di un quadro elettrico domestico?

Per ciascuna rilevazione registrare provider, prodotto, data, lingua/località, prompt esatto, risposta completa, URL citati e correttezza della citazione. Separare menzione, citazione, visita e richiesta commerciale. Search Console resta la fonte per impressioni, query, click e indicizzazione Google; la configurazione verificata esistente non è stata alterata.

## Elementi che richiedono dati o attività esterne

- **SEO locale e conversione:** servono telefono, email commerciale, ragione sociale ed eventuale indirizzo aziendale pubblico confermati. Il form attuale prepara/copia un messaggio e non lo invia al server. Non pubblicare l'indirizzo della casa prototipo per riempire i campi LocalBusiness.
- **Profili e backlink:** usare soltanto profili ufficiali reali, riferimenti di partner e lavori documentabili. Nessun account o collegamento fittizio è stato creato. Links F non può essere risolto aggiungendo tag al codice.
- **Analytics/Meta Pixel:** non aggiunti per soddisfare un checkbox del report. Non sono requisiti di indicizzazione. Un eventuale progetto di misurazione delle conversioni va configurato con account e scopi effettivi.
- **Dominio:** i resolver pubblici/autorità puntano a Vercel, ma il resolver locale conserva ancora la vecchia risposta Squarespace. I controlli pubblici via IP Vercel mantengono hostname e verifica TLS; non richiedono modifiche DNS ulteriori.

## Fonti primarie e limiti

Consultate il 23 settembre 2026:

| Fonte | Supporto e limite | Confidenza / aggiornamento |
|---|---|---|
| [Google: Organization](https://developers.google.com/search/docs/appearance/structured-data/organization) | Identità dell'organizzazione e proprietà reali; non garantisce ranking o visualizzazione | Alta; ricontrollare se cambiano proprietà o documentazione |
| [Google: site names](https://developers.google.com/search/docs/appearance/site-names) | WebSite sul sito per il nome; nome finale scelto da Google | Alta; rivedere al cambio brand/dominio |
| [Google: AI features](https://developers.google.com/search/docs/appearance/ai-features) | Le normali basi SEO valgono anche per le funzioni AI; nessun file AI speciale richiesto | Alta per Google; non estendere automaticamente ad altri provider |
| [Google: helpful content](https://developers.google.com/search/docs/fundamentals/creating-helpful-content) | Contenuti utili e affidabili, nessuna lunghezza minima universale | Alta; non confondere densità o word count con ranking |
| [llms.txt proposal](https://llmstxt.org/) | Proposta di indice testuale leggibile; non standard universale di indicizzazione | Proposta, adozione provider non misurata; rivedere prima di farne dipendere un obiettivo |

Responsabile editoriale/commerciale: Lostar, per dati aziendali, veridicità delle offerte e aggiornamenti. Controlli tecnici: ripetere l'audit quando si aggiunge una pagina o si cambia un URL; verificare nuovamente il confine pubblico dopo ogni pubblicazione.

## Pubblicazione e nuova misura

Rilascio iniziale delle ottimizzazioni: commit `2385476`, deployment Vercel `dpl_AbzBy6wLsb92VKFbDy1whk3LLDNn`, pronto e assegnato al dominio definitivo. I 24 controlli HTTP pubblici sono passati: nove pagine identiche ai sorgenti, metadati/assets/robots/sitemap/llms serviti, route inesistente 404, redirect 308 delle vecchie route e tre download disponibili. Il redirect `/contatti.html?servizio=quadri` conserva la query in `/contatti?servizio=quadri`.

SEOptimer, dopo il rilascio, ha nuovamente rifiutato Quick Audit per limite giornaliero gratuito. **B resta il voto storico osservato, non il voto del codice aggiornato.** Nessun piano a pagamento è stato attivato.

Test indipendente Seobility sulla homepage aggiornata, modalità standard senza JavaScript: **87/100**, metadati 100%, qualità pagina 90%, struttura 100%, link 62%, server 100%, fattori esterni 3%. Il report mostra la nuova descrizione House Studio, HTTP 200 e stato Follow/Index; ha quindi letto il rilascio corretto. Questo punteggio riguarda la homepage e non è direttamente confrontabile con il B di SEOptimer. Seobility trova un backlink da un dominio: la differenza rispetto a zero di SEOptimer dipende dai rispettivi indici e non misura un backlink acquisito durante il lavoro.

Il test ha individuato l'assenza di apple-touch-icon: aggiunta l'icona del marchio a 180×180 su fondo bianco in tutte le pagine, con provenienza incorporata. Non sono state alterate le righe servizio interamente cliccabili, la ripetizione dei link nei menu/footer o le query che preselezionano il servizio per inseguire gli avvisi euristici. Nessuna destinazione esterna o condivisione social artificiale aggiunta solo per il punteggio.

Fonte vendor osservata: [Seobility, homepage Lostar](https://www.seobility.net/en/seocheck/check/?url=https%3A%2F%2Flostartechnology.com%2F&mode=standard). Dettagli riproducibili in `seobility-home-2026-09-23.json`.


### Verifica finale dopo l'icona

Rilascio finale del sito: commit `bd97343`, deployment `dpl_Gk3Pj8QP2LkURsLWL9iz7FCJUtuq`, pronto sul dominio canonico. **25/25 controlli pubblici superati**, compreso il PNG Apple; tutte le nove risposte HTML coincidono con i file locali. Il browser sul deployment autenticato ha verificato il passaggio `/#quadri` → `/quadri`, homepage finale, canonical e link all'icona. Non è stato modificato il comportamento dei form.

Una seconda scansione Seobility della stessa homepage e nella stessa modalità rileva **88/100**, rispetto a 87 prima dell'icona: metadati 100%, qualità pagina 94%, struttura 100%, link 62%, server 100%, fattori esterni 3%. L'avviso sull'icona Apple è risolto. È un incremento osservato nel controllo on-page del vendor, non una misura del posizionamento su Google.

La revisione indipendente Impeccable ha concluso SHIP nel perimetro visivo esaminato. La sola modifica successiva riguarda l'icona e i suoi metadati, verificati nei nove sorgenti e nella risposta pubblica; CSS e layout restano invariati. Documentazione completata in `site-design-documentation.md`; nessuna modifica al sistema grafico globale.
