<div align="center">
  <a href="https://github.com/hsushjk/AndWall">
    <img src="../app/src/main/res/mipmap-xxxhdpi/ic_launcher_round.png" alt="AndWall Icon" width="72" />
  </a>
  <h1>AndWall</h1>
  <strong>Firewall per Android con backend VPN locale opzionale.</strong><br/>
  Privacy al primo posto, tutto in locale, con un backend VPN integrato che non richiede configurazione — oppure Shizuku / daemon ADB locale / Root per le modalità avanzate.
</div>

<p align="center">
  <a href="../README.md">English</a> ·
  <a href="README.tr.md">Türkçe</a> ·
  <a href="README.de.md">Deutsch</a> ·
  <b>Italiano</b> ·
  <a href="README.pt.md">Português</a> ·
  <a href="README.cs.md">Čeština</a> ·
  <a href="README.ru.md">Русский</a> ·
  <a href="README.ar.md">العربية</a> ·
  <a href="README.hi.md">हिन्दी</a> ·
  <a href="README.zh.md">中文</a> ·
  <a href="README.ja.md">日本語</a>
</p>
<div style="height: 20px;">&nbsp;</div>
<p align="center">
  <a href="https://tooomm.github.io/github-release-stats/?username=hsushjk&repository=AndWall">
    <img alt="Downloads (GitHub)" src="https://img.shields.io/github/downloads/hsushjk/AndWall/total?color=ff9500&label=Total%20Downloads%20(GitHub)&logo=github&logoColor=white&style=flat-square" />
  </a>
  <img alt="Repo size" src="https://img.shields.io/github/repo-size/hsushjk/AndWall?style=flat-square" />
  <img alt="Minimum SDK" src="https://img.shields.io/badge/Minimum%20SDK-30%20(Android%2011)-839192?style=flat-square&logo=android&logoColor=white" />
</p>

<p align="center">
  <a href="https://github.com/hsushjk/AndWall/issues">Domande e problemi</a> ·
  <a href="https://github.com/AhmetCanArslan/ShizuWall">Progetto originale (ShizuWall)</a>
</p>

## Funziona con

Android 11+ e uno di questi backend:

- **VPN**: backend VPN locale integrato, senza configurazione e senza app companion (solo le app dell'utente corrente)
- **Shizuku** (i fork sono supportati)
- **LibADB**: si associa tramite Debug wireless, senza computer
- **Root**

Le nuove installazioni partono con il backend **VPN** e la modalità firewall **Whitelist**, quindi l'app funziona subito. Gli altri backend possono essere selezionati in seguito in **Impostazioni → Backend e modalità di funzionamento**.

## Perché AndWall

- **Nessuna configurazione privilegiata in modalità VPN**: basta concedere una sola volta il consenso VPN di sistema — niente Shizuku, niente ADB, niente root.
- **Controllo per app**: blocca la rete app per app, comprese le copie nel profilo di lavoro e i cloni nelle modalità chain-3.
- **Privato**: offline-first, niente analytics, niente telemetria, nessun server VPN remoto — il tunnel è locale e tutti i pacchetti vengono scartati.
- **Accesso rapido**: riquadro delle Impostazioni rapide, widget e pulsante flottante.
- **Automazione**: controllabile tramite broadcast da adb, Tasker, MacroDroid e simili.

## Schermata

<p align="center">
  <img src="../assets/screenshots/v4.7.1/1.png" alt="Schermata principale di AndWall" width="30%" />
  <img src="../assets/screenshots/v4.7.1/4.png" alt="Impostazioni" width="30%" />
</p>

## Come funziona

Solo i backend Shizuku, LibADB e Root richiedono un componente privilegiato. Usano la **chain 3** di connectivity di Android, un meccanismo della piattaforma che controlla l'accesso alla rete per pacchetto:

```bash
cmd connectivity set-chain3-enabled true                                # firewall attivo
cmd connectivity set-package-networking-enabled false <package.name>   # blocca
cmd connectivity set-package-networking-enabled true <package.name>    # sblocca
cmd connectivity set-chain3-enabled false                               # firewall disattivo
```

Android cancella queste regole al riavvio.

### Backend VPN

Il backend VPN è un dispositivo tun locale basato su `VpnService` che intercetta solo le app che scegli di bloccare e scarta ogni pacchetto che riceve. Richiede un'unica finestra di consenso di sistema e nessun'altra configurazione. È il backend predefinito per le nuove installazioni, abbinato alla modalità firewall **Whitelist** (scegli le app consentite, tutto il resto viene bloccato), e può essere cambiato in **Impostazioni → Backend e modalità di funzionamento**.

Il tunnel viene riavviato automaticamente dopo un riavvio e, se il processo dell'app viene arrestato mentre il firewall è attivo, la protezione viene ripristinata alla successiva apertura di AndWall. La modalità Blocco schermo è supportata (il tunnel segue lo stato di blocco) purché il dispositivo abbia un blocco schermo.

Limiti del backend VPN: gestisce solo le app dell'utente corrente (nessuna copia dei profili di lavoro o dei cloni), le regole per i soli dati mobili e le modalità basate sul rilevamento del primo piano non sono disponibili, e l'insieme delle app bloccate viene mantenuto senza catene firewall per UID. Per queste funzioni continua a usare Shizuku, LibADB o Root.

## Modifiche in questo fork

AndWall è un fork di [ShizuWall](https://github.com/AhmetCanArslan/ShizuWall). Questo fork aggiunge:

- **Backend VPN**: un backend firewall senza configurazione che funziona subito dopo la finestra di consenso di sistema.
- **Impostazioni predefinite per i nuovi utenti**: backend VPN, modalità firewall Whitelist e occultamento delle app senza accesso alla rete.
- **Onboarding aggiornato**: il flusso di benvenuto e la selezione della modalità ora menzionano tutti e quattro i backend.
- **Adattamento dell'interfaccia alla modalità VPN**: le modalità firewall e le funzioni che il backend VPN non può offrire vengono nascoste e la modalità firewall ripiega su Whitelist.
- **Rebranding, voci delle impostazioni e traduzioni aggiornate**, oltre a changelog più dettagliati e controlli degli aggiornamenti per questo fork.

## Automazione

Ogni broadcast richiede un backend attivo. `apps` è un elenco CSV di chiavi app: il solo nome del pacchetto indica l'utente 0, una chiave con prefisso come `150:com.example.app` indica la copia in quel profilo di lavoro o spazio clone.

### Firewall: `shizuwall.CONTROL`

| Extra | Tipo | |
|---|---|---|
| `state` | boolean, obbligatorio | `true` = blocca, `false` = sblocca |
| `apps` | string, facoltativo | App da bloccare o sbloccare. Senza questo extra il broadcast attiva o disattiva l'intero firewall. |

```bash
# Attiva il firewall (state false lo disattiva)
adb shell am broadcast -a shizuwall.CONTROL -n com.arslan.shizuwall/.receivers.FirewallControlReceiver --ez state true

# Blocca un'app e la sua copia nel profilo di lavoro (state false le sblocca)
adb shell am broadcast -a shizuwall.CONTROL -n com.arslan.shizuwall/.receivers.FirewallControlReceiver --ez state true --es apps "com.example.app,150:com.example.app"
```

Con `apps`, la selezione nell'app si adegua: `state true` seleziona le app e attiva il firewall se era disattivato, `state false` le deseleziona e lascia il firewall attivo. In modalità Whitelist `apps` indica l'elenco delle app consentite, quindi i due stati sono invertiti.

In modalità VPN i broadcast funzionano allo stesso modo, ma `apps` deve fare riferimento solo alle app dell'utente corrente; le chiavi dei profili di lavoro vengono ignorate.

### Nuova app: `shizuwall.APP_INSTALLED`

Segnala un'app appena installata, così la policy e la notifica per le nuove app vengono applicate come se AndWall avesse rilevato l'installazione. AndWall vede solo le installazioni nel proprio profilo: questo è il modo per coprire gli altri.

```bash
adb shell am broadcast -a shizuwall.APP_INSTALLED -n com.arslan.shizuwall/.receivers.AppInstalledReceiver --es apps "com.example.newapp"
```

Nessuna regola viene applicata mentre il firewall è disattivato. Le app senza il permesso `INTERNET` vengono ignorate.

### Profilo: `shizuwall.PROFILE`

Attiva un profilo salvato: la sua selezione di app, la modalità firewall, le modalità per app e l'impostazione *Mostra app di sistema*.

| Extra | Tipo | |
|---|---|---|
| `profile` | string | Nome del profilo, come mostrato nel pannello dei profili. |
| `profile_id` | string | ID interno; viene controllato per primo e non è influenzato dalle rinomine. La finestra **Automazione** del profilo mostra il comando già pronto. |
| `force_enable` | boolean, facoltativo | Attiva il firewall dopo il cambio anche se era disattivato. |

È richiesto uno tra `profile` e `profile_id`. Se nessuno dei due corrisponde, non cambia nulla e un toast lo segnala.

```bash
adb shell am broadcast -a shizuwall.PROFILE -n com.arslan.shizuwall/.receivers.ProfileControlReceiver --es profile "Work"
```

## Compilazione dai sorgenti

Il progetto si compila con il Gradle wrapper e l'Android SDK:

```bash
./gradlew assembleDebug
```

La build di debug usa il suffisso `.debug` e può essere installata accanto alla build di release. La build di release richiede una configurazione di firma propria.

## Avvertenza

AndWall è fornito **"così com'è"**, senza garanzie. Si basa su permessi di sistema avanzati e lo sviluppatore non è responsabile di instabilità del sistema, perdita di dati o effetti collaterali del blocco della rete. Verifica sempre quali app blocchi.

Questo fork è distribuito solo tramite [questo repository GitHub](https://github.com/hsushjk/AndWall). Gli APK provenienti da altre fonti non sono affiliati e si usano a proprio rischio. AndWall non ha un sito web ufficiale e non è pubblicato su Google Play o F-Droid.

Il permesso `INTERNET` è richiesto solo per l'associazione del debug wireless (LibADB).

## Licenza

[GPLv3](../LICENSE.md). I dati delle firme dei tracker inclusi sono concessi in licenza separatamente con ODbL v1.0, vedi [TRACKER_DATA_LICENSE.md](../TRACKER_DATA_LICENSE.md).

## Riconoscimenti

- [ShizuWall](https://github.com/AhmetCanArslan/ShizuWall): il progetto upstream su cui si basa questo fork.
- [Shizuku](https://github.com/RikkaApps/Shizuku): esecuzione di comandi privilegiati.
- [LibADB](https://github.com/MuntashirAkon/libadb-android): debug wireless e connessione al daemon.
- [Exodus Privacy](https://exodus-privacy.eu.org/): database delle firme dei tracker. AndWall ne include una copia ridotta e non contatta mai la loro API.
