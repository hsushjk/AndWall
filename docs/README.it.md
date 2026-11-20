<div align="center">
  <a href="https://github.com/hsushjk/AndWall">
    <img src="../app/src/main/res/mipmap-xxxhdpi/ic_launcher_round.png" alt="Icona di AndWall" width="72" />
  </a>
  <h1>AndWall</h1>
  <strong>Firewall per Android.</strong><br/>
  Basato su VPN / Shizuku / demone ADB locale / Root
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
  <img alt="SDK minimo" src="https://img.shields.io/badge/Minimum%20SDK-30%20(Android%2011)-839192?style=flat-square&logo=android&logoColor=white" />
</p>

<p align="center">
  <a href="https://github.com/hsushjk/AndWall/issues">Problemi e feedback</a> ·
  <a href="https://github.com/AhmetCanArslan/ShizuWall">Progetto upstream (ShizuWall)</a>
</p>

## Perché esiste AndWall

[ShizuWall](https://github.com/AhmetCanArslan/ShizuWall) è un progetto eccellente: potente ma senza fronzoli, con un'interfaccia bella e ben congegnata. Non supporta però la modalità VPN.

Il proprietario di questo fork ritiene che un buon progetto debba essere inclusivo. Integrare un backend VPN abbassa la soglia d'uso, ma non abbassa il livello massimo.

## Perché scegliere AndWall

- **Più modalità disponibili**: puoi usare la VPN, la più semplice. Oppure le modalità più potenti Shizuku, ADB e root.
- **Controllo per app**: blocca la rete per singola app; in modalità concatenata include anche i profili di lavoro e i cloni delle app.
- **Privacy**: offline-first, nessuna analisi, nessuna telemetria, nessun server VPN remoto: il tunnel è del tutto locale e tutti i pacchetti vengono scartati.
- **Accesso rapido**: riquadro nelle impostazioni rapide, widget e pulsante flottante.
- **Automazione**: controllabile tramite broadcast inviati da adb, Tasker, MacroDroid e altri.

## Modifiche principali di questo fork

AndWall è un fork di [ShizuWall](https://github.com/AhmetCanArslan/ShizuWall). Questo fork aggiunge:

- **Backend VPN**: un backend firewall senza configurazione, utilizzabile confermando la sola finestra di autorizzazione di sistema.
- **Adattamento dell'interfaccia per la modalità VPN**: nasconde le modalità e le funzioni del firewall che il backend VPN non può offrire e riporta la modalità firewall alla whitelist.
- **Rebranding, impostazioni e traduzioni aggiornate**, oltre a un changelog più dettagliato e al controllo degli aggiornamenti per questo fork.
- **Rimozione dell'informativa sulla privacy**: il progetto non è pubblicato su alcun app store e non tratta dati personali, quindi non sorge alcun obbligo di informativa.

## Ambienti compatibili

Android 11 o successivo, con uno dei seguenti backend:

- **VPN**
- **Shizuku** (con supporto per la versione fork)
- **LibADB**: associazione tramite debug wireless, senza computer
- **Root**

## Screenshot

<p align="center">
  <img src="../assets/screenshots/v4.7.1/1.png" alt="Schermata principale di AndWall" width="30%" />
</p>

## Come funziona

Solo i backend Shizuku, LibADB e Root richiedono il componente companion privilegiato. Essi operano sulla **chain 3** della connettività Android, un meccanismo di piattaforma che controlla l'accesso alla rete per nome pacchetto:

```bash
cmd connectivity set-chain3-enabled true                                # attiva il firewall
cmd connectivity set-package-networking-enabled false <package.name>   # blocca
cmd connectivity set-package-networking-enabled true <package.name>    # sblocca
cmd connectivity set-chain3-enabled false                               # disattiva il firewall
```

Android cancella queste regole al riavvio del dispositivo.

### Backend VPN

Il backend VPN è un dispositivo tun `VpnService` locale che intercetta solo le app che scegli di bloccare e scarta tutti i pacchetti che riceve. Richiede una sola finestra di autorizzazione di sistema e nessun'altra configurazione. Le nuove installazioni usano questo backend per impostazione predefinita, con la modalità firewall **whitelist** (scegli le app a cui consentire la rete, tutto il resto viene bloccato), modificabile in **Impostazioni → Backend e modalità operative**.

Dopo un riavvio il tunnel si riavvia automaticamente; se il processo dell'app viene terminato mentre il firewall è attivo, la protezione viene ripristinata alla successiva apertura di AndWall. La modalità schermo bloccato è supportata purché il dispositivo abbia un blocco schermo (il tunnel segue lo stato di blocco).

Limiti del backend VPN: gestisce solo le app dell'utente corrente (profili di lavoro e cloni non supportati), non supporta le regole valide solo per i dati mobili né le modalità basate sul rilevamento in primo piano, e la manutenzione dell'insieme delle app bloccate non si basa sulle catene firewall per UID. Se ti servono queste funzioni, continua a usare Shizuku, LibADB o Root.

## Automazione

Ogni broadcast richiede un backend attivo. `apps` è un elenco di chiavi app separate da virgola: il solo nome del pacchetto indica l'utente 0, mentre con un prefisso (ad es. `150:com.example.app`) indica la copia in quel profilo di lavoro o spazio clone.

### Firewall: `shizuwall.CONTROL`

| Parametro | Tipo | |
|---|---|---|
| `state` | boolean, obbligatorio | `true` = blocca, `false` = sblocca |
| `apps` | string, facoltativo | Le app da bloccare o sbloccare. Se omesso, il broadcast attiva o disattiva l'intero firewall. |

```bash
# attiva il firewall (state false lo disattiva)
adb shell am broadcast -a shizuwall.CONTROL -n com.arslan.shizuwall/.receivers.FirewallControlReceiver --ez state true

# blocca un'app e le sue copie nel profilo di lavoro (state false sblocca)
adb shell am broadcast -a shizuwall.CONTROL -n com.arslan.shizuwall/.receivers.FirewallControlReceiver --ez state true --es apps "com.example.app,150:com.example.app"
```

Con `apps` la selezione nell'app resta sincronizzata: `state true` seleziona queste app e attiva il firewall se prima era disattivato; `state false` le deseleziona e mantiene il firewall attivo. In modalità whitelist `apps` indica l'elenco delle app consentite, quindi il significato dei due stati è invertito.

In modalità VPN i broadcast funzionano allo stesso modo, ma `apps` deve riferirsi solo alle app dell'utente corrente; le chiavi dei profili di lavoro vengono ignorate.

### Nuove app: `shizuwall.APP_INSTALLED`

Segnala un'app appena installata, così che i criteri per le nuove app e le notifiche abbiano effetto come se AndWall avesse rilevato l'installazione da solo. AndWall vede solo le installazioni nel proprio profilo utente, quindi gli altri profili vanno coperti in questo modo.

```bash
adb shell am broadcast -a shizuwall.APP_INSTALLED -n com.arslan.shizuwall/.receivers.AppInstalledReceiver --es apps "com.example.newapp"
```

Con il firewall disattivato non viene applicata alcuna regola. Le app senza autorizzazione `INTERNET` vengono ignorate.

### Profilo: `shizuwall.PROFILE`

Attiva un profilo salvato: applica la selezione di app, la modalità firewall, la modalità per singola app e l'impostazione *Mostra app di sistema* del profilo.

| Parametro | Tipo | |
|---|---|---|
| `profile` | string | Nome del profilo, uguale a quello mostrato nel pannello dei profili. |
| `profile_id` | string | ID interno; viene controllato per primo e non è influenzato dalla rinomina. La finestra **Automazione** di quel profilo mostra il comando già pronto. |
| `force_enable` | boolean, facoltativo | Attiva il firewall dopo il cambio, anche se prima era disattivato. |

Indica almeno uno tra `profile` e `profile_id`. Se nessuno dei due corrisponde, non viene applicata alcuna modifica e compare un toast.

```bash
adb shell am broadcast -a shizuwall.PROFILE -n com.arslan.shizuwall/.receivers.ProfileControlReceiver --es profile "Work"
```

## Compilazione dai sorgenti

Il progetto si compila con Gradle Wrapper e Android SDK:

```bash
./gradlew assembleDebug
```

Le build di debug usano il suffisso `.debug` e possono essere installate accanto alla versione di release. Le build di release richiedono la tua configurazione di firma.

## Esclusione di responsabilità

AndWall è fornito **“così com'è”**, senza alcuna garanzia. Si basa su permessi di sistema avanzati e gli sviluppatori non sono responsabili di instabilità del sistema, perdita di dati o effetti collaterali del blocco della rete. Verifica sempre quali app stai bloccando.

Questo fork è distribuito esclusivamente tramite [questo repository GitHub](https://github.com/hsushjk/AndWall). Gli APK provenienti da altre fonti non sono correlati a questo progetto e l'uso è a tuo rischio. AndWall non ha un sito ufficiale e non viene pubblicato su Google Play né su F-Droid.

L'autorizzazione `INTERNET` serve solo per l'associazione tramite debug wireless (LibADB).

## Sponsor

Se ritieni che questo progetto ti sia stato utile, puoi mettere una Star sia a questo repository sia a quello upstream. Se vuoi sponsorizzare, vai al repository upstream e fai clic su `Buy me a coffee`

## Licenza

[GPLv3](../LICENSE.md). I dati delle firme dei tracker inclusi sono concessi separatamente con licenza ODbL v1.0; vedi [TRACKER_DATA_LICENSE.md](../TRACKER_DATA_LICENSE.md).

## Ringraziamenti

- [ShizuWall](https://github.com/AhmetCanArslan/ShizuWall): il progetto upstream su cui si basa questo fork.
- [Shizuku](https://github.com/RikkaApps/Shizuku): esecuzione di comandi privilegiati.
- [LibADB](https://github.com/MuntashirAkon/libadb-android): debug wireless e connessione al demone.
- [Exodus Privacy](https://exodus-privacy.eu.org/): database delle firme dei tracker. AndWall ne include una copia ridotta e non accede mai alla sua API.
