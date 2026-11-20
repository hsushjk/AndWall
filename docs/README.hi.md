<div align="center">
  <a href="https://github.com/hsushjk/AndWall">
    <img src="../app/src/main/res/mipmap-xxxhdpi/ic_launcher_round.png" alt="AndWall Icon" width="72" />
  </a>
  <h1>AndWall</h1>
  <strong>वैकल्पिक स्थानीय VPN बैकएंड वाला Android फ़ायरवॉल।</strong><br/>
  गोपनीयता सर्वोपरि, पूरी तरह स्थानीय — बिना किसी सेटअप वाले अंतर्निहित VPN बैकएंड से संचालित, या उन्नत मोड के लिए Shizuku / स्थानीय ADB डेमॉन / रूट से।
</div>

<p align="center">
  <a href="../README.md">English</a> ·
  <a href="README.tr.md">Türkçe</a> ·
  <a href="README.de.md">Deutsch</a> ·
  <a href="README.it.md">Italiano</a> ·
  <a href="README.pt.md">Português</a> ·
  <a href="README.cs.md">Čeština</a> ·
  <a href="README.ru.md">Русский</a> ·
  <a href="README.ar.md">العربية</a> ·
  <b>हिन्दी</b> ·
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
  <a href="https://github.com/hsushjk/AndWall/issues">प्रश्न और समस्याएँ</a> ·
  <a href="https://github.com/AhmetCanArslan/ShizuWall">अपस्ट्रीम परियोजना (ShizuWall)</a>
</p>

## इनके साथ काम करता है

Android 11+ और इनमें से कोई एक बैकएंड:

- **VPN**: अंतर्निहित स्थानीय VPN बैकएंड, कोई सेटअप और कोई साथी ऐप आवश्यक नहीं (केवल वर्तमान उपयोगकर्ता के ऐप्स)
- **Shizuku** (फ़ोर्क समर्थित हैं)
- **LibADB**: वायरलेस डीबगिंग से पेयर होता है, कंप्यूटर की ज़रूरत नहीं
- **Root**

नई इंस्टॉल **VPN** बैकएंड और **व्हाइटलिस्ट** फ़ायरवॉल मोड के साथ शुरू होती हैं, इसलिए ऐप बिना किसी अतिरिक्त सेटअप के काम करता है। बाकी बैकएंड बाद में **सेटिंग्स → बैकएंड और कार्य मोड** में चुने जा सकते हैं।

## AndWall क्यों

- **VPN मोड में कोई विशेषाधिकार प्राप्त सेटअप नहीं**: बस एक बार सिस्टम की VPN सहमति दें — न Shizuku, न ADB, न रूट।
- **प्रति-ऐप नियंत्रण**: चेन-3 मोड में वर्क प्रोफ़ाइल और क्लोन प्रतियों सहित, हर ऐप के लिए नेटवर्किंग ब्लॉक करता है।
- **निजी**: ऑफ़लाइन-फ़र्स्ट, कोई एनालिटिक्स नहीं, कोई टेलीमेट्री नहीं, कोई दूरस्थ VPN सर्वर नहीं — टनल स्थानीय है और सभी पैकेट ड्रॉप कर दिए जाते हैं।
- **त्वरित पहुँच**: क्विक सेटिंग्स टाइल, विजेट और फ़्लोटिंग बटन।
- **ऑटोमेशन**: adb, Tasker, MacroDroid और इसी तरह के ऐप्स से भेजे गए ब्रॉडकास्ट से नियंत्रित किया जा सकता है।

## स्क्रीनशॉट

<p align="center">
  <img src="../assets/screenshots/v4.7.1/1.png" alt="AndWall main screen" width="30%" />
  <img src="../assets/screenshots/v4.7.1/4.png" alt="सेटिंग्स" width="30%" />
</p>

## यह कैसे काम करता है

केवल Shizuku, LibADB और Root बैकएंड को एक विशेषाधिकार प्राप्त साथी की ज़रूरत होती है। ये Android की connectivity **chain 3** चलाते हैं — एक प्लेटफ़ॉर्म मैकेनिज़्म जो प्रति-पैकेज नेटवर्क एक्सेस नियंत्रित करता है:

```bash
cmd connectivity set-chain3-enabled true                                # firewall on
cmd connectivity set-package-networking-enabled false <package.name>   # block
cmd connectivity set-package-networking-enabled true <package.name>    # unblock
cmd connectivity set-chain3-enabled false                               # firewall off
```

Android रीबूट पर ये नियम हटा देता है।

### VPN बैकएंड

VPN बैकएंड एक स्थानीय `VpnService` टन डिवाइस है जो केवल उन्हीं ऐप्स को पकड़ता है जिन्हें आप ब्लॉक करना चुनते हैं और अपने पास आने वाला हर पैकेट ड्रॉप कर देता है। इसके लिए केवल एक सिस्टम सहमति संवाद चाहिए, कोई अन्य सेटअप नहीं। यह नई इंस्टॉल के लिए डिफ़ॉल्ट है और **व्हाइटलिस्ट** फ़ायरवॉल मोड के साथ आता है (जिन ऐप्स को अनुमति देनी है उन्हें चुनें, बाकी सब ब्लॉक रहते हैं), और इसे **सेटिंग्स → बैकएंड और कार्य मोड** में बदला जा सकता है।

रीबूट के बाद टनल स्वतः फिर से शुरू हो जाती है, और यदि फ़ायरवॉल चालू रहते हुए ऐप प्रोसेस रुक जाए, तो अगली बार AndWall खोलने पर सुरक्षा बहाल हो जाती है। जब तक डिवाइस पर स्क्रीन लॉक सेट है, स्क्रीन लॉक मोड समर्थित है (टनल लॉक स्थिति का अनुसरण करती है)।

VPN बैकएंड की सीमाएँ: यह केवल वर्तमान उपयोगकर्ता के ऐप्स संभालता है (वर्क प्रोफ़ाइल/क्लोन प्रतियाँ नहीं), केवल-मोबाइल-डेटा नियम और फ़ोरग्राउंड-पहचान आधारित मोड उपलब्ध नहीं हैं, और ब्लॉक किए गए ऐप्स का समूह प्रति-uid फ़ायरवॉल चेन के बिना बनाए रखा जाता है। इन सुविधाओं के लिए Shizuku, LibADB या Root का उपयोग जारी रखें।

## इस फ़ोर्क में बदलाव

AndWall, [ShizuWall](https://github.com/AhmetCanArslan/ShizuWall) का फ़ोर्क है। यह फ़ोर्क इन्हें जोड़ता है:

- **VPN बैकएंड**: सेटअप-मुक्त फ़ायरवॉल बैकएंड जो सिस्टम सहमति संवाद के तुरंत बाद काम करता है।
- **नए उपयोगकर्ता के लिए डिफ़ॉल्ट**: VPN बैकएंड, व्हाइटलिस्ट फ़ायरवॉल मोड और नेटवर्क एक्सेस के बिना ऐप्स को छिपाना।
- **अद्यतन ऑनबोर्डिंग**: स्वागत फ़्लो और मोड चयन अब सभी चार बैकएंड का उल्लेख करते हैं।
- **VPN मोड के लिए UI अनुकूलन**: जो फ़ायरवॉल मोड और सुविधाएँ VPN बैकएंड प्रदान नहीं कर सकता, उन्हें छिपा दिया जाता है, और फ़ायरवॉल मोड व्हाइटलिस्ट पर वापस आ जाता है।
- **रीब्रांडिंग, अद्यतन सेटिंग प्रविष्टियाँ और अनुवाद**, साथ ही इस फ़ोर्क के लिए बड़े चेंजलॉग और अपडेट जाँच।

## ऑटोमेशन

हर ब्रॉडकास्ट के लिए एक सक्रिय बैकएंड चाहिए। `apps` ऐप कुंजियों की CSV सूची है: सादा पैकेज नाम यूज़र 0 को लक्षित करता है, और `150:com.example.app` जैसी प्रीफ़िक्स वाली कुंजी उस वर्क प्रोफ़ाइल या क्लोन स्पेस की प्रति को लक्षित करती है।

### फ़ायरवॉल: `shizuwall.CONTROL`

| एक्स्ट्रा | प्रकार | |
|---|---|---|
| `state` | boolean, आवश्यक | `true` = ब्लॉक, `false` = अनब्लॉक |
| `apps` | string, वैकल्पिक | ब्लॉक या अनब्लॉक किए जाने वाले ऐप्स। इसके बिना ब्रॉडकास्ट पूरा फ़ायरवॉल चालू/बंद करता है। |

```bash
# Firewall on (state false turns it off)
adb shell am broadcast -a shizuwall.CONTROL -n com.arslan.shizuwall/.receivers.FirewallControlReceiver --ez state true

# Block an app and its work-profile copy (state false unblocks them)
adb shell am broadcast -a shizuwall.CONTROL -n com.arslan.shizuwall/.receivers.FirewallControlReceiver --ez state true --es apps "com.example.app,150:com.example.app"
```

`apps` के साथ ऐप के अंदर का चयन भी उसी के अनुसार बदलता है: `state true` ऐप्स को चुनता है और फ़ायरवॉल बंद हो तो उसे चालू करता है; `state false` उन्हें चयन से हटाता है और फ़ायरवॉल चालू रहने देता है। व्हाइटलिस्ट मोड में `apps` अनुमति सूची को संबोधित करता है, इसलिए दोनों स्थितियाँ उलट जाती हैं।

VPN मोड में ब्रॉडकास्ट उसी तरह काम करते हैं, लेकिन `apps` में केवल वर्तमान उपयोगकर्ता के ऐप्स होने चाहिए; वर्क प्रोफ़ाइल की कुंजियाँ छोड़ दी जाती हैं।

### नया ऐप: `shizuwall.APP_INSTALLED`

नए इंस्टॉल हुए ऐप की सूचना देता है, ताकि नए ऐप की नीति और सूचना उस पर लागू हो जाएँ, जैसे AndWall ने वह इंस्टॉल खुद देखा हो। AndWall केवल अपनी प्रोफ़ाइल के इंस्टॉल देखता है; बाकी प्रोफ़ाइलों को कवर करने का यही तरीका है।

```bash
adb shell am broadcast -a shizuwall.APP_INSTALLED -n com.arslan.shizuwall/.receivers.AppInstalledReceiver --es apps "com.example.newapp"
```

फ़ायरवॉल बंद होने पर कोई नियम लागू नहीं होता। `INTERNET` अनुमति के बिना वाले ऐप्स को अनदेखा किया जाता है।

### प्रोफ़ाइल: `shizuwall.PROFILE`

सहेजी गई प्रोफ़ाइल सक्रिय करता है: उसका ऐप चयन, फ़ायरवॉल मोड, प्रति-ऐप मोड और *सिस्टम ऐप्स दिखाएँ* सेटिंग।

| एक्स्ट्रा | प्रकार | |
|---|---|---|
| `profile` | string | प्रोफ़ाइल का नाम, जैसा प्रोफ़ाइल शीट में दिखता है। |
| `profile_id` | string | आंतरिक id; पहले इसे जाँचा जाता है और नाम बदलने से इस पर असर नहीं पड़ता। प्रोफ़ाइल का **ऑटोमेशन** संवाद तैयार कमांड दिखाता है। |
| `force_enable` | boolean, वैकल्पिक | स्विच के बाद फ़ायरवॉल चालू करें, भले ही वह बंद था। |

`profile` या `profile_id` में से एक आवश्यक है। यदि दोनों में से कोई भी मेल न खाए तो कुछ नहीं बदलता और एक टोस्ट इसकी सूचना देता है।

```bash
adb shell am broadcast -a shizuwall.PROFILE -n com.arslan.shizuwall/.receivers.ProfileControlReceiver --es profile "Work"
```

## स्रोत से बिल्ड करें

यह परियोजना Gradle wrapper और Android SDK के साथ बिल्ड होती है:

```bash
./gradlew assembleDebug
```

डीबग बिल्ड `.debug` सफ़िक्स का उपयोग करती है और रिलीज़ बिल्ड के साथ-साथ इंस्टॉल की जा सकती है। रिलीज़ बिल्ड के लिए आपका अपना साइनिंग कॉन्फ़िगरेशन आवश्यक है।

## अस्वीकरण

AndWall **"जैसा है"** के रूप में, बिना किसी वारंटी के उपलब्ध कराया जाता है। यह उन्नत सिस्टम अनुमतियों पर निर्भर है, और सिस्टम अस्थिरता, डेटा हानि या नेटवर्क ब्लॉक करने के दुष्प्रभावों के लिए डेवलपर ज़िम्मेदार नहीं है। हमेशा जाँच लें कि आप किन ऐप्स को ब्लॉक कर रहे हैं।

यह फ़ोर्क केवल [इस GitHub रिपॉज़िटरी](https://github.com/hsushjk/AndWall) के ज़रिए वितरित किया जाता है। कहीं और से मिले APK इस परियोजना से संबद्ध नहीं हैं और उनका उपयोग आपके अपने जोखिम पर है। AndWall की कोई आधिकारिक वेबसाइट नहीं है और यह Google Play या F-Droid पर प्रकाशित नहीं है।

`INTERNET` अनुमति केवल वायरलेस डीबगिंग पेयरिंग (LibADB) के लिए माँगी जाती है।

## लाइसेंस

[GPLv3](../LICENSE.md)। साथ में दिया गया ट्रैकर सिग्नेचर डेटा अलग से ODbL v1.0 के तहत लाइसेंस प्राप्त है, देखें [TRACKER_DATA_LICENSE.md](../TRACKER_DATA_LICENSE.md)।

## आभार

- [ShizuWall](https://github.com/AhmetCanArslan/ShizuWall): वह अपस्ट्रीम परियोजना जिस पर यह फ़ोर्क आधारित है।
- [Shizuku](https://github.com/RikkaApps/Shizuku): विशेषाधिकार प्राप्त कमांड निष्पादन।
- [LibADB](https://github.com/MuntashirAkon/libadb-android): वायरलेस डीबगिंग और डेमॉन कनेक्शन।
- [Exodus Privacy](https://exodus-privacy.eu.org/): ट्रैकर सिग्नेचर डेटाबेस। AndWall इसकी छोटी की गई प्रतिलिपि साथ रखता है और उनके API से कभी संपर्क नहीं करता।