# hMOTD

![Java CI](https://github.com/Hauchdev/hMOTD/actions/workflows/ci.yml/badge.svg)

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

- `hmotd.reload` — Allows using `/hmotd reload` and `/hmotd preview`.

## Configuration

See `src/main/resources/config.yml` for the full default configuration with comments.

### Example

```yaml
motds:
  - "<gradient:#00FF00:#0000FF>hMOTD</gradient>\n<white>Optimized MOTD for Velocity"
  - "&aWelcome to &6&lhMOTD &r&7- &e%online%/%max% players"
  - "#FF5555%server_name% &bwith mixed formats"

rotation-mode: RANDOM
format-mode: MIXED

center:
  enabled: true
  mode: CENTER
  width: 270
  offset: 0

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

- `MINIMESSAGE`: Only parse MiniMessage tags (`<red>`, `<gradient>`, `<#RRGGBB>`, etc.). Standalone hex colors like `#RRGGBB` are also supported.
- `LEGACY`: Only parse legacy `&` codes and hex colors converted to legacy `§x§R§R§G§G§B§B` format.
- `MIXED`: Automatically detects whether the input is MiniMessage or legacy and parses accordingly. You can safely mix tags and standalone hex colors.

## Multi-line MOTDs

Use `\n` inside any MOTD string to split it into multiple lines. Each line is centered independently when centering is enabled.

## Centering & alignment

The Minecraft server list renders the MOTD left aligned inside a fixed text area, so exact centering
has to be emulated by prefixing each line with space characters. hMOTD does this with **pixel
accurate** measurements:

1. The line is rendered into an Adventure `Component` (MiniMessage / legacy).
2. The real advance width of every glyph is looked up in a table extracted from the vanilla
   `assets/minecraft/font` definitions. The table contains the *advance* (glyph width plus left
   bearing) for regular **and bold** text, because bold glyphs are rendered wider.
3. The padding is computed in pixels and converted into whole spaces (a space is 4 pixels wide).
4. Multi line MOTDs are aligned line by line, so every line is centered individually.

### Settings

```yaml
center:
  enabled: true
  # LEFT, CENTER or RIGHT
  mode: CENTER
  # Width of the server list text area in pixels.
  # 270 matches the vanilla server list and is the value used by most MOTD plugins.
  width: 270
  # Fine tuning offset in pixels, positive moves the text to the right
  offset: 0
```

Because the padding can only be applied in whole spaces the result can be off by at most two pixels.
If the text does not look perfectly centered on your resolution, adjust `center.offset`.

Use `/hmotd preview` to see the rendered MOTD and the active alignment settings in game.

## License

This project is licensed under the MIT License. See `LICENSE` for details.
