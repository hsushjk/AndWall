<div align="center">
  <a href="https://github.com/hsushjk/AndWall">
    <img src="../app/src/main/res/mipmap-xxxhdpi/ic_launcher_round.png" alt="Icona di AndWall" width="72" />
  </a>
  <h1>AndWall</h1>
  <strong>Firewall per Android con backend VPN locale opzionale.</strong><br/>
  Privacy al primo posto e funzionamento interamente locale, con un backend VPN integrato che non richiede alcuna configurazione — mentre le modalità avanzate usano Shizuku / il daemon ADB locale / Root.
</div>

<p align="center">
  <a href="README.en.md">English</a> ·
  <a href="README.tr.md">Türkçe</a> ·
  <a href="README.de.md">Deutsch</a> ·
  <a href="README.es.md">Español</a> ·
  <b>Italiano</b> ·
  <a href="README.pt.md">Português</a> ·
  <a href="README.cs.md">Čeština</a> ·
  <a href="README.ru.md">Русский</a> ·
  <a href="README.ar.md">العربية</a> ·
  <a href="README.hi.md">हिन्दी</a> ·
  <a href="../README.md">中文</a> ·
  <a href="README.ja.md">日本語</a>
</p>
<div style="height: 20px;">&nbsp;</div>
<p align="center">
  <a href="https://tooomm.github.io/github-release-stats/?username=hsushjk&repository=AndWall">
    <img alt="Download (GitHub)" src="https://img.shields.io/github/downloads/hsushjk/AndWall/total?color=ff9500&label=Total%20Downloads%20(GitHub)&logo=github&logoColor=white&style=flat-square" />
  </a>
  <img alt="Dimensione del repository" src="https://img.shields.io/github/repo-size/hsushjk/AndWall?style=flat-square" />
  <img alt="SDK minimo" src="https://img.shields.io/badge/Minimum%20SDK-30%20(Android%2011)-839192?style=flat-square&logo=android&logoColor=white" />
</p>

<p align="center">
  <a href="https://github.com/hsushjk/AndWall/issues">Domande e feedback</a> ·
  <a href="https://github.com/AhmetCanArslan/ShizuWall">Progetto upstream (ShizuWall)</a>
</p>

## Compatibilità

Android 11 o successivo, con uno dei seguenti backend:

- **VPN**: backend VPN locale integrato, senza configurazione e senza app companion (solo le app dell'utente corrente)
- **Shizuku** (i fork sono supportati)
- **LibADB**: si abbina tramite Debug wireless, senza computer
- **Root**

Le nuove installazioni partono con il backend **VPN** e la modalità firewall **Whitelist**, quindi l'app è pronta all'uso. Gli altri backend possono essere selezionati in seguito in **Impostazioni → Backend e modalità di funzionamento**.

## Perché AndWall

- **Nessuna configurazione privilegiata in modalità VPN**: basta concedere una sola volta il consenso VPN di sistema — niente Shizuku, niente ADB, niente root.
- **Controllo per app**: blocca la rete app per app, comprese le copie nel profilo di lavoro e i cloni nelle modalità chain-3.
- **Privacy**: offline-first, nessuna analisi, nessuna telemetria, nessun server VPN remoto — il tunnel è locale e tutti i pacchetti vengono scartati.
- **Accesso rapido**: riquadro delle Impostazioni rapide, widget e pulsante flottante.
- **Automazione**: controllabile tramite i broadcast inviati da adb, Tasker, MacroDroid e simili.

## Screenshot

<p align="center">
  <img src="../assets/screenshots/v4.7.1/1.png" alt="Schermata principale di AndWall" width="30%" />
  <img src="../assets/screenshots/v4.7.1/4.png" alt="Impostazioni" width="30%" />
</p>

## Come funziona

Solo i backend Shizuku, LibADB e Root richiedono un componente companion privilegiato. Essi agiscono sulla **chain 3** della connettività di Android, un meccanismo della piattaforma che controlla l'accesso alla rete per nome di pacchetto:

```bash
cmd connectivity set-chain3-enabled true                                # attiva il firewall
cmd connectivity set-package-networking-enabled false <package.name>   # blocca
cmd connectivity set-package-networking-enabled true <package.name>    # sblocca
cmd connectivity set-chain3-enabled false                               # disattiva il firewall
```

Android cancella queste regole al riavvio.

### Backend VPN

Il backend VPN è un dispositivo tun locale basato su `VpnService` che intercetta solo le app che scegli di bloccare e scarta tutti i pacchetti che riceve. Richiede un'unica finestra di consenso di sistema e nessun'altra configurazione. Le nuove installazioni lo usano come backend predefinito, abbinato alla modalità firewall **Whitelist** (si scelgono le app a cui consentire la rete e tutto il resto viene bloccato); si può cambiare in **Impostazioni → Backend e modalità di funzionamento**.

Dopo un riavvio il tunnel si riavvia automaticamente; se il processo dell'app viene terminato mentre il firewall è attivo, la protezione viene ripristinata alla successiva apertura di AndWall. La modalità Blocco schermo è supportata (il tunnel segue lo stato di blocco dello schermo) purché il dispositivo abbia un blocco schermo impostato.

Limiti del backend VPN: gestisce solo le app dell'utente corrente (nessuna copia dei profili di lavoro o dei cloni), non supporta le regole per i soli dati mobili né le modalità basate sul rilevamento del primo piano, e la manutenzione dell'insieme delle app bloccate non si basa sulle catene firewall per UID. Per queste funzioni continua a usare Shizuku, LibADB o Root.

## Modifiche in questo fork

AndWall è un fork di [ShizuWall](https://github.com/AhmetCanArslan/ShizuWall). Questo fork aggiunge:

- **Backend VPN**: un backend firewall senza configurazione, disponibile subito dopo aver confermato la finestra di consenso di sistema.
- **Impostazioni predefinite per i nuovi utenti**: backend VPN, modalità firewall Whitelist e occultamento delle app senza permesso di rete.
- **Onboarding aggiornato**: il flusso di benvenuto e la selezione della modalità ora citano tutti e quattro i backend.
- **Adattamento dell'interfaccia per la modalità VPN**: nasconde le modalità firewall e le funzioni che il backend VPN non può offrire e riporta la modalità firewall a Whitelist.
- **Rebranding, voci delle impostazioni e traduzioni aggiornate**, oltre a changelog più dettagliati e a controlli degli aggiornamenti per questo fork.

## Automazione

Ogni broadcast richiede un backend attivo. `apps` è un elenco di chiavi app separate da virgole: il solo nome del pacchetto indica l'utente 0, mentre una chiave con prefisso (ad esempio `150:com.example.app`) indica la copia in quel profilo di lavoro o spazio clone.

### Firewall: `shizuwall.CONTROL`

| Parametro | Tipo | |
|---|---|---|
| `state` | boolean, obbligatorio | `true` = blocca, `false` = sblocca |
| `apps` | string, facoltativo | App da bloccare o sbloccare. Se omesso, il broadcast attiva o disattiva l'intero firewall. |

```bash
# Attiva il firewall (state false lo disattiva)
adb shell am broadcast -a shizuwall.CONTROL -n com.arslan.shizuwall/.receivers.FirewallControlReceiver --ez state true

# Blocca un'app e la sua copia nel profilo di lavoro (state false le sblocca)
adb shell am broadcast -a shizuwall.CONTROL -n com.arslan.shizuwall/.receivers.FirewallControlReceiver --ez state true --es apps "com.example.app,150:com.example.app"
```

Con `apps` la selezione nell'app si allinea: `state true` seleziona le app e attiva il firewall se prima era disattivato, mentre `state false` le deseleziona e lascia il firewall attivo. In modalità Whitelist `apps` indica l'elenco delle app consentite, quindi i due stati hanno significato opposto.

In modalità VPN i broadcast funzionano allo stesso modo, ma `apps` deve fare riferimento solo alle app dell'utente corrente; le chiavi dei profili di lavoro vengono ignorate.

### Nuova app: `shizuwall.APP_INSTALLED`

Segnala un'app appena installata, così la policy per le nuove app e la notifica si applicano come se AndWall avesse rilevato l'installazione. AndWall vede solo le installazioni nel proprio profilo utente, quindi gli altri profili vanno coperti in questo modo.

```bash
adb shell am broadcast -a shizuwall.APP_INSTALLED -n com.arslan.shizuwall/.receivers.AppInstalledReceiver --es apps "com.example.newapp"
```

Nessuna regola viene applicata quando il firewall è disattivato. Le app senza il permesso `INTERNET` vengono ignorate.

### Profilo: `shizuwall.PROFILE`

Attiva un profilo salvato: applica la selezione di app del profilo, la modalità firewall, le modalità per app e l'impostazione *Mostra app di sistema*.

| Parametro | Tipo | |
|---|---|---|
| `profile` | string | Nome del profilo, come mostrato nel pannello dei profili. |
| `profile_id` | string | ID interno; viene controllato per primo e non è influenzato dalle rinomine. La finestra **Automazione** del profilo mostra il comando già pronto. |
| `force_enable` | boolean, facoltativo | Attiva il firewall dopo il cambio anche se prima era disattivato. |

È necessario fornire almeno uno tra `profile` e `profile_id`. Se nessuno dei due corrisponde, non viene applicata alcuna modifica e un toast lo segnala.

```bash
adb shell am broadcast -a shizuwall.PROFILE -n com.arslan.shizuwall/.receivers.ProfileControlReceiver --es profile "Work"
```

## Compilazione dai sorgenti

Il progetto si compila con il Gradle Wrapper e l'Android SDK:

```bash
./gradlew assembleDebug
```

La build di debug usa il suffisso `.debug` e può essere installata accanto alla build di release. La build di release richiede una propria configurazione di firma.

## Avvertenza

AndWall è fornito **"così com'è"**, senza alcuna garanzia. Si basa su permessi di sistema avanzati e lo sviluppatore non è responsabile di instabilità del sistema, perdita di dati o effetti collaterali causati dal blocco della rete. Verifica sempre quali app stai bloccando.

Questo fork è distribuito solo tramite [questo repository GitHub](https://github.com/hsushjk/AndWall). Gli APK provenienti da altre fonti non sono affiliati a questo progetto e l'uso è a proprio rischio. AndWall non ha un sito web ufficiale e non è pubblicato su Google Play o F-Droid.

Il permesso `INTERNET` serve solo per l'abbinamento tramite debug wireless (LibADB).

## Licenza

[GPLv3](../LICENSE.md). I dati delle firme dei tracker inclusi sono concessi separatamente con licenza ODbL v1.0, vedi [TRACKER_DATA_LICENSE.md](../TRACKER_DATA_LICENSE.md).

## Riconoscimenti

- [ShizuWall](https://github.com/AhmetCanArslan/ShizuWall): il progetto upstream su cui si basa questo fork.
- [Shizuku](https://github.com/RikkaApps/Shizuku): esecuzione di comandi privilegiati.
- [LibADB](https://github.com/MuntashirAkon/libadb-android): debug wireless e connessione al daemon.
- [Exodus Privacy](https://exodus-privacy.eu.org/): database delle firme dei tracker. AndWall ne include una copia ridotta e non contatta mai la sua API.
