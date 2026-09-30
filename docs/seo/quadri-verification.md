# Quadri elettrici — implementazione e verifica SEO/GEO

Verifica del 23 settembre 2026. Proprietario editoriale: Lostar. Superficie canonica: `https://lostartechnology.com/articoli/quadri-elettrici`.

## Obiettivo e confini

Google Search, AI Overviews e AI Mode: rendere accessibile e interpretabile una guida italiana sui quadri elettrici domestici, collegata alla fornitura Lostar e alla progettazione House Studio. Torino, Piemonte e Nord Italia compaiono nel contesto commerciale; non sono dichiarazioni di copertura garantita. Le risposte tecniche sono leggibili anche da altri motori, senza attribuire loro requisiti o risultati non verificati.

La pagina `/quadri` conserva l'intento commerciale; l'articolo risponde a domande su circuiti, contemporaneità, protezioni, contattori, misura, posa, documentazione e computo. Non è una duplicazione geografica della pagina servizio. Il prototipo resta anonimo, compresi marchi e modelli del suo impianto.

## Implementato e osservato localmente

- Titolo e descrizione unici, una H1, sezioni tematiche, indice con ancore, tabella navigabile da tastiera, risposte brevi seguite da spiegazioni e fonti.
- Canonical assoluta; Open Graph; `BlogPosting` e `BreadcrumbList`, con autore, immagine, date e citazioni coerenti con il testo visibile.
- Data editoriale richiesta dall'utente: **1 luglio 2026**. Data dell'aggiornamento attuale: **23 settembre 2026**, indicata in pagina e metadati. L'impostazione editoriale non attesta che l'URL fosse online a luglio. `lastmod` della sitemap conserva la data reale di modifica.
- Link in ingresso dall'indice articoli, dalla pagina quadri e dall'articolo LED; link in uscita verso questi percorsi, prodotto e preparazione richiesta. La form non viene descritta come invio automatico.
- Sitemap portata a nove URL unici. HTML statico disponibile senza dipendere da JavaScript.
- Modello di prodotto aggiornato dal documento fornito dall'utente: House Core locale; House Studio per progettare; dipendenze elettriche dichiarate; pacchetti di anteprima; quantità esportate distinte da dimensionamento e certificazione.
- Audit strutturale aggiornato: `quadri-audit.json`, nessuna segnalazione del relativo script. Una H1, JSON-LD decodificabile, cinque intestazioni di domanda. Controllati link/risorse locali, frammenti, ID unici, assenza dell'indirizzo privato e di Torino nei sottotitoli.
- Revisione visuale indipendente: `../article-quadri-finish-review.md`. Il controllo non equivale a validazione ufficiale dei rich result o a misura del ranking.

## Evidenza e fonti

Le seguenti sono fonti primarie consultate il 23 settembre 2026. Affidabilità alta sul rispettivo campo; nessuna certifica l'impianto del prototipo. Aggiornare i controlli quando cambiano norme, componenti, progetto o affermazioni della guida.

| Fonte | Supporto e limite |
|---|---|
| [CEI 64-8](https://www.ceinorme.it/comunicati-stampa/pubblicata-la-nuova-edizione-della-norma-cei-64-8/) | Riferimento per impianti BT e pubblicazione dell'edizione; consultata la comunicazione pubblica, non il testo integrale a pagamento. |
| [Catalogo CEI EN IEC 61439-3](https://mycatalogo.ceinorme.it/cei/item/0000025487?sso=y) | Campo dei quadri di distribuzione per persone comuni; non implica applicabilità universale a ogni quadro. |
| [DM 37/2008, testo aggiornato](https://www.normattiva.it/atto/caricaDettaglioAtto?atto.codiceRedazionale=008G0060&atto.dataPubblicazioneGazzetta=2008-03-12&tipoDettaglio=multivigenza) | Portale del provvedimento; articoli 5 e 7 consultati anche nelle pagine Gazzetta Ufficiale collegate in articolo. Il contenuto evita soglie numeriche e pareri sul singolo lavoro. |
| [Schneider: domanda di potenza](https://www.electrical-installation.org/enwiki/Estimation_of_actual_maximum_kVA_demand) | Utilizzazione e contemporaneità; non fornisce i coefficienti del prototipo. |
| [Schneider: interruttori](https://www.electrical-installation.org/enwiki/Fundamental_characteristics_of_a_circuit-breaker) | Funzioni termica/magnetica e caratteristiche; non è una scelta di curva o taglia per l'impianto narrato. |
| [Schneider: differenziali](https://www.electrical-installation.org/enwiki/Description_of_RCDs) | Principio della corrente differenziale; non dimostra protezione in qualsiasi condizione. |
| [Schneider: contattori](https://www.electrical-installation.org/enwiki/Elementary_switching_devices#Contactor) | Bobina, manovra e categorie d'impiego; non sostituisce il dimensionamento. |
| [Google: funzioni AI](https://developers.google.com/search/docs/appearance/ai-features) | Fondamenti SEO ed eleggibilità nelle funzioni AI; nessuna garanzia di menzione o citazione. |
| [Google: Article](https://developers.google.com/search/docs/appearance/structured-data/article) | Proprietà e coerenza dei dati strutturati; JSON valido non garantisce un rich result. |
| [Google: keyword stuffing](https://developers.google.com/search/docs/essentials/spam-policies#keyword-stuffing) | Ripetizione artificiale non è un requisito di ranking. La ricorrenza della keyword rimane legata a spiegazioni distinte. |

Il capitolato master rev. 0.6 è una fonte interna preliminare. Il documento di prodotto House Core fornito dall'utente e il master brief chiariscono capacità, limiti e nomi; non vengono pubblicati né allegati. Nessuna immagine è presentata come foto del quadro reale: il disegno riusa lo schema illustrativo del sito.

## Misurazione riproducibile e limiti

`quadri-prompt-matrix.json` congela sette domande e i relativi hash. Per ciascun futuro controllo registrare provider/prodotto, lingua e località, data, stato della sessione, testo esatto della risposta, URL citati e corrispondenza fra citazione e affermazione. Il controllo negativo riguarda l'errata equivalenza PDF = certificazione.

Non sono stati misurati volumi di ricerca, ranking, impressioni, visite, conversioni né citazioni dei motori di risposta. Non è stato eseguito un esperimento causale. L'articolo non è dichiarato indicizzato. La proprietà Search Console e la sitemap del dominio risultavano già configurate nel lavoro precedente; l'inclusione nella sitemap non dimostra acquisizione del nuovo URL.

## Disponibilità del dominio

Durante la lavorazione il DNS locale conservava i record della pagina di parcheggio Squarespace. Autoritativi, Google e Cloudflare restituivano il record Vercel. La connessione HTTPS con risoluzione esplicita all'indirizzo Vercel restituiva il sito corretto. Si tratta di evidenza su cache e instradamento, non di un motivo per cambiare nuovamente i DNS o i nameserver. La verifica di produzione del nuovo articolo viene registrata dopo il deploy.

## Verifica di produzione

Osservata il 2026-09-23T16:17:36+02:00. Commit contenuti `d7df7bd`, deployment `dpl_4gkETEhZPB1vrP1CKk9amCTFnYWC`, stato READY e alias lostartechnology.com assegnato.

Verificati nove URL HTTPS sul dominio canonico: homepage, articolo, indice, pagina quadri, articolo LED, sitemap, robots, CSS e SVG. Tutti rispondono HTTP 200. HTML dell’articolo identico al file sorgente; canonical, date luglio/settembre e House Studio presenti. Nessun X-Robots-Tag noindex sulla risposta canonica. Sitemap include il nuovo articolo e robots indica la sitemap del dominio.

Per il dominio canonico è stata usata la risoluzione esplicita al record Vercel 216.198.79.1: la cache DNS del resolver locale continua a restituire il precedente parcheggio, mentre Cloudflare restituisce Vercel. Ciò verifica deploy e instradamento corretto, non che ogni cache utente sia già aggiornata. Il dominio di deployment unico restituisce 302 verso Vercel SSO alle richieste prive di sessione; il sito canonico è invece pubblico. L’alias lostar-electric.vercel.app redirige con 308 al dominio canonico.

Verificata anche la pagina pubblicata in Chrome nella sessione Vercel già autenticata: titolo, contenuto e date visibili corretti. L’anteprima del deployment è stata lasciata aperta per l’utente; fuori da una sessione autorizzata può richiedere il login Vercel.
