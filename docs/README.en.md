<div align="center">
  <a href="https://github.com/hsushjk/AndWall">
    <img src="../app/src/main/res/mipmap-xxxhdpi/ic_launcher_round.png" alt="AndWall Icon" width="72" />
  </a>
  <h1>AndWall</h1>
  <strong>Android firewall.</strong><br/>
  Based on VPN / Shizuku / local ADB daemon / Root
</div>

<p align="center">
  <b>English</b> ·
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
  <a href="README.ja.md">日本語</a>
</p>
<div style="height: 20px;">&nbsp;</div>
<p align="center">
  <img alt="Minimum SDK" src="https://img.shields.io/badge/Minimum%20SDK-30%20(Android%2011)-839192?style=flat-square&logo=android&logoColor=white" />
</p>

<p align="center">
  <a href="https://github.com/hsushjk/AndWall/issues">Questions &amp; feedback</a> ·
  <a href="https://github.com/AhmetCanArslan/ShizuWall">Upstream project (ShizuWall)</a>
</p>

## Why AndWall exists

[ShizuWall](https://github.com/AhmetCanArslan/ShizuWall) is a very good project: powerful without being bloated, with a beautiful and sensible UI design. But it does not support VPN mode.

The owner of this fork believes that a good project should be inclusive. Adding a VPN backend lowers the barrier to entry, but does not lower the ceiling.

## Why choose AndWall

- **Multiple modes**: you can use the most basic VPN, or the more powerful Shizuku, ADB and root modes.
- **Per-app control**: block networking per app, including work profiles and clone copies in chain modes.
- **Privacy**: offline-first, no analytics, no telemetry, no remote VPN server — the tunnel is completely local and every packet is dropped.
- **Quick access**: Quick Settings tile, widget and floating button.
- **Automation**: controllable through broadcasts sent by adb, Tasker, MacroDroid and similar.

## Main changes in this fork

AndWall is a fork of [ShizuWall](https://github.com/AhmetCanArslan/ShizuWall). This fork adds:

- **VPN backend**: a configuration-free firewall backend; just confirm the system consent dialog to use it.
- **UI adaptation for VPN mode**: hides the firewall modes and features that the VPN backend cannot provide, and falls the firewall mode back to Whitelist.
- **Rebranding, updated settings entries and translations**, plus more detailed changelogs and update checks for this fork.
- **Removed privacy policy**: this project is not published on any app store and does not process personal data, so there is no obligation to inform.

## Compatibility

Android 11 and above, with any one of the following backends:

- **VPN**
- **Shizuku** (fork builds are supported)
- **LibADB**: pairs over wireless debugging, no computer needed
- **Root**

## Screenshots

<p align="center">
  <img src="../assets/screenshots/v4.7.1/1.png" alt="AndWall main screen" width="30%" />
</p>

## How It Works

Only the Shizuku, LibADB and Root backends need a privileged companion. They operate Android connectivity's **chain 3**, a platform mechanism that controls network access per package:

```bash
cmd connectivity set-chain3-enabled true                                # enable the firewall
cmd connectivity set-package-networking-enabled false <package.name>   # block
cmd connectivity set-package-networking-enabled true <package.name>    # unblock
cmd connectivity set-chain3-enabled false                               # disable the firewall
```

Android clears these rules after a device reboot.

### VPN backend

The VPN backend is a local `VpnService` tun device that captures only the apps you choose to block and drops every packet it receives. It requires a single system consent dialog and no other configuration. New installs use this backend by default, paired with **Whitelist** firewall mode (choose the apps allowed to use the network; everything else is blocked), which can be changed in **Settings → Backend & working mode**.

The tunnel restarts automatically after a reboot; if the app process is terminated while the firewall is on, protection is restored the next time AndWall is opened. Screen Lock mode is supported (the tunnel follows the lock state) as long as the device has a screen lock set.

Limitations of the VPN backend: it only handles apps of the current user (no work-profile/clone copies), it does not support mobile-data-only rules or foreground-detection-based modes, and the blocked-app set is maintained without per-UID firewall chains. For these features, keep using Shizuku, LibADB or Root.

## Automation

Every broadcast requires an active backend. `apps` is a comma-separated list of app keys: a bare package name means user 0, and a prefixed one (such as `150:com.example.app`) means the copy in that work profile or clone space.

### Firewall: `shizuwall.CONTROL`

| Extra | Type | |
|---|---|---|
| `state` | boolean, required | `true` = block, `false` = unblock |
| `apps` | string, optional | Apps to block or unblock. When omitted, the broadcast toggles the whole firewall. |

```bash
# Turn the firewall on (state false turns it off)
adb shell am broadcast -a shizuwall.CONTROL -n com.arslan.shizuwall/.receivers.FirewallControlReceiver --ez state true

# Block an app and its copy in the work profile (state false unblocks it)
adb shell am broadcast -a shizuwall.CONTROL -n com.arslan.shizuwall/.receivers.FirewallControlReceiver --ez state true --es apps "com.example.app,150:com.example.app"
```

With `apps`, the selection in the app stays in sync: `state true` selects those apps and turns the firewall on if it was off; `state false` deselects them and keeps the firewall on. In whitelist mode, `apps` targets the allow list, so the two states mean the opposite.

In VPN mode, broadcasts work the same way, but `apps` should reference apps of the current user only; work-profile keys are skipped.

### New app: `shizuwall.APP_INSTALLED`

Reports a newly installed app so that the new-app policy and notification take effect as if AndWall had detected the install itself. AndWall can only see installs in its own user profile, so installs in other profiles must be covered this way.

```bash
adb shell am broadcast -a shizuwall.APP_INSTALLED -n com.arslan.shizuwall/.receivers.AppInstalledReceiver --es apps "com.example.newapp"
```

No rules are applied while the firewall is off. Apps without the `INTERNET` permission are ignored.

### Profile: `shizuwall.PROFILE`

Activates a saved profile: applies the profile's app selection, firewall mode, per-app modes and the *Show system apps* setting.

| Extra | Type | |
|---|---|---|
| `profile` | string | Profile name, matching the name shown in the profiles panel. |
| `profile_id` | string | Internal ID; checked first and unaffected by renames. The profile's **Automation** dialog shows a ready-made command. |
| `force_enable` | boolean, optional | Turns the firewall on after switching, even if it was off. |

Provide at least one of `profile` or `profile_id`. If neither matches, nothing changes and a toast reports it.

```bash
adb shell am broadcast -a shizuwall.PROFILE -n com.arslan.shizuwall/.receivers.ProfileControlReceiver --es profile "Work"
```

## Build from source

The project builds with the Gradle wrapper and the Android SDK:

```bash
./gradlew assembleDebug
```

The debug build uses the `.debug` suffix and can be installed alongside the release build. The release build requires your own signing config.

## Disclaimer

AndWall is provided **"as is"** with no warranty. It relies on advanced system permissions, and the developer is not responsible for system instability, data loss or side effects caused by blocked networking. Always verify which apps you block.

This fork is distributed only through [this GitHub repository](https://github.com/hsushjk/AndWall). APKs from other sources are unrelated to this project and used at your own risk. AndWall has no official website and is not published on Google Play or F-Droid.

The `INTERNET` permission is used only for wireless debugging pairing (LibADB).

## Sponsorship

If you find this project helpful, you can give both this repository and the upstream repository a Star. To sponsor the project, click `Buy me a coffee` in the upstream repository

## License

[GPLv3](../LICENSE.md). The bundled tracker signature data is licensed separately under ODbL v1.0, see [TRACKER_DATA_LICENSE.md](../TRACKER_DATA_LICENSE.md).

## Credits

- [ShizuWall](https://github.com/AhmetCanArslan/ShizuWall): the upstream project this fork is based on.
- [Shizuku](https://github.com/RikkaApps/Shizuku): privileged command execution.
- [LibADB](https://github.com/MuntashirAkon/libadb-android): wireless debugging and daemon connection.
- [Exodus Privacy](https://exodus-privacy.eu.org/): tracker signature database. AndWall bundles a trimmed copy and never accesses their API.
