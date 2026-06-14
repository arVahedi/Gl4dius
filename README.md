<h1 align="center">Gl4dius</h1>

<p align="center">
  <strong>An interactive command-line toolkit for authorized MITM security labs.</strong>
</p>

<p align="center">
  <a href="https://github.com/arVahedi/Gl4dius/blob/main/LICENSE"><img alt="License: GPL v3" src="https://img.shields.io/badge/License-GPLv3-blue.svg"></a>
  <img alt="Java 21" src="https://img.shields.io/badge/Java-21-orange.svg">
  <img alt="Platform: Linux" src="https://img.shields.io/badge/Platform-Linux-lightgrey.svg">
  <a href="https://deepwiki.com/arVahedi/Gl4dius"><img alt="Ask DeepWiki" src="https://deepwiki.com/badge.svg"></a>
</p>

<p align="center">
  <img src="misc/screenshot.png" alt="Gl4dius terminal screenshot" width="900">
</p>

## Overview

Gl4dius gives security learners and researchers a guided shell for controlled network interception exercises. It helps organize repeatable lab sessions, switch between interception modes, manage session settings, and clean up local system changes after a run.

Use Gl4dius only on systems and networks you own or have explicit permission to test. It is intended for isolated labs, training environments, and authorized research.

## Highlights

- Interactive terminal experience with session-based workflows.
- Session modes for sniffing, defacing, and phishing lab scenarios.
- Global and per-session configuration commands.
- Built-in cleanup command for restoring local forwarding and firewall changes.
- Optional Docker-based development lab with a sample target container.

## Features

- Deface attack on the whole LAN network.
- Phishing attack.
- Sniffing attack.
- SSL downgrade attack, also known as SSL-Stripping.
- HSTS protection bypass support.
- Automatic discovery-resistance mode for lab scenarios.

Be careful: there is no guarantee that you will be completely invisible. A skilled defender who understands MITM attacks deeply can still detect suspicious behavior, but Gl4dius is designed to make discovery more challenging in controlled lab exercises.

## Requirements

- Linux
- Java 21
- `iptables`
- Root or equivalent network administration privileges
- Docker and Docker Compose, only for the bundled development lab

## Quick Start

Build the CLI:

```shell
./mvnw clean package
```

Run it locally:

```shell
sudo java -jar cli/target/gl4dius-cli.jar
```

Or open the bundled development lab:

```shell
./misc/installation/dev-env/dev-shell.sh
java -jar gl4dius-cli.jar
```

Inside the shell, start with:

```shell
help
config show
session ls
```

## Basic Workflow

Create a session:

```shell
session init --name lab --description "Authorized test environment"
session switch lab
```

Choose a mode:

```shell
session mode sn
```

Show or update session settings:

```shell
session config show
session config set sslstripping false
```

Start the session on an interface in your authorized lab:

```shell
session start --interface eth0 --target-ip 172.28.0.20 --gateway 172.28.0.1
```

Stop the session and clean up:

```shell
session stop
cleanup
```

## Commands

### Application

| Command | What it does |
| --- | --- |
| `help` | Show available shell commands. |
| `clear` | Clear the terminal screen. |
| `exit` | Exit the Gl4dius shell. |

### Global Configuration

| Command | What it does |
| --- | --- |
| `config show` | Show global settings. |
| `config set <key> <value>` | Update a global setting. |
| `cleanup` | Remove leftover local network changes from a previous run. |

Common global keys:

| Key | Meaning |
| --- | --- |
| `PPORT` | Proxy server port. |
| `WPORT` | Web server port. |
| `SRU` | Static resource URI. |

### Session Management

| Command | What it does |
| --- | --- |
| `session init --name <name> --description <text>` | Create a session. |
| `session ls` | List sessions. |
| `session get <id-or-name>` | Show one session. |
| `session edit <id-or-name> --name <name> --description <text>` | Update a session. |
| `session rm <id-or-name>` | Delete a session. |
| `session switch <id-or-name>` | Make a session active. |
| `session exit` | Leave the current session. |

### Session Modes

| Mode | Alias | Useful settings |
| --- | --- | --- |
| `SNIFFING` | `sn` | `sslstripping` |
| `DEFACING` | `def` | `template`, `verbose` |
| `PHISHING` | `ph` | `domain`, `template`, `sslstripping`, `verbose` |

Set a mode:

```shell
session mode sn
session mode def
session mode ph
```

For template-based modes, point `template` at an HTML file. In the bundled lab, the sample template is mounted at:

```shell
session config set template /home/gl4dius/template/index.html
```

### Session Execution

| Command | What it does |
| --- | --- |
| `session start --interface <nic>` | Start the active session for a lab interface. |
| `session start --interface <nic> --target-ip <ip>` | Start against a specific authorized lab target. |
| `session start --interface <nic> --target-ip <ip> --target-mac <mac> --gateway <ip>` | Start with explicit target and gateway details. |
| `session stop` | Stop the active session. |

### ARP Lab Command

| Command | What it does |
| --- | --- |
| `arp poison --interface <nic>` | Start ARP poisoning in the current authorized lab network. |
| `arp poison --interface <nic> --target-ip <ip> --spoof <ip>` | Start with explicit target and spoofed address. |
| `arp poison --interface <nic> --daemon true` | Run the ARP module in the background. |

## Development Commands

Run tests:

```shell
./mvnw test
```

Build the project:

```shell
./mvnw clean package
```

Start the app from source:

```shell
./mvnw -pl cli spring-boot:run
```

Start the Docker lab:

```shell
./misc/installation/dev-env/dev-shell.sh
```

## Responsible Use

Gl4dius can affect network traffic and host networking state. Keep usage inside networks where you have explicit permission, prefer isolated lab environments, and run `session stop` followed by `cleanup` when finished.

## Contributing

Bug reports and feature suggestions are welcome through GitHub Issues.

## License

Gl4dius is released under the [GNU General Public License v3.0](LICENSE).
