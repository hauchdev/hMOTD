# hMOTD

A highly optimized **Velocity** MOTD plugin inspired by [MiniMOTD](https://github.com/jpenilla/MiniMOTD), built from the ground up for performance and flexibility.

## Features

- **Velocity-first**: Designed exclusively for the Velocity proxy platform.
- **Multiple text formats**: Supports MiniMessage, legacy `&` color codes, hex colors (`#RRGGBB` / `&#RRGGBB`) and a smart mixed mode.
- **Automatic MOTD centering**: Calculates Minecraft font widths to center your MOTD in the server list.
- **MOTD rotation**: Random, sequential or disabled rotation across multiple MOTDs.
- **Rotating server icons**: Use a single icon or a list of icons with rotation modes.
- **Server info overrides**: Custom version text, fake/max player counts, hover sample lines.
- **Placeholders**: Built-in `%online%`, `%max%`, `%proxy_name%`, `%time%`, `%date%` plus user-defined extras.
- **Hot reload**: `/hmotd reload` reloads configuration on the fly.
- **Optimized**: Parsed components and icons are cached; formatting codes are stripped efficiently for centering.

## Requirements

- Java 21+
- Velocity 3.x

## Installation

1. Build the plugin with Maven: `mvn clean package`
2. Copy `target/hMOTD-*.jar` to your Velocity `plugins/` folder.
3. Start the proxy to generate the default configuration.
4. Edit `plugins/hMOTD/config.yml` and run `/hmotd reload`.

## Permissions

- `hmotd.reload` — Allows using `/hmotd reload`.

## Configuration

See `src/main/resources/config.yml` for the full default configuration with comments.

### Example

```yaml
motds:
  - "<gradient:#00FF00:#0000FF>hMOTD</gradient> <white>Optimized MOTD"
  - "&aWelcome to &6&lhMOTD &r&7- &e%online%/%max% players"
  - "<#FF5555>%server_name% &bwith mixed formats"

rotation-mode: RANDOM
format-mode: MIXED

center:
  enabled: true
  width: 248

icon:
  enabled: true
  file: "server-icon.png"
  files:
    - "icon-day.png"
    - "icon-night.png"
  rotation-mode: RANDOM

server-info:
  override-version: true
  version-text: "<#FF0000>hMOTD"
  override-players: false
  online-players: 0
  max-players: 100
  hide-online: false
  hide-max: false
  override-sample: true
  sample-lines:
    - "&aPowered by hMOTD"
    - "&bIt is %time% on %date%"

placeholders: true

extra-placeholders:
  server_name: "My Awesome Server"

debug: false
```

## Format Modes

- `MINIMESSAGE`: Only parse MiniMessage tags (`<red>`, `<gradient>`, `<#RRGGBB>`, etc.).
- `LEGACY`: Only parse legacy `&` codes and hex colors converted to legacy format.
- `MIXED`: Automatically detects whether the input is MiniMessage or legacy and parses accordingly.

## Centering

When `center.enabled` is `true`, hMOTD strips visible formatting codes and tags, calculates the rendered width using an internal Minecraft font width table, and prepends spaces so the MOTD appears centered in the server list.

## License

This project is licensed under the MIT License. See `LICENSE` for details.
