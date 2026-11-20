<div align="center">
  <a href="https://github.com/hsushjk/AndWall">
    <img src="../app/src/main/res/mipmap-xxxhdpi/ic_launcher_round.png" alt="أيقونة AndWall" width="72" />
  </a>
  <h1>AndWall</h1>
  <strong>جدار حماية أندرويد.</strong><br/>
  مبني على VPN / Shizuku / خدمة ADB محلية / Root
</div>

<p align="center">
  <a href="README.en.md">English</a> ·
  <a href="README.tr.md">Türkçe</a> ·
  <a href="README.de.md">Deutsch</a> ·
  <a href="README.es.md">Español</a> ·
  <a href="README.it.md">Italiano</a> ·
  <a href="README.pt.md">Português</a> ·
  <a href="README.cs.md">Čeština</a> ·
  <a href="README.ru.md">Русский</a> ·
  <b>العربية</b> ·
  <a href="README.hi.md">हिन्दी</a> ·
  <a href="../README.md">中文</a> ·
  <a href="README.ja.md">日本語</a>
</p>
<div style="height: 20px;">&nbsp;</div>
<p align="center">
  <img alt="الحد الأدنى لإصدار SDK" src="https://img.shields.io/badge/Minimum%20SDK-30%20(Android%2011)-839192?style=flat-square&logo=android&logoColor=white" />
</p>

<p align="center">
  <a href="https://github.com/hsushjk/AndWall/issues">المشكلات والملاحظات</a> ·
  <a href="https://github.com/AhmetCanArslan/ShizuWall">المشروع الأصلي (ShizuWall)</a>
</p>

## لماذا وُجد AndWall

[ShizuWall](https://github.com/AhmetCanArslan/ShizuWall) مشروع ممتاز: قوي في وظائفه دون تضخم، وتصميم واجهته جميل ومنطقي. لكنه لا يدعم وضع VPN.

يرى مالك هذا الفرع أن المشروع الجيد ينبغي أن يكون جامعًا متسامحًا. فدمج خلفية VPN يخفض عتبة الاستخدام، لكنه لا يخفض السقف.

## لماذا تختار AndWall

- **أوضاع متعددة للاختيار**: يمكن استخدام وضع VPN الأساسي، أو الأوضاع الأقوى: Shizuku وADB وroot.
- **التحكم حسب التطبيق**: احجب الشبكة حسب التطبيق، وفي الأوضاع المتسلسلة يشمل ذلك ملفات العمل والنسخ المُستنسخة.
- **الخصوصية**: الأولوية للعمل دون اتصال، بلا تحليلات ولا قياس عن بُعد ولا خادم VPN بعيد — فالنفق محلي بالكامل، وكل حزم البيانات تُسقَط.
- **وصول سريع**: مربّع في الإعدادات السريعة، وودجات، وزر عائم.
- **الأتمتة**: التحكم عبر البثوث (broadcasts) التي ترسلها adb وTasker وMacroDroid وغيرها.

## أبرز التغييرات في هذا الفرع

AndWall فرع من [ShizuWall](https://github.com/AhmetCanArslan/ShizuWall). ويضيف هذا الفرع:

- **خلفية VPN**: خلفية جدار حماية بلا إعدادات، تكفي الموافقة على مربّع حوار تفويض النظام لاستخدامها.
- **تكييف الواجهة مع وضع VPN**: إخفاء أوضاع جدار الحماية والميزات التي لا توفرها خلفية VPN، وإرجاع وضع جدار الحماية إلى القائمة البيضاء.
- **إعادة العلامة التجارية وتحديث عناصر الإعدادات والترجمات**، مع سجل تغييرات أكثر تفصيلًا وفحص تحديثات خاص بهذا الفرع.
- **إزالة سياسة الخصوصية**: لا يُنشر هذا المشروع في أي متجر تطبيقات ولا يعالج بيانات شخصية، لذا لا ينشأ عنه التزام بالإفصاح.

## البيئة المتوافقة

يتطلب Android 11 أو أحدث، بالإضافة إلى أي من الخلفيات التالية:

- **VPN**
- **Shizuku** (إصدارات هذا الفرع مدعومة)
- **LibADB**: الاقتران عبر التصحيح اللاسلكي، دون حاجة إلى حاسوب
- **Root**

## لقطات الشاشة

<p align="center">
  <img src="../assets/screenshots/v4.7.1/1.png" alt="الشاشة الرئيسية لتطبيق AndWall" width="30%" />
</p>

## كيف يعمل

تحتاج خلفيات Shizuku وLibADB وRoot وحدها إلى مكوّن مرافق ذي صلاحيات. وهي تتعامل مع **chain 3** في connectivity على Android، أي مع آلية على مستوى المنصّة للتحكم في وصول الشبكة حسب اسم الحزمة:

```bash
cmd connectivity set-chain3-enabled true                                # تشغيل جدار الحماية
cmd connectivity set-package-networking-enabled false <package.name>   # الحجب
cmd connectivity set-package-networking-enabled true <package.name>    # إلغاء الحجب
cmd connectivity set-chain3-enabled false                               # إيقاف جدار الحماية
```

يمسح Android هذه القواعد بعد إعادة تشغيل الجهاز.

### خلفية VPN

خلفية VPN عبارة عن جهاز tun محلي من `VpnService` يلتقط التطبيقات التي تختار حجبها فقط، ويُسقط كل حزم البيانات التي يستقبلها. ولا تحتاج إلى سوى مربّع حوار تفويض واحد من النظام، دون أي إعداد آخر. ويستخدم التثبيت الجديد هذه الخلفية افتراضيًا مع وضع جدار الحماية **القائمة البيضاء** (تختار التطبيقات المسموح لها بالشبكة ويُحجب ما سواها)، ويمكن تغيير ذلك من **الإعدادات → الخلفية ووضع العمل**.

يُعاد تشغيل النفق تلقائيًا بعد إعادة التشغيل؛ وإذا أُوقفت عملية التطبيق أثناء تشغيل جدار الحماية، تُستعاد الحماية عند فتح AndWall في المرة التالية. ويكون وضع شاشة القفل مدعومًا ما دام الجهاز محميًا بقفل شاشة (يتبع النفق حالة شاشة القفل).

قيود خلفية VPN: تتعامل مع تطبيقات المستخدم الحالي فقط (لا تدعم ملفات العمل/النسخ المُستنسخة)، ولا تدعم القواعد الخاصة ببيانات الجوال فقط ولا الأوضاع القائمة على كشف التطبيق في المقدمة، كما أن حفظ مجموعة التطبيقات المحجوبة لا يعتمد على سلاسل جدار الحماية حسب UID. وإن احتجت هذه الميزات فتابع استخدام Shizuku أو LibADB أو Root.

## الأتمتة

يتطلب كل بثّ خلفية نشطة. و`apps` قائمة بمفاتيح التطبيقات مفصولة بفواصل: اسم الحزمة وحده يعني المستخدم 0، أما المسبوق (مثل `150:com.example.app`) فيعني النسخة في ملف العمل أو مساحة الاستنساخ تلك.

### جدار الحماية: `shizuwall.CONTROL`

| المعامل | النوع | |
|---|---|---|
| `state` | boolean، مطلوب | `true` = الحجب، `false` = إلغاء الحجب |
| `apps` | string، اختياري | التطبيقات المراد حجبها أو إلغاء حجبها. عند حذفه، يبدّل البثّ جدار الحماية بالكامل. |

```bash
# تشغيل جدار الحماية (state false تعني الإيقاف)
adb shell am broadcast -a shizuwall.CONTROL -n com.arslan.shizuwall/.receivers.FirewallControlReceiver --ez state true

# حجب تطبيق ونسخه في ملف العمل (state false تعني إلغاء الحجب)
adb shell am broadcast -a shizuwall.CONTROL -n com.arslan.shizuwall/.receivers.FirewallControlReceiver --ez state true --es apps "com.example.app,150:com.example.app"
```

عند تمرير `apps` تتزامن الاختيارات داخل التطبيق: `state true` يحدّد هذه التطبيقات ويشغّل جدار الحماية إن كان متوقفًا؛ و`state false` يلغي تحديدها ويُبقي جدار الحماية مشغّلًا. وفي وضع القائمة البيضاء تشير `apps` إلى قائمة التطبيقات المسموح لها، لذا ينعكس معنى الحالتين.

وفي وضع VPN يعمل البثّ بالطريقة نفسها، لكن ينبغي أن تشير `apps` إلى تطبيقات المستخدم الحالي فقط؛ وتُتجاهل مفاتيح ملفات العمل.

### تطبيق جديد: `shizuwall.APP_INSTALLED`

يُبلّغ عن تطبيق مثبَّت حديثًا، بحيث تعمل سياسة التطبيقات الجديدة والإشعارات كما لو أن AndWall اكتشف التثبيت بنفسه. ولا يرى AndWall إلا التثبيتات في ملف المستخدم الذي يعمل فيه، لذا يلزم تغطية الملفات الأخرى بهذه الطريقة.

```bash
adb shell am broadcast -a shizuwall.APP_INSTALLED -n com.arslan.shizuwall/.receivers.AppInstalledReceiver --es apps "com.example.newapp"
```

لا تُطبَّق أي قواعد عندما يكون جدار الحماية متوقفًا. وتُتجاهل التطبيقات التي تفتقر إلى إذن `INTERNET`.

### ملف التعريف: `shizuwall.PROFILE`

تفعيل ملف تعريف محفوظ: يطبّق اختيارات التطبيقات ووضع جدار الحماية وأوضاع التطبيقات وإعداد *إظهار تطبيقات النظام* الخاصة بملف التعريف.

| المعامل | النوع | |
|---|---|---|
| `profile` | string | اسم ملف التعريف كما يظهر في لوحة ملفات التعريف. |
| `profile_id` | string | المعرّف الداخلي؛ يُفحص أولًا ولا يتأثر بإعادة التسمية. ويعرض مربّع حوار **الأتمتة** لهذا الملف التعريفي أوامر جاهزة. |
| `force_enable` | boolean، اختياري | يشغّل جدار الحماية بعد التبديل حتى لو كان متوقفًا من قبل. |

يجب تقديم `profile` أو `profile_id` على الأقل. وإذا لم يتطابق أي منهما فلن يحدث أي تغيير، مع ظهور تلميح toast.

```bash
adb shell am broadcast -a shizuwall.PROFILE -n com.arslan.shizuwall/.receivers.ProfileControlReceiver --es profile "Work"
```

## البناء من المصدر

يُبنى هذا المشروع باستخدام Gradle Wrapper وAndroid SDK:

```bash
./gradlew assembleDebug
```

يحمل بناء التصحيح لاحقة `.debug` ويمكن تثبيته إلى جانب إصدار الإطلاق. أما بناء الإطلاق فيتطلب إعداد التوقيع الخاص بك.

## إخلاء المسؤولية

يُقدَّم AndWall **"كما هي"** دون أي ضمانات. وهو يعتمد على صلاحيات نظام متقدمة، ولا يتحمل المطوّرون مسؤولية عدم استقرار النظام أو فقدان البيانات أو الآثار الجانبية الناتجة عن حجب الشبكة. تأكد دائمًا من التطبيقات التي تحجبها.

يُوزَّع هذا الفرع فقط عبر [مستودع GitHub هذا](https://github.com/hsushjk/AndWall). وملفات APK القادمة من مصادر أخرى لا علاقة لها بهذا المشروع، واستخدامها على مسؤوليتك. وليس لـ AndWall موقع ويب رسمي، ولن يُنشر على Google Play أو F-Droid.

إذن `INTERNET` مخصص فقط للاقتران عبر التصحيح اللاسلكي (LibADB).

## الرعاية

إن رأيت أن هذا المشروع أفادك، يمكنك وضع نجمة Star على هذا المستودع وعلى المستودع الأصلي. وإن أردت الرعاية، فانقر `Buy me a coffee` في المستودع الأصلي.

## الترخيص

[GPLv3](../LICENSE.md). وبيانات توقيعات المتتبِّعات المرفقة مرخّصة بشكل منفصل بموجب ODbL v1.0، انظر [TRACKER_DATA_LICENSE.md](../TRACKER_DATA_LICENSE.md).

## شكر وتقدير

- [ShizuWall](https://github.com/AhmetCanArslan/ShizuWall): المشروع الأصلي الذي يقوم عليه هذا الفرع.
- [Shizuku](https://github.com/RikkaApps/Shizuku): تنفيذ الأوامر ذات الصلاحيات.
- [LibADB](https://github.com/MuntashirAkon/libadb-android): التصحيح اللاسلكي والاتصال بالخدمة المرافقة.
- [Exodus Privacy](https://exodus-privacy.eu.org/): قاعدة بيانات توقيعات المتتبِّعات. يضم AndWall نسخة مختصرة مدمجة، ولا يصل إلى واجهة API الخاصة به أبدًا.
