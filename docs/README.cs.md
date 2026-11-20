<div align="center">
  <a href="https://github.com/hsushjk/AndWall">
    <img src="../app/src/main/res/mipmap-xxxhdpi/ic_launcher_round.png" alt="AndWall Icon" width="72" />
  </a>
  <h1>AndWall</h1>
  <strong>Firewall pro Android s volitelným lokálním backendem VPN.</strong><br/>
  Soukromí na prvním místě, zcela lokálně. Pohání jej vestavěný backend VPN, který nevyžaduje žádné nastavení – nebo Shizuku / lokální démon ADB / root pro pokročilé režimy.
</div>

<p align="center">
  <a href="../README.md">English</a> ·
  <a href="README.tr.md">Türkçe</a> ·
  <a href="README.de.md">Deutsch</a> ·
  <a href="README.it.md">Italiano</a> ·
  <a href="README.pt.md">Português</a> ·
  <b>Čeština</b> ·
  <a href="README.ru.md">Русский</a> ·
  <a href="README.ar.md">العربية</a> ·
  <a href="README.hi.md">हिन्दी</a> ·
  <a href="README.zh.md">中文</a> ·
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
- **Shizuku** (forky jsou podporovány)
- **LibADB**: páruje se přes bezdrátové ladění, bez počítače
- **Root**

Nové instalace začínají s backendem **VPN** a režimem firewallu **Bílá listina**, takže aplikace funguje hned po instalaci. Ostatní backendy lze vybrat později v **Nastavení → Backend a pracovní režim**.

## Proč AndWall

- **V režimu VPN není potřeba privilegované nastavení**: stačí jednou udělit systémový souhlas s VPN – žádné Shizuku, žádné ADB, žádný root.
- **Řízení po aplikacích**: blokuje síť po jednotlivých aplikacích, včetně kopií v pracovním profilu a klonů v režimech chain 3.
- **Soukromé**: offline-first, žádná analytika, žádná telemetrie, žádný vzdálený server VPN – tunel je lokální a všechny pakety se zahazují.
- **Rychlý přístup**: dlaždice Rychlého nastavení, widget a plovoucí tlačítko.
- **Automatizace**: ovladatelné broadcasty z adb, Taskeru, MacroDroidu a podobných nástrojů.

## Snímek obrazovky

<p align="center">
  <img src="../assets/screenshots/v4.7.1/1.png" alt="Hlavní obrazovka AndWall" width="30%" />
  <img src="../assets/screenshots/v4.7.1/4.png" alt="Nastavení" width="30%" />
</p>

## Jak to funguje

Privilegovaného pomocníka potřebují pouze backendy Shizuku, LibADB a Root. Ovládají androidí connectivity **chain 3**, mechanismus platformy, který řídí přístup k síti po balíčcích:

```bash
cmd connectivity set-chain3-enabled true                                # firewall zapnut
cmd connectivity set-package-networking-enabled false <package.name>   # blokovat
cmd connectivity set-package-networking-enabled true <package.name>    # odblokovat
cmd connectivity set-chain3-enabled false                               # firewall vypnut
```

Android tato pravidla při restartu smaže.

### Backend VPN

Backend VPN je lokální tunelové zařízení `VpnService`, které zachytává pouze aplikace, jež se rozhodnete blokovat, a každý přijatý paket zahodí. Vyžaduje jediný systémový dialog se souhlasem a žádné další nastavení. Je výchozí pro nové instalace, spárovaný s režimem firewallu **Bílá listina** (vyberte aplikace, které mají být povoleny, vše ostatní se blokuje), a lze jej změnit v **Nastavení → Backend a pracovní režim**.

Tunel se po restartu automaticky spustí znovu, a pokud je proces aplikace ukončen při zapnutém firewallu, ochrana se obnoví při příštím otevření AndWall. Režim uzamčení obrazovky je podporován (tunel sleduje stav zámku), dokud má zařízení nastavený zámek obrazovky.

Omezení backendu VPN: zpracovává pouze aplikace aktuálního uživatele (žádné kopie v pracovním profilu ani klony), pravidla pouze pro mobilní data a režimy založené na detekci aplikace v popředí nejsou dostupné a sada blokovaných aplikací se udržuje bez firewallových řetězců pro jednotlivá UID. Pro tyto funkce nadále používejte Shizuku, LibADB nebo Root.

## Změny v tomto forku

AndWall je fork projektu [ShizuWall](https://github.com/AhmetCanArslan/ShizuWall). Tento fork přidává:

- **Backend VPN**: firewallový backend bez nutnosti nastavení, který funguje hned po potvrzení systémového dialogu se souhlasem.
- **Výchozí nastavení pro nové uživatele**: backend VPN, režim firewallu Bílá listina a skrývání aplikací bez přístupu k síti.
- **Aktualizované úvodní obrazovky**: uvítací průvodce a výběr režimu nyní zmiňují všechny čtyři backendy.
- **Přizpůsobení UI pro režim VPN**: režimy firewallu a funkce, které backend VPN neumí zajistit, se skrývají a režim firewallu se přepne na Bílou listinu.
- **Přejmenování, aktualizované položky nastavení a překlady**, navíc podrobnější changelogy a kontroly aktualizací pro tento fork.

## Automatizace

Každý broadcast vyžaduje aktivní backend. `apps` je seznam klíčů aplikací oddělených čárkou: samotný název balíčku míří na uživatele 0, klíč s prefixem jako `150:com.example.app` na kopii v daném pracovním profilu nebo klonovaném prostoru.

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

S `apps` se přizpůsobí i výběr v aplikaci: `state true` aplikace vybere a zapne firewall, pokud byl vypnutý; `state false` je z výběru odebere a firewall nechá zapnutý. V režimu whitelistu `apps` míří na seznam povolených, takže jsou oba stavy obrácené.

V režimu VPN broadcasty fungují stejně, ale `apps` by mělo odkazovat pouze na aplikace aktuálního uživatele; klíče pracovního profilu se přeskakují.

### Nová aplikace: `shizuwall.APP_INSTALLED`

Oznámí nově nainstalovanou aplikaci, takže se na ni použijí pravidla i oznámení pro nové aplikace, jako by instalaci zaznamenal sám AndWall. AndWall vidí jen instalace ve vlastním profilu; takto lze pokrýt i ostatní.

```bash
adb shell am broadcast -a shizuwall.APP_INSTALLED -n com.arslan.shizuwall/.receivers.AppInstalledReceiver --es apps "com.example.newapp"
```

Když je firewall vypnutý, nepoužije se žádné pravidlo. Aplikace bez oprávnění `INTERNET` se ignorují.

### Profil: `shizuwall.PROFILE`

Aktivuje uložený profil: jeho výběr aplikací, režim firewallu, režimy jednotlivých aplikací a nastavení *Zobrazit systémové aplikace*.

| Extra | Typ | |
|---|---|---|
| `profile` | string | Název profilu, jak je uveden v seznamu profilů. |
| `profile_id` | string | Interní ID; kontroluje se jako první a přejmenování ho neovlivní. Dialog **Automatizace** u profilu ukazuje hotový příkaz. |
| `force_enable` | boolean, volitelné | Po přepnutí zapne firewall, i když byl vypnutý. |

Je vyžadováno `profile` nebo `profile_id`. Pokud žádné neodpovídá, nic se nezmění a oznámí to toast.

```bash
adb shell am broadcast -a shizuwall.PROFILE -n com.arslan.shizuwall/.receivers.ProfileControlReceiver --es profile "Work"
```

## Sestavení ze zdrojů

Projekt se sestavuje pomocí Gradle wrapperu a Android SDK:

```bash
./gradlew assembleDebug
```

Debug sestavení používá příponu `.debug` a lze jej nainstalovat vedle release sestavení. Release sestavení vyžaduje vlastní konfiguraci podepisování.

## Prohlášení o odpovědnosti

AndWall je poskytován **„tak, jak je“**, bez záruky. Využívá pokročilá systémová oprávnění a vývojář neodpovídá za nestabilitu systému, ztrátu dat ani vedlejší účinky blokování sítě. Vždy si ověřte, které aplikace blokujete.

Tento fork je distribuován pouze prostřednictvím [tohoto repozitáře na GitHubu](https://github.com/hsushjk/AndWall). APK odjinud s projektem nesouvisí a používáte je na vlastní riziko. AndWall nemá žádné oficiální webové stránky a není publikován na Google Play ani F-Droid.

Oprávnění `INTERNET` se vyžaduje jen pro párování bezdrátového ladění (LibADB).

## Licence

[GPLv3](../LICENSE.md). Přibalená data signatur trackerů jsou licencována samostatně pod ODbL v1.0, viz [TRACKER_DATA_LICENSE.md](../TRACKER_DATA_LICENSE.md).

## Poděkování

- [ShizuWall](https://github.com/AhmetCanArslan/ShizuWall): původní projekt, na kterém je tento fork založen.
- [Shizuku](https://github.com/RikkaApps/Shizuku): spouštění privilegovaných příkazů.
- [LibADB](https://github.com/MuntashirAkon/libadb-android): bezdrátové ladění a připojení k démonu.
- [Exodus Privacy](https://exodus-privacy.eu.org/): databáze signatur trackerů. AndWall obsahuje její zkrácenou kopii a nikdy nekontaktuje jejich API.
