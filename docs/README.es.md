<div align="center">
  <a href="https://github.com/hsushjk/AndWall">
    <img src="../app/src/main/res/mipmap-xxxhdpi/ic_launcher_round.png" alt="AndWall Icon" width="72" />
  </a>
  <h1>AndWall</h1>
  <strong>Cortafuegos de Android con un backend VPN local opcional.</strong><br/>
  Privacidad ante todo: todo se ejecuta localmente con un backend VPN integrado que no necesita configuración, o con Shizuku / demonio ADB local / Root para los modos avanzados.
</div>

<p align="center">
  <a href="../README.md">English</a> ·
  <b>Español</b> ·
  <a href="README.tr.md">Türkçe</a> ·
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
    <img alt="Downloads (GitHub)" src="https://img.shields.io/github/downloads/hsushjk/AndWall/total?color=ff9500&label=Total%20Downloads%20(GitHub)&logo=github&logoColor=white&style=flat-square" />
  </a>
  <img alt="Repo size" src="https://img.shields.io/github/repo-size/hsushjk/AndWall?style=flat-square" />
  <img alt="Minimum SDK" src="https://img.shields.io/badge/Minimum%20SDK-30%20(Android%2011)-839192?style=flat-square&logo=android&logoColor=white" />
</p>

<p align="center">
  <a href="https://github.com/hsushjk/AndWall/issues">Preguntas y problemas</a> ·
  <a href="https://github.com/AhmetCanArslan/ShizuWall">Proyecto original (ShizuWall)</a>
</p>

## Compatible con

Android 11 o posterior y uno de estos backends:

- **VPN**: backend VPN local integrado, sin configuración y sin necesidad de una app complementaria (solo las apps del usuario actual)
- **Shizuku** (también se admiten forks)
- **LibADB**: se vincula mediante Depuración inalámbrica, sin necesidad de ordenador
- **Root**

Las instalaciones nuevas empiezan con el backend **VPN** y el modo de firewall **Lista blanca**, así que la app funciona desde el primer momento. Los demás backends se pueden seleccionar más adelante en **Ajustes → Backend y modo de trabajo**.

## Por qué AndWall

- **Sin configuración con privilegios en el modo VPN**: basta con conceder una vez el consentimiento de VPN del sistema; no hace falta Shizuku, ADB ni root.
- **Control por app**: bloquea el acceso a la red de cada app, incluidas las copias del perfil de trabajo y de clones en los modos de cadena 3.
- **Privacidad**: funciona sin conexión, sin analíticas, sin telemetría y sin servidor VPN remoto: el túnel es local y todos los paquetes se descartan.
- **Acceso rápido**: mosaico de Ajustes rápidos, widget y botón flotante.
- **Automatización**: se puede controlar mediante broadcasts desde adb, Tasker, MacroDroid y herramientas similares.

## Captura de pantalla

<p align="center">
  <img src="../assets/screenshots/v4.7.1/1.png" alt="AndWall main screen" width="30%" />
  <img src="../assets/screenshots/v4.7.1/4.png" alt="Ajustes" width="30%" />
</p>

## Cómo funciona

Solo los backends Shizuku, LibADB y Root necesitan un complemento con privilegios. Controlan la **cadena 3** de conectividad de Android, un mecanismo de la plataforma que controla el acceso a la red por paquete:

```bash
cmd connectivity set-chain3-enabled true                                # firewall on
cmd connectivity set-package-networking-enabled false <package.name>   # block
cmd connectivity set-package-networking-enabled true <package.name>    # unblock
cmd connectivity set-chain3-enabled false                               # firewall off
```

Android borra estas reglas al reiniciar.

### Backend VPN

El backend VPN es un dispositivo tun local basado en `VpnService` que captura solo las apps que eliges bloquear y descarta todos los paquetes que recibe. Requiere un único diálogo de consentimiento del sistema y ninguna otra configuración. Es el predeterminado en las instalaciones nuevas, junto con el modo de firewall **Lista blanca** (se eligen las apps permitidas y se bloquea todo lo demás), y se puede cambiar en **Ajustes → Backend y modo de trabajo**.

El túnel se vuelve a iniciar automáticamente tras reiniciar, y si el proceso de la app se detiene mientras el firewall está activado, la protección se restaura la próxima vez que se abra AndWall. El modo Bloqueo de pantalla es compatible (el túnel sigue el estado de bloqueo) siempre que el dispositivo tenga bloqueo de pantalla.

Limitaciones del backend VPN: solo gestiona las apps del usuario actual (no las copias del perfil de trabajo ni de clones), las reglas de solo datos móviles y los modos basados en la detección de primer plano no están disponibles, y el conjunto de apps bloqueadas se mantiene sin cadenas de firewall por uid. Para esas funciones, sigue usando Shizuku, LibADB o Root.

## Cambios en este fork

AndWall es un fork de [ShizuWall](https://github.com/AhmetCanArslan/ShizuWall). Este fork añade:

- **Backend VPN**: un backend de firewall sin configuración que funciona justo después del diálogo de consentimiento del sistema.
- **Valores predeterminados para nuevos usuarios**: backend VPN, modo de firewall Lista blanca y ocultación de las apps sin acceso a la red.
- **Guía de introducción actualizada**: el flujo de bienvenida y la selección de modo ahora mencionan los cuatro backends.
- **Adaptación de la interfaz al modo VPN**: se ocultan los modos y funciones de firewall que el backend VPN no puede ofrecer, y el modo de firewall vuelve a Lista blanca.
- **Nueva imagen de marca, entradas de ajustes y traducciones actualizadas**, además de registros de cambios más amplios y comprobaciones de actualizaciones para este fork.

## Automatización

Cada broadcast necesita un backend activo. `apps` es una lista CSV de claves de app: un nombre de paquete sin prefijo se dirige al usuario 0, y uno con prefijo como `150:com.example.app` se dirige a la copia de ese perfil de trabajo o espacio de clones.

### Firewall: `shizuwall.CONTROL`

| Extra | Tipo | |
|---|---|---|
| `state` | booleano, obligatorio | `true` = bloquear, `false` = desbloquear |
| `apps` | cadena, opcional | Apps que se van a bloquear o desbloquear. Sin este extra, el broadcast alterna el firewall completo. |

```bash
# Firewall on (state false turns it off)
adb shell am broadcast -a shizuwall.CONTROL -n com.arslan.shizuwall/.receivers.FirewallControlReceiver --ez state true

# Block an app and its work-profile copy (state false unblocks them)
adb shell am broadcast -a shizuwall.CONTROL -n com.arslan.shizuwall/.receivers.FirewallControlReceiver --ez state true --es apps "com.example.app,150:com.example.app"
```

Con `apps`, la selección de la app se actualiza: `state true` selecciona las apps y activa el firewall si estaba desactivado; `state false` las deselecciona y deja el firewall activado. En el modo lista blanca, `apps` afecta a la lista de permitidas, por lo que los dos estados se invierten.

En el modo VPN, los broadcasts funcionan igual, pero `apps` solo debe referirse a apps del usuario actual; las claves de perfil de trabajo se omiten.

### App nueva: `shizuwall.APP_INSTALLED`

Notifica una app recién instalada, de modo que la política y la notificación de apps nuevas se le apliquen como si AndWall hubiera detectado la instalación por sí mismo. AndWall solo ve las instalaciones de su propio perfil, así que esta es la forma de cubrir las demás.

```bash
adb shell am broadcast -a shizuwall.APP_INSTALLED -n com.arslan.shizuwall/.receivers.AppInstalledReceiver --es apps "com.example.newapp"
```

No se aplica ninguna regla mientras el firewall está desactivado. Las apps sin el permiso `INTERNET` se ignoran.

### Perfil: `shizuwall.PROFILE`

Activa un perfil guardado: su selección de apps, el modo de firewall, los modos por app y el ajuste *Mostrar aplicaciones del sistema*.

| Extra | Tipo | |
|---|---|---|
| `profile` | cadena | Nombre del perfil, tal como aparece en la hoja de perfiles. |
| `profile_id` | cadena | Id interno; se comprueba primero y no se ve afectado por los cambios de nombre. El diálogo **Automatización** del perfil muestra el comando ya preparado. |
| `force_enable` | booleano, opcional | Activa el firewall después de cambiar, aunque estuviera desactivado. |

Se requiere `profile` o `profile_id`. Si ninguno coincide, no se cambia nada y un aviso (toast) lo indica.

```bash
adb shell am broadcast -a shizuwall.PROFILE -n com.arslan.shizuwall/.receivers.ProfileControlReceiver --es profile "Work"
```

## Compilar desde el código fuente

El proyecto se compila con el wrapper de Gradle y el SDK de Android:

```bash
./gradlew assembleDebug
```

La versión de depuración usa el sufijo `.debug` y se puede instalar junto a la versión de lanzamiento. La versión de lanzamiento requiere una configuración de firma propia.

## Aviso legal

AndWall se proporciona **"tal cual"**, sin garantía. Se basa en permisos avanzados del sistema, y el desarrollador no se hace responsable de la inestabilidad del sistema, la pérdida de datos ni los efectos secundarios del bloqueo de red. Comprueba siempre qué apps bloqueas.

Este fork se distribuye únicamente a través de [este repositorio de GitHub](https://github.com/hsushjk/AndWall). Los APK de cualquier otro origen no están afiliados y se usan bajo tu propia responsabilidad. AndWall no tiene sitio web oficial y no está publicado en Google Play ni en F-Droid.

El permiso `INTERNET` solo se solicita para el emparejamiento de la depuración inalámbrica (LibADB).

## Licencia

[GPLv3](../LICENSE.md). Los datos de firmas de rastreadores incluidos tienen una licencia independiente bajo ODbL v1.0; consulta [TRACKER_DATA_LICENSE.md](../TRACKER_DATA_LICENSE.md).

## Créditos

- [ShizuWall](https://github.com/AhmetCanArslan/ShizuWall): el proyecto original en el que se basa este fork.
- [Shizuku](https://github.com/RikkaApps/Shizuku): ejecución de comandos con privilegios.
- [LibADB](https://github.com/MuntashirAkon/libadb-android): depuración inalámbrica y conexión con el demonio.
- [Exodus Privacy](https://exodus-privacy.eu.org/): base de datos de firmas de rastreadores. AndWall incluye una copia reducida y nunca contacta con su API.
