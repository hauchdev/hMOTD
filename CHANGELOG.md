# Changelog

## 1.0.0

### Added
- Initial release of hMOTD for Velocity.
- Support for MiniMessage, legacy `&` codes and hex colors.
- Standalone hex color support inside MiniMessage and mixed modes.
- Mixed format mode with robust automatic detection.
- MOTD rotation: random, sequential and disabled.
- Multi-line MOTD support with independent line centering.
- Automatic MOTD centering based on an accurate Minecraft font width table.
- Configurable centering space width.
- Rotating server icons with multiple files.
- Player count, version text and sample hover overrides.
- Built-in placeholders (`%online%`, `%max%`, `%proxy_name%`, `%time%`, `%date%`).
- User-defined custom placeholders.
- Hot configuration reload via `/hmotd reload`.
- Unit tests for formatter, centering, placeholders and MOTD manager.
