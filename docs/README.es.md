<div align="center">
  <a href="https://github.com/hsushjk/AndWall">
    <img src="../app/src/main/res/mipmap-xxxhdpi/ic_launcher_round.png" alt="Icono de AndWall" width="72" />
  </a>
  <h1>AndWall</h1>
  <strong>Cortafuegos de Android con backend VPN local opcional.</strong><br/>
  Privacidad ante todo y ejecución totalmente local, impulsado por un backend VPN integrado que no necesita ninguna configuración; los modos avanzados usan Shizuku / el demonio ADB local / Root.
</div>

<p align="center">
  <a href="README.en.md">English</a> ·
  <a href="README.tr.md">Türkçe</a> ·
  <a href="README.de.md">Deutsch</a> ·
  <b>Español</b> ·
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
  <a href="https://tooomm.github.io/github-release-stats/?username=hsushjk&repository=AndWall">
    <img alt="Descargas (GitHub)" src="https://img.shields.io/github/downloads/hsushjk/AndWall/total?color=ff9500&label=Total%20Downloads%20(GitHub)&logo=github&logoColor=white&style=flat-square" />
  </a>
  <img alt="Tamaño del repositorio" src="https://img.shields.io/github/repo-size/hsushjk/AndWall?style=flat-square" />
  <img alt="SDK mínimo" src="https://img.shields.io/badge/Minimum%20SDK-30%20(Android%2011)-839192?style=flat-square&logo=android&logoColor=white" />
</p>

<p align="center">
  <a href="https://github.com/hsushjk/AndWall/issues">Preguntas y comentarios</a> ·
  <a href="https://github.com/AhmetCanArslan/ShizuWall">Proyecto original (ShizuWall)</a>
</p>

## Compatibilidad

Android 11 o superior y uno de estos backends:

- **VPN**: backend VPN local integrado, sin configuración y sin necesidad de una app complementaria (solo las apps del usuario actual)
- **Shizuku** (también se admiten los forks)
- **LibADB**: emparejamiento mediante depuración inalámbrica, sin necesidad de ordenador
- **Root**

Las instalaciones nuevas usan de forma predeterminada el backend **VPN** y el modo de firewall **Lista blanca**, así que la app funciona nada más instalarla. Los demás backends se pueden elegir más adelante en **Ajustes → Backend y modo de trabajo**.

## Por qué elegir AndWall

- **El modo VPN no necesita configuración con privilegios**: basta con conceder una vez el consentimiento de VPN del sistema; no hace falta Shizuku, ni ADB, ni root.
- **Control por app**: bloquea la red por app y, en los modos de cadena, también en el perfil de trabajo y en las copias clonadas.
- **Privacidad**: funciona primero sin conexión, sin analíticas, sin telemetría y sin servidores VPN remotos: el túnel es totalmente local y todos los paquetes se descartan.
- **Acceso rápido**: mosaico de Ajustes rápidos, widget y botón flotante.
- **Automatización**: se puede controlar mediante broadcasts enviados desde adb, Tasker, MacroDroid y herramientas similares.

## Capturas de pantalla

<p align="center">
  <img src="../assets/screenshots/v4.7.1/1.png" alt="Pantalla principal de AndWall" width="30%" />
  <img src="../assets/screenshots/v4.7.1/4.png" alt="Ajustes" width="30%" />
</p>

## Cómo funciona

Solo los backends Shizuku, LibADB y Root necesitan un componente complementario con privilegios. Controlan la **cadena 3** de la conectividad de Android, un mecanismo de la plataforma que controla el acceso a la red por nombre de paquete:

```bash
cmd connectivity set-chain3-enabled true                                # activar el firewall
cmd connectivity set-package-networking-enabled false <package.name>   # bloquear
cmd connectivity set-package-networking-enabled true <package.name>    # desbloquear
cmd connectivity set-chain3-enabled false                               # desactivar el firewall
```

Android borra estas reglas al reiniciar el dispositivo.

### Backend VPN

El backend VPN es un dispositivo tun local de `VpnService` que solo captura las apps que eliges bloquear y descarta todos los paquetes que recibe. Solo requiere un diálogo de consentimiento del sistema y ninguna otra configuración. Las instalaciones nuevas lo usan de forma predeterminada, junto con el modo de firewall **Lista blanca** (se eligen las apps a las que se permite conectarse y se bloquea el resto), y se puede cambiar en **Ajustes → Backend y modo de trabajo**.

El túnel se reinicia automáticamente tras reiniciar el dispositivo; si el proceso de la app se detiene mientras el firewall está activado, la protección se restaura la próxima vez que se abre AndWall. El modo Bloqueo de pantalla es compatible (el túnel sigue el estado de bloqueo) siempre que el dispositivo tenga bloqueo de pantalla.

Limitaciones del backend VPN: solo gestiona las apps del usuario actual (no admite el perfil de trabajo ni las copias clonadas), no admite las reglas exclusivas para datos móviles ni los modos basados en la detección de primer plano, y el mantenimiento del conjunto de apps bloqueadas no depende de cadenas de firewall por UID. Si necesitas estas funciones, sigue usando Shizuku, LibADB o Root.

## Cambios en este fork

AndWall es un fork de [ShizuWall](https://github.com/AhmetCanArslan/ShizuWall). Este fork añade:

- **Backend VPN**: un backend de firewall sin configuración que funciona con solo confirmar el diálogo de consentimiento del sistema.
- **Valores predeterminados para nuevos usuarios**: backend VPN, modo de firewall Lista blanca y ocultación de las apps sin permiso de red.
- **Guía de introducción actualizada**: el flujo de bienvenida y la selección de modo ahora mencionan los cuatro backends.
- **Adaptación de la interfaz al modo VPN**: se ocultan los modos y funciones de firewall que el backend VPN no puede ofrecer, y el modo de firewall vuelve a Lista blanca.
- **Nueva imagen de marca, entradas de ajustes y traducciones actualizadas**, además de un registro de cambios más detallado y comprobaciones de actualizaciones para este fork.

## Automatización

Cada broadcast necesita un backend activo. `apps` es una lista de claves de app separadas por comas: un nombre de paquete sin prefijo se refiere al usuario 0, y uno con prefijo (como `150:com.example.app`) se refiere a la copia de ese perfil de trabajo o espacio de clones.

### Firewall: `shizuwall.CONTROL`

| Extra | Tipo | |
|---|---|---|
| `state` | booleano, obligatorio | `true` = bloquear, `false` = desbloquear |
| `apps` | cadena, opcional | Apps que se van a bloquear o desbloquear. Si se omite, el broadcast activa o desactiva todo el firewall. |

```bash
# Activar el firewall (state false lo desactiva)
adb shell am broadcast -a shizuwall.CONTROL -n com.arslan.shizuwall/.receivers.FirewallControlReceiver --ez state true

# Bloquear una app y sus copias en el perfil de trabajo (state false las desbloquea)
adb shell am broadcast -a shizuwall.CONTROL -n com.arslan.shizuwall/.receivers.FirewallControlReceiver --ez state true --es apps "com.example.app,150:com.example.app"
```

Con `apps`, la selección dentro de la app se sincroniza: `state true` marca esas apps y activa el firewall si estaba desactivado; `state false` las desmarca y mantiene el firewall activado. En el modo Lista blanca, `apps` apunta a la lista de permitidas, así que el significado de los dos estados se invierte.

En el modo VPN, los broadcasts funcionan igual, pero `apps` solo debe hacer referencia a apps del usuario actual; las claves de perfil de trabajo se omiten.

### App nueva: `shizuwall.APP_INSTALLED`

Notifica una app recién instalada para que la política y la notificación de apps nuevas se apliquen como si AndWall hubiera detectado la instalación por sí mismo. AndWall solo ve las instalaciones de su propio perfil, así que esta es la forma de cubrir los demás perfiles.

```bash
adb shell am broadcast -a shizuwall.APP_INSTALLED -n com.arslan.shizuwall/.receivers.AppInstalledReceiver --es apps "com.example.newapp"
```

No se aplica ninguna regla mientras el firewall está desactivado. Las apps sin el permiso `INTERNET` se ignoran.

### Perfil: `shizuwall.PROFILE`

Activa un perfil guardado: aplica la selección de apps de ese perfil, el modo de firewall, los modos por app y el ajuste *Mostrar aplicaciones del sistema*.

| Extra | Tipo | |
|---|---|---|
| `profile` | cadena | Nombre del perfil, igual que el que aparece en el panel de perfiles. |
| `profile_id` | cadena | Id interno; se comprueba primero y no le afectan los cambios de nombre. El diálogo **Automatización** de ese perfil muestra el comando listo para usar. |
| `force_enable` | booleano, opcional | Activa el firewall tras el cambio, aunque estuviera desactivado. |

Debes proporcionar `profile` o `profile_id`. Si ninguno coincide, no se aplica ningún cambio y se avisa con un toast.

```bash
adb shell am broadcast -a shizuwall.PROFILE -n com.arslan.shizuwall/.receivers.ProfileControlReceiver --es profile "Work"
```

## Compilar desde el código fuente

El proyecto se compila con el wrapper de Gradle y el SDK de Android:

```bash
./gradlew assembleDebug
```

La compilación de depuración usa el sufijo `.debug` y puede instalarse junto a la versión de lanzamiento. La compilación de lanzamiento requiere tu propia configuración de firma.

## Aviso legal

AndWall se proporciona **«tal cual»**, sin garantía de ningún tipo. Depende de permisos avanzados del sistema, y el desarrollador no se hace responsable de la inestabilidad del sistema, la pérdida de datos ni los efectos secundarios del bloqueo de red. Comprueba siempre qué apps bloqueas.

Este fork solo se distribuye a través de [este repositorio de GitHub](https://github.com/hsushjk/AndWall). Los APK de cualquier otro origen no están afiliados a este proyecto y se usan bajo tu propia responsabilidad. AndWall no tiene sitio web oficial y no se publica en Google Play ni en F-Droid.

El permiso `INTERNET` solo se usa para el emparejamiento de la depuración inalámbrica (LibADB).

## Licencia

[GPLv3](../LICENSE.md). Los datos de firmas de rastreadores incluidos se distribuyen por separado bajo ODbL v1.0; consulta [TRACKER_DATA_LICENSE.md](../TRACKER_DATA_LICENSE.md).

## Créditos

- [ShizuWall](https://github.com/AhmetCanArslan/ShizuWall): el proyecto original en el que se basa este fork.
- [Shizuku](https://github.com/RikkaApps/Shizuku): ejecución de comandos con privilegios.
- [LibADB](https://github.com/MuntashirAkon/libadb-android): depuración inalámbrica y conexión con el demonio.
- [Exodus Privacy](https://exodus-privacy.eu.org/): base de datos de firmas de rastreadores. AndWall incluye una copia reducida y nunca accede a su API.
