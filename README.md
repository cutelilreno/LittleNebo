# ✨ Little Nebo
[![Java CI with Maven](https://github.com/cutelilreno/LittleNebo/actions/workflows/maven.yml/badge.svg)](https://github.com/cutelilreno/LittleNebo/actions/workflows/maven.yml)

A lightweight, lovingly-built chat plugin for Paper servers - inspired by **Nebo**, the God of wisdom and communication.  
**Little Nebo** keeps your chat customisable and safe, with graceful support for MiniMessage, PlaceholderAPI, and legacy color codes.

Designed with 1.21 in mind, but backported to run cleanly on **1.19.1+**. 🧵

---
## 🌷 Features

- 💬 **MiniMessage formatting** — create flexible chat layouts with color, hover text, gradients, and more.
- 🧩 **PlaceholderAPI integration** — support for any registered placeholder (e.g. `%player_name%`, `%luckperms_prefix%`).
- 🎨 **Legacy color code support** — allows player messages to use `&`-style Minecraft color codes (toggleable).
- 🌈 **Pride tag support (1.19.1+)** — includes backported `<pride:...>` tags for servers before 1.21.
- 🔐 **Safe input parsing** — filters out dangerous tags like `<click>` or `<hover>` in player messages.
- 🛠 **Simple admin tools** — reload config, toggle debug, or test formats live in-game.

---
## 📸 Pride Tags!

Little Nebo includes support for MiniMessage `<pride:...>` tags even if your server doesn't natively support them.

![Pride chat demo](https://raw.githubusercontent.com/cutelilreno/LittleNebo/main/.github/assets/pride-demo.png)

---
## 📥 Installation

1. Drop `LittleNebo.jar` into your `/plugins/` folder.
2. *(Optional)* Add PlaceholderAPI for extended placeholder support.
3. Start your server once to generate the config.
4. Open `/plugins/LittleNebo/config.yml` to start customizing!

---
## 🧭 Commands

| Command                        | Permission         | Description                             |
|-------------------------------|---------------------|-----------------------------------------|
| `/littlenebo reload`          | `littlenebo.reload` | Reloads the plugin config.              |
| `/littlenebo debug`           | `littlenebo.admin`  | Toggles debug mode on/off.              |
| `/littlenebo debug config`    | `littlenebo.admin`  | Shows current configuration values.     |
| `/littlenebo debug test <msg>`| `littlenebo.admin`  | Previews how `<msg>` will be formatted. |

---
## 🎀 Permissions

- `littlenebo.admin` - manage the plugin and test formats  
- `littlenebo.format.*` - use named chat formats like `admin`, `donator`, etc.

---
## 🧾 Configuration Example

Here's a simple default format using MiniMessage:

```yaml
formats:
  default:
    format: "<gray>[<green>Chat</green>]</gray> <white>{display_name}</white><gray>: </gray><white>{message}</white>"
```

---
## Notes:

- `{display_name}` and `{message}` are automatically replaced by the plugin.
- You can include `%player_name%` or any PlaceholderAPI placeholder, if installed.
- Set `settings.parse-player-colors` to `true` to allow `&` codes in player messages.

More examples and explanations are available in the generated config.yml.

## 🛠 Development & Contribution

This project is still growing! If you'd like to help:
- Open an issue if something's broken or confusing 🧵
- Pull requests welcome for bug fixes or improvements 🤝
- Test changes carefully before using them on production servers 🧪
Thank you for helping Little Nebo grow. 💛

---
## 📚 Documentation

- [View JavaDocs](https://cutelilreno.github.io/LittleNebo/)

---
## 📜 License

MIT License - use it, remix it, improve it. Just be kind. 

---

This plugin was made with care and curiosity - thank you for your patience while I learn. 🫶