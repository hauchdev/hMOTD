# Changelog

## 1.0.0

### Added
- Initial release of hMOTD for Velocity.
- Support for MiniMessage, legacy `&` codes and hex colors.
- Standalone hex color support inside MiniMessage and mixed modes.
- Mixed format mode with robust automatic detection.
- MOTD rotation: random, sequential and disabled.
- Multi-line MOTD support with independent line alignment.
- Pixel accurate alignment using the real advance widths of the vanilla Minecraft font
  (including bold variants) extracted from the vanilla font assets.
- Alignment modes `LEFT`, `CENTER` and `RIGHT` with a configurable text area width and pixel offset.
- Rotating server icons with multiple files.
- Player count, version text and sample hover overrides.
- Built-in placeholders (`%online%`, `%max%`, `%proxy_name%`, `%time%`, `%date%`).
- User-defined custom placeholders.
- Hot configuration reload via `/hmotd reload`.
- `/hmotd preview` command to inspect the rendered MOTD and the active alignment settings.
- Unit tests for formatter, font metrics, alignment, placeholders, config and MOTD manager.
