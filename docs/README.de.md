<div align="center">
  <a href="https://github.com/hsushjk/AndWall">
    <img src="../app/src/main/res/mipmap-xxxhdpi/ic_launcher_round.png" alt="AndWall-Symbol" width="72" />
  </a>
  <h1>AndWall</h1>
  <strong>Android-Firewall.</strong><br/>
  Basierend auf VPN / Shizuku / lokalem ADB-Daemon / Root
</div>

<p align="center">
  <a href="README.en.md">English</a> ·
  <a href="README.tr.md">Türkçe</a> ·
  <b>Deutsch</b> ·
  <a href="README.es.md">Español</a> ·
  <a href="README.it.md">Italiano</a> ·
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
  <img alt="Minimum SDK" src="https://img.shields.io/badge/Minimum%20SDK-30%20(Android%2011)-839192?style=flat-square&logo=android&logoColor=white" />
</p>

<p align="center">
  <a href="https://github.com/hsushjk/AndWall/issues">Fragen und Feedback</a> ·
  <a href="https://github.com/AhmetCanArslan/ShizuWall">Upstream-Projekt (ShizuWall)</a>
</p>

## Warum es AndWall gibt

[ShizuWall](https://github.com/AhmetCanArslan/ShizuWall) ist ein sehr gutes Projekt – funktionsstark, aber nicht überladen, mit einem schönen und durchdachten UI-Design. Es unterstützt jedoch keinen VPN-Modus.

Der Eigentümer dieses Fork-Repositorys ist der Ansicht, dass ein gutes Projekt Offenheit für verschiedene Ansätze zeigen sollte. Die Integration eines VPN-Backends senkt zwar die Einstiegshürde, senkt aber nicht die Obergrenze.

## Warum AndWall

- **Mehrere Modi zur Auswahl**: Sie können das einfachste VPN verwenden – oder die leistungsfähigeren Modi Shizuku, ADB und Root.
- **Steuerung pro App**: Netzwerk pro App blockieren, im Kettenmodus auch für Arbeitsprofile und Klonkopien.
- **Privatsphäre**: Offline-first, keine Analyse, keine Telemetrie, keine entfernten VPN-Server – der Tunnel ist vollständig lokal, alle Pakete werden verworfen.
- **Schnellzugriff**: Schnelleinstellungs-Kachel, Widgets und schwebende Schaltfläche.
- **Automatisierung**: Steuerung über Broadcasts, die von adb, Tasker, MacroDroid usw. gesendet werden.

## Wichtigste Änderungen in diesem Fork

AndWall ist ein Fork von [ShizuWall](https://github.com/AhmetCanArslan/ShizuWall). Dieser Fork fügt hinzu:

- **VPN-Backend**: Ein konfigurationsfreies Firewall-Backend, das nach Bestätigung des Systemberechtigungsdialogs sofort einsatzbereit ist.
- **UI-Anpassung für den VPN-Modus**: Firewall-Modi und Funktionen, die das VPN-Backend nicht bieten kann, werden ausgeblendet und der Firewall-Modus wird auf die Whitelist zurückgesetzt.
- **Neue Marke, aktualisierte Einstellungen und Übersetzungen** sowie ein ausführlicheres Änderungsprotokoll und eine Update-Prüfung für diesen Fork.
- **Entfernung der Datenschutzrichtlinie**: Dieses Projekt wird in keinem App-Store veröffentlicht und verarbeitet keine personenbezogenen Daten, daher entsteht keine Informationspflicht.

## Kompatible Umgebung

Android 11 oder höher, mit einem der folgenden Backends:

- **VPN**
- **Shizuku** (Versionen dieses Forks werden unterstützt)
- **LibADB**: Pairing über drahtloses Debugging, kein PC erforderlich
- **Root**

## Screenshots

<p align="center">
  <img src="../assets/screenshots/v4.7.1/1.png" alt="AndWall-Hauptbildschirm" width="30%" />
</p>

## Funktionsweise

Nur die Backends Shizuku, LibADB und Root benötigen eine privilegierte Begleitkomponente. Sie operieren auf **chain 3** der Android-Connectivity – einem Plattformmechanismus zur Steuerung des Netzwerkzugriffs pro Paketname:

```bash
cmd connectivity set-chain3-enabled true                                # Firewall aktivieren
cmd connectivity set-package-networking-enabled false <package.name>   # Blockieren
cmd connectivity set-package-networking-enabled true <package.name>    # Blockierung aufheben
cmd connectivity set-chain3-enabled false                               # Firewall deaktivieren
```

Android löscht diese Regeln nach einem Neustart des Geräts.

### VPN-Backend

Das VPN-Backend ist ein lokales `VpnService`-tun-Gerät, das nur die Apps erfasst, die Sie blockieren möchten, und alle empfangenen Pakete verwirft. Es benötigt nur einen Systemberechtigungsdialog, keine weitere Konfiguration. Bei einer Neuinstallation wird dieses Backend standardmäßig mit dem Firewall-Modus **Whitelist** verwendet (Apps auswählen, denen Netzwerkzugriff erlaubt ist, alles andere blockieren), änderbar unter **Einstellungen → Backend und Arbeitsmodus**.

Nach einem Neustart startet der Tunnel automatisch neu; wird der App-Prozess bei aktiver Firewall beendet, wird der Schutz beim nächsten Öffnen von AndWall wiederhergestellt. Solange das Gerät eine Bildschirmsperre hat, wird ein Sperrbildschirm-Modus unterstützt (der Tunnel folgt dem Status der Bildschirmsperre).

Einschränkungen des VPN-Backends: Es behandelt nur Apps des aktuellen Benutzers (keine Arbeitsprofile/Klonkopien), unterstützt keine Regeln nur für mobile Daten und keine Modi auf Basis der Vordergrunderkennung, und die Pflege der Menge blockierter Apps hängt nicht von UID-basierten Firewall-Ketten ab. Für diese Funktionen nutzen Sie bitte weiterhin Shizuku, LibADB oder Root.

## Automatisierung

Jeder Broadcast erfordert ein aktives Backend. `apps` ist eine kommagetrennte Liste von App-Schlüsseln: nur der Paketname bedeutet Benutzer 0, ein Präfix (z. B. `150:com.example.app`) bedeutet die Kopie in diesem Arbeitsprofil oder Klonbereich.

### Firewall: `shizuwall.CONTROL`

| Parameter | Typ | |
|---|---|---|
| `state` | boolean, erforderlich | `true` = blockieren, `false` = Blockierung aufheben |
| `apps` | string, optional | Die Apps, die blockiert oder freigegeben werden sollen. Wird es weggelassen, schaltet der Broadcast die gesamte Firewall ein oder aus. |

```bash
# Firewall aktivieren (state false bedeutet deaktivieren)
adb shell am broadcast -a shizuwall.CONTROL -n com.arslan.shizuwall/.receivers.FirewallControlReceiver --ez state true

# Eine App und ihre Kopie im Arbeitsprofil blockieren (state false hebt die Blockierung auf)
adb shell am broadcast -a shizuwall.CONTROL -n com.arslan.shizuwall/.receivers.FirewallControlReceiver --ez state true --es apps "com.example.app,150:com.example.app"
```

Bei Angabe von `apps` wird die Auswahl in der App synchronisiert: `state true` wählt diese Apps aus und aktiviert die Firewall, wenn sie zuvor ausgeschaltet war; `state false` hebt die Auswahl auf und lässt die Firewall eingeschaltet. Im Whitelist-Modus bezieht sich `apps` auf die Positivliste, sodass die Bedeutung beider Zustände umgekehrt ist.

Im VPN-Modus funktionieren die Broadcasts gleich, aber `apps` sollte nur Apps des aktuellen Benutzers referenzieren; Schlüssel für Arbeitsprofile werden übersprungen.

### Neue App: `shizuwall.APP_INSTALLED`

Meldet eine neu installierte App, damit die Richtlinie für neue Apps und die Benachrichtigungen so greifen, als hätte AndWall die Installation selbst erkannt. AndWall sieht nur Installationen im eigenen Benutzerprofil, daher müssen andere Profile auf diesem Weg abgedeckt werden.

```bash
adb shell am broadcast -a shizuwall.APP_INSTALLED -n com.arslan.shizuwall/.receivers.AppInstalledReceiver --es apps "com.example.newapp"
```

Bei ausgeschalteter Firewall werden keine Regeln angewendet. Apps ohne `INTERNET`-Berechtigung werden ignoriert.

### Profil: `shizuwall.PROFILE`

Aktiviert ein gespeichertes Profil: Es wendet die App-Auswahl des Profils, den Firewall-Modus, die Modi pro App und die Einstellung *System-Apps anzeigen* an.

| Parameter | Typ | |
|---|---|---|
| `profile` | string | Profilname, identisch mit dem im Profilbereich angezeigten Namen. |
| `profile_id` | string | Interne ID; wird zuerst geprüft und ist von Umbenennungen nicht betroffen. Im **Automatisierung**-Dialog dieses Profils werden fertige Befehle angezeigt. |
| `force_enable` | boolean, optional | Schaltet die Firewall nach dem Umschalten ein, auch wenn sie zuvor ausgeschaltet war. |

Mindestens eines von `profile` und `profile_id` muss angegeben werden. Passt keines von beiden, wird nichts geändert und ein Toast angezeigt.

```bash
adb shell am broadcast -a shizuwall.PROFILE -n com.arslan.shizuwall/.receivers.ProfileControlReceiver --es profile "Work"
```

## Aus dem Quellcode bauen

Dieses Projekt wird mit Gradle Wrapper und dem Android SDK gebaut:

```bash
./gradlew assembleDebug
```

Debug-Builds verwenden das Suffix `.debug` und können parallel zur Release-Version installiert werden. Release-Builds erfordern Ihre eigene Signaturkonfiguration.

## Haftungsausschluss

AndWall wird **„wie besehen“** bereitgestellt, ohne jegliche Gewährleistung. Es ist auf erweiterte Systemberechtigungen angewiesen; die Entwickler haften nicht für Systeminstabilität, Datenverluste oder Nebenwirkungen der Netzwerkblockierung. Prüfen Sie unbedingt, welche Apps Sie blockieren.

Dieser Fork wird ausschließlich über [dieses GitHub-Repository](https://github.com/hsushjk/AndWall) verteilt. APKs aus anderen Quellen stehen in keinem Zusammenhang mit diesem Projekt; die Nutzung erfolgt auf eigenes Risiko. AndWall hat keine offizielle Website und wird nicht bei Google Play oder F-Droid veröffentlicht.

Die `INTERNET`-Berechtigung wird nur für das Pairing über drahtloses Debugging (LibADB) verwendet.

## Spenden

Wenn Sie dieses Projekt hilfreich finden, können Sie diesem Repository und dem Upstream-Projekt jeweils einen Star geben. Wenn Sie spenden möchten, klicken Sie bitte im Upstream-Repository auf `Buy me a coffee`.

## Lizenz

[GPLv3](../LICENSE.md). Die mitgelieferten Tracker-Signaturdaten sind separat unter ODbL v1.0 lizenziert, siehe [TRACKER_DATA_LICENSE.md](../TRACKER_DATA_LICENSE.md).

## Danksagungen

- [ShizuWall](https://github.com/AhmetCanArslan/ShizuWall): Das Upstream-Projekt, auf dem dieser Fork basiert.
- [Shizuku](https://github.com/RikkaApps/Shizuku): Ausführung privilegierter Befehle.
- [LibADB](https://github.com/MuntashirAkon/libadb-android): Drahtloses Debugging und Daemon-Verbindung.
- [Exodus Privacy](https://exodus-privacy.eu.org/): Datenbank der Tracker-Signaturen. AndWall enthält eine reduzierte Kopie und greift nie auf deren API zu.
