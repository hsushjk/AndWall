<div align="center">
  <a href="https://github.com/hsushjk/AndWall">
    <img src="../app/src/main/res/mipmap-xxxhdpi/ic_launcher_round.png" alt="AndWall Icon" width="72" />
  </a>
  <h1>AndWall</h1>
  <strong>Firewall para Android com um backend VPN local opcional.</strong><br/>
  Privacidade em primeiro lugar, apenas local, com um backend VPN integrado que não precisa de configuração — ou com Shizuku / daemon ADB local / Root para os modos avançados.
</div>

<p align="center">
  <a href="../README.md">English</a> ·
  <a href="README.tr.md">Türkçe</a> ·
  <a href="README.de.md">Deutsch</a> ·
  <a href="README.it.md">Italiano</a> ·
  <b>Português</b> ·
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
  <a href="https://github.com/hsushjk/AndWall/issues">Dúvidas e problemas</a> ·
  <a href="https://github.com/AhmetCanArslan/ShizuWall">Projeto original (ShizuWall)</a>
</p>

## Funciona com

Android 11+ e um destes backends:

- **VPN**: backend VPN local integrado, sem configuração e sem app auxiliar (apenas apps do usuário atual)
- **Shizuku** (forks são suportados)
- **LibADB**: pareia por Depuração sem fio, sem precisar de computador
- **Root**

Novas instalações começam com o backend **VPN** e o modo de firewall **Lista branca**, então o app funciona logo de cara. Os outros backends podem ser selecionados depois em **Configurações → Backend e modo de trabalho**.

## Por que o AndWall

- **Sem configuração privilegiada no modo VPN**: basta conceder o consentimento de VPN do sistema uma vez — sem Shizuku, sem ADB, sem root.
- **Controle por app**: bloqueia a rede por app, incluindo cópias de perfil de trabalho e clones nos modos chain-3.
- **Privado**: offline-first, sem analytics, sem telemetria, sem servidor VPN remoto — o túnel é local e todos os pacotes são descartados.
- **Acesso rápido**: bloco das Configurações rápidas, widget e botão flutuante.
- **Automação**: controlável por broadcasts a partir do adb, Tasker, MacroDroid e similares.

## Captura de tela

<p align="center">
  <img src="../assets/screenshots/v4.7.1/1.png" alt="Tela principal do AndWall" width="30%" />
  <img src="../assets/screenshots/v4.7.1/4.png" alt="Configurações" width="30%" />
</p>

## Como funciona

Apenas os backends Shizuku, LibADB e Root precisam de um auxiliar privilegiado. Eles usam a **chain 3** de conectividade do Android, um mecanismo da plataforma que controla o acesso à rede por pacote:

```bash
cmd connectivity set-chain3-enabled true                                # firewall on
cmd connectivity set-package-networking-enabled false <package.name>   # block
cmd connectivity set-package-networking-enabled true <package.name>    # unblock
cmd connectivity set-chain3-enabled false                               # firewall off
```

O Android apaga essas regras ao reiniciar.

### Backend VPN

O backend VPN é um dispositivo tun local (`VpnService`) que captura apenas os apps que você escolher bloquear e descarta todos os pacotes que recebe. Ele exige apenas uma caixa de consentimento do sistema e nenhuma outra configuração. É o padrão para novas instalações, combinado com o modo de firewall **Lista branca** (escolha os apps permitidos; todo o resto é bloqueado), e pode ser alterado em **Configurações → Backend e modo de trabalho**.

O túnel é iniciado automaticamente de novo após uma reinicialização e, se o processo do app for interrompido com o firewall ligado, a proteção é restaurada na próxima vez que o AndWall for aberto. O modo de bloqueio de tela é suportado (o túnel acompanha o estado de bloqueio), desde que o dispositivo tenha bloqueio de tela.

Limitações do backend VPN: ele lida apenas com apps do usuário atual (sem cópias de perfil de trabalho ou clone), regras apenas para dados móveis e modos baseados em detecção de primeiro plano não estão disponíveis, e o conjunto de apps bloqueados é mantido sem chains de firewall por uid. Para esses recursos, continue usando Shizuku, LibADB ou Root.

## Mudanças neste fork

O AndWall é um fork do [ShizuWall](https://github.com/AhmetCanArslan/ShizuWall). Este fork adiciona:

- **Backend VPN**: um backend de firewall sem configuração que funciona logo após a caixa de consentimento do sistema.
- **Padrões para novos usuários**: backend VPN, modo de firewall Lista branca e ocultação de apps sem acesso à rede.
- **Onboarding atualizado**: o fluxo de boas-vindas e a seleção de modo agora mencionam os quatro backends.
- **Adaptação da interface para o modo VPN**: modos de firewall e recursos que o backend VPN não pode oferecer ficam ocultos, e o modo de firewall volta para Lista branca.
- **Rebranding, entradas de configurações e traduções atualizadas**, além de changelogs maiores e verificação de atualizações para este fork.

## Automação

Todo broadcast precisa de um backend ativo. `apps` é uma lista CSV de chaves de app: um nome de pacote simples aponta para o usuário 0; um com prefixo, como `150:com.example.app`, aponta para a cópia naquele perfil de trabalho ou espaço de clone.

### Firewall: `shizuwall.CONTROL`

| Extra | Tipo | |
|---|---|---|
| `state` | boolean, obrigatório | `true` = bloquear, `false` = desbloquear |
| `apps` | string, opcional | Apps a bloquear ou desbloquear. Sem ele, o broadcast alterna o firewall inteiro. |

```bash
# Firewall on (state false turns it off)
adb shell am broadcast -a shizuwall.CONTROL -n com.arslan.shizuwall/.receivers.FirewallControlReceiver --ez state true

# Block an app and its work-profile copy (state false unblocks them)
adb shell am broadcast -a shizuwall.CONTROL -n com.arslan.shizuwall/.receivers.FirewallControlReceiver --ez state true --es apps "com.example.app,150:com.example.app"
```

Com `apps`, a seleção no app acompanha: `state true` seleciona os apps e liga o firewall se ele estiver desligado; `state false` desmarca os apps e deixa o firewall ligado. No modo Lista branca, `apps` se refere à lista de permitidos, então os dois estados se invertem.

No modo VPN, os broadcasts funcionam da mesma forma, mas `apps` deve se referir apenas a apps do usuário atual; chaves de perfil de trabalho são ignoradas.

### Novo app: `shizuwall.APP_INSTALLED`

Informa um app recém-instalado, para que a política e a notificação de novos apps se apliquem a ele como se o próprio AndWall tivesse detectado a instalação. O AndWall só vê instalações no próprio perfil, então é assim que se cobrem os demais.

```bash
adb shell am broadcast -a shizuwall.APP_INSTALLED -n com.arslan.shizuwall/.receivers.AppInstalledReceiver --es apps "com.example.newapp"
```

Nenhuma regra é aplicada enquanto o firewall está desligado. Apps sem a permissão `INTERNET` são ignorados.

### Perfil: `shizuwall.PROFILE`

Ativa um perfil salvo: sua seleção de apps, o modo de firewall, os modos por app e a configuração *Mostrar apps do sistema*.

| Extra | Tipo | |
|---|---|---|
| `profile` | string | Nome do perfil, como aparece na lista de perfis. |
| `profile_id` | string | ID interno; é verificado primeiro e não é afetado por renomeações. O diálogo **Automação** do perfil mostra o comando pronto. |
| `force_enable` | boolean, opcional | Liga o firewall após a troca mesmo que ele estivesse desligado. |

É obrigatório informar `profile` ou `profile_id`. Se nenhum corresponder, nada muda e um toast avisa.

```bash
adb shell am broadcast -a shizuwall.PROFILE -n com.arslan.shizuwall/.receivers.ProfileControlReceiver --es profile "Work"
```

## Compilar a partir do código-fonte

O projeto compila com o wrapper do Gradle e o SDK do Android:

```bash
./gradlew assembleDebug
```

A build de debug usa o sufixo `.debug` e pode ser instalada junto com a build de release. A build de release exige uma configuração de assinatura própria.

## Aviso legal

O AndWall é fornecido **"como está"**, sem garantia. Ele depende de permissões avançadas do sistema, e o desenvolvedor não se responsabiliza por instabilidade do sistema, perda de dados ou efeitos colaterais do bloqueio de rede. Sempre verifique quais apps você bloqueia.

Este fork é distribuído apenas por [este repositório no GitHub](https://github.com/hsushjk/AndWall). APKs de qualquer outro lugar não têm vínculo com o projeto e são usados por sua conta e risco. O AndWall não possui site oficial e não é publicado no Google Play nem no F-Droid.

A permissão `INTERNET` é solicitada apenas para o pareamento da depuração sem fio (LibADB).

## Licença

[GPLv3](../LICENSE.md). Os dados de assinaturas de rastreadores incluídos são licenciados separadamente sob ODbL v1.0; veja [TRACKER_DATA_LICENSE.md](../TRACKER_DATA_LICENSE.md).

## Créditos

- [ShizuWall](https://github.com/AhmetCanArslan/ShizuWall): o projeto original no qual este fork se baseia.
- [Shizuku](https://github.com/RikkaApps/Shizuku): execução de comandos privilegiados.
- [LibADB](https://github.com/MuntashirAkon/libadb-android): depuração sem fio e conexão com o daemon.
- [Exodus Privacy](https://exodus-privacy.eu.org/): banco de dados de assinaturas de rastreadores. O AndWall inclui uma cópia reduzida e nunca acessa a API deles.