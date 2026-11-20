<div align="center">
  <a href="https://github.com/hsushjk/AndWall">
    <img src="app/src/main/res/mipmap-xxxhdpi/ic_launcher_round.png" alt="AndWall Icon" width="72" />
  </a>
  <h1>AndWall</h1>
  <strong>可选本地 VPN 后端的 Android 防火墙。</strong><br/>
  隐私优先、纯本地运行，由无需任何配置的内置 VPN 后端驱动——高级模式则使用 Shizuku / 本地 ADB 守护进程 / Root。
</div>

<p align="center">
  <a href="docs/README.en.md">English</a> ·
  <a href="docs/README.tr.md">Türkçe</a> ·
  <a href="docs/README.de.md">Deutsch</a> ·
  <a href="docs/README.es.md">Español</a> ·
  <a href="docs/README.it.md">Italiano</a> ·
  <a href="docs/README.pt.md">Português</a> ·
  <a href="docs/README.cs.md">Čeština</a> ·
  <a href="docs/README.ru.md">Русский</a> ·
  <a href="docs/README.ar.md">العربية</a> ·
  <a href="docs/README.hi.md">हिन्दी</a> ·
  <b>中文</b> ·
  <a href="docs/README.ja.md">日本語</a>
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
  <a href="https://github.com/hsushjk/AndWall/issues">问题与反馈</a> ·
  <a href="https://github.com/AhmetCanArslan/ShizuWall">上游项目（ShizuWall）</a>
</p>

## 兼容环境

Android 11 及以上，并需要以下任一后端：

- **VPN**：内置本地 VPN 后端，无需配置，也无需配套应用（仅限当前用户的应用）
- **Shizuku**（支持分支版本）
- **LibADB**：通过无线调试配对，无需电脑
- **Root**

新安装默认使用 **VPN** 后端和 **白名单** 防火墙模式，因此应用开箱即用。其他后端可稍后在 **设置 → 后端与工作模式** 中选择。

## 为什么选择 AndWall

- **VPN 模式无需特权配置**：只需授权一次系统 VPN 即可——无需 Shizuku、无需 ADB、无需 root。
- **按应用控制**：按应用拦截网络，在链式模式中还包括工作资料和分身副本。
- **隐私**：离线优先，无分析、无遥测、无远程 VPN 服务器——隧道完全在本地，所有数据包都会被丢弃。
- **快捷访问**：快速设置磁贴、小部件和悬浮按钮。
- **自动化**：可通过 adb、Tasker、MacroDroid 等发送的广播进行控制。

## 截图

<p align="center">
  <img src="assets/screenshots/v4.7.1/1.png" alt="AndWall 主界面" width="30%" />
  <img src="assets/screenshots/v4.7.1/4.png" alt="设置" width="30%" />
</p>

## 工作原理

只有 Shizuku、LibADB 和 Root 后端需要特权配套组件。它们会操作 Android connectivity 的 **chain 3**，这是一种按包名控制网络访问的平台机制：

```bash
cmd connectivity set-chain3-enabled true                                # 开启防火墙
cmd connectivity set-package-networking-enabled false <package.name>   # 拦截
cmd connectivity set-package-networking-enabled true <package.name>    # 解除拦截
cmd connectivity set-chain3-enabled false                               # 关闭防火墙
```

Android 会在设备重启后清除这些规则。

### VPN 后端

VPN 后端是一个本地 `VpnService` tun 设备，只捕获你选择要拦截的应用，并丢弃它收到的所有数据包。它只需要一次系统授权对话框，无需其他配置。新安装默认使用该后端，并搭配 **白名单** 防火墙模式（选择允许联网的应用，其余全部拦截），可在 **设置 → 后端与工作模式** 中更改。

重启后隧道会自动重新启动；如果在防火墙开启期间应用进程被终止，下次打开 AndWall 时会恢复保护。只要设备设置了屏幕锁，就支持锁屏模式（隧道会跟随锁屏状态）。

VPN 后端的限制：它只处理当前用户的应用（不支持工作资料/分身副本），不支持仅移动数据规则和基于前台检测的模式，并且被拦截应用集合的维护不依赖按 UID 的防火墙链。如需这些功能，请继续使用 Shizuku、LibADB 或 Root。

## 本分支的改动

AndWall 是 [ShizuWall](https://github.com/AhmetCanArslan/ShizuWall) 的分支。本分支新增了：

- **VPN 后端**：免配置的防火墙后端，只需确认系统授权对话框即可使用。
- **新用户默认设置**：VPN 后端、白名单防火墙模式，并隐藏无网络权限的应用。
- **更新的引导流程**：欢迎流程和模式选择现在会提到全部四种后端。
- **VPN 模式的界面适配**：隐藏 VPN 后端无法提供的防火墙模式和功能，并将防火墙模式回退为白名单。
- **重新品牌化、更新设置项和翻译**，以及为本分支提供更详细的更新日志和更新检查。

## 自动化

每条广播都需要一个处于活动状态的后端。`apps` 是以逗号分隔的应用键列表：仅写包名表示用户 0，带前缀的（如 `150:com.example.app`）表示该工作资料或分身空间中的副本。

### 防火墙：`shizuwall.CONTROL`

| 参数 | 类型 | |
|---|---|---|
| `state` | boolean，必填 | `true` = 拦截，`false` = 解除拦截 |
| `apps` | string，可选 | 要拦截或解除拦截的应用。省略时，广播会开关整个防火墙。 |

```bash
# 开启防火墙（state false 表示关闭）
adb shell am broadcast -a shizuwall.CONTROL -n com.arslan.shizuwall/.receivers.FirewallControlReceiver --ez state true

# 拦截某个应用及其工作资料中的副本（state false 表示解除拦截）
adb shell am broadcast -a shizuwall.CONTROL -n com.arslan.shizuwall/.receivers.FirewallControlReceiver --ez state true --es apps "com.example.app,150:com.example.app"
```

带 `apps` 时，应用内的选择会同步：`state true` 会选中这些应用，并在防火墙原本关闭时将其开启；`state false` 会取消选中，并保持防火墙开启。在白名单模式下，`apps` 指向允许列表，因此这两种状态的含义相反。

在 VPN 模式下，广播的工作方式相同，但 `apps` 应只引用当前用户的应用；工作资料的键会被跳过。

### 新应用：`shizuwall.APP_INSTALLED`

上报新安装的应用，使新应用策略和通知能像 AndWall 自己检测到安装一样生效。AndWall 只能看到自身所在用户资料中的安装，因此其他资料需要通过这种方式覆盖。

```bash
adb shell am broadcast -a shizuwall.APP_INSTALLED -n com.arslan.shizuwall/.receivers.AppInstalledReceiver --es apps "com.example.newapp"
```

防火墙关闭时不会应用任何规则。没有 `INTERNET` 权限的应用会被忽略。

### 配置文件：`shizuwall.PROFILE`

激活已保存的配置文件：应用该配置文件的应用选择、防火墙模式、各应用模式和 *显示系统应用* 设置。

| 参数 | 类型 | |
|---|---|---|
| `profile` | string | 配置文件名称，与配置文件面板中显示的名称一致。 |
| `profile_id` | string | 内部 ID；会优先检查，且不受重命名影响。该配置文件的**自动化**对话框中会显示现成的命令。 |
| `force_enable` | boolean，可选 | 切换后开启防火墙，即使此前处于关闭状态。 |

`profile` 与 `profile_id` 至少提供一个。若两者都不匹配，则不会有任何更改，并通过 toast 提示。

```bash
adb shell am broadcast -a shizuwall.PROFILE -n com.arslan.shizuwall/.receivers.ProfileControlReceiver --es profile "Work"
```

## 从源码构建

本项目使用 Gradle Wrapper 和 Android SDK 构建：

```bash
./gradlew assembleDebug
```

调试构建使用 `.debug` 后缀，可与发布版本同时安装。发布构建需要你自己的签名配置。

## 免责声明

AndWall 按**“原样”**提供，不附带任何保证。它依赖高级系统权限，开发者不对系统不稳定、数据丢失或网络拦截造成的副作用负责。请务必确认你拦截了哪些应用。

本分支仅通过[此 GitHub 仓库](https://github.com/hsushjk/AndWall)分发。其他来源的 APK 与本项目无关，使用风险自负。AndWall 没有官方网站，也未在 Google Play 或 F-Droid 上发布。

`INTERNET` 权限仅用于无线调试配对（LibADB）。

## 许可证

[GPLv3](LICENSE.md)。随附的追踪器签名数据单独以 ODbL v1.0 授权，参见 [TRACKER_DATA_LICENSE.md](TRACKER_DATA_LICENSE.md)。

## 致谢

- [ShizuWall](https://github.com/AhmetCanArslan/ShizuWall)：本分支所基于的上游项目。
- [Shizuku](https://github.com/RikkaApps/Shizuku)：特权命令执行。
- [LibADB](https://github.com/MuntashirAkon/libadb-android)：无线调试与守护进程连接。
- [Exodus Privacy](https://exodus-privacy.eu.org/)：追踪器签名数据库。AndWall 内置了精简副本，且从不访问其 API。
