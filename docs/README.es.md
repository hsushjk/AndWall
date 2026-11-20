<div align="center">
  <a href="https://github.com/hsushjk/AndWall">
    <img src="../app/src/main/res/mipmap-xxxhdpi/ic_launcher_round.png" alt="AndWall Icon" width="72" />
  </a>
  <h1>AndWall</h1>
  <strong>Cortafuegos para Android.</strong><br/>
  Basado en VPN / Shizuku / demonio ADB local / Root
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
  <img alt="Minimum SDK" src="https://img.shields.io/badge/Minimum%20SDK-30%20(Android%2011)-839192?style=flat-square&logo=android&logoColor=white" />
</p>

<p align="center">
  <a href="https://github.com/hsushjk/AndWall/issues">Problemas y comentarios</a> ·
  <a href="https://github.com/AhmetCanArslan/ShizuWall">Proyecto original (ShizuWall)</a>
</p>

## Por qué existe AndWall

[ShizuWall](https://github.com/AhmetCanArslan/ShizuWall) es un proyecto excelente: potente pero sin excesos, con un diseño de interfaz bonito y coherente. Pero no admite el modo VPN.

El propietario de este repositorio bifurcado considera que un buen proyecto debe ser inclusivo. Integrar un backend VPN reduce la barrera de entrada, pero no rebaja el techo.

## Por qué elegir AndWall

- **Varios modos disponibles**: puedes usar el modo VPN más básico, o los modos más potentes de Shizuku, ADB y root.
- **Control por aplicación**: bloquea la red por aplicación; en el modo encadenado también se incluyen el perfil de trabajo y las copias clonadas.
- **Privacidad**: prioridad al funcionamiento sin conexión, sin análisis, sin telemetría ni servidores VPN remotos: el túnel es totalmente local y todos los paquetes se descartan.
- **Acceso rápido**: mosaicos de Ajustes rápidos, widgets y botón flotante.
- **Automatización**: se puede controlar mediante difusiones (broadcasts) enviadas desde adb, Tasker, MacroDroid, etc.

## Cambios principales de esta bifurcación

AndWall es una bifurcación de [ShizuWall](https://github.com/AhmetCanArslan/ShizuWall). Esta bifurcación añade:

- **Backend VPN**: un backend de cortafuegos sin configuración; basta con confirmar el diálogo de autorización del sistema para usarlo.
- **Adaptación de la interfaz al modo VPN**: oculta los modos y funciones de cortafuegos que el backend VPN no puede ofrecer, y hace que el modo de cortafuegos vuelva a la lista blanca.
- **Nuevo nombre de marca, opciones de configuración y traducciones actualizadas**, además de un registro de cambios y una comprobación de actualizaciones más detallados para esta bifurcación.
- **Eliminación de la política de privacidad**: este proyecto no se publica en ninguna tienda de aplicaciones ni trata datos personales, por lo que no genera obligaciones de información.

## Entornos compatibles

Android 11 o superior, y requiere uno de los siguientes backends:

- **VPN**
- **Shizuku** (la versión de esta bifurcación es compatible)
- **LibADB**: emparejamiento mediante depuración inalámbrica, sin necesidad de un ordenador
- **Root**

## Capturas de pantalla

<p align="center">
  <img src="../assets/screenshots/v4.7.1/1.png" alt="Pantalla principal de AndWall" width="30%" />
</p>

## Cómo funciona

Solo los backends Shizuku, LibADB y Root necesitan un componente auxiliar con privilegios. Estos operan sobre la **chain 3** de Android connectivity, un mecanismo de la plataforma que controla el acceso a la red por nombre de paquete:

```bash
cmd connectivity set-chain3-enabled true                                # Activar el cortafuegos
cmd connectivity set-package-networking-enabled false <package.name>   # Bloquear
cmd connectivity set-package-networking-enabled true <package.name>    # Desbloquear
cmd connectivity set-chain3-enabled false                               # Desactivar el cortafuegos
```

Android borra estas reglas al reiniciar el dispositivo.

### Backend VPN

El backend VPN es un dispositivo tun de `VpnService` local que solo captura las aplicaciones que eliges bloquear y descarta todos los paquetes que recibe. Solo necesita un diálogo de autorización del sistema, sin más configuración. Una instalación nueva usa este backend por defecto, junto con el modo de cortafuegos **lista blanca** (se eligen las aplicaciones que pueden acceder a la red y se bloquea el resto), y se puede cambiar en **Ajustes → Backend y modo de trabajo**.

Tras un reinicio, el túnel se vuelve a iniciar automáticamente; si el proceso de la aplicación se termina mientras el cortafuegos está activo, la protección se restaura la próxima vez que abras AndWall. El modo de pantalla de bloqueo es compatible siempre que el dispositivo tenga configurado un bloqueo de pantalla (el túnel sigue el estado de la pantalla de bloqueo).

Limitaciones del backend VPN: solo gestiona las aplicaciones del usuario actual (no admite perfiles de trabajo ni copias clonadas), no admite reglas solo para datos móviles ni modos basados en la detección de primer plano, y el mantenimiento del conjunto de aplicaciones bloqueadas no depende de la cadena de cortafuegos por UID. Si necesitas estas funciones, sigue usando Shizuku, LibADB o Root.

## Automatización

Cada difusión requiere un backend activo. `apps` es una lista de claves de aplicación separadas por comas: escribir solo el nombre del paquete indica el usuario 0, mientras que un prefijo (como `150:com.example.app`) indica la copia en ese perfil de trabajo o espacio clonado.

### Cortafuegos: `shizuwall.CONTROL`

| Parámetro | Tipo | |
|---|---|---|
| `state` | boolean, obligatorio | `true` = bloquear, `false` = desbloquear |
| `apps` | string, opcional | Aplicaciones que se van a bloquear o desbloquear. Si se omite, la difusión activa o desactiva todo el cortafuegos. |

```bash
# Activar el cortafuegos (state false lo desactiva)
adb shell am broadcast -a shizuwall.CONTROL -n com.arslan.shizuwall/.receivers.FirewallControlReceiver --ez state true

# Bloquear una aplicación y su copia en el perfil de trabajo (state false la desbloquea)
adb shell am broadcast -a shizuwall.CONTROL -n com.arslan.shizuwall/.receivers.FirewallControlReceiver --ez state true --es apps "com.example.app,150:com.example.app"
```

Cuando se incluye `apps`, la selección dentro de la aplicación se sincroniza: `state true` marca esas aplicaciones y activa el cortafuegos si antes estaba desactivado; `state false` las desmarca y mantiene el cortafuegos activado. En el modo de lista blanca, `apps` apunta a la lista de permitidas, por lo que el significado de ambos estados se invierte.

En el modo VPN las difusiones funcionan igual, pero `apps` solo debe referirse a aplicaciones del usuario actual; las claves de los perfiles de trabajo se omiten.

### Aplicaciones nuevas: `shizuwall.APP_INSTALLED`

Notifica una aplicación recién instalada para que la política y las notificaciones de nuevas aplicaciones se apliquen como si AndWall hubiera detectado la instalación por sí mismo. AndWall solo ve las instalaciones del perfil de usuario en el que se ejecuta, por lo que otros perfiles deben cubrirse mediante este mecanismo.

```bash
adb shell am broadcast -a shizuwall.APP_INSTALLED -n com.arslan.shizuwall/.receivers.AppInstalledReceiver --es apps "com.example.newapp"
```

Cuando el cortafuegos está desactivado no se aplica ninguna regla. Las aplicaciones sin el permiso `INTERNET` se ignoran.

### Perfiles: `shizuwall.PROFILE`

Activa un perfil guardado: aplica la selección de aplicaciones del perfil, el modo de cortafuegos, el modo por aplicación y el ajuste *Mostrar aplicaciones del sistema*.

| Parámetro | Tipo | |
|---|---|---|
| `profile` | string | Nombre del perfil, igual al que se muestra en el panel de perfiles. |
| `profile_id` | string | ID interno; se comprueba primero y no se ve afectado por los cambios de nombre. El diálogo de **automatización** de ese perfil muestra el comando ya listo. |
| `force_enable` | boolean, opcional | Activa el cortafuegos tras la alternancia, incluso si antes estaba desactivado. |

Debes proporcionar al menos uno de `profile` y `profile_id`. Si ninguno coincide, no se realiza ningún cambio y se muestra un aviso toast.

```bash
adb shell am broadcast -a shizuwall.PROFILE -n com.arslan.shizuwall/.receivers.ProfileControlReceiver --es profile "Work"
```

## Compilar desde el código fuente

Este proyecto se compila con Gradle Wrapper y el SDK de Android:

```bash
./gradlew assembleDebug
```

La compilación de depuración usa el sufijo `.debug` y puede instalarse junto a la versión de lanzamiento. La compilación de lanzamiento requiere tu propia configuración de firma.

## Descargo de responsabilidad

AndWall se proporciona **“tal cual”**, sin ningún tipo de garantía. Depende de permisos avanzados del sistema y los desarrolladores no se hacen responsables de la inestabilidad del sistema, la pérdida de datos ni los efectos secundarios del bloqueo de red. Comprueba siempre qué aplicaciones estás bloqueando.

Esta bifurcación se distribuye únicamente a través de [este repositorio de GitHub](https://github.com/hsushjk/AndWall). Los APK de otras fuentes no tienen relación con este proyecto y se usan bajo tu propio riesgo. AndWall no tiene sitio web oficial y tampoco se publica en Google Play ni en F-Droid.

El permiso `INTERNET` se usa únicamente para el emparejamiento de la depuración inalámbrica (LibADB).

## Patrocinio

Si este proyecto te resulta útil, puedes dar una Star tanto a este repositorio como al proyecto original. Si quieres patrocinarlo, haz clic en `Buy me a coffee` en el repositorio original.

## Licencia

[GPLv3](../LICENSE.md). Los datos de firmas de rastreadores incluidos se licencian por separado bajo ODbL v1.0; consulta [TRACKER_DATA_LICENSE.md](../TRACKER_DATA_LICENSE.md).

## Agradecimientos

- [ShizuWall](https://github.com/AhmetCanArslan/ShizuWall): el proyecto original en el que se basa esta bifurcación.
- [Shizuku](https://github.com/RikkaApps/Shizuku): ejecución de comandos con privilegios.
- [LibADB](https://github.com/MuntashirAkon/libadb-android): depuración inalámbrica y conexión con el demonio.
- [Exodus Privacy](https://exodus-privacy.eu.org/): base de datos de firmas de rastreadores. AndWall incluye una copia reducida y nunca accede a su API.
