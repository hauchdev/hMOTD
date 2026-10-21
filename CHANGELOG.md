# Changelog

## 1.1.0

### Changed
- **Rewrote the alignment engine.** The MOTD is now aligned using the real advance widths of the
  vanilla Minecraft font, including the bold variants, instead of an approximated glyph width table.
  The previous table used the raw glyph width which was missing the left bearing, so every line was
  rendered noticeably off centre.
- The padding is now rounded to the closest possible space, which reduces the maximum deviation
  from the centre to a single pixel.
- The alignment is applied on the rendered component, so bold text is measured correctly.
- The default text area width changed from `248` to `270` pixels, matching the vanilla server list
  and the value used by other MOTD plugins.

### Added
- Alignment modes `LEFT`, `CENTER` and `RIGHT`.
- `center.offset` option for fine tuning the alignment in pixels.
- `/hmotd test <text>` command that previews the alignment of an arbitrary text.
- Automatic migration of the removed `center.space-width` option and of the old default width.
- Unit tests covering the font metrics, the alignment accuracy and the configuration migration.

### Fixed
- MiniMessage tags are no longer broken by the hex colour normalisation.
- Multi line MOTDs are aligned line by line instead of only the first line.
- MOTDs containing live placeholders are no longer cached, so `%online%` and friends stay up to date.
- The user configuration file is no longer rewritten on every reload.

### Infrastructure
- GitHub Actions pipeline that publishes a signed GitHub Release for every `v*` tag, including the
  plugin, the sources, the javadoc, the detached GPG signatures and a `checksums.txt`.
- The release notes are generated automatically from the matching section of this changelog.
- The packaged plugin descriptor is verified before the release is published.
- Dependabot keeps the Maven dependencies and the GitHub Actions up to date.

## 1.0.0

### Added
- Initial release of hMOTD for Velocity.
- Support for MiniMessage, legacy `&` codes and hex colors.
- Standalone hex color support inside MiniMessage and mixed modes.
- Mixed format mode with robust automatic detection.
- MOTD rotation: random, sequential and disabled.
- Multi-line MOTD support.
- Rotating server icons with multiple files.
- Player count, version text and sample hover overrides.
- Built-in placeholders (`%online%`, `%max%`, `%proxy_name%`, `%time%`, `%date%`).
- User-defined custom placeholders.
- Hot configuration reload via `/hmotd reload`.
- `/hmotd preview` command to inspect the rendered MOTD and the active alignment settings.
