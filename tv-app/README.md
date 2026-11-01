# Lostar TV — app IPTV per Android TV / Google TV

App per TV Sony Bravia (Android TV / Google TV) che riproduce la **tua** lista IPTV a pagamento
con un'interfaccia moderna in stile Sky Q / Apple TV. L'app non contiene né fornisce alcun contenuto.

## Cosa fa

- **Accesso**: Xtream Codes (server, utente, password) oppure link M3U. I link `get.php?username=…&password=…`
  vengono convertiti in automatico in Xtream, così hai locandine, trame e guida TV.
- **Home**: sfondo grande che cambia col titolo selezionato, righe *Continua a guardare*, *I tuoi canali*,
  *Visti di recente*, *Film aggiunti di recente*, *Serie TV aggiornate*, *I più votati* e una riga per categoria.
- **TV in diretta**: categorie a sinistra, canali al centro, guida TV (in onda / a seguire) a destra.
  Tieni premuto OK su un canale per aggiungerlo ai preferiti.
- **Film / Serie TV**: griglia di locandine per categoria; schede di dettaglio con trama, cast, stagioni ed episodi.
- **Lettore** (ExoPlayer / Media3, HLS e MPEG-TS):
  - in diretta: ▲▼ o P+/P− cambiano canale, OK apre la lista canali, ◀ info, ▶ audio/sottotitoli/formato;
  - film/episodi: OK pausa, ◀ −10 s, ▶ +30 s, ▲ audio/sottotitoli/formato. L'episodio successivo parte da solo.
- **Ricerca**, **Preferiti**, **Riprendi da dove eri rimasto**, impostazioni con scadenza dell'abbonamento.
- Il catalogo resta in cache: l'app si apre subito e aggiorna la lista in background (ogni 6 ore o a richiesta).

## Scaricare l'APK

Ogni push che modifica `tv-app/` avvia il workflow **"Lostar TV · Android TV APK"** su GitHub Actions.
Apri l'esecuzione completata → sezione *Artifacts* → scarica `LostarTV-apk-N` (zip con due APK):

- `LostarTV-1.0.N.apk` → **quello da installare** (ottimizzato, più fluido sul TV);
- `LostarTV-1.0.N-debug.apk` → solo per diagnosi.

## Installare sul TV Sony

1. Sul TV: *Impostazioni → Sistema → Info → Build*: premi OK 7 volte per attivare le *Opzioni sviluppatore*.
2. Installa dal Play Store l'app **Downloader** (AFTVnews) oppure **Send files to TV**.
3. *Impostazioni → App → Sicurezza e limitazioni → Origini sconosciute*: abilita l'app scelta al punto 2.
4. Copia l'APK sul TV (link diretto in Downloader, oppure *Send files to TV* dal telefono) e aprilo per installarlo.

In alternativa, con il TV e il computer sulla stessa rete (debug ADB attivo nelle opzioni sviluppatore):

```sh
adb connect <IP-del-TV>
adb install -r LostarTV-1.0.N.apk
```

## Aggiornamenti senza perdere le impostazioni

Senza configurazione, ogni build è firmata con una chiave di debug diversa: per aggiornare devi prima
disinstallare l'app. Per evitarlo, crea una chiave di firma una volta sola e salvala nei *Secrets* del repository:

```sh
keytool -genkeypair -v -keystore lostar-tv.jks -alias lostartv -keyalg RSA -keysize 2048 -validity 10000
base64 -w0 lostar-tv.jks   # su macOS: base64 -i lostar-tv.jks
```

GitHub → *Settings → Secrets and variables → Actions*:
`TV_KEYSTORE_BASE64` (output di base64), `TV_KEYSTORE_PASSWORD`, `TV_KEY_ALIAS` (`lostartv`), `TV_KEY_PASSWORD`.
Non mettere mai il file `.jks` nel repository.

## Sviluppo

Progetto Gradle autonomo (Kotlin, Jetpack Compose for TV, Media3, Coil, OkHttp, kotlinx.serialization).
Apri la cartella `tv-app/` con Android Studio, oppure:

```sh
cd tv-app
./gradlew testDebugUnitTest assembleRelease
```

Struttura:

| Percorso | Contenuto |
| --- | --- |
| `data/` | parser M3U, client Xtream Codes, catalogo con cache, preferiti e cronologia (DataStore) |
| `ui/main` | contenitore con menu laterale espandibile |
| `ui/home`, `ui/live`, `ui/vod`, `ui/detail` | schermate principali |
| `ui/player` | lettore ExoPlayer con controlli da telecomando |
| `ui/components` | card, righe, menu, liste categorie |
