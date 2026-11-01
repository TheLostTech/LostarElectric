# LostTV per Mac

Versione desktop di LostTV, per provare sul Mac la stessa logica dell'app TV:
lista Xtream/M3U, **canali doppi uniti**, **scelta del link migliore** e **cambio automatico quando uno stream si blocca**.
Il codice di lettura della lista, unione dei doppioni e salute dei link è condiviso con `tv-app/` (non è duplicato).

## Requisiti

- macOS 12 o successivo
- **VLC** installato in *Applicazioni* (https://www.videolan.org/vlc/): LostTV usa il motore di VLC per riprodurre
  TS, HLS, HEVC e tutti i formati delle liste IPTV.

## Scaricare

GitHub → *Actions* → **"LostTV · Mac DMG"** → ultima esecuzione → *Artifacts*:

- `LostTV-mac-apple-silicon-N` per Mac M1/M2/M3/M4
- `LostTV-mac-intel-N` per Mac Intel

Apri il `.dmg`, trascina LostTV in *Applicazioni*. L'app non è firmata da Apple, quindi al primo avvio:

```sh
xattr -cr /Applications/LostTV.app
```

(oppure *Impostazioni di Sistema → Privacy e sicurezza → Apri comunque*).

## Uso

- **TV in diretta**: filtri in alto, categorie unite a sinistra, canali al centro, anteprima e guida a destra.
  Clic = anteprima, doppio clic = schermo intero.
- **Schermo intero**: clicca sul **nome del canale** in alto per aprire la tendina trasparente con tutti i canali
  (e cosa va in onda) e la guida del canale corrente, senza fermare la visione. ↑ ↓ cambiano canale,
  Invio apre/chiude la tendina, F schermo intero del Mac, Esc esce.
- Se lo stream va in errore, non parte entro 12 s, resta a caricare più di 8 s o l'immagine si ferma per 10 s,
  LostTV passa da solo al link successivo e lo segnala in basso a destra.
- Film e serie: griglia per categoria, scheda con trama, "Riprendi", episodio successivo automatico.

Impostazioni, preferiti e statistiche dei link sono in `~/Library/Application Support/LostTV`.

## Sviluppo

```sh
cd mac-app
./gradlew run          # avvia l'app
./gradlew packageDmg   # crea il .dmg (solo su macOS)
```

## Locandine e voti (TMDB) e ricerca nella guida TV

- **Locandine, sfondi, voti e trame in italiano** arrivano da TMDB (themoviedb.org). Serve una chiave API gratuita:
  crea un account su themoviedb.org → *Impostazioni → API* → richiedi una chiave "Developer" e copia la
  "Chiave API" (o il "Token di accesso in lettura"). Mettila in GitHub → *Settings → Secrets and variables → Actions*
  come `TMDB_API_KEY` (vale per TV e Mac a ogni build) oppure incollala nelle *Impostazioni* dell'app.
- **Cerca cosa va in onda**: la ricerca guarda anche nella guida TV completa (XMLTV del tuo abbonamento):
  "matrix" → *Matrix · Sky Cinema Uno · in onda ora*, "juve inter" → la partita su qualunque canale, anche più tardi.
  La guida si scarica in background dopo l'avvio e si aggiorna ogni 6 ore.
