<div align="center">
  <a href="https://github.com/hsushjk/AndWall">
    <img src="../app/src/main/res/mipmap-xxxhdpi/ic_launcher_round.png" alt="AndWall Icon" width="72" />
  </a>
  <h1>AndWall</h1>
  <strong>Android-Firewall mit optionalem lokalem VPN-Backend.</strong><br/>
  Datenschutz zuerst, vollständig lokal – angetrieben von einem integrierten VPN-Backend ohne Einrichtung oder von Shizuku / lokalem ADB-Daemon / Root für die erweiterten Modi.
</div>

<p align="center">
  <a href="../README.md">English</a> ·
  <a href="README.tr.md">Türkçe</a> ·
  <b>Deutsch</b> ·
  <a href="README.it.md">Italiano</a> ·
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
  <a href="https://github.com/hsushjk/AndWall/issues">Fragen &amp; Probleme</a> ·
  <a href="https://github.com/AhmetCanArslan/ShizuWall">Upstream-Projekt (ShizuWall)</a>
</p>

## Funktioniert mit

Android 11+ und einem dieser Backends:

- **VPN**: integriertes lokales VPN-Backend, keine Einrichtung und keine zusätzliche App nötig (nur Apps des aktuellen Nutzers)
- **Shizuku** (Forks werden unterstützt)
- **LibADB**: koppelt über Wireless-Debugging, kein Computer nötig
- **Root**

Neue Installationen starten mit dem **VPN**-Backend und dem Firewall-Modus **Whitelist**, sodass die App sofort einsatzbereit ist. Die anderen Backends lassen sich später unter **Einstellungen → Backend & Arbeitsmodus** auswählen.

## Warum AndWall

- **Keine privilegierte Einrichtung im VPN-Modus**: einmal die Systemzustimmung für das VPN erteilen – kein Shizuku, kein ADB, kein Root.
- **Steuerung pro App**: blockiert den Netzwerkzugriff pro App, einschließlich Arbeitsprofil- und Klon-Kopien in den Chain-3-Modi.
- **Privat**: Offline-first, keine Analysen, keine Telemetrie, kein entfernter VPN-Server – der Tunnel ist lokal, und alle Pakete werden verworfen.
- **Schnellzugriff**: Schnelleinstellungs-Kachel, Widget und schwebende Schaltfläche.
- **Automatisierung**: steuerbar über Broadcasts aus adb, Tasker, MacroDroid und Ähnlichem.

## Screenshot

<p align="center">
  <img src="../assets/screenshots/v4.7.1/1.png" alt="AndWall main screen" width="30%" />
  <img src="../assets/screenshots/v4.7.1/4.png" alt="Einstellungen" width="30%" />
</p>

## Funktionsweise

Nur die Backends Shizuku, LibADB und Root benötigen einen privilegierten Helfer. Sie steuern Androids Connectivity-**Chain 3**, einen Plattformmechanismus, der den Netzwerkzugriff pro Paket regelt:

```bash
cmd connectivity set-chain3-enabled true                                # Firewall an
cmd connectivity set-package-networking-enabled false <package.name>   # sperren
cmd connectivity set-package-networking-enabled true <package.name>    # entsperren
cmd connectivity set-chain3-enabled false                               # Firewall aus
```

Android löscht diese Regeln beim Neustart.

### VPN-Backend

Das VPN-Backend ist ein lokales `VpnService`-Tun-Gerät, das nur die Apps erfasst, die du zum Blockieren auswählst, und jedes empfangene Paket verwirft. Es benötigt einen einzigen System-Zustimmungsdialog und keine weitere Einrichtung. Es ist der Standard für neue Installationen, kombiniert mit dem Firewall-Modus **Whitelist** (wähle die Apps aus, die erlaubt sind; alles andere wird blockiert), und lässt sich unter **Einstellungen → Backend & Arbeitsmodus** ändern.

Der Tunnel wird nach einem Neustart automatisch wieder gestartet, und wenn der App-Prozess bei eingeschalteter Firewall beendet wird, wird der Schutz beim nächsten Öffnen von AndWall wiederhergestellt. Der Modus Bildschirmsperre wird unterstützt (der Tunnel folgt dem Sperrstatus), solange das Gerät eine Bildschirmsperre hat.

Einschränkungen des VPN-Backends: Es verwaltet nur Apps des aktuellen Nutzers (keine Arbeitsprofil- oder Klon-Kopien), Regeln nur für mobile Daten und Modi mit Vordergrunderkennung sind nicht verfügbar, und die Liste der blockierten Apps wird ohne Firewall-Chains pro UID gepflegt. Für diese Funktionen weiterhin Shizuku, LibADB oder Root verwenden.

## Änderungen in diesem Fork

AndWall ist ein Fork von [ShizuWall](https://github.com/AhmetCanArslan/ShizuWall). Dieser Fork ergänzt:

- **VPN-Backend**: ein einrichtungsfreies Firewall-Backend, das direkt nach dem System-Zustimmungsdialog funktioniert.
- **Standardwerte für neue Nutzer**: VPN-Backend, Firewall-Modus Whitelist und Ausblenden von Apps ohne Netzwerkzugriff.
- **Aktualisiertes Onboarding**: Willkommensablauf und Modusauswahl nennen jetzt alle vier Backends.
- **UI-Anpassung für den VPN-Modus**: Firewall-Modi und Funktionen, die das VPN-Backend nicht bieten kann, werden ausgeblendet, und der Firewall-Modus fällt auf Whitelist zurück.
- **Rebranding, aktualisierte Einstellungseinträge und Übersetzungen** sowie ausführlichere Changelogs und Update-Prüfungen für diesen Fork.

## Automatisierung

Jeder Broadcast benötigt ein aktives Backend. `apps` ist eine CSV-Liste von App-Schlüsseln: Ein bloßer Paketname zielt auf Nutzer 0, ein Schlüssel mit Präfix wie `150:com.example.app` auf die Kopie in diesem Arbeitsprofil oder Klonbereich.

### Firewall: `shizuwall.CONTROL`

| Extra | Typ | |
|---|---|---|
| `state` | boolean, erforderlich | `true` = sperren, `false` = entsperren |
| `apps` | string, optional | Apps, die gesperrt oder entsperrt werden. Ohne dieses Extra schaltet der Broadcast die gesamte Firewall um. |

```bash
# Firewall einschalten (state false schaltet sie aus)
adb shell am broadcast -a shizuwall.CONTROL -n com.arslan.shizuwall/.receivers.FirewallControlReceiver --ez state true

# Eine App und ihre Arbeitsprofil-Kopie sperren (state false entsperrt sie)
adb shell am broadcast -a shizuwall.CONTROL -n com.arslan.shizuwall/.receivers.FirewallControlReceiver --ez state true --es apps "com.example.app,150:com.example.app"
```

Mit `apps` folgt die Auswahl in der App: `state true` wählt die Apps aus und schaltet die Firewall ein, falls sie aus war; `state false` wählt sie ab und lässt die Firewall an. Im Whitelist-Modus adressiert `apps` die Erlaubnisliste, die beiden Zustände sind also vertauscht.

Im VPN-Modus funktionieren Broadcasts genauso, aber `apps` sollte nur Apps des aktuellen Nutzers referenzieren; Arbeitsprofil-Schlüssel werden übersprungen.

### Neue App: `shizuwall.APP_INSTALLED`

Meldet eine neu installierte App, sodass die Richtlinie für neue Apps und die Benachrichtigung so angewendet werden, als hätte AndWall die Installation selbst erkannt. AndWall sieht nur Installationen im eigenen Profil; so lassen sich die anderen abdecken.

```bash
adb shell am broadcast -a shizuwall.APP_INSTALLED -n com.arslan.shizuwall/.receivers.AppInstalledReceiver --es apps "com.example.newapp"
```

Solange die Firewall aus ist, wird keine Regel angewendet. Apps ohne die Berechtigung `INTERNET` werden ignoriert.

### Profil: `shizuwall.PROFILE`

Aktiviert ein gespeichertes Profil: dessen App-Auswahl, Firewall-Modus, App-spezifische Modi und die Einstellung *System-Apps anzeigen*.

| Extra | Typ | |
|---|---|---|
| `profile` | string | Profilname, wie in der Profilübersicht angezeigt. |
| `profile_id` | string | Interne ID; wird zuerst geprüft und bleibt von Umbenennungen unberührt. Der Dialog **Automatisierung** des Profils zeigt den fertigen Befehl. |
| `force_enable` | boolean, optional | Schaltet die Firewall nach dem Wechsel ein, auch wenn sie aus war. |

Entweder `profile` oder `profile_id` ist erforderlich. Passt keines, ändert sich nichts und ein Toast meldet es.

```bash
adb shell am broadcast -a shizuwall.PROFILE -n com.arslan.shizuwall/.receivers.ProfileControlReceiver --es profile "Work"
```

## Aus dem Quellcode bauen

Das Projekt lässt sich mit dem Gradle-Wrapper und dem Android SDK bauen:

```bash
./gradlew assembleDebug
```

Der Debug-Build verwendet das Suffix `.debug` und kann neben dem Release-Build installiert werden. Der Release-Build erfordert eine eigene Signaturkonfiguration.

## Haftungsausschluss

AndWall wird **„wie besehen“** ohne Gewährleistung bereitgestellt. Es nutzt weitreichende Systemberechtigungen, und der Entwickler ist nicht verantwortlich für Systeminstabilität, Datenverlust oder Nebenwirkungen blockierter Netzwerkzugriffe. Prüfe immer, welche Apps du blockierst.

Dieser Fork wird ausschließlich über [dieses GitHub-Repository](https://github.com/hsushjk/AndWall) vertrieben. APKs von anderswo gehören nicht dazu und werden auf eigenes Risiko verwendet. AndWall hat keine offizielle Website und ist nicht bei Google Play oder F-Droid veröffentlicht.

`INTERNET` wird nur für das Koppeln über Wireless-Debugging (LibADB) angefordert.

## Lizenz

[GPLv3](../LICENSE.md). Die mitgelieferten Tracker-Signaturdaten sind separat unter ODbL v1.0 lizenziert, siehe [TRACKER_DATA_LICENSE.md](../TRACKER_DATA_LICENSE.md).

## Danksagungen

- [ShizuWall](https://github.com/AhmetCanArslan/ShizuWall): das Upstream-Projekt, auf dem dieser Fork basiert.
- [Shizuku](https://github.com/RikkaApps/Shizuku): Ausführung privilegierter Befehle.
- [LibADB](https://github.com/MuntashirAkon/libadb-android): Wireless-Debugging und Daemon-Verbindung.
- [Exodus Privacy](https://exodus-privacy.eu.org/): Tracker-Signaturdatenbank. AndWall liefert eine gekürzte Kopie mit und kontaktiert deren API nie.