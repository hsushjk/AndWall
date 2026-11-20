<div align="center">
  <a href="https://github.com/hsushjk/AndWall">
    <img src="../app/src/main/res/mipmap-xxxhdpi/ic_launcher_round.png" alt="أيقونة AndWall" width="72" />
  </a>
  <h1>AndWall</h1>
  <strong>جدار حماية أندرويد مع خلفية VPN محلية اختيارية.</strong><br/>
  الخصوصية أولاً، يعمل محلياً بالكامل، مدعوم بخلفية VPN مدمجة لا تحتاج إلى أي إعداد — أما الأوضاع المتقدمة فتستخدم Shizuku / خادم ADB المحلي / Root.
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
  <a href="https://tooomm.github.io/github-release-stats/?username=hsushjk&repository=AndWall">
    <img alt="التنزيلات (GitHub)" src="https://img.shields.io/github/downloads/hsushjk/AndWall/total?color=ff9500&label=Total%20Downloads%20(GitHub)&logo=github&logoColor=white&style=flat-square" />
  </a>
  <img alt="حجم المستودع" src="https://img.shields.io/github/repo-size/hsushjk/AndWall?style=flat-square" />
  <img alt="الحد الأدنى لإصدار SDK" src="https://img.shields.io/badge/Minimum%20SDK-30%20(Android%2011)-839192?style=flat-square&logo=android&logoColor=white" />
</p>

<p align="center">
  <a href="https://github.com/hsushjk/AndWall/issues">المشكلات والملاحظات</a> ·
  <a href="https://github.com/AhmetCanArslan/ShizuWall">المشروع الأصلي (ShizuWall)</a>
</p>

## البيئات المتوافقة

يتطلب Android 11 أو أحدث، مع إحدى الخلفيات التالية:

- **VPN**：خلفية VPN محلية مدمجة، لا تحتاج إلى إعداد ولا إلى تطبيق مرافق (تطبيقات المستخدم الحالي فقط)
- **Shizuku**（الإصدارات المتفرعة مدعومة）
- **LibADB**：يتزاوج عبر التصحيح اللاسلكي، دون الحاجة إلى حاسوب
- **Root**

تستخدم عمليات التثبيت الجديدة خلفية **VPN** ووضع جدار الحماية **القائمة البيضاء** افتراضياً، لذا يعمل التطبيق مباشرة بعد التثبيت. يمكن اختيار الخلفيات الأخرى لاحقاً من **الإعدادات → الخلفية ووضع العمل**.

## لماذا AndWall

- **لا حاجة إلى إعدادات بامتيازات في وضع VPN**：يكفي منح موافقة النظام على VPN مرة واحدة — بلا Shizuku، بلا ADB، بلا root.
- **تحكم لكل تطبيق**：يحجب الشبكة لكل تطبيق، بما في ذلك نسخ ملف العمل والنسخ المستنسخة في أوضاع chain-3.
- **الخصوصية**：يعمل دون اتصال أولاً، بلا تحليلات، بلا قياس عن بُعد، بلا خادم VPN بعيد — النفق محلي بالكامل وتُسقط جميع الحزم.
- **وصول سريع**：بلاطة الإعدادات السريعة، والودجت، والزر العائم.
- **الأتمتة**：يمكن التحكم فيه عبر البثوث المرسلة من adb وTasker وMacroDroid وما شابه.

## لقطات الشاشة

<p align="center">
  <img src="../assets/screenshots/v4.7.1/1.png" alt="الشاشة الرئيسية لـ AndWall" width="30%" />
  <img src="../assets/screenshots/v4.7.1/4.png" alt="الإعدادات" width="30%" />
</p>

## كيف يعمل

تحتاج الخلفيات Shizuku وLibADB وRoot فقط إلى مكوّن مرافق ذي امتيازات. وهي تتحكم في **chain 3** الخاصة بـ connectivity في Android، وهي آلية من المنصة تتحكم في الوصول إلى الشبكة حسب اسم الحزمة:

```bash
cmd connectivity set-chain3-enabled true                                # تشغيل جدار الحماية
cmd connectivity set-package-networking-enabled false <package.name>   # حجب
cmd connectivity set-package-networking-enabled true <package.name>    # إلغاء الحجب
cmd connectivity set-chain3-enabled false                               # إيقاف جدار الحماية
```

يمسح Android هذه القواعد بعد إعادة تشغيل الجهاز.

### خلفية VPN

خلفية VPN عبارة عن جهاز tun محلي من `VpnService` يلتقط فقط التطبيقات التي تختار حجبها ويُسقط كل حزمة يستقبلها. لا تحتاج إلا إلى مربع حوار موافقة واحد من النظام ولا تحتاج إلى أي إعداد آخر. وهي الخلفية الافتراضية للتثبيتات الجديدة، إلى جانب وضع جدار الحماية **القائمة البيضاء** (اختر التطبيقات المسموح لها بالشبكة ويُحجب كل ما عداها)، ويمكن تغييره من **الإعدادات → الخلفية ووضع العمل**.

يُعاد تشغيل النفق تلقائياً بعد إعادة التشغيل؛ وإذا أُوقفت عملية التطبيق بينما جدار الحماية مفعّل، تُستعاد الحماية عند فتح AndWall في المرة التالية. وضع قفل الشاشة مدعوم (يتبع النفق حالة القفل) ما دام الجهاز محمياً بقفل شاشة.

قيود خلفية VPN: تتعامل فقط مع تطبيقات المستخدم الحالي (لا نسخ ملف العمل/الاستنساخ)، ولا تتوفر قواعد بيانات الجوال فقط ولا الأوضاع القائمة على اكتشاف التطبيق الأمامي، وتُصان مجموعة التطبيقات المحجوبة دون سلاسل جدار حماية لكل UID. للاستفادة من هذه الميزات، تابع استخدام Shizuku أو LibADB أو Root.

## التغييرات في هذا الفرع

AndWall هو فرع من [ShizuWall](https://github.com/AhmetCanArslan/ShizuWall). ويضيف هذا الفرع:

- **خلفية VPN**：خلفية جدار حماية بلا إعداد تعمل مباشرة بمجرد تأكيد مربع حوار موافقة النظام.
- **الإعدادات الافتراضية للمستخدمين الجدد**：خلفية VPN، ووضع جدار الحماية القائمة البيضاء، وإخفاء التطبيقات التي لا تملك صلاحية الشبكة.
- **تهيئة محدّثة**：يذكر مسار الترحيب واختيار الوضع الآن الخلفيات الأربع جميعها.
- **تكييف الواجهة لوضع VPN**：إخفاء أوضاع جدار الحماية والميزات التي لا تستطيع خلفية VPN توفيرها، مع إرجاع وضع جدار الحماية إلى القائمة البيضاء.
- **إعادة العلامة التجارية وتحديث عناصر الإعدادات والترجمات**، إضافة إلى سجلات تغييرات وعمليات تحقق من التحديثات أكثر تفصيلاً لهذا الفرع.

## الأتمتة

يحتاج كل بث إلى خلفية نشطة. `apps` قائمة مفاتيح تطبيقات مفصولة بفواصل: اسم الحزمة وحده يعني المستخدم 0، أما المسبوق (مثل `150:com.example.app`) فيعين النسخة في ملف العمل أو مساحة الاستنساخ تلك.

### جدار الحماية: `shizuwall.CONTROL`

| المعامل | النوع | |
|---|---|---|
| `state` | boolean، مطلوب | `true` = حجب، `false` = إلغاء الحجب |
| `apps` | string، اختياري | التطبيقات المراد حجبها أو إلغاء حجبها. عند الإغفال، يبدّل البث جدار الحماية بالكامل. |

```bash
# تشغيل جدار الحماية (state false يوقفه)
adb shell am broadcast -a shizuwall.CONTROL -n com.arslan.shizuwall/.receivers.FirewallControlReceiver --ez state true

# حجب تطبيق ونسخته في ملف العمل (state false يلغي حجبهما)
adb shell am broadcast -a shizuwall.CONTROL -n com.arslan.shizuwall/.receivers.FirewallControlReceiver --ez state true --es apps "com.example.app,150:com.example.app"
```

عند تمرير `apps`، يتبع التحديد داخل التطبيق: `state true` يحدد هذه التطبيقات ويشغّل جدار الحماية إن كان متوقفاً؛ `state false` يلغي تحديدها ويُبقي جدار الحماية مشتغلاً. في وضع القائمة البيضاء، يشير `apps` إلى قائمة السماح، لذا ينعكس معنى الحالتين.

في وضع VPN، تعمل البثوث بالطريقة نفسها، لكن `apps` يجب أن تشير إلى تطبيقات المستخدم الحالي فقط؛ تُتجاهل مفاتيح ملفات العمل.

### تطبيق جديد: `shizuwall.APP_INSTALLED`

يبلّغ عن تطبيق مثبّت حديثاً، لكي تُطبَّق سياسة التطبيقات الجديدة والإشعار عليه كما لو اكتشف AndWall التثبيت بنفسه. لا يرى AndWall سوى عمليات التثبيت في ملفه الشخصي، ولهذا تُغطّى الملفات الأخرى بهذه الطريقة.

```bash
adb shell am broadcast -a shizuwall.APP_INSTALLED -n com.arslan.shizuwall/.receivers.AppInstalledReceiver --es apps "com.example.newapp"
```

لا تُطبّق أي قاعدة بينما جدار الحماية متوقف. تُتجاهل التطبيقات التي لا تملك صلاحية `INTERNET`.

### الملف الشخصي: `shizuwall.PROFILE`

يفعّل ملفاً شخصياً محفوظاً: تحديد تطبيقاته، ووضع جدار الحماية، وأوضاع كل تطبيق، وإعداد *إظهار تطبيقات النظام*.

| المعامل | النوع | |
|---|---|---|
| `profile` | string | اسم الملف الشخصي، كما يظهر في لوحة الملفات الشخصية. |
| `profile_id` | string | معرّف داخلي؛ يُفحص أولاً ولا يتأثر بإعادة التسمية. يعرض مربع حوار **الأتمتة** لهذا الملف الشخصي الأمر الجاهز. |
| `force_enable` | boolean، اختياري | تشغيل جدار الحماية بعد التبديل حتى لو كان متوقفاً. |

يجب توفير أحد الحقلين `profile` أو `profile_id` على الأقل. وإذا لم يتطابق أي منهما، فلن يتغيّر شيء ويُبلّغ عن ذلك عبر toast.

```bash
adb shell am broadcast -a shizuwall.PROFILE -n com.arslan.shizuwall/.receivers.ProfileControlReceiver --es profile "Work"
```

## البناء من المصدر

يُبنى المشروع باستخدام Gradle Wrapper وAndroid SDK:

```bash
./gradlew assembleDebug
```

يستخدم بناء التصحيح اللاحقة `.debug` ويمكن تثبيته إلى جانب بناء الإصدار. أما بناء الإصدار فيتطلب إعداد توقيع خاصاً بك.

## إخلاء المسؤولية

يُقدَّم AndWall **"كما هو"** دون أي ضمان. وهو يعتمد على صلاحيات نظام متقدمة، ولا يتحمل المطوّر مسؤولية عدم استقرار النظام أو فقدان البيانات أو الآثار الجانبية الناتجة عن حجب الشبكة. تحقق دائماً من التطبيقات التي تحجبها.

يُوزَّع هذا الفرع فقط عبر [مستودع GitHub هذا](https://github.com/hsushjk/AndWall). ملفات APK من أي مصدر آخر غير مرتبطة بالمشروع، ويُستخدم على مسؤوليتك الخاصة. ليس لـ AndWall موقع رسمي، ولم يُنشر على Google Play أو F-Droid.

تُطلب صلاحية `INTERNET` فقط لإقران التصحيح اللاسلكي (LibADB).

## الترخيص

[GPLv3](../LICENSE.md). بيانات توقيع المتتبعات المضمّنة مرخّصة بشكل منفصل بموجب ODbL v1.0، انظر [TRACKER_DATA_LICENSE.md](../TRACKER_DATA_LICENSE.md).

## شكر وتقدير

- [ShizuWall](https://github.com/AhmetCanArslan/ShizuWall)：المشروع الأصلي الذي يقوم عليه هذا الفرع.
- [Shizuku](https://github.com/RikkaApps/Shizuku)：تنفيذ الأوامر بامتيازات.
- [LibADB](https://github.com/MuntashirAkon/libadb-android)：التصحيح اللاسلكي والاتصال بالخادم.
- [Exodus Privacy](https://exodus-privacy.eu.org/)：قاعدة بيانات توقيعات المتتبعات. يضمّ AndWall نسخة مختصرة ولا يتصل بواجهتها البرمجية أبداً.
