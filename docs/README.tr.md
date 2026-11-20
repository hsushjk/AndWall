<div align="center">
  <a href="https://github.com/hsushjk/AndWall">
    <img src="../app/src/main/res/mipmap-xxxhdpi/ic_launcher_round.png" alt="AndWall Simgesi" width="72" />
  </a>
  <h1>AndWall</h1>
  <strong>Android güvenlik duvarı.</strong><br/>
  VPN / Shizuku / yerel ADB arka plan programı / Root tabanlı
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
  <img alt="Minimum SDK sürümü" src="https://img.shields.io/badge/Minimum%20SDK-30%20(Android%2011)-839192?style=flat-square&logo=android&logoColor=white" />
</p>

<p align="center">
  <a href="https://github.com/hsushjk/AndWall/issues">Sorunlar ve geri bildirim</a> ·
  <a href="https://github.com/AhmetCanArslan/ShizuWall">Üst kaynak proje (ShizuWall)</a>
</p>

## AndWall neden var

[ShizuWall](https://github.com/AhmetCanArslan/ShizuWall) çok iyi bir proje: güçlü ama şişkin değil, arayüz tasarımı da güzel ve tutarlı. Ancak VPN modunu desteklemiyor.

Bu çatalın deposunun sahibi, iyi bir projenin kapsayıcı olması gerektiğini düşünüyor. VPN arka ucunun entegre edilmesi kullanım eşiğini düşürse de üst sınırı düşürmez.

## Neden AndWall

- **Birden fazla mod seçeneği**: En temel VPN kullanılabilir. Daha güçlü Shizuku, ADB ve root modları da kullanılabilir.
- **Uygulama bazında kontrol**: Ağ erişimini uygulama bazında engelleyin; zincir modunda iş profilleri ve klon kopyalar da buna dahildir.
- **Gizlilik**: Çevrimdışı öncelikli; analiz yok, telemetri yok, uzak VPN sunucusu yok — tünel tamamen yereldir ve tüm paketler atılır.
- **Hızlı erişim**: Hızlı ayarlar döşemesi, widget'lar ve yüzen düğme.
- **Otomasyon**: adb, Tasker, MacroDroid vb. tarafından gönderilen yayınlar aracılığıyla kontrol edilebilir.

## Bu çataldaki başlıca değişiklikler

AndWall, [ShizuWall](https://github.com/AhmetCanArslan/ShizuWall) projesinin bir çatalıdır. Bu çatala eklenenler:

- **VPN arka ucu**: Yapılandırma gerektirmeyen güvenlik duvarı arka ucu; kullanmak için yalnızca sistem yetkilendirme iletişim kutusunu onaylamak yeterlidir.
- **VPN modu için arayüz uyarlaması**: VPN arka ucunun sunamadığı güvenlik duvarı modlarını ve özelliklerini gizler, güvenlik duvarı modunu beyaz listeye geri düşürür.
- **Yeniden markalama, ayarların ve çevirilerin güncellenmesi**, ayrıca bu çatal için daha ayrıntılı sürüm notları ve güncelleme denetimi.
- **Gizlilik politikasının kaldırılması**: Bu proje hiçbir uygulama mağazasında yayınlanmaz ve kişisel veri işlemez; bu nedenle herhangi bir bilgilendirme yükümlülüğü doğmaz.

## Uyumlu ortam

Android 11 ve üzeri sürümler; ayrıca aşağıdaki arka uçlardan biri gereklidir:

- **VPN**
- **Shizuku** (çatal sürümleri desteklenir)
- **LibADB**: Bilgisayar gerektirmeden kablosuz hata ayıklama ile eşleştirme
- **Root**

## Ekran görüntüleri

<p align="center">
  <img src="../assets/screenshots/v4.7.1/1.png" alt="AndWall ana arayüzü" width="30%" />
</p>

## Nasıl çalışır

Yalnızca Shizuku, LibADB ve Root arka uçları ayrıcalıklı yardımcı bileşenlere ihtiyaç duyar. Bunlar Android connectivity'nin **chain 3** mekanizmasını kullanır; bu, ağ erişimini paket adına göre kontrol eden bir platform mekanizmasıdır:

```bash
cmd connectivity set-chain3-enabled true                                # güvenlik duvarını aç
cmd connectivity set-package-networking-enabled false <package.name>   # engelle
cmd connectivity set-package-networking-enabled true <package.name>    # engeli kaldır
cmd connectivity set-chain3-enabled false                               # güvenlik duvarını kapat
```

Android bu kuralları cihaz yeniden başlatıldıktan sonra temizler.

### VPN arka ucu

VPN arka ucu, yerel bir `VpnService` tun aygıtıdır; yalnızca engellemeyi seçtiğiniz uygulamaların trafiğini yakalar ve aldığı tüm paketleri atar. Yalnızca bir kez sistem yetkilendirme iletişim kutusu gerektirir, başka bir yapılandırma gerekmez. Yeni kurulumlar bu arka ucu **beyaz liste** güvenlik duvarı moduyla (ağ erişimine izin verilen uygulamaları seçin, geri kalan her şey engellenir) birlikte varsayılan olarak kullanır; bu ayar **Ayarlar → Arka uç ve çalışma modu** bölümünden değiştirilebilir.

Yeniden başlatmadan sonra tünel otomatik olarak yeniden başlar; güvenlik duvarı açıkken uygulama süreci sonlandırılırsa AndWall bir sonraki açılışında korumayı geri yükler. Cihazda ekran kilidi tanımlı olduğu sürece kilit ekranı modu desteklenir (tünel kilit ekranı durumunu izler).

VPN arka ucunun sınırlamaları: yalnızca geçerli kullanıcının uygulamalarını işler (iş profilleri/klon kopyalar desteklenmez), yalnızca mobil veri kurallarını ve ön plan algılamasına dayalı modları desteklemez, ayrıca engellenen uygulama kümesinin bakımı UID bazlı güvenlik duvarı zincirine dayanmaz. Bu özelliklere ihtiyacınız varsa Shizuku, LibADB veya Root kullanmaya devam edin.

## Otomasyon

Her yayın, etkin durumda bir arka uç gerektirir. `apps` virgülle ayrılmış uygulama anahtarları listesidir: yalnızca paket adı yazmak kullanıcı 0 anlamına gelir, önek içerenler (ör. `150:com.example.app`) o iş profilindeki veya klon alanındaki kopyayı belirtir.

### Güvenlik duvarı: `shizuwall.CONTROL`

| Parametre | Tür | |
|---|---|---|
| `state` | boolean, zorunlu | `true` = engelle, `false` = engeli kaldır |
| `apps` | string, isteğe bağlı | Engellenecek veya engeli kaldırılacak uygulamalar. Atlanırsa yayın güvenlik duvarının tamamını açar veya kapatır. |

```bash
# güvenlik duvarını aç (state false kapatmak anlamına gelir)
adb shell am broadcast -a shizuwall.CONTROL -n com.arslan.shizuwall/.receivers.FirewallControlReceiver --ez state true

# bir uygulamayı ve iş profilindeki kopyasını engelle (state false engeli kaldırmak anlamına gelir)
adb shell am broadcast -a shizuwall.CONTROL -n com.arslan.shizuwall/.receivers.FirewallControlReceiver --ez state true --es apps "com.example.app,150:com.example.app"
```

`apps` belirtildiğinde uygulama içindeki seçim de senkronize edilir: `state true` bu uygulamaları seçer ve güvenlik duvarı önceden kapalıysa onu açar; `state false` seçimi kaldırır ve güvenlik duvarını açık bırakır. Beyaz liste modunda `apps` izin verilen listeye işaret eder, bu nedenle bu iki durumun anlamı terstir.

VPN modunda yayınlar aynı şekilde çalışır, ancak `apps` yalnızca geçerli kullanıcının uygulamalarına başvurmalıdır; iş profili anahtarları atlanır.

### Yeni uygulama: `shizuwall.APP_INSTALLED`

Yeni yüklenen uygulamaları bildirir; böylece yeni uygulama ilkesi ve bildirimler, AndWall kurulumu kendisi algılamış gibi devreye girer. AndWall yalnızca kendi bulunduğu kullanıcı profilindeki kurulumları görebildiğinden, diğer profillerin bu yolla kapsanması gerekir.

```bash
adb shell am broadcast -a shizuwall.APP_INSTALLED -n com.arslan.shizuwall/.receivers.AppInstalledReceiver --es apps "com.example.newapp"
```

Güvenlik duvarı kapalıyken hiçbir kural uygulanmaz. `INTERNET` izni olmayan uygulamalar yok sayılır.

### Profil: `shizuwall.PROFILE`

Kaydedilmiş bir profili etkinleştirir: o profilin uygulama seçimini, güvenlik duvarı modunu, uygulama bazında modları ve *sistem uygulamalarını göster* ayarını uygular.

| Parametre | Tür | |
|---|---|---|
| `profile` | string | Profil adı; profil panelinde gösterilen adla aynıdır. |
| `profile_id` | string | Dahili kimlik; önce kontrol edilir ve yeniden adlandırmadan etkilenmez. Bu profilin **Otomasyon** iletişim kutusunda hazır komutlar gösterilir. |
| `force_enable` | boolean, isteğe bağlı | Etkinleştirildiğinde, güvenlik duvarı daha önce kapalı olsa bile onu açar. |

`profile` ve `profile_id` değerlerinden en az biri sağlanmalıdır. İkisi de eşleşmezse hiçbir değişiklik yapılmaz ve bir toast bildirimi gösterilir.

```bash
adb shell am broadcast -a shizuwall.PROFILE -n com.arslan.shizuwall/.receivers.ProfileControlReceiver --es profile "Work"
```

## Kaynaktan derleme

Bu proje Gradle Wrapper ve Android SDK ile derlenir:

```bash
./gradlew assembleDebug
```

Hata ayıklama derlemeleri `.debug` sonekini kullanır ve sürüm derlemeleriyle aynı anda yüklenebilir. Sürüm derlemeleri kendi imzalama yapılandırmanızı gerektirir.

## Sorumluluk reddi

AndWall **“olduğu gibi”** sunulur ve hiçbir garanti verilmez. Üst düzey sistem izinlerine dayanır; geliştirici sistem kararsızlığı, veri kaybı veya ağ engellemesinin yol açtığı yan etkilerden sorumlu değildir. Hangi uygulamaları engellediğinizi mutlaka doğrulayın.

Bu çatal yalnızca [bu GitHub deposu](https://github.com/hsushjk/AndWall) aracılığıyla dağıtılır. Başka kaynaklardan gelen APK'lar bu projeyle ilgili değildir; kullanım riski size aittir. AndWall'un resmî bir web sitesi yoktur ve Google Play veya F-Droid üzerinde yayınlanmaz.

`INTERNET` izni yalnızca kablosuz hata ayıklama eşleştirmesi (LibADB) için kullanılır.

## Sponsorluk

Bu projenin size yardımcı olduğunu düşünüyorsanız, bu depoya ve üst kaynak depoya birer Star verebilirsiniz. Sponsor olmak isterseniz, üst kaynak depoya gidip `Buy me a coffee` düğmesine tıklayın

## Lisans

[GPLv3](../LICENSE.md). Birlikte verilen izleyici imza verileri ayrı olarak ODbL v1.0 ile lisanslanmıştır; bkz. [TRACKER_DATA_LICENSE.md](../TRACKER_DATA_LICENSE.md).

## Teşekkür

- [ShizuWall](https://github.com/AhmetCanArslan/ShizuWall): Bu çatalın dayandığı üst kaynak proje.
- [Shizuku](https://github.com/RikkaApps/Shizuku): Ayrıcalıklı komut yürütme.
- [LibADB](https://github.com/MuntashirAkon/libadb-android): Kablosuz hata ayıklama ve arka plan programı bağlantısı.
- [Exodus Privacy](https://exodus-privacy.eu.org/): İzleyici imza veritabanı. AndWall, sadeleştirilmiş bir kopyayı içerir ve API'sine hiçbir zaman erişmez.
