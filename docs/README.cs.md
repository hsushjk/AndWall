<div align="center">
  <a href="https://github.com/hsushjk/AndWall">
    <img src="../app/src/main/res/mipmap-xxxhdpi/ic_launcher_round.png" alt="Ikona AndWall" width="72" />
  </a>
  <h1>AndWall</h1>
  <strong>Android firewall.</strong><br/>
  Založeno na VPN / Shizuku / lokálním ADB démonu / Root
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
  <img alt="Minimum SDK" src="https://img.shields.io/badge/Minimum%20SDK-30%20(Android%2011)-839192?style=flat-square&logo=android&logoColor=white" />
</p>

<p align="center">
  <a href="https://github.com/hsushjk/AndWall/issues">Problémy a zpětná vazba</a> ·
  <a href="https://github.com/AhmetCanArslan/ShizuWall">Upstream projekt (ShizuWall)</a>
</p>

## Proč AndWall existuje

[ShizuWall](https://github.com/AhmetCanArslan/ShizuWall) je velmi dobrý projekt — funkčně bohatý, ale ne přeplácaný, s krásným a promyšleným designem UI. Nepodporuje však režim VPN.

Vlastník tohoto forku se domnívá, že dobrý projekt má být otevřený a inkluzivní. Přidání VPN backendu sice snižuje bariéru vstupu, ale nesnižuje jeho strop.

## Proč si vybrat AndWall

- **Více režimů na výběr**: Můžete použít tu nejzákladnější VPN. Můžete použít i výkonnější režimy Shizuku, ADB a root.
- **Ovládání po aplikacích**: Blokování sítě po aplikacích, v řetězovém režimu včetně pracovních profilů a klonovaných kopií.
- **Soukromí**: Offline-first, žádná analytika, žádná telemetrie, žádné vzdálené VPN servery — tunel je zcela lokální, všechny pakety jsou zahozeny.
- **Rychlý přístup**: Dlaždice v rychlém nastavení, widgety a plovoucí tlačítko.
- **Automatizace**: Ovládání pomocí broadcastů odesílaných z adb, Taskeru, MacroDroidu a podobně.

## Hlavní změny v tomto forku

AndWall je fork projektu [ShizuWall](https://github.com/AhmetCanArslan/ShizuWall). Tento fork přidává:

- **VPN backend**: Firewall backend bez konfigurace — stačí potvrdit systémové dialogové okno o oprávnění.
- **Přizpůsobení rozhraní pro režim VPN**: Skrytí režimů firewallu a funkcí, které VPN backend neumí nabídnout, a přepnutí režimu firewallu na allowlist (seznam povolených).
- **Rebranding, aktualizované položky nastavení a překlady** a podrobnější changelog a kontrola aktualizací pro tento fork.
- **Odstranění zásad ochrany osobních údajů**: Projekt není distribuován v žádném obchodě s aplikacemi a nezpracovává osobní údaje, takže nevzniká žádná povinnost informovat.

## Kompatibilní prostředí

Android 11 a novější a jeden z následujících backendů:

- **VPN**
- **Shizuku** (podporované verze forku)
- **LibADB**: Párování přes bezdrátové ladění, bez počítače
- **Root**

## Snímky obrazovky

<p align="center">
  <img src="../assets/screenshots/v4.7.1/1.png" alt="Hlavní obrazovka AndWall" width="30%" />
</p>

## Jak to funguje

Pouze backendy Shizuku, LibADB a Root vyžadují privilegovanou doprovodnou komponentu. Ta ovládá **chain 3** v Android connectivity — platformní mechanismus pro řízení síťového přístupu podle názvu balíčku:

```bash
cmd connectivity set-chain3-enabled true                                # zapnout firewall
cmd connectivity set-package-networking-enabled false <package.name>   # zablokovat
cmd connectivity set-package-networking-enabled true <package.name>    # odblokovat
cmd connectivity set-chain3-enabled false                               # vypnout firewall
```

Android tyto zásady po restartu zařízení vymaže.

### VPN backend

VPN backend je lokální zařízení tun `VpnService`, které zachytává pouze aplikace, jež si vyberete k zablokování, a zahazuje všechny přijaté pakety. Vyžaduje pouze jedno systémové dialogové okno o oprávnění a žádnou další konfiguraci. Nová instalace standardně používá tento backend v kombinaci s režimem firewallu **allowlist** (vyberete aplikace, kterým povolíte síť, vše ostatní se blokuje); změnit to lze v **Nastavení → Backend a pracovní režim**.

Po restartu se tunel automaticky obnoví; pokud je proces aplikace ukončen, zatímco je firewall zapnutý, ochrana se obnoví při příštím otevření AndWall. Režim uzamčené obrazovky je podporován, pokud má zařízení nastaven zámek obrazovky (tunel sleduje stav uzamčení obrazovky).

Omezení VPN backendu: zpracovává pouze aplikace aktuálního uživatele (nepodporuje pracovní profily ani klonované kopie), nepodporuje pravidla pouze pro mobilní data ani režimy založené na detekci popředí a údržba sady blokovaných aplikací se neopírá o firewallový chain podle UID. Pokud tyto funkce potřebujete, používejte nadále Shizuku, LibADB nebo Root.

## Automatizace

Každý broadcast vyžaduje aktivní backend. `apps` je čárkami oddělený seznam klíčů aplikací: samotný název balíčku označuje uživatele 0, verze s předponou (např. `150:com.example.app`) označuje kopii v daném pracovním profilu nebo klonovaném prostoru.

### Firewall: `shizuwall.CONTROL`

| Parametr | Typ | |
|---|---|---|
| `state` | boolean, povinné | `true` = zablokovat, `false` = odblokovat |
| `apps` | string, volitelné | Aplikace, které se mají zablokovat nebo odblokovat. Pokud je vynecháte, broadcast zapne nebo vypne celý firewall. |

```bash
# zapnout firewall (state false znamená vypnout)
adb shell am broadcast -a shizuwall.CONTROL -n com.arslan.shizuwall/.receivers.FirewallControlReceiver --ez state true

# zablokovat aplikaci a její kopii v pracovním profilu (state false znamená odblokovat)
adb shell am broadcast -a shizuwall.CONTROL -n com.arslan.shizuwall/.receivers.FirewallControlReceiver --ez state true --es apps "com.example.app,150:com.example.app"
```

Při použití `apps` se výběr v aplikaci synchronizuje: `state true` tyto aplikace vybere a pokud byl firewall dříve vypnutý, zapne jej; `state false` je odznačí a firewall ponechá zapnutý. V režimu allowlist `apps` odkazuje na seznam povolených, takže význam obou stavů je opačný.

V režimu VPN fungují broadcasty stejně, ale `apps` by mělo odkazovat pouze na aplikace aktuálního uživatele; klíče pracovních profilů jsou přeskočeny.

### Nové aplikace: `shizuwall.APP_INSTALLED`

Hlásí nově nainstalované aplikace, aby zásady a oznámení pro nové aplikace fungovaly stejně, jako když AndWall instalaci zjistí sám. AndWall vidí pouze instalace ve vlastním uživatelském profilu, proto je ostatní profily musí pokrýt tímto způsobem.

```bash
adb shell am broadcast -a shizuwall.APP_INSTALLED -n com.arslan.shizuwall/.receivers.AppInstalledReceiver --es apps "com.example.newapp"
```

Když je firewall vypnutý, žádná pravidla se neuplatní. Aplikace bez oprávnění `INTERNET` jsou ignorovány.

### Profily: `shizuwall.PROFILE`

Aktivuje uložený profil: použije výběr aplikací daného profilu, režim firewallu, režim jednotlivých aplikací a nastavení *Zobrazovat systémové aplikace*.

| Parametr | Typ | |
|---|---|---|
| `profile` | string | Název profilu, shodný s názvem zobrazeným v panelu profilů. |
| `profile_id` | string | Interní ID; kontroluje se přednostně a není ovlivněno přejmenováním. Hotový příkaz se zobrazuje v dialogu **Automatizace** daného profilu. |
| `force_enable` | boolean, volitelné | Po přepnutí zapne firewall, i když byl dříve vypnutý. |

Musí být zadán alespoň jeden z údajů `profile` a `profile_id`. Pokud ani jeden neodpovídá, nic se nezmění a zobrazí se oznámení toast.

```bash
adb shell am broadcast -a shizuwall.PROFILE -n com.arslan.shizuwall/.receivers.ProfileControlReceiver --es profile "Work"
```

## Sestavení ze zdrojového kódu

Tento projekt se sestavuje pomocí Gradle Wrapper a Android SDK:

```bash
./gradlew assembleDebug
```

Ladicí sestavení používá příponu `.debug` a lze je nainstalovat souběžně s vydáním. Vydání vyžaduje vlastní konfiguraci podpisu.

## Vyloučení odpovědnosti

AndWall je poskytován **„tak, jak je“** bez jakýchkoli záruk. Spoléhá na pokročilá systémová oprávnění a vývojář nenese odpovědnost za nestabilitu systému, ztrátu dat ani vedlejší účinky způsobené blokováním sítě. Vždy si ověřte, které aplikace blokujete.

Tento fork je distribuován výhradně prostřednictvím [tohoto GitHub repozitáře](https://github.com/hsushjk/AndWall). APK z jiných zdrojů s tímto projektem nesouvisejí a používáte je na vlastní riziko. AndWall nemá oficiální webové stránky a nevychází na Google Play ani F-Droid.

Oprávnění `INTERNET` slouží pouze k párování bezdrátového ladění (LibADB).

## Sponzoring

Pokud máte pocit, že vám tento projekt pomohl, můžete dát hvězdičku tomuto repozitáři i upstreamu. Pokud chcete přispět finančně, přejděte do upstream repozitáře a klikněte na `Buy me a coffee`.

## Licence

[GPLv3](../LICENSE.md). Přiložená data podpisů trackerů jsou samostatně licencována pod ODbL v1.0, viz [TRACKER_DATA_LICENSE.md](../TRACKER_DATA_LICENSE.md).

## Poděkování

- [ShizuWall](https://github.com/AhmetCanArslan/ShizuWall): Upstream projekt, na kterém je tento fork založen.
- [Shizuku](https://github.com/RikkaApps/Shizuku): Spouštění privilegovaných příkazů.
- [LibADB](https://github.com/MuntashirAkon/libadb-android): Bezdrátové ladění a připojení démona.
- [Exodus Privacy](https://exodus-privacy.eu.org/): Databáze podpisů trackerů. AndWall obsahuje její zmenšenou kopii a k jejímu API nikdy nepřistupuje.
