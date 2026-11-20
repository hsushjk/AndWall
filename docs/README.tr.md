<div align="center">
  <a href="https://github.com/hsushjk/AndWall">
    <img src="../app/src/main/res/mipmap-xxxhdpi/ic_launcher_round.png" alt="AndWall simgesi" width="72" />
  </a>
  <h1>AndWall</h1>
  <strong>İsteğe bağlı yerel VPN arka ucu sunan Android güvenlik duvarı.</strong><br/>
  Gizlilik öncelikli, tamamen yerel; kurulum gerektirmeyen yerleşik bir VPN arka ucuyla veya gelişmiş modlar için Shizuku / yerel ADB arka plan servisi / Root ile çalışır.
</div>

<p align="center">
  <a href="../README.md">English</a> ·
  <b>Türkçe</b> ·
  <a href="README.de.md">Deutsch</a> ·
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
    <img alt="İndirmeler (GitHub)" src="https://img.shields.io/github/downloads/hsushjk/AndWall/total?color=ff9500&label=Total%20Downloads%20(GitHub)&logo=github&logoColor=white&style=flat-square" />
  </a>
  <img alt="Depo boyutu" src="https://img.shields.io/github/repo-size/hsushjk/AndWall?style=flat-square" />
  <img alt="Minimum SDK" src="https://img.shields.io/badge/Minimum%20SDK-30%20(Android%2011)-839192?style=flat-square&logo=android&logoColor=white" />
</p>

<p align="center">
  <a href="https://github.com/hsushjk/AndWall/issues">Sorular ve sorunlar</a> ·
  <a href="https://github.com/AhmetCanArslan/ShizuWall">Üst proje (ShizuWall)</a>
</p>

## Şunlarla çalışır

Android 11+ ve şu arka uçlardan biri:

- **VPN**: yerleşik yerel VPN arka ucu; kurulum ve ek uygulama gerekmez (yalnızca geçerli kullanıcının uygulamaları)
- **Shizuku** (fork'lar desteklenir)
- **LibADB**: Kablosuz Hata Ayıklama üzerinden eşleşir, bilgisayar gerekmez
- **Root**

Yeni kurulumlar **VPN** arka ucu ve **Beyaz Liste** güvenlik duvarı moduyla başlar, böylece uygulama kutudan çıktığı gibi çalışır. Diğer arka uçlar daha sonra **Ayarlar → Arka uç ve çalışma modu** bölümünden seçilebilir.

## Neden AndWall

- **VPN modunda ayrıcalıklı kurulum yok**: sistem VPN onayını bir kez vermeniz yeter — Shizuku, ADB veya root gerekmez.
- **Uygulama bazlı denetim**: ağ erişimini uygulama bazında engeller; chain-3 modlarında iş profili ve klon kopyaları dahil.
- **Gizlilik**: çevrimdışı öncelikli; analitik yok, telemetri yok, uzak VPN sunucusu yok — tünel yereldir ve tüm paketler düşürülür.
- **Hızlı erişim**: Hızlı Ayarlar kutucuğu, widget ve yüzen düğme.
- **Otomasyon**: adb, Tasker, MacroDroid ve benzerlerinden gönderilen yayınlarla (broadcast) kontrol edilebilir.

## Ekran Görüntüsü

<p align="center">
  <img src="../assets/screenshots/v4.7.1/1.png" alt="AndWall ana ekranı" width="30%" />
  <img src="../assets/screenshots/v4.7.1/4.png" alt="Ayarlar" width="30%" />
</p>

## Nasıl Çalışır

Yalnızca Shizuku, LibADB ve Root arka uçları ayrıcalıklı bir yardımcıya ihtiyaç duyar. Bu arka uçlar, ağ erişimini paket bazında denetleyen bir platform mekanizması olan Android'in connectivity **chain 3** zincirini kullanır:

```bash
cmd connectivity set-chain3-enabled true                                # güvenlik duvarı açık
cmd connectivity set-package-networking-enabled false <package.name>   # engelle
cmd connectivity set-package-networking-enabled true <package.name>    # engeli kaldır
cmd connectivity set-chain3-enabled false                               # güvenlik duvarı kapalı
```

Android bu kuralları yeniden başlatmada temizler.

### VPN arka ucu

VPN arka ucu, yalnızca engellemeyi seçtiğiniz uygulamaları yakalayan ve aldığı her paketi düşüren yerel bir `VpnService` tun aygıtıdır. Tek bir sistem onay penceresi gerektirir, başka bir kurulum gerektirmez. Yeni kurulumlar için varsayılandır ve **Beyaz Liste** güvenlik duvarı moduyla birlikte gelir (izin verilen uygulamaları seçin, geri kalan her şey engellenir); **Ayarlar → Arka uç ve çalışma modu** bölümünden değiştirilebilir.

Tünel, yeniden başlatmanın ardından otomatik olarak yeniden başlatılır; güvenlik duvarı açıkken uygulama süreci durdurulursa koruma, AndWall bir sonraki açılışında geri yüklenir. Cihazda ekran kilidi olduğu sürece Ekran Kilidi modu desteklenir (tünel kilit durumunu izler).

VPN arka ucunun sınırlamaları: yalnızca geçerli kullanıcının uygulamalarıyla ilgilenir (iş profili/klon kopyaları yok), yalnızca mobil veri kuralları ve ön plan algılamaya dayalı modlar kullanılamaz ve engellenen uygulama kümesi uid başına güvenlik duvarı zincirleri olmadan yönetilir. Bu özellikler için Shizuku, LibADB veya Root kullanmaya devam edin.

## Bu fork'taki değişiklikler

AndWall, [ShizuWall](https://github.com/AhmetCanArslan/ShizuWall) projesinin bir fork'udur. Bu fork şunları ekler:

- **VPN arka ucu**: sistem onay penceresinden hemen sonra çalışan, kurulum gerektirmeyen bir güvenlik duvarı arka ucu.
- **Yeni kullanıcı varsayılanları**: VPN arka ucu, Beyaz Liste güvenlik duvarı modu ve ağ erişimi olmayan uygulamaların gizlenmesi.
- **Güncellenen karşılama akışı**: karşılama ve mod seçimi ekranları artık dört arka ucun tamamından söz ediyor.
- **VPN modu için arayüz uyarlaması**: VPN arka ucunun sunamadığı güvenlik duvarı modları ve özellikleri gizlenir ve güvenlik duvarı modu Beyaz Liste'ye geri döner.
- **Yeniden markalama, güncellenen ayar girdileri ve çeviriler**, ayrıca bu fork için daha büyük sürüm notları ve güncelleme denetimleri.

## Otomasyon

Her yayın için etkin bir arka uç gerekir. `apps`, virgülle ayrılmış uygulama anahtarları listesidir: yalın bir paket adı kullanıcı 0'ı, `150:com.example.app` gibi ön ekli bir anahtar ise o iş profilindeki veya klon alanındaki kopyayı hedefler.

### Güvenlik duvarı: `shizuwall.CONTROL`

| Ekstra | Tür | |
|---|---|---|
| `state` | boolean, zorunlu | `true` = engelle, `false` = engeli kaldır |
| `apps` | string, isteğe bağlı | Engellenecek veya engeli kaldırılacak uygulamalar. Verilmezse yayın tüm güvenlik duvarını açıp kapatır. |

```bash
# Güvenlik duvarını aç (state false kapatır)
adb shell am broadcast -a shizuwall.CONTROL -n com.arslan.shizuwall/.receivers.FirewallControlReceiver --ez state true

# Bir uygulamayı ve iş profilindeki kopyasını engelle (state false engeli kaldırır)
adb shell am broadcast -a shizuwall.CONTROL -n com.arslan.shizuwall/.receivers.FirewallControlReceiver --ez state true --es apps "com.example.app,150:com.example.app"
```

`apps` verildiğinde uygulamadaki seçim de buna uyar: `state true` uygulamaları seçer ve güvenlik duvarı kapalıysa açar, `state false` seçimi kaldırır ve güvenlik duvarını açık bırakır. Beyaz Liste modunda `apps` izin listesini hedefler, bu yüzden iki durum ters çalışır.

VPN modunda yayınlar aynı şekilde çalışır, ancak `apps` yalnızca geçerli kullanıcının uygulamalarına başvurmalıdır; iş profili anahtarları atlanır.

### Yeni uygulama: `shizuwall.APP_INSTALLED`

Yeni kurulan bir uygulamayı bildirir; böylece yeni uygulama politikası ve bildirimi, sanki AndWall kurulumu kendisi algılamış gibi ona uygulanır. AndWall yalnızca kendi profilindeki kurulumları görür; diğerlerini kapsamanın yolu budur.

```bash
adb shell am broadcast -a shizuwall.APP_INSTALLED -n com.arslan.shizuwall/.receivers.AppInstalledReceiver --es apps "com.example.newapp"
```

Güvenlik duvarı kapalıyken hiçbir kural uygulanmaz. `INTERNET` izni olmayan uygulamalar yok sayılır.

### Profil: `shizuwall.PROFILE`

Kayıtlı bir profili etkinleştirir: uygulama seçimini, güvenlik duvarı modunu, uygulama bazlı modları ve *Sistem uygulamalarını göster* ayarını.

| Ekstra | Tür | |
|---|---|---|
| `profile` | string | Profil adı, profiller sayfasında göründüğü gibi. |
| `profile_id` | string | Dahili kimlik; önce buna bakılır ve yeniden adlandırmalardan etkilenmez. Profilin **Otomasyon** penceresi hazır komutu gösterir. |
| `force_enable` | boolean, isteğe bağlı | Kapalı olsa bile geçişten sonra güvenlik duvarını açar. |

`profile` veya `profile_id` değerlerinden biri zorunludur. İkisi de eşleşmezse hiçbir şey değişmez ve bir toast bunu bildirir.

```bash
adb shell am broadcast -a shizuwall.PROFILE -n com.arslan.shizuwall/.receivers.ProfileControlReceiver --es profile "Work"
```

## Kaynaktan derleme

Proje, Gradle wrapper ve Android SDK ile derlenir:

```bash
./gradlew assembleDebug
```

Hata ayıklama (debug) derlemesi `.debug` son ekini kullanır ve sürüm derlemesinin (release) yanına kurulabilir. Sürüm derlemesi kendi imzalama yapılandırmanızı gerektirir.

## Feragatname

AndWall **"olduğu gibi"**, hiçbir garanti verilmeksizin sunulur. Gelişmiş sistem izinlerine dayanır; geliştirici sistem kararsızlığı, veri kaybı veya engellenen ağ erişiminin yan etkilerinden sorumlu değildir. Hangi uygulamaları engellediğinizi daima doğrulayın.

Bu fork yalnızca [bu GitHub deposu](https://github.com/hsushjk/AndWall) üzerinden dağıtılır. Başka yerlerden gelen APK'lar bu projeyle ilişkili değildir ve kendi riskinizle kullanılır. AndWall'ın resmî bir web sitesi yoktur ve Google Play veya F-Droid'de yayınlanmaz.

`INTERNET` izni yalnızca kablosuz hata ayıklama eşleştirmesi (LibADB) için istenir.

## Lisans

[GPLv3](../LICENSE.md). Paketle gelen izleyici imza verisi ayrıca ODbL v1.0 ile lisanslıdır, bkz. [TRACKER_DATA_LICENSE.md](../TRACKER_DATA_LICENSE.md).

## Teşekkürler

- [ShizuWall](https://github.com/AhmetCanArslan/ShizuWall): bu fork'un temel aldığı üst proje.
- [Shizuku](https://github.com/RikkaApps/Shizuku): ayrıcalıklı komut çalıştırma.
- [LibADB](https://github.com/MuntashirAkon/libadb-android): kablosuz hata ayıklama ve arka plan servisi bağlantısı.
- [Exodus Privacy](https://exodus-privacy.eu.org/): izleyici imza veritabanı. AndWall kırpılmış bir kopyayı paketler ve API'lerine asla bağlanmaz.