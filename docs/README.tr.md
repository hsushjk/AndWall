<div align="center">
  <a href="https://github.com/hsushjk/AndWall">
    <img src="../app/src/main/res/mipmap-xxxhdpi/ic_launcher_round.png" alt="AndWall simgesi" width="72" />
  </a>
  <h1>AndWall</h1>
  <strong>İsteğe bağlı yerel VPN arka ucu sunan Android güvenlik duvarı.</strong><br/>
  Gizlilik öncelikli, tamamen yerel; kurulum gerektirmeyen yerleşik bir VPN arka ucuyla, gelişmiş modlarda ise Shizuku / yerel ADB arka plan servisi / Root ile çalışır.
</div>

<p align="center">
  <a href="README.en.md">English</a> ·
  <b>Türkçe</b> ·
  <a href="README.de.md">Deutsch</a> ·
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
  <a href="https://tooomm.github.io/github-release-stats/?username=hsushjk&repository=AndWall">
    <img alt="İndirmeler (GitHub)" src="https://img.shields.io/github/downloads/hsushjk/AndWall/total?color=ff9500&label=Total%20Downloads%20(GitHub)&logo=github&logoColor=white&style=flat-square" />
  </a>
  <img alt="Depo boyutu" src="https://img.shields.io/github/repo-size/hsushjk/AndWall?style=flat-square" />
  <img alt="Minimum SDK" src="https://img.shields.io/badge/Minimum%20SDK-30%20(Android%2011)-839192?style=flat-square&logo=android&logoColor=white" />
</p>

<p align="center">
  <a href="https://github.com/hsushjk/AndWall/issues">Sorular ve geri bildirim</a> ·
  <a href="https://github.com/AhmetCanArslan/ShizuWall">Üst proje (ShizuWall)</a>
</p>

## Uyumlu ortamlar

Android 11 ve üzeri ile aşağıdaki arka uçlardan biri gerekir:

- **VPN**: yerleşik yerel VPN arka ucu; kurulum ve ek uygulama gerekmez (yalnızca geçerli kullanıcının uygulamaları)
- **Shizuku** (fork sürümleri desteklenir)
- **LibADB**: Kablosuz hata ayıklama üzerinden eşleşir, bilgisayar gerekmez
- **Root**

Yeni kurulumlarda varsayılan olarak **VPN** arka ucu ve **Beyaz Liste** güvenlik duvarı modu kullanılır, böylece uygulama kutudan çıktığı gibi çalışır. Diğer arka uçlar daha sonra **Ayarlar → Arka uç ve çalışma modu** bölümünden seçilebilir.

## Neden AndWall

- **VPN modunda ayrıcalıklı kurulum gerekmez**: yalnızca sistem VPN onayını bir kez vermeniz yeterli — Shizuku, ADB veya root gerekmez.
- **Uygulama bazlı denetim**: ağı uygulama bazında engeller; zincir modlarında (chain-3) iş profili ve klon kopyaları da kapsanır.
- **Gizlilik**: önce çevrimdışı; analiz yok, telemetri yok, uzak VPN sunucusu yok — tünel tamamen yereldir ve tüm paketler düşürülür.
- **Hızlı erişim**: Hızlı Ayarlar kutucuğu, widget ve yüzen düğme.
- **Otomasyon**: adb, Tasker, MacroDroid ve benzerlerinden gönderilen yayınlarla denetlenebilir.

## Ekran görüntüsü

<p align="center">
  <img src="../assets/screenshots/v4.7.1/1.png" alt="AndWall ana ekranı" width="30%" />
  <img src="../assets/screenshots/v4.7.1/4.png" alt="Ayarlar" width="30%" />
</p>

## Nasıl çalışır

Yalnızca Shizuku, LibADB ve Root arka uçları ayrıcalıklı bir bileşene ihtiyaç duyar. Bu arka uçlar, ağ erişimini paket adı bazında denetleyen bir platform mekanizması olan Android connectivity'nin **chain 3** mekanizmasını kullanır:

```bash
cmd connectivity set-chain3-enabled true                                # güvenlik duvarını aç
cmd connectivity set-package-networking-enabled false <package.name>   # engelle
cmd connectivity set-package-networking-enabled true <package.name>    # engeli kaldır
cmd connectivity set-chain3-enabled false                               # güvenlik duvarını kapat
```

Android bu kuralları cihaz yeniden başlatıldığında temizler.

### VPN arka ucu

VPN arka ucu, yalnızca engellemeyi seçtiğiniz uygulamaları yakalayan ve aldığı tüm paketleri düşüren yerel bir `VpnService` tun aygıtıdır. Tek bir sistem onay penceresi gerektirir, başka bir yapılandırma gerekmez. Yeni kurulumlarda varsayılan olarak **Beyaz Liste** güvenlik duvarı moduyla birlikte kullanılır (ağa izin verilen uygulamaları seçersiniz, geri kalan her şey engellenir) ve **Ayarlar → Arka uç ve çalışma modu** bölümünden değiştirilebilir.

Tünel yeniden başlatmadan sonra otomatik olarak yeniden başlatılır; güvenlik duvarı açıkken uygulama süreci sonlandırılırsa, AndWall bir sonraki açılışında korumayı geri yükler. Cihazda ekran kilidi ayarlı olduğu sürece ekran kilidi modu desteklenir (tünel kilit durumunu izler).

VPN arka ucunun sınırlamaları: yalnızca geçerli kullanıcının uygulamalarını işler (iş profili/klon kopyaları desteklenmez), yalnızca mobil veri kuralları ve ön plan algılamasına dayalı modlar desteklenmez ve engellenen uygulama kümesinin bakımı UID bazlı güvenlik duvarı zincirlerine dayanmaz. Bu özellikler için Shizuku, LibADB veya Root kullanmaya devam edin.

## Bu fork'taki değişiklikler

AndWall, [ShizuWall](https://github.com/AhmetCanArslan/ShizuWall) projesinin bir fork'udur. Bu fork şunları ekler:

- **VPN arka ucu**: kurulum gerektirmeyen, yalnızca sistem onay penceresini onaylayarak kullanılabilen güvenlik duvarı arka ucu.
- **Yeni kullanıcı varsayılanları**: VPN arka ucu, Beyaz Liste güvenlik duvarı modu ve ağ erişim izni olmayan uygulamaların gizlenmesi.
- **Güncellenmiş ilk kurulum akışı**: karşılama akışı ve mod seçimi artık dört arka ucun tamamından söz ediyor.
- **VPN modu için arayüz uyarlaması**: VPN arka ucunun sunamadığı güvenlik duvarı modları ve özellikler gizlenir ve güvenlik duvarı modu Beyaz Liste'ye geri döner.
- **Yeniden markalama, güncellenmiş ayar öğeleri ve çeviriler**, ayrıca bu fork için daha ayrıntılı değişiklik günlükleri ve güncelleme denetimleri.

## Otomasyon

Her yayın etkin bir arka uç gerektirir. `apps`, virgülle ayrılmış uygulama anahtarları listesidir: yalnızca paket adı yazmak 0. kullanıcıyı belirtir; `150:com.example.app` gibi önekli bir yazım ise o iş profilindeki veya klon alanındaki kopyayı belirtir.

### Güvenlik duvarı: `shizuwall.CONTROL`

| Parametre | Tür | |
|---|---|---|
| `state` | boolean, zorunlu | `true` = engelle, `false` = engeli kaldır |
| `apps` | string, isteğe bağlı | Engellenecek veya engeli kaldırılacak uygulamalar. Atlanırsa yayın güvenlik duvarının tamamını açıp kapatır. |

```bash
# Güvenlik duvarını aç (state false kapatır)
adb shell am broadcast -a shizuwall.CONTROL -n com.arslan.shizuwall/.receivers.FirewallControlReceiver --ez state true

# Bir uygulamayı ve iş profilindeki kopyasını engelle (state false engeli kaldırır)
adb shell am broadcast -a shizuwall.CONTROL -n com.arslan.shizuwall/.receivers.FirewallControlReceiver --ez state true --es apps "com.example.app,150:com.example.app"
```

`apps` verildiğinde uygulama içindeki seçim de buna uyar: `state true` bu uygulamaları seçer ve güvenlik duvarı önceden kapalıysa açar; `state false` seçimi kaldırır ve güvenlik duvarını açık bırakır. Beyaz liste modunda `apps` izin verilenler listesini hedefler, bu nedenle iki durumun anlamı terstir.

VPN modunda yayınlar aynı şekilde çalışır, ancak `apps` yalnızca geçerli kullanıcının uygulamalarına başvurmalıdır; iş profili anahtarları atlanır.

### Yeni uygulama: `shizuwall.APP_INSTALLED`

Yeni yüklenen bir uygulamayı bildirir; böylece yeni uygulama ilkesi ve bildirimi, AndWall kurulumu kendisi algılamış gibi uygulanır. AndWall yalnızca kendi bulunduğu kullanıcı profilindeki kurulumları görebildiği için diğer profiller bu yolla kapsanır.

```bash
adb shell am broadcast -a shizuwall.APP_INSTALLED -n com.arslan.shizuwall/.receivers.AppInstalledReceiver --es apps "com.example.newapp"
```

Güvenlik duvarı kapalıyken hiçbir kural uygulanmaz. `INTERNET` izni olmayan uygulamalar yok sayılır.

### Profil: `shizuwall.PROFILE`

Kaydedilmiş bir profili etkinleştirir: profilin uygulama seçimini, güvenlik duvarı modunu, uygulama bazlı modları ve *Sistem uygulamalarını göster* ayarını uygular.

| Parametre | Tür | |
|---|---|---|
| `profile` | string | Profil adı, profil panelinde göründüğü şekilde. |
| `profile_id` | string | Dahili kimlik; önce denetlenir ve yeniden adlandırmalardan etkilenmez. Profilin **Otomasyon** iletişim penceresinde hazır komut görüntülenir. |
| `force_enable` | boolean, isteğe bağlı | Değiştirdikten sonra güvenlik duvarını, önceden kapalı olsa bile açar. |

`profile` veya `profile_id` değerlerinden en az biri gereklidir. İkisi de eşleşmezse hiçbir değişiklik yapılmaz ve bir toast ile bildirilir.

```bash
adb shell am broadcast -a shizuwall.PROFILE -n com.arslan.shizuwall/.receivers.ProfileControlReceiver --es profile "Work"
```

## Kaynaktan derleme

Proje, Gradle Wrapper ve Android SDK ile derlenir:

```bash
./gradlew assembleDebug
```

Hata ayıklama derlemesi `.debug` son ekini kullanır ve sürüm derlemesiyle birlikte kurulabilir. Sürüm derlemesi kendi imzalama yapılandırmanızı gerektirir.

## Sorumluluk reddi

AndWall **“olduğu gibi”** sunulur ve hiçbir garanti verilmez. Gelişmiş sistem izinlerine dayanır; geliştirici sistem kararsızlığı, veri kaybı veya ağ engellemenin yol açtığı yan etkilerden sorumlu değildir. Hangi uygulamaları engellediğinizi mutlaka doğrulayın.

Bu fork yalnızca [bu GitHub deposu](https://github.com/hsushjk/AndWall) üzerinden dağıtılır. Başka kaynaklardan gelen APK'lar bu projeyle ilgili değildir ve kullanım riski size aittir. AndWall'un resmi bir web sitesi yoktur ve Google Play veya F-Droid'de yayımlanmamıştır.

`INTERNET` izni yalnızca kablosuz hata ayıklama eşleştirmesi (LibADB) için istenir.

## Lisans

[GPLv3](../LICENSE.md). Paketlenen izleyici imza verileri ayrıca ODbL v1.0 ile lisanslanmıştır, bkz. [TRACKER_DATA_LICENSE.md](../TRACKER_DATA_LICENSE.md).

## Teşekkürler

- [ShizuWall](https://github.com/AhmetCanArslan/ShizuWall): bu fork'un temel aldığı üst proje.
- [Shizuku](https://github.com/RikkaApps/Shizuku): ayrıcalıklı komut yürütme.
- [LibADB](https://github.com/MuntashirAkon/libadb-android): kablosuz hata ayıklama ve arka plan servisi bağlantısı.
- [Exodus Privacy](https://exodus-privacy.eu.org/): izleyici imza veritabanı. AndWall kısaltılmış bir kopyayı içerir ve API'lerine hiçbir zaman bağlanmaz.
