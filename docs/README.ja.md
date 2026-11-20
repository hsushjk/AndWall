<div align="center">
  <a href="https://github.com/hsushjk/AndWall">
    <img src="../app/src/main/res/mipmap-xxxhdpi/ic_launcher_round.png" alt="AndWall Icon" width="72" />
  </a>
  <h1>AndWall</h1>
  <strong>ローカル VPN バックエンドをオプションで備えた Android ファイアウォール。</strong><br/>
  プライバシー第一、完全ローカル。セットアップ不要の内蔵 VPN バックエンド、または上級モード向けの Shizuku / ローカル ADB デーモン / Root で動作します。
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
  <a href="README.hi.md">हिन्दी</a> ·
  <a href="README.zh.md">中文</a> ·
  <b>日本語</b>
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
  <a href="https://github.com/hsushjk/AndWall/issues">質問・不具合報告</a> ·
  <a href="https://github.com/AhmetCanArslan/ShizuWall">アップストリームプロジェクト (ShizuWall)</a>
</p>

## 対応環境

Android 11 以上と、次のいずれかのバックエンド:

- **VPN**: 内蔵のローカル VPN バックエンド。セットアップもコンパニオンアプリも不要です（現在のユーザーのアプリのみ）
- **Shizuku**（フォークにも対応）
- **LibADB**: ワイヤレスデバッグでペアリング、パソコン不要
- **Root**

新規インストールでは **VPN** バックエンドと **ホワイトリスト** ファイアウォールモードが既定となるため、アプリはすぐにそのまま使えます。他のバックエンドは後から **設定 → バックエンドと動作モード** で選択できます。

## AndWall を選ぶ理由

- **VPN モードでは特権セットアップ不要**: システムの VPN 同意を一度許可するだけ。Shizuku も ADB も root も不要です。
- **アプリ単位の制御**: アプリごとに通信をブロックします。chain 3 系のモードでは仕事用プロファイルやクローンのコピーも対象になります。
- **プライベート**: オフラインファースト。解析もテレメトリもなく、リモート VPN サーバーもありません。トンネルはローカルで、すべてのパケットは破棄されます。
- **クイックアクセス**: クイック設定タイル、ウィジェット、フローティングボタン。
- **自動化**: adb、Tasker、MacroDroid などからのブロードキャストで制御できます。

## スクリーンショット

<p align="center">
  <img src="../assets/screenshots/v4.7.1/1.png" alt="AndWall のメイン画面" width="30%" />
  <img src="../assets/screenshots/v4.7.1/4.png" alt="設定" width="30%" />
</p>

## 仕組み

特権コンパニオンを必要とするのは Shizuku、LibADB、Root の各バックエンドだけです。これらは Android の connectivity **chain 3** を操作します。chain 3 はパッケージ単位でネットワークアクセスを制御するプラットフォームの仕組みです:

```bash
cmd connectivity set-chain3-enabled true                                # ファイアウォール ON
cmd connectivity set-package-networking-enabled false <package.name>   # ブロック
cmd connectivity set-package-networking-enabled true <package.name>    # ブロック解除
cmd connectivity set-chain3-enabled false                               # ファイアウォール OFF
```

これらのルールは再起動時に Android によって消去されます。

### VPN バックエンド

VPN バックエンドはローカルの `VpnService` tun デバイスで、ブロック対象として選択したアプリだけを捕捉し、受け取ったすべてのパケットを破棄します。必要なのはシステムの同意ダイアログを 1 回承認することだけで、その他のセットアップは不要です。新規インストールでは既定のバックエンドであり、**ホワイトリスト** ファイアウォールモード（許可するアプリを選び、それ以外はすべてブロック）と組み合わせて使用します。**設定 → バックエンドと動作モード** で変更できます。

再起動後はトンネルが自動的に再起動します。また、ファイアウォールがオンの状態でアプリのプロセスが停止した場合でも、次に AndWall を開いたときに保護が復元されます。端末に画面ロックが設定されていれば、画面ロックモードにも対応します（トンネルがロック状態に追従します）。

VPN バックエンドの制限: 現在のユーザーのアプリのみを扱い（仕事用プロファイルやクローンのコピーは対象外）、モバイルデータのみのルールや前面アプリ検出に基づくモードは利用できません。また、ブロック対象アプリの集合は UID ごとのファイアウォールチェーンを使わずに維持されます。これらの機能が必要な場合は Shizuku、LibADB、Root を引き続き使用してください。

## このフォークでの変更点

AndWall は [ShizuWall](https://github.com/AhmetCanArslan/ShizuWall) のフォークです。このフォークでは次を追加しています:

- **VPN バックエンド**: システムの同意ダイアログの直後から使える、セットアップ不要のファイアウォールバックエンド。
- **新規ユーザー向けの既定値**: VPN バックエンド、ホワイトリスト ファイアウォールモード、ネットワークアクセスのないアプリの非表示。
- **オンボーディングの更新**: ウェルカムフローとモード選択で、4 つのバックエンドすべてに言及。
- **VPN モード向けの UI 調整**: VPN バックエンドで提供できないファイアウォールモードや機能は非表示になり、ファイアウォールモードはホワイトリストにフォールバックします。
- **リブランディング、設定項目と翻訳の更新**、およびこのフォーク向けの詳細な変更履歴とアップデート確認。

## 自動化

どのブロードキャストにも、有効なバックエンドが必要です。`apps` はアプリキーの CSV リストです。パッケージ名だけの場合はユーザー 0 を、`150:com.example.app` のようにプレフィックス付きの場合はその仕事用プロファイルまたはクローン領域のコピーを指します。

### ファイアウォール: `shizuwall.CONTROL`

| エクストラ | 型 | |
|---|---|---|
| `state` | boolean、必須 | `true` = ブロック、`false` = ブロック解除 |
| `apps` | string、任意 | ブロックまたはブロック解除するアプリ。省略するとブロードキャストはファイアウォール全体を切り替えます。 |

```bash
# ファイアウォール ON（state false で OFF）
adb shell am broadcast -a shizuwall.CONTROL -n com.arslan.shizuwall/.receivers.FirewallControlReceiver --ez state true

# アプリとその仕事用プロファイルのコピーをブロック（state false でブロック解除）
adb shell am broadcast -a shizuwall.CONTROL -n com.arslan.shizuwall/.receivers.FirewallControlReceiver --ez state true --es apps "com.example.app,150:com.example.app"
```

`apps` を付けると、アプリ内の選択も追従します。`state true` はアプリを選択し、ファイアウォールがオフだった場合はオンにします。`state false` は選択を解除し、ファイアウォールはオンのままです。ホワイトリストモードでは `apps` は許可リストを指すため、2 つの状態は逆になります。

VPN モードでもブロードキャストは同じように動作しますが、`apps` には現在のユーザーのアプリのみを指定してください。仕事用プロファイルのキーはスキップされます。

### 新しいアプリ: `shizuwall.APP_INSTALLED`

新しくインストールされたアプリを通知し、AndWall 自身がインストールを検出した場合と同じように新規アプリのポリシーと通知を適用させます。AndWall は自身のプロファイル内のインストールしか検出できないため、他のプロファイルはこの方法でカバーします。

```bash
adb shell am broadcast -a shizuwall.APP_INSTALLED -n com.arslan.shizuwall/.receivers.AppInstalledReceiver --es apps "com.example.newapp"
```

ファイアウォールがオフの間はルールは適用されません。`INTERNET` 権限のないアプリは無視されます。

### プロファイル: `shizuwall.PROFILE`

保存済みのプロファイルを適用します。そのアプリの選択、ファイアウォールモード、アプリごとのモード、*システムアプリを表示* の設定を反映します。

| エクストラ | 型 | |
|---|---|---|
| `profile` | string | プロファイル名。プロファイル一覧に表示される名前です。 |
| `profile_id` | string | 内部 ID。先に確認され、名前を変更しても影響を受けません。プロファイルの **自動化** ダイアログにそのまま使えるコマンドが表示されます。 |
| `force_enable` | boolean、任意 | オフだった場合でも、切り替え後にファイアウォールをオンにします。 |

`profile` と `profile_id` のいずれかが必須です。どちらも一致しない場合は何も変更されず、トーストで通知されます。

```bash
adb shell am broadcast -a shizuwall.PROFILE -n com.arslan.shizuwall/.receivers.ProfileControlReceiver --es profile "Work"
```

## ソースからのビルド

このプロジェクトは Gradle ラッパーと Android SDK でビルドします:

```bash
./gradlew assembleDebug
```

デバッグビルドは接尾辞 `.debug` を使用するため、リリースビルドと併存してインストールできます。リリースビルドには独自の署名設定が必要です。

## 免責事項

AndWall は**「現状有姿」**で、いかなる保証もなく提供されます。高度なシステム権限を利用するため、システムの不安定化、データ損失、ネットワーク遮断による副作用について開発者は責任を負いません。ブロックするアプリは必ず確認してください。

このフォークは [この GitHub リポジトリ](https://github.com/hsushjk/AndWall) を通じてのみ配布されています。他の場所から入手した APK は本プロジェクトとは無関係であり、自己責任でご利用ください。AndWall には公式ウェブサイトはなく、Google Play や F-Droid でも公開されていません。

`INTERNET` 権限は、ワイヤレスデバッグのペアリング（LibADB）にのみ要求されます。

## ライセンス

[GPLv3](../LICENSE.md)。同梱のトラッカーシグネチャデータは別途 ODbL v1.0 でライセンスされています。[TRACKER_DATA_LICENSE.md](../TRACKER_DATA_LICENSE.md) を参照してください。

## クレジット

- [ShizuWall](https://github.com/AhmetCanArslan/ShizuWall): このフォークのベースとなったアップストリームプロジェクト。
- [Shizuku](https://github.com/RikkaApps/Shizuku): 特権コマンドの実行。
- [LibADB](https://github.com/MuntashirAkon/libadb-android): ワイヤレスデバッグとデーモン接続。
- [Exodus Privacy](https://exodus-privacy.eu.org/): トラッカーシグネチャデータベース。AndWall は縮小版を同梱しており、彼らの API には一切接続しません。