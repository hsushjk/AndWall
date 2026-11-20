<div align="center">
  <a href="https://github.com/hsushjk/AndWall">
    <img src="../app/src/main/res/mipmap-xxxhdpi/ic_launcher_round.png" alt="Ícone do AndWall" width="72" />
  </a>
  <h1>AndWall</h1>
  <strong>Firewall para Android com backend VPN local opcional.</strong><br/>
  Privacidade em primeiro lugar e execução totalmente local, alimentado por um backend VPN integrado que não exige nenhuma configuração — os modos avançados usam Shizuku / daemon ADB local / Root.
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
  <a href="https://tooomm.github.io/github-release-stats/?username=hsushjk&repository=AndWall">
    <img alt="Downloads (GitHub)" src="https://img.shields.io/github/downloads/hsushjk/AndWall/total?color=ff9500&label=Total%20Downloads%20(GitHub)&logo=github&logoColor=white&style=flat-square" />
  </a>
  <img alt="Tamanho do repositório" src="https://img.shields.io/github/repo-size/hsushjk/AndWall?style=flat-square" />
  <img alt="SDK mínimo" src="https://img.shields.io/badge/Minimum%20SDK-30%20(Android%2011)-839192?style=flat-square&logo=android&logoColor=white" />
</p>

<p align="center">
  <a href="https://github.com/hsushjk/AndWall/issues">Problemas e feedback</a> ·
  <a href="https://github.com/AhmetCanArslan/ShizuWall">Projeto upstream (ShizuWall)</a>
</p>

## Ambiente compatível

Android 11 ou superior, e é necessário um dos seguintes backends:

- **VPN**: backend VPN local integrado, sem necessidade de configuração e sem aplicativo complementar (apenas aplicativos do usuário atual)
- **Shizuku** (builds de fork compatíveis)
- **LibADB**: emparelhamento por depuração sem fio, sem necessidade de computador
- **Root**

Novas instalações usam por padrão o backend **VPN** e o modo de firewall **lista de permissões**, portanto o aplicativo funciona imediatamente após a instalação. Os outros backends podem ser escolhidos depois em **Configurações → Backend e modo de trabalho**.

## Por que escolher o AndWall

- **O modo VPN não exige configuração privilegiada**: basta autorizar a VPN do sistema uma vez — sem Shizuku, sem ADB, sem root.
- **Controle por aplicativo**: bloqueie a rede por aplicativo e, no modo encadeado, também os perfis de trabalho e as cópias clonadas.
- **Privacidade**: prioridade ao modo offline, sem análises, sem telemetria, sem servidores VPN remotos — o túnel é totalmente local e todos os pacotes são descartados.
- **Acesso rápido**: bloco de configurações rápidas, widget e botão flutuante.
- **Automação**: controle por broadcasts enviados via adb, Tasker, MacroDroid etc.

## Capturas de tela

<p align="center">
  <img src="../assets/screenshots/v4.7.1/1.png" alt="Tela principal do AndWall" width="30%" />
  <img src="../assets/screenshots/v4.7.1/4.png" alt="Configurações" width="30%" />
</p>

## Como funciona

Apenas os backends Shizuku, LibADB e Root exigem o componente privilegiado complementar. Eles operam a **chain 3** da connectivity do Android, um mecanismo de plataforma para controlar o acesso à rede por nome de pacote:

```bash
cmd connectivity set-chain3-enabled true                                # ativar o firewall
cmd connectivity set-package-networking-enabled false <package.name>   # bloquear
cmd connectivity set-package-networking-enabled true <package.name>    # desbloquear
cmd connectivity set-chain3-enabled false                               # desativar o firewall
```

O Android apaga essas regras após a reinicialização do dispositivo.

### Backend VPN

O backend VPN é um dispositivo tun `VpnService` local que captura apenas os aplicativos que você escolher para bloquear e descarta todos os pacotes que recebe. Ele exige somente uma caixa de diálogo de autorização do sistema, sem nenhuma outra configuração. Novas instalações usam esse backend por padrão, junto com o modo de firewall **lista de permissões** (escolha os aplicativos que podem acessar a rede e todo o resto fica bloqueado), que pode ser alterado em **Configurações → Backend e modo de trabalho**.

Após uma reinicialização, o túnel é iniciado novamente de forma automática; se o processo do aplicativo for encerrado enquanto o firewall estiver ativo, a proteção será restaurada na próxima vez que o AndWall for aberto. O modo de tela de bloqueio é compatível desde que o dispositivo tenha um bloqueio de tela definido (o túnel acompanha o estado da tela de bloqueio).

Limitações do backend VPN: ele lida apenas com aplicativos do usuário atual (sem suporte a perfis de trabalho/cópias clonadas), não oferece suporte a regras somente de dados móveis nem a modos baseados em detecção de primeiro plano, e a manutenção do conjunto de aplicativos bloqueados não depende de cadeias de firewall por UID. Se você precisar desses recursos, continue usando Shizuku, LibADB ou Root.

## Mudanças neste fork

O AndWall é um fork do [ShizuWall](https://github.com/AhmetCanArslan/ShizuWall). Este fork adiciona:

- **Backend VPN**: um backend de firewall sem configuração, que pode ser usado apenas confirmando a caixa de diálogo de autorização do sistema.
- **Configurações padrão para novos usuários**: backend VPN, modo de firewall de lista de permissões e ocultação dos aplicativos sem permissão de rede.
- **Fluxo de integração atualizado**: o fluxo de boas-vindas e a seleção de modo agora mencionam todos os quatro backends.
- **Adaptações de interface para o modo VPN**: oculta os modos de firewall e os recursos que o backend VPN não pode oferecer, e reverte o modo de firewall para lista de permissões.
- **Rebranding, configurações e traduções atualizadas**, além de um changelog e de uma verificação de atualizações mais detalhados para este fork.

## Automação

Cada broadcast exige um backend ativo. `apps` é uma lista de chaves de aplicativos separada por vírgulas: apenas o nome do pacote significa usuário 0; com prefixo (como `150:com.example.app`) significa a cópia naquele perfil de trabalho ou espaço de clonagem.

### Firewall: `shizuwall.CONTROL`

| Parâmetro | Tipo | |
|---|---|---|
| `state` | boolean, obrigatório | `true` = bloquear, `false` = desbloquear |
| `apps` | string, opcional | Aplicativos a bloquear ou desbloquear. Se omitido, o broadcast liga ou desliga o firewall inteiro. |

```bash
# Ativar o firewall (state false o desativa)
adb shell am broadcast -a shizuwall.CONTROL -n com.arslan.shizuwall/.receivers.FirewallControlReceiver --ez state true

# Bloquear um aplicativo e sua cópia no perfil de trabalho (state false desbloqueia)
adb shell am broadcast -a shizuwall.CONTROL -n com.arslan.shizuwall/.receivers.FirewallControlReceiver --ez state true --es apps "com.example.app,150:com.example.app"
```

Com `apps`, a seleção dentro do aplicativo é sincronizada: `state true` seleciona esses aplicativos e ativa o firewall caso ele estivesse desativado; `state false` desmarca e mantém o firewall ativo. No modo de lista de permissões, `apps` aponta para a lista de permitidos, portanto o significado dos dois estados é invertido.

No modo VPN, os broadcasts funcionam da mesma forma, mas `apps` deve referenciar apenas aplicativos do usuário atual; chaves de perfis de trabalho são ignoradas.

### Novos aplicativos: `shizuwall.APP_INSTALLED`

Reporta aplicativos recém-instalados, para que a política de novos aplicativos e as notificações entrem em vigor como se o AndWall os tivesse detectado por conta própria. O AndWall só consegue ver instalações no próprio perfil de usuário, então os outros perfis precisam ser cobertos dessa forma.

```bash
adb shell am broadcast -a shizuwall.APP_INSTALLED -n com.arslan.shizuwall/.receivers.AppInstalledReceiver --es apps "com.example.newapp"
```

Nenhuma regra é aplicada quando o firewall está desligado. Aplicativos sem a permissão `INTERNET` são ignorados.

### Perfil: `shizuwall.PROFILE`

Ativa um perfil salvo: aplica a seleção de aplicativos do perfil, o modo de firewall, os modos por aplicativo e a configuração *Mostrar aplicativos do sistema*.

| Parâmetro | Tipo | |
|---|---|---|
| `profile` | string | Nome do perfil, igual ao exibido no painel de perfis. |
| `profile_id` | string | ID interno; é verificado primeiro e não é afetado por renomeações. O comando pronto aparece na caixa de diálogo **Automação** desse perfil. |
| `force_enable` | boolean, opcional | Ativa o firewall após a troca, mesmo que ele estivesse desligado. |

Informe pelo menos um entre `profile` e `profile_id`. Se nenhum corresponder, nada é alterado e um toast é exibido.

```bash
adb shell am broadcast -a shizuwall.PROFILE -n com.arslan.shizuwall/.receivers.ProfileControlReceiver --es profile "Work"
```

## Compilando a partir do código-fonte

Este projeto é compilado com o Gradle Wrapper e o Android SDK:

```bash
./gradlew assembleDebug
```

Builds de depuração usam o sufixo `.debug` e podem ser instaladas junto com a versão de lançamento. Builds de lançamento exigem sua própria configuração de assinatura.

## Aviso legal

O AndWall é fornecido **"como está"**, sem qualquer garantia. Ele depende de permissões avançadas do sistema, e o desenvolvedor não é responsável por instabilidade do sistema, perda de dados ou efeitos colaterais causados pelo bloqueio de rede. Certifique-se sempre de quais aplicativos você bloqueou.

Este fork é distribuído exclusivamente por meio [deste repositório do GitHub](https://github.com/hsushjk/AndWall). APKs de outras fontes não têm relação com este projeto e são usados por sua conta e risco. O AndWall não tem site oficial e não é publicado no Google Play nem no F-Droid.

A permissão `INTERNET` é usada apenas para o emparelhamento por depuração sem fio (LibADB).

## Licença

[GPLv3](../LICENSE.md). Os dados de assinatura de rastreadores incluídos são licenciados separadamente sob a ODbL v1.0, consulte [TRACKER_DATA_LICENSE.md](../TRACKER_DATA_LICENSE.md).

## Agradecimentos

- [ShizuWall](https://github.com/AhmetCanArslan/ShizuWall): o projeto upstream no qual este fork se baseia.
- [Shizuku](https://github.com/RikkaApps/Shizuku): execução de comandos privilegiados.
- [LibADB](https://github.com/MuntashirAkon/libadb-android): depuração sem fio e conexão com o daemon.
- [Exodus Privacy](https://exodus-privacy.eu.org/): banco de dados de assinaturas de rastreadores. O AndWall inclui uma cópia reduzida e nunca acessa sua API.
