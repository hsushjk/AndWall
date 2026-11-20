<div align="center">
  <a href="https://github.com/hsushjk/AndWall">
    <img src="../app/src/main/res/mipmap-xxxhdpi/ic_launcher_round.png" alt="Ikona AndWall" width="72" />
  </a>
  <h1>AndWall</h1>
  <strong>Firewall pro Android s volitelným lokálním backendem VPN.</strong><br/>
  Soukromí na prvním místě, běží zcela lokálně – pohání jej vestavěný backend VPN, který nevyžaduje žádné nastavení; pokročilé režimy pak využívají Shizuku / lokální démon ADB / Root.
</div>

<p align="center">
  <a href="README.en.md">English</a> ·
  <a href="README.tr.md">Türkçe</a> ·
  <a href="README.de.md">Deutsch</a> ·
  <a href="README.es.md">Español</a> ·
  <a href="README.it.md">Italiano</a> ·
  <a href="README.pt.md">Português</a> ·
  <b>Čeština</b> ·
  <a href="README.ru.md">Русский</a> ·
  <a href="README.ar.md">العربية</a> ·
  <a href="README.hi.md">हिन्दी</a> ·
  <a href="../README.md">中文</a> ·
  <a href="README.ja.md">日本語</a>
</p>
<div style="height: 20px;">&nbsp;</div>
<p align="center">
  <a href="https://tooomm.github.io/github-release-stats/?username=hsushjk&repository=AndWall">
    <img alt="Stažení (GitHub)" src="https://img.shields.io/github/downloads/hsushjk/AndWall/total?color=ff9500&label=Total%20Downloads%20(GitHub)&logo=github&logoColor=white&style=flat-square" />
  </a>
  <img alt="Velikost repozitáře" src="https://img.shields.io/github/repo-size/hsushjk/AndWall?style=flat-square" />
  <img alt="Minimální SDK" src="https://img.shields.io/badge/Minimum%20SDK-30%20(Android%2011)-839192?style=flat-square&logo=android&logoColor=white" />
</p>

<p align="center">
  <a href="https://github.com/hsushjk/AndWall/issues">Dotazy a problémy</a> ·
  <a href="https://github.com/AhmetCanArslan/ShizuWall">Upstream projekt (ShizuWall)</a>
</p>

## Funguje s

Android 11+ a jeden z těchto backendů:

- **VPN**: vestavěný lokální backend VPN, bez nastavení a bez doplňkové aplikace (pouze aplikace aktuálního uživatele)
- **Shizuku** (podporovány i forky)
- **LibADB**: párování přes bezdrátové ladění, bez počítače
- **Root**

Nové instalace startují s backendem **VPN** a režimem firewallu **Bílá listina**, takže aplikace funguje ihned po instalaci. Ostatní backendy lze zvolit později v **Nastavení → Backend a pracovní režim**.

## Proč AndWall

- **V režimu VPN není potřeba privilegované nastavení**: stačí jednou udělit systémový souhlas s VPN – žádné Shizuku, žádné ADB, žádný root.
- **Řízení po aplikacích**: blokuje síť po jednotlivých aplikacích a v režimech chain 3 pokrývá i kopie v pracovním profilu a v klonovaném prostoru.
- **Soukromí**: offline-first, žádná analytika, žádná telemetrie, žádný vzdálený server VPN – tunel je zcela lokální a všechny pakety se zahazují.
- **Rychlý přístup**: dlaždice Rychlého nastavení, widget a plovoucí tlačítko.
- **Automatizace**: ovládání pomocí broadcastů z adb, Taskeru, MacroDroidu a podobných nástrojů.

## Snímek obrazovky

<p align="center">
  <img src="../assets/screenshots/v4.7.1/1.png" alt="Hlavní obrazovka AndWall" width="30%" />
  <img src="../assets/screenshots/v4.7.1/4.png" alt="Nastavení AndWall" width="30%" />
</p>

## Jak to funguje

Privilegovaného pomocníka vyžadují pouze backendy Shizuku, LibADB a Root. Ovládají **chain 3** v Android connectivity – mechanismus platformy, který řídí přístup k síti podle balíčků:

```bash
cmd connectivity set-chain3-enabled true                                # firewall zapnut
cmd connectivity set-package-networking-enabled false <package.name>   # zablokovat
cmd connectivity set-package-networking-enabled true <package.name>    # odblokovat
cmd connectivity set-chain3-enabled false                               # firewall vypnut
```

Android tato pravidla při restartu zařízení vymaže.

### Backend VPN

Backend VPN je lokální tunelové zařízení `VpnService`, které zachytává pouze aplikace, jež se rozhodnete blokovat, a všechny přijaté pakety zahazuje. Vyžaduje jediný systémový dialog se souhlasem a žádné další nastavení. Je výchozí volbou pro nové instalace, spárovanou s režimem firewallu **Bílá listina** (vyberte aplikace, kterým je síť povolena, vše ostatní se blokuje), a lze jej změnit v **Nastavení → Backend a pracovní režim**.

Tunel se po restartu automaticky spustí znovu; pokud je proces aplikace ukončen při zapnutém firewallu, ochrana se obnoví při příštím otevření AndWall. Režim uzamčené obrazovky je podporován (tunel sleduje stav zámku), pokud má zařízení nastavený zámek obrazovky.

Omezení backendu VPN: pracuje pouze s aplikacemi aktuálního uživatele (bez kopií v pracovním profilu a klonů), pravidla pouze pro mobilní data a režimy založené na detekci aplikace v popředí nejsou dostupné a sada blokovaných aplikací se spravuje bez firewallových řetězců pro jednotlivá UID. Pro tyto funkce nadále používejte Shizuku, LibADB nebo Root.

## Změny v tomto forku

AndWall je fork projektu [ShizuWall](https://github.com/AhmetCanArslan/ShizuWall). Tento fork přidává:

- **Backend VPN**: firewallový backend bez nutnosti nastavení, funkční hned po potvrzení systémového dialogu se souhlasem.
- **Výchozí nastavení pro nové uživatele**: backend VPN, režim firewallu Bílá listina a skrývání aplikací bez oprávnění k síti.
- **Aktualizovaný úvodní průvodce**: uvítací obrazovka a výběr režimu nyní zmiňují všechny čtyři backendy.
- **Úpravy rozhraní pro režim VPN**: režimy firewallu a funkce, které backend VPN neumí zajistit, jsou skryté a režim firewallu se přepne na Bílou listinu.
- **Rebranding, aktualizované položky nastavení a překlady**, navíc podrobnější seznamy změn a kontroly aktualizací pro tento fork.

## Automatizace

Každý broadcast vyžaduje aktivní backend. `apps` je čárkou oddělený seznam klíčů aplikací: samotný název balíčku míří na uživatele 0, název s prefixem jako `150:com.example.app` na kopii v daném pracovním profilu nebo klonovaném prostoru.

### Firewall: `shizuwall.CONTROL`

| Extra | Typ | |
|---|---|---|
| `state` | boolean, povinné | `true` = blokovat, `false` = odblokovat |
| `apps` | string, volitelné | Aplikace k zablokování nebo odblokování. Bez něj broadcast přepíná celý firewall. |

```bash
# Zapnout firewall (state false ho vypne)
adb shell am broadcast -a shizuwall.CONTROL -n com.arslan.shizuwall/.receivers.FirewallControlReceiver --ez state true

# Zablokovat aplikaci a její kopii v pracovním profilu (state false je odblokuje)
adb shell am broadcast -a shizuwall.CONTROL -n com.arslan.shizuwall/.receivers.FirewallControlReceiver --ez state true --es apps "com.example.app,150:com.example.app"
```

S `apps` se synchronizuje i výběr v aplikaci: `state true` aplikace vybere a firewall zapne, pokud byl vypnutý; `state false` je z výběru odebere a firewall ponechá zapnutý. V režimu whitelistu `apps` míří na seznam povolených, takže jsou oba stavy obrácené.

V režimu VPN broadcasty fungují stejně, ale `apps` by mělo odkazovat pouze na aplikace aktuálního uživatele; klíče pracovního profilu se přeskakují.

### Nová aplikace: `shizuwall.APP_INSTALLED`

Oznámí nově nainstalovanou aplikaci, takže se na ni použije zásada pro nové aplikace i oznámení, jako by instalaci zaznamenal sám AndWall. AndWall vidí instalace pouze ve svém vlastním profilu, proto se takto pokrývají ostatní profily.

```bash
adb shell am broadcast -a shizuwall.APP_INSTALLED -n com.arslan.shizuwall/.receivers.AppInstalledReceiver --es apps "com.example.newapp"
```

Když je firewall vypnutý, neaplikuje se žádné pravidlo. Aplikace bez oprávnění `INTERNET` se ignorují.

### Profil: `shizuwall.PROFILE`

Aktivuje uložený profil: jeho výběr aplikací, režim firewallu, režimy jednotlivých aplikací a nastavení *Zobrazit systémové aplikace*.

| Extra | Typ | |
|---|---|---|
| `profile` | string | Název profilu, shodný s názvem zobrazeným v panelu profilů. |
| `profile_id` | string | Interní ID; kontroluje se jako první a změna názvu ho neovlivní. Dialog **Automatizace** u daného profilu zobrazuje hotový příkaz. |
| `force_enable` | boolean, volitelné | Po přepnutí zapne firewall, i když byl předtím vypnutý. |

Je nutné zadat alespoň jedno z `profile` nebo `profile_id`. Pokud neodpovídá ani jedno, nic se nezmění a oznámí to toast.

```bash
adb shell am broadcast -a shizuwall.PROFILE -n com.arslan.shizuwall/.receivers.ProfileControlReceiver --es profile "Work"
```

## Sestavení ze zdrojů

Projekt se sestavuje pomocí Gradle Wrapperu a Android SDK:

```bash
./gradlew assembleDebug
```

Debug sestavení používá příponu `.debug` a lze jej nainstalovat souběžně s release sestavením. Release sestavení vyžaduje vlastní konfiguraci podepisování.

## Prohlášení o odpovědnosti

AndWall je poskytován **„tak, jak je“**, bez jakékoli záruky. Spoléhá na pokročilá systémová oprávnění a vývojář neodpovídá za nestabilitu systému, ztrátu dat ani vedlejší účinky blokování sítě. Vždy si ověřte, které aplikace blokujete.

Tento fork je distribuován pouze prostřednictvím [tohoto repozitáře na GitHubu](https://github.com/hsushjk/AndWall). APK z jiných zdrojů s tímto projektem nesouvisí a používáte je na vlastní riziko. AndWall nemá žádné oficiální webové stránky a není publikován na Google Play ani F-Droid.

Oprávnění `INTERNET` se vyžaduje pouze pro párování bezdrátového ladění (LibADB).

## Licence

[GPLv3](../LICENSE.md). Přibalená data signatur trackerů jsou licencována samostatně pod ODbL v1.0, viz [TRACKER_DATA_LICENSE.md](../TRACKER_DATA_LICENSE.md).

## Poděkování

- [ShizuWall](https://github.com/AhmetCanArslan/ShizuWall): upstream projekt, na kterém je tento fork založen.
- [Shizuku](https://github.com/RikkaApps/Shizuku): spouštění privilegovaných příkazů.
- [LibADB](https://github.com/MuntashirAkon/libadb-android): bezdrátové ladění a připojení k démonu.
- [Exodus Privacy](https://exodus-privacy.eu.org/): databáze signatur trackerů. AndWall obsahuje zkrácenou kopii a nikdy nekomunikuje s jejich API.
