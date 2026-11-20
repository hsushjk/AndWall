<div align="center">
  <a href="https://github.com/hsushjk/AndWall">
    <img src="../app/src/main/res/mipmap-xxxhdpi/ic_launcher_round.png" alt="AndWall Icon" width="72" />
  </a>
  <h1>AndWall</h1>
  <strong>Android ファイアウォール。</strong><br/>
  VPN / Shizuku / ローカル ADB デーモン / Root をベースとしています
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
  <a href="README.ar.md">العربية</a> ·
  <a href="README.hi.md">हिन्दी</a> ·
  <a href="../README.md">中文</a> ·
  <b>日本語</b>
</p>
<div style="height: 20px;">&nbsp;</div>
<p align="center">
  <img alt="Minimum SDK" src="https://img.shields.io/badge/Minimum%20SDK-30%20(Android%2011)-839192?style=flat-square&logo=android&logoColor=white" />
</p>

<p align="center">
  <a href="https://github.com/hsushjk/AndWall/issues">問題とフィードバック</a> ·
  <a href="https://github.com/AhmetCanArslan/ShizuWall">上流プロジェクト（ShizuWall）</a>
</p>

## なぜ AndWall が生まれたのか

[ShizuWall](https://github.com/AhmetCanArslan/ShizuWall) は非常に優れたプロジェクトで、機能は強力でありながら肥大化せず、UI デザインも美しく合理的です。しかし、VPN モードには対応していません。

このフォークリポジトリの所有者は、優れたプロジェクトは幅広く取り込むべきだと考えています。VPN バックエンドの統合は利用のハードルを下げますが、上限を引き下げることにはなりません。

## なぜ AndWall を選ぶのか

- **複数モードを選択可能**：最も基本的な VPN を利用できます。より強力な Shizuku、ADB、root モードも利用できます。
- **アプリ単位の制御**：アプリごとにネットワークをブロックし、チェーンモードでは仕事用プロファイルとクローンも対象になります。
- **プライバシー**：オフライン優先で、解析もテレメトリもリモート VPN サーバーもありません。トンネルは完全にローカルで、すべてのパケットが破棄されます。
- **クイックアクセス**：クイック設定タイル、ウィジェット、フローティングボタン。
- **自動化**：adb、Tasker、MacroDroid などから送信されるブロードキャストで制御できます。

## このブランチでの主な変更

AndWall は [ShizuWall](https://github.com/AhmetCanArslan/ShizuWall) のフォークです。このブランチでは以下を追加しました：

- **VPN バックエンド**：設定不要のファイアウォールバックエンドで、システムの認可ダイアログを確認するだけで使用できます。
- **VPN モード向けの UI 対応**：VPN バックエンドでは提供できないファイアウォールモードと機能を非表示にし、ファイアウォールモードをホワイトリストにフォールバックします。
- **リブランディング、設定項目と翻訳の更新**、およびこのブランチ向けのより詳細な変更履歴とアップデート確認。
- **プライバシーポリシーの削除**：本プロジェクトはどのアプリストアにも公開しておらず、個人データを処理しないため、告知義務は生じません。

## 対応環境

Android 11 以上が必要で、以下のいずれかのバックエンドが必要です：

- **VPN**
- **Shizuku**（フォーク版をサポート）
- **LibADB**：ワイヤレスデバッグでペア設定でき、PC は不要です
- **Root**

## スクリーンショット

<p align="center">
  <img src="../assets/screenshots/v4.7.1/1.png" alt="AndWall メイン画面" width="30%" />
</p>

## 動作の仕組み

Shizuku、LibADB、Root の各バックエンドのみ、特権付きのコンパニオンコンポーネントが必要です。これらは Android connectivity の **chain 3** を操作します。これはパッケージ名単位でネットワークアクセスを制御するプラットフォームの仕組みです：

```bash
cmd connectivity set-chain3-enabled true                                # ファイアウォールを有効化
cmd connectivity set-package-networking-enabled false <package.name>   # ブロック
cmd connectivity set-package-networking-enabled true <package.name>    # ブロックを解除
cmd connectivity set-chain3-enabled false                               # ファイアウォールを無効化
```

Android はデバイスの再起動後にこれらのルールを消去します。

### VPN バックエンド

VPN バックエンドはローカルの `VpnService` tun デバイスで、ブロック対象として選択したアプリのみを捕捉し、受信したすべてのパケットを破棄します。必要なのはシステムの認可ダイアログ 1 回だけで、その他の設定は不要です。新規インストールではこのバックエンドが既定で使用され、**ホワイトリスト** ファイアウォールモード（ネットワークを許可するアプリを選択し、それ以外はすべてブロック）と組み合わせられます。**設定 → バックエンドと動作モード** で変更できます。

再起動後、トンネルは自動的に再起動します。ファイアウォールが有効な間にアプリのプロセスが終了した場合でも、次に AndWall を開いたときに保護が復元されます。デバイスに画面ロックが設定されていれば、ロック画面モードにも対応します（トンネルはロック画面の状態に追従します）。

VPN バックエンドの制限：現在のユーザーのアプリのみを扱い（仕事用プロファイルやクローンには非対応）、モバイルデータのみのルールやフォアグラウンド検出に基づくモードにも対応していません。また、ブロック対象アプリ集合の管理は UID 単位のファイアウォールチェーンに依存しません。これらの機能が必要な場合は、引き続き Shizuku、LibADB、または Root を使用してください。

## 自動化

各ブロードキャストには、アクティブな状態のバックエンドが必要です。`apps` はカンマ区切りのアプリキー一覧です：パッケージ名のみの場合はユーザー 0 を意味し、プレフィックス付き（例：`150:com.example.app`）はその仕事用プロファイルまたはクローン空間内のコピーを意味します。

### ファイアウォール：`shizuwall.CONTROL`

| パラメータ | 型 | |
|---|---|---|
| `state` | boolean、必須 | `true` = ブロック、`false` = ブロック解除 |
| `apps` | string、任意 | ブロックまたはブロック解除するアプリ。省略した場合、ブロードキャストはファイアウォール全体をオン/オフします。 |

```bash
# ファイアウォールを有効化（state false は無効化を意味します）
adb shell am broadcast -a shizuwall.CONTROL -n com.arslan.shizuwall/.receivers.FirewallControlReceiver --ez state true

# あるアプリとその仕事用プロファイル内のコピーをブロック（state false はブロック解除を意味します）
adb shell am broadcast -a shizuwall.CONTROL -n com.arslan.shizuwall/.receivers.FirewallControlReceiver --ez state true --es apps "com.example.app,150:com.example.app"
```

`apps` を指定すると、アプリ内の選択状態も同期します：`state true` はこれらのアプリを選択し、ファイアウォールが元々無効だった場合は有効化します。`state false` は選択を解除し、ファイアウォールは有効なままにします。ホワイトリストモードでは `apps` は許可リストを指すため、この 2 つの状態の意味は逆になります。

VPN モードでもブロードキャストは同じように動作しますが、`apps` は現在のユーザーのアプリのみを参照する必要があります。仕事用プロファイルのキーはスキップされます。

### 新しいアプリ：`shizuwall.APP_INSTALLED`

新しくインストールされたアプリを通知し、AndWall 自身がインストールを検出した場合と同じように、新規アプリのポリシーと通知を有効にします。AndWall は自身が存在するユーザープロファイル内のインストールのみを認識できるため、他のプロファイルはこの方法で補う必要があります。

```bash
adb shell am broadcast -a shizuwall.APP_INSTALLED -n com.arslan.shizuwall/.receivers.AppInstalledReceiver --es apps "com.example.newapp"
```

ファイアウォールが無効なときは、どのルールも適用されません。`INTERNET` 権限を持たないアプリは無視されます。

### プロファイル：`shizuwall.PROFILE`

保存済みのプロファイルを有効化します：そのプロファイルのアプリ選択、ファイアウォールモード、アプリごとのモード、および *システムアプリを表示* 設定を適用します。

| パラメータ | 型 | |
|---|---|---|
| `profile` | string | プロファイル名。プロファイルパネルに表示される名前と一致します。 |
| `profile_id` | string | 内部 ID。優先的に確認され、名前変更の影響を受けません。そのプロファイルの**自動化**ダイアログに、すぐに使えるコマンドが表示されます。 |
| `force_enable` | boolean、任意 | 切り替えの後にファイアウォールを有効化します（以前に無効だった場合でも）。 |

`profile` と `profile_id` の少なくとも一方を指定してください。どちらも一致しない場合は何も変更されず、トーストで通知されます。

```bash
adb shell am broadcast -a shizuwall.PROFILE -n com.arslan.shizuwall/.receivers.ProfileControlReceiver --es profile "Work"
```

## ソースからビルド

本プロジェクトは Gradle Wrapper と Android SDK を使用してビルドします：

```bash
./gradlew assembleDebug
```

デバッグビルドは `.debug` サフィックスを使用し、リリースビルドと同時にインストールできます。リリースビルドには独自の署名設定が必要です。

## 免責事項

AndWall は**「現状のまま」**提供され、いかなる保証も伴いません。高度なシステム権限に依存しているため、開発者はシステムの不安定化、データの損失、ネットワークのブロックによる副作用について責任を負いません。どのアプリをブロックしたかを必ず確認してください。

このブランチは[この GitHub リポジトリ](https://github.com/hsushjk/AndWall)でのみ配布されます。他の配布元の APK は本プロジェクトとは無関係であり、使用は自己責任でお願いします。AndWall には公式サイトがなく、Google Play や F-Droid でも公開されません。

`INTERNET` 権限はワイヤレスデバッグのペア設定（LibADB）にのみ使用されます。

## スポンサー

このプロジェクトがお役に立ったと感じられたら、本リポジトリと上流リポジトリのそれぞれに Star を付けていただけます。スポンサーをご希望の場合は、上流リポジトリで`Buy me a coffee`をクリックしてください。

## ライセンス

[GPLv3](../LICENSE.md)。同梱のトラッカー署名データは別途 ODbL v1.0 でライセンスされており、[TRACKER_DATA_LICENSE.md](../TRACKER_DATA_LICENSE.md) を参照してください。

## 謝辞

- [ShizuWall](https://github.com/AhmetCanArslan/ShizuWall)：このブランチの基になった上流プロジェクト。
- [Shizuku](https://github.com/RikkaApps/Shizuku)：特権コマンドの実行。
- [LibADB](https://github.com/MuntashirAkon/libadb-android)：ワイヤレスデバッグとデーモン接続。
- [Exodus Privacy](https://exodus-privacy.eu.org/)：トラッカー署名データベース。AndWall には簡素化したコピーが組み込まれており、その API には決してアクセスしません。
