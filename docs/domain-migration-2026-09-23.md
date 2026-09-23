# Dominio definitivo Lostar — 23 settembre 2026

Dominio acquistato dall'utente: `lostartechnology.com`, gestito su Squarespace.
Progetto Vercel esistente: `lostar-electric` nel team `alessandrolostumbo-9141s-projects`.

## Preparato e verificato

- Dominio apex e `www` associati al progetto Vercel, proprietà verificata.
- `www.lostartechnology.com` configurato su Vercel come redirect permanente 308 verso `lostartechnology.com`.
- Canonical e `og:url` delle sei pagine impostati sul dominio definitivo.
- `robots.txt` e sitemap con sei URL canonici.
- Anteprima pronta: https://lostar-electric-nxqq8dqws-alessandrolostumbo-9141s-projects.vercel.app
- Dopo la conferma esplicita dell'utente, i sei record del preset Squarespace sono stati sostituiti con i due record Vercel elencati sotto. I preset email e Domain Connect sono rimasti invariati.
- I DNS autoritativi restituiscono i nuovi valori; l'API Vercel segnala `misconfigured: false` per apex e www. Il record HTTPS del parcheggio è assente. Il dominio apex risponde con HTTPS valido e HTTP 200 sull'indirizzo Vercel. Alcuni resolver possono conservare il parcheggio durante il TTL precedente di quattro ore.
- Pubblicato in produzione il commit `998ff24`, deployment `dpl_5FbceHCPMsY7MZFoCMR54j5Lqqyi` (https://lostar-electric-patkhgwlf-alessandrolostumbo-9141s-projects.vercel.app).
- Certificati Vercel emessi per apex e www, rinnovo automatico abilitato.
- `www.lostartechnology.com` e `lostar-electric.vercel.app` restituiscono 308 verso il dominio definitivo, conservando percorso e query. Verificato con `/house-core?dominio=test`.
- Verificate in produzione le sei pagine con HTTPS valido, HTTP 200, canonical e `og:url` coerenti, robots e sitemap con sei URL. Verificati HTTP 200 per i download Windows, Mac e archivio PDF d'esempio.
- La verifica di contenuti e download è stata eseguita con `curl --resolve lostartechnology.com:443:216.198.79.1`, senza disabilitare la verifica TLS: la rete locale conservava ancora il DNS precedente. I resolver pubblici Google (`8.8.8.8`) e Cloudflare (`1.1.1.1`) restituivano già il nuovo record. Nessuna modifica ulteriore ai DNS è necessaria per la propagazione.

## DNS configurati

Nel pannello Squarespace è stato sostituito esclusivamente il preset **Impostazioni predefinite di Squarespace** con:

| Tipo | Host | Valore |
| --- | --- | --- |
| A | @ | 216.198.79.1 |
| CNAME | www | 8ba04a5570f5618e.vercel-dns-017.com |

Record A confermato nel pannello Vercel Domains; CNAME confermato dall'API di configurazione dominio Vercel. Non cambiare nameserver, Domain Connect o record email.

TTL dei due nuovi record: quattro ore. La conferma dell'utente per sostituire il preset è stata ricevuta ed eseguita. Nessun trasferimento di dominio o cambio dei nameserver è necessario.

## Record del preset originale

TTL comune: 4 ore. Questi sono i sei record del parcheggio Squarespace da sostituire:

| Tipo | Host | Valore |
| --- | --- | --- |
| A | @ | 198.185.159.144 |
| A | @ | 198.185.159.145 |
| A | @ | 198.49.23.144 |
| A | @ | 198.49.23.145 |
| CNAME | www | ext-sq.squarespace.com |
| HTTPS | @ | `1 . alpn="h2,http/1.1" ipv4hint="198.185.159.144,198.185.159.145,198.49.23.144,198.49.23.145"` |

Gli altri preset rilevati sono Squarespace Domain Connect e Sicurezza della posta elettronica: conservarli integralmente.
