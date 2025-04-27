# Little Nebo

[![Java CI with Maven](https://github.com/cutelilreno/LittleNebo/actions/workflows/maven.yml/badge.svg)](https://github.com/cutelilreno/LittleNebo/actions/workflows/maven.yml)

A lightweight chat plugin for Paper servers, inspired by Nebo, the god of communication and wisdom. Little Nebo aims to make chat formatting simple and flexible, using MiniMessage and PlaceholderAPI, while keeping things as safe and efficient as possible.

Created to be a functional and configurable chat plugin, but as lean as possible. Designed for papermc 1.21+.

---

## 🌟 Features

- **Custom MiniMessage formats**: Define your own chat layouts with colors, gradients, and text decorations.
- **PlaceholderAPI support**: Use any PlaceholderAPI placeholder in configuration.
- **Optional legacy codes**: Enable `&`-style color codes in player messages if you prefer.
- **Safe parsing**: Player input is sanitized to prevent interactive tags (click, hover, etc.).
- **Easy** `/littlenebo` **commands**: Toggle debug, and preview formats in-game. (reload coming soon 🙏)

---

## 📥 Installation

1. Build or download `LittleNebo.jar`.
2. Place it into your server’s `/plugins/` folder.
3. Install PlaceholderAPI (required)
4. Start or restart the server.
5. Edit `plugins/LittleNebo/config.yml` to adjust formats and settings.

---

## 📖 Usage

### Commands

| Command                        | Permission         | Description                                     |
| ------------------------------ | ------------------ | ----------------------------------------------- |
| `/littlenebo reload`           | `littlenebo.admin` | Reloads the plugin configuration. (not working) |
| `/littlenebo debug`            | `littlenebo.admin` | Toggles debug logging.                          |
| `/littlenebo debug config`     | `littlenebo.admin` | Shows current config values in chat.            |
| `/littlenebo debug test <msg>` | `littlenebo.admin` | Previews how `<msg>` will be formatted.         |

### Permissions

- `littlenebo.admin` — manage plugin (reload, debug)
- `littlenebo.format.*` — use custom formats (admin, staff, vip, etc.)

---

## ⚙️ Configuration (`config.yml`)

A sample snippet for default format:

```yaml
formats:
  default:
    format: "<gray>[<green>Chat</green>]</gray> <white>{display_name}</white><gray>: </gray><white>{message}</white>"
```

- **formats**: Define named formats with `format` (MiniMessage) or `legacy-format` (`&` codes).
- **settings.parse-player-colors**: `true` to allow `&` codes in messages.

For more details, see comments in the default `config.yml`.

---

## 🛠 Development & Contribution

This plugin is still a work in progress, and I’d appreciate any feedback or fixes:

- Feel free to open an issue if something isn’t working as expected.
- Pull requests are welcome for enhancements or bug fixes.
- Please test changes carefully before using on production servers.

Thanks for trying Little Nebo! 😊

---
## 📚 Documentation

- [View JavaDocs](https://cutelilreno.github.io/LittleNebo/)

---
## 📜 License

MIT License — see `LICENSE` for details.

---

*I’m learning as I go, so there may be things I’ve overlooked. Thanks for your patience and help!*

