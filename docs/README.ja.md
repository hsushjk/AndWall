<div align="center">
  <a href="https://github.com/hsushjk/AndWall">
    <img src="../app/src/main/res/mipmap-xxxhdpi/ic_launcher_round.png" alt="AndWall アイコン" width="72" />
  </a>
  <h1>AndWall</h1>
  <strong>ローカル VPN バックエンドを選択できる Android ファイアウォール。</strong><br/>
  プライバシー優先で完全にローカル動作し、設定不要の内蔵 VPN バックエンドによって駆動されます。高度なモードでは Shizuku / ローカル ADB デーモン / Root を使用します。
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
  <a href="https://tooomm.github.io/github-release-stats/?username=hsushjk&repository=AndWall">
    <img alt="ダウンロード数 (GitHub)" src="https://img.shields.io/github/downloads/hsushjk/AndWall/total?color=ff9500&label=Total%20Downloads%20(GitHub)&logo=github&logoColor=white&style=flat-square" />
  </a>
  <img alt="リポジトリサイズ" src="https://img.shields.io/github/repo-size/hsushjk/AndWall?style=flat-square" />
  <img alt="最小 SDK" src="https://img.shields.io/badge/Minimum%20SDK-30%20(Android%2011)-839192?style=flat-square&logo=android&logoColor=white" />
</p>

<p align="center">
  <a href="https://github.com/hsushjk/AndWall/issues">問題報告・フィードバック</a> ·
  <a href="https://github.com/AhmetCanArslan/ShizuWall">上流プロジェクト（ShizuWall）</a>
</p>

## 対応環境

Android 11 以降で、次のいずれかのバックエンドが必要です：

- **VPN**：設定不要の内蔵ローカル VPN バックエンド。専用アプリも必要ありません（現在のユーザーのアプリのみ）。
- **Shizuku**（フォーク版で対応）
- **LibADB**：ワイヤレスデバッグでペアリングでき、PC は不要です
- **Root**

新規インストールでは既定で **VPN** バックエンドと **ホワイトリスト** ファイアウォールモードが使用されるため、すぐに使い始められます。他のバックエンドは後から **設定 → バックエンドと動作モード** で選択できます。

## AndWall を選ぶ理由

- **VPN モードは特権設定が不要**：システム VPN を一度許可するだけです。Shizuku も ADB も root も不要です。
- **アプリ単位の制御**：アプリごとにネットワークをブロックでき、チェーンモードではワークプロファイルやクローンも対象になります。
- **プライバシー**：オフライン優先で、解析もテレメトリもリモート VPN サーバーもありません。トンネルは完全にローカルで、すべてのパケットが破棄されます。
- **すばやいアクセス**：クイック設定タイル、ウィジェット、フローティングボタン。
- **自動化**：adb、Tasker、MacroDroid などから送信したブロードキャストで制御できます。

## スクリーンショット

<p align="center">
  <img src="../assets/screenshots/v4.7.1/1.png" alt="AndWall メイン画面" width="30%" />
  <img src="../assets/screenshots/v4.7.1/4.png" alt="設定" width="30%" />
</p>

## 仕組み

特権コンパニオンコンポーネントが必要なのは Shizuku、LibADB、Root の各バックエンドだけです。これらは Android connectivity の **chain 3** を操作します。これはパッケージ名単位でネットワークアクセスを制御するプラットフォームの仕組みです：

```bash
cmd connectivity set-chain3-enabled true                                # ファイアウォールを有効化
cmd connectivity set-package-networking-enabled false <package.name>   # ブロック
cmd connectivity set-package-networking-enabled true <package.name>    # ブロックを解除
cmd connectivity set-chain3-enabled false                               # ファイアウォールを無効化
```

Android はデバイスの再起動時にこれらのルールをクリアします。

### VPN バックエンド

VPN バックエンドはローカルの `VpnService` tun デバイスで、ブロック対象として選択したアプリだけを捕捉し、受け取ったすべてのパケットを破棄します。必要なのはシステムの許可ダイアログを一度承認することだけで、その他の設定は不要です。新規インストールではこのバックエンドが既定で使用され、**ホワイトリスト** ファイアウォールモード（ネットワークを許可するアプリを選び、それ以外はすべてブロック）と組み合わせられます。**設定 → バックエンドと動作モード** で変更できます。

再起動後はトンネルが自動的に再起動します。ファイアウォールの有効中にアプリのプロセスが終了した場合でも、次に AndWall を開いたときに保護が復元されます。デバイスに画面ロックが設定されていれば、ロック画面モードにも対応します（トンネルは画面ロックの状態に追従します）。

VPN バックエンドの制限：現在のユーザーのアプリのみを扱います（ワークプロファイル/クローンには非対応）。モバイルデータのみのルールやフォアグラウンド検出に基づくモードには対応しておらず、ブロック対象アプリ集合の管理は UID 単位のファイアウォールチェーンに依存しません。これらの機能が必要な場合は、引き続き Shizuku、LibADB、Root のいずれかを使用してください。

## このフォークでの変更

AndWall は [ShizuWall](https://github.com/AhmetCanArslan/ShizuWall) のフォークです。このフォークでは次を追加しました：

- **VPN バックエンド**：設定不要のファイアウォールバックエンド。システムの許可ダイアログを承認するだけで使用できます。
- **新規ユーザー向けの既定設定**：VPN バックエンド、ホワイトリストファイアウォールモード、ネットワーク権限のないアプリの非表示。
- **更新されたオンボーディングフロー**：ウェルカムフローとモード選択で、4 種類すべてのバックエンドに言及するようになりました。
- **VPN モード向けの UI 調整**：VPN バックエンドで提供できないファイアウォールモードと機能を非表示にし、ファイアウォールモードはホワイトリストにフォールバックします。
- **リブランディング、設定項目と翻訳の更新**、およびこのフォーク向けのより詳細な変更履歴とアップデートチェック。

## 自動化

どのブロードキャストも、有効なバックエンドが必要です。`apps` はカンマ区切りのアプリキーのリストです。パッケージ名だけを書くとユーザー 0 を意味し、接頭辞付き（例：`150:com.example.app`）はそのワークプロファイルまたはクローン空間内の複製を意味します。

### ファイアウォール：`shizuwall.CONTROL`

| パラメータ | 型 | |
|---|---|---|
| `state` | boolean、必須 | `true` = ブロック、`false` = ブロック解除 |
| `apps` | string、任意 | ブロックまたはブロック解除するアプリ。省略すると、ブロードキャストはファイアウォール全体を切り替えます。 |

```bash
# ファイアウォールを有効化（state false は無効化）
adb shell am broadcast -a shizuwall.CONTROL -n com.arslan.shizuwall/.receivers.FirewallControlReceiver --ez state true

# アプリとそのワークプロファイル内の複製をブロック（state false はブロック解除）
adb shell am broadcast -a shizuwall.CONTROL -n com.arslan.shizuwall/.receivers.FirewallControlReceiver --ez state true --es apps "com.example.app,150:com.example.app"
```

`apps` を指定すると、アプリ内の選択が同期されます。`state true` はこれらのアプリを選択し、ファイアウォールがオフだった場合はオンにします。`state false` は選択を解除し、ファイアウォールはオンのままにします。ホワイトリストモードでは `apps` は許可リストを指すため、この 2 つの状態の意味は逆になります。

VPN モードでもブロードキャストの動作は同じですが、`apps` は現在のユーザーのアプリのみを指定してください。ワークプロファイルのキーはスキップされます。

### 新しいアプリ：`shizuwall.APP_INSTALLED`

新しくインストールされたアプリを通知し、AndWall 自身がインストールを検出した場合と同じように、新規アプリのポリシーと通知を機能させます。AndWall は自身が属するユーザープロファイル内のインストールしか認識できないため、他のプロファイルはこの方法でカバーする必要があります。

```bash
adb shell am broadcast -a shizuwall.APP_INSTALLED -n com.arslan.shizuwall/.receivers.AppInstalledReceiver --es apps "com.example.newapp"
```

ファイアウォールがオフのときはルールが適用されません。`INTERNET` 権限のないアプリは無視されます。

### プロファイル：`shizuwall.PROFILE`

保存済みのプロファイルを有効化します。そのプロファイルのアプリ選択、ファイアウォールモード、アプリごとのモード、*システムアプリを表示* 設定を適用します。

| パラメータ | 型 | |
|---|---|---|
| `profile` | string | プロファイル名。プロファイルパネルに表示される名前と一致します。 |
| `profile_id` | string | 内部 ID。先にチェックされ、名前の変更の影響を受けません。このプロファイルの**自動化**ダイアログに、すぐに使えるコマンドが表示されます。 |
| `force_enable` | boolean、任意 | 有効にすると、以前オフだった場合でもファイアウォールをオンにします。 |

`profile` と `profile_id` の少なくとも一方を指定してください。どちらも一致しない場合は何も変更されず、トーストで通知されます。

```bash
adb shell am broadcast -a shizuwall.PROFILE -n com.arslan.shizuwall/.receivers.ProfileControlReceiver --es profile "Work"
```

## ソースからビルド

このプロジェクトは Gradle Wrapper と Android SDK でビルドします：

```bash
./gradlew assembleDebug
```

デバッグビルドは `.debug` サフィックスを使用し、リリースビルドと同時にインストールできます。リリースビルドには独自の署名設定が必要です。

## 免責事項

AndWall は**「現状のまま」**提供され、いかなる保証も伴いません。高度なシステム権限に依存するため、開発者はシステムの不安定化、データ損失、ネットワークブロックによる副作用について責任を負いません。どのアプリをブロックしているかは必ず確認してください。

このフォークは[この GitHub リポジトリ](https://github.com/hsushjk/AndWall)でのみ配布されています。他の配布元の APK は本プロジェクトとは無関係であり、使用は自己責任となります。AndWall には公式サイトがなく、Google Play や F-Droid でも公開されていません。

`INTERNET` 権限はワイヤレスデバッグのペアリング（LibADB）にのみ使用されます。

## ライセンス

[GPLv3](../LICENSE.md)。付属のトラッカー署名データは別途 ODbL v1.0 でライセンスされており、[TRACKER_DATA_LICENSE.md](../TRACKER_DATA_LICENSE.md) を参照してください。

## 謝辞

- [ShizuWall](https://github.com/AhmetCanArslan/ShizuWall)：このフォークの基になった上流プロジェクト。
- [Shizuku](https://github.com/RikkaApps/Shizuku)：特権コマンドの実行。
- [LibADB](https://github.com/MuntashirAkon/libadb-android)：ワイヤレスデバッグとデーモン接続。
- [Exodus Privacy](https://exodus-privacy.eu.org/)：トラッカー署名データベース。AndWall は軽量なコピーを内蔵しており、その API には一切アクセスしません。
