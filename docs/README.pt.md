<div align="center">
  <a href="https://github.com/hsushjk/AndWall">
    <img src="../app/src/main/res/mipmap-xxxhdpi/ic_launcher_round.png" alt="Ícone do AndWall" width="72" />
  </a>
  <h1>AndWall</h1>
  <strong>Firewall para Android.</strong><br/>
  Baseado em VPN / Shizuku / daemon ADB local / Root
</div>

<p align="center">
  <a href="README.en.md">English</a> ·
  <a href="README.tr.md">Türkçe</a> ·
  <a href="README.de.md">Deutsch</a> ·
  <a href="README.es.md">Español</a> ·
  <a href="README.it.md">Italiano</a> ·
  <b>Português</b> ·
  <a href="README.cs.md">Čeština</a> ·
  <a href="README.ru.md">Русский</a> ·
  <a href="README.ar.md">العربية</a> ·
  <a href="README.hi.md">हिन्दी</a> ·
  <a href="../README.md">中文</a> ·
  <a href="README.ja.md">日本語</a>
</p>
<div style="height: 20px;">&nbsp;</div>
<p align="center">
  <img alt="SDK mínimo" src="https://img.shields.io/badge/Minimum%20SDK-30%20(Android%2011)-839192?style=flat-square&logo=android&logoColor=white" />
</p>

<p align="center">
  <a href="https://github.com/hsushjk/AndWall/issues">Dúvidas e feedback</a> ·
  <a href="https://github.com/AhmetCanArslan/ShizuWall">Projeto upstream (ShizuWall)</a>
</p>

## Por que o AndWall existe

[ShizuWall](https://github.com/AhmetCanArslan/ShizuWall) é um projeto excelente: poderoso sem ser inchado, com um design de interface bonito e coerente. Porém, não oferece suporte ao modo VPN.

O mantenedor deste fork acredita que um bom projeto deve ser inclusivo. Integrar um backend VPN reduz a barreira de entrada, mas não rebaixa o teto.

## Por que escolher o AndWall

- **Vários modos disponíveis**: é possível usar o VPN mais básico, ou os modos mais poderosos Shizuku, ADB e root.
- **Controle por aplicativo**: bloqueie a rede por aplicativo, incluindo perfis de trabalho e clones nos modos encadeados.
- **Privacidade**: offline-first, sem análises, sem telemetria e sem servidores VPN remotos — o túnel é totalmente local e todos os pacotes são descartados.
- **Acesso rápido**: ícones de configurações rápidas, widgets e botão flutuante.
- **Automação**: controlável por broadcasts enviados via adb, Tasker, MacroDroid etc.

## Principais mudanças neste fork

AndWall é um fork do [ShizuWall](https://github.com/AhmetCanArslan/ShizuWall). Este fork adiciona:

- **Backend VPN**: um backend de firewall sem configuração, que basta confirmar a caixa de diálogo de autorização do sistema para usar.
- **Adaptação da interface ao modo VPN**: oculta os modos e recursos de firewall que o backend VPN não pode oferecer e recorre ao modo lista de permissões.
- **Rebranding, configurações e traduções atualizadas**, além de um changelog e de uma verificação de atualizações mais detalhados para este fork.
- **Remoção da política de privacidade**: este projeto não é distribuído em nenhuma loja de aplicativos e não trata dados pessoais, portanto não gera obrigação de informação.

## Ambiente compatível

Android 11 ou superior, com um destes backends:

- **VPN**
- **Shizuku** (com suporte neste fork)
- **LibADB**: emparelhamento por depuração sem fio, sem precisar de computador
- **Root**

## Capturas de tela

<p align="center">
  <img src="../assets/screenshots/v4.7.1/1.png" alt="Tela principal do AndWall" width="30%" />
</p>

## Como funciona

Apenas os backends Shizuku, LibADB e Root exigem um componente auxiliar privilegiado. Eles operam o **chain 3** da connectivity do Android, um mecanismo da plataforma para controlar o acesso à rede por nome de pacote:

```bash
cmd connectivity set-chain3-enabled true                                # ativar o firewall
cmd connectivity set-package-networking-enabled false <package.name>   # bloquear
cmd connectivity set-package-networking-enabled true <package.name>    # desbloquear
cmd connectivity set-chain3-enabled false                               # desativar o firewall
```

O Android apaga essas regras após a reinicialização do dispositivo.

### Backend VPN

O backend VPN é um dispositivo tun `VpnService` local que captura apenas os aplicativos que você escolher bloquear e descarta todos os pacotes que recebe. Ele precisa apenas de uma caixa de diálogo de autorização do sistema, sem qualquer outra configuração. Instalações novas usam esse backend por padrão, com o modo de firewall **lista de permissões** (escolha os aplicativos que podem acessar a rede; todos os demais são bloqueados), alterável em **Configurações → Backend e modo de trabalho**.

O túnel reinicia automaticamente após uma reinicialização; se o processo do aplicativo for encerrado enquanto o firewall estiver ativo, a proteção é restaurada na próxima vez que o AndWall for aberto. O modo de tela de bloqueio é suportado desde que o dispositivo tenha um bloqueio de tela definido (o túnel acompanha o estado da tela de bloqueio).

Limitações do backend VPN: ele lida apenas com aplicativos do usuário atual (sem suporte a perfis de trabalho/clones), não oferece suporte a regras exclusivas para dados móveis nem a modos baseados em detecção de primeiro plano, e a manutenção do conjunto de aplicativos bloqueados não depende de cadeias de firewall por UID. Se precisar desses recursos, continue usando Shizuku, LibADB ou Root.

## Automação

Cada broadcast exige um backend ativo. `apps` é uma lista de chaves de aplicativos separadas por vírgula: apenas o nome do pacote indica o usuário 0, enquanto as formas com prefixo (como `150:com.example.app`) indicam a cópia daquele perfil de trabalho ou espaço de clonagem.

### Firewall: `shizuwall.CONTROL`

| Parâmetro | Tipo | |
|---|---|---|
| `state` | boolean, obrigatório | `true` = bloquear, `false` = desbloquear |
| `apps` | string, opcional | Aplicativos a bloquear ou desbloquear. Se omitido, o broadcast liga ou desliga o firewall inteiro. |

```bash
# ativar o firewall (state false significa desativar)
adb shell am broadcast -a shizuwall.CONTROL -n com.arslan.shizuwall/.receivers.FirewallControlReceiver --ez state true

# bloquear um aplicativo e sua cópia no perfil de trabalho (state false significa desbloquear)
adb shell am broadcast -a shizuwall.CONTROL -n com.arslan.shizuwall/.receivers.FirewallControlReceiver --ez state true --es apps "com.example.app,150:com.example.app"
```

Com `apps`, a seleção dentro do aplicativo é sincronizada: `state true` marca esses aplicativos e, se o firewall estava desligado, o liga; `state false` desmarca e mantém o firewall ligado. No modo lista de permissões, `apps` refere-se à lista de permitidos, portanto esses dois estados significam o oposto.

No modo VPN, os broadcasts funcionam da mesma forma, mas `apps` deve referenciar apenas aplicativos do usuário atual; as chaves de perfis de trabalho são ignoradas.

### Novo aplicativo: `shizuwall.APP_INSTALLED`

Informa um aplicativo recém-instalado, para que a política de novos aplicativos e as notificações tenham efeito como se o AndWall tivesse detectado a instalação por conta própria. O AndWall só vê instalações no próprio perfil de usuário, então outros perfis precisam ser cobertos por esse meio.

```bash
adb shell am broadcast -a shizuwall.APP_INSTALLED -n com.arslan.shizuwall/.receivers.AppInstalledReceiver --es apps "com.example.newapp"
```

Com o firewall desligado, nenhuma regra é aplicada. Aplicativos sem a permissão `INTERNET` são ignorados.

### Perfil: `shizuwall.PROFILE`

Ativa um perfil salvo: aplica a seleção de aplicativos, o modo de firewall, os modos por aplicativo e a configuração *Mostrar aplicativos do sistema* do perfil.

| Parâmetro | Tipo | |
|---|---|---|
| `profile` | string | Nome do perfil, igual ao exibido no painel de perfis. |
| `profile_id` | string | ID interno; é verificado primeiro e não é afetado por renomeações. A caixa de diálogo **Automação** desse perfil mostra um comando pronto. |
| `force_enable` | boolean, opcional | Liga o firewall depois da alternância, mesmo que estivesse desligado antes. |

Forneça pelo menos um entre `profile` e `profile_id`. Se nenhum corresponder, nada é alterado e um aviso toast é exibido.

```bash
adb shell am broadcast -a shizuwall.PROFILE -n com.arslan.shizuwall/.receivers.ProfileControlReceiver --es profile "Work"
```

## Compilar a partir do código-fonte

Este projeto é compilado com o Gradle Wrapper e o Android SDK:

```bash
./gradlew assembleDebug
```

As compilações de depuração usam o sufixo `.debug` e podem ser instaladas junto com a versão de lançamento. As compilações de lançamento exigem sua própria configuração de assinatura.

## Aviso legal

O AndWall é fornecido **“como está”**, sem garantias de qualquer tipo. Ele depende de permissões avançadas do sistema, e os desenvolvedores não se responsabilizam por instabilidade do sistema, perda de dados ou efeitos colaterais causados pelo bloqueio de rede. Sempre confirme quais aplicativos você está bloqueando.

Este fork é distribuído apenas por meio [deste repositório do GitHub](https://github.com/hsushjk/AndWall). APKs de outras fontes não têm relação com este projeto e são usados por sua conta e risco. O AndWall não possui site oficial e não será publicado no Google Play nem no F-Droid.

A permissão `INTERNET` é usada apenas para o emparelhamento da depuração sem fio (LibADB).

## Patrocínio

Se você acha que este projeto lhe foi útil, dê uma estrela neste repositório e também no repositório upstream. Se quiser patrocinar, clique em `Buy me a coffee` no repositório upstream

## Licença

[GPLv3](../LICENSE.md). Os dados de assinatura de rastreadores incluídos são licenciados separadamente sob a ODbL v1.0, consulte [TRACKER_DATA_LICENSE.md](../TRACKER_DATA_LICENSE.md).

## Agradecimentos

- [ShizuWall](https://github.com/AhmetCanArslan/ShizuWall): o projeto upstream no qual este fork se baseia.
- [Shizuku](https://github.com/RikkaApps/Shizuku): execução de comandos privilegiados.
- [LibADB](https://github.com/MuntashirAkon/libadb-android): depuração sem fio e conexão com o daemon.
- [Exodus Privacy](https://exodus-privacy.eu.org/): banco de dados de assinaturas de rastreadores. O AndWall inclui uma cópia reduzida e nunca acessa sua API.
