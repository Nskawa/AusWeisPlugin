# AusWeis — Cloudflare Turnstile Human Verification Plugin

[CN](https://github.com/ChineseLiyao/AusWeisPlugin/blob/main/README_CN.md) | [EN](https://github.com/ChineseLiyao/AusWeisPlugin/blob/main/README.md)

[![License](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)
[![Bukkit](https://img.shields.io/badge/Bukkit-1.8+-orange.svg)](https://www.spigotmc.org/)
[![Paper](https://img.shields.io/badge/Paper-1.8+-yellow.svg)](https://papermc.io/)
[![Maven](https://img.shields.io/badge/Maven-3.6+-brightgreen.svg)](https://maven.apache.org/)

**AusWeis** is a lightweight Bukkit/Paper plugin that integrates Cloudflare Turnstile to implement human verification before players can join the server, effectively preventing malicious bot attacks. Players are kicked on first login with a verification URL displayed, and can rejoin normally after completing verification on the webpage.

---

## Features

- **Simple verification flow**: Player login → Kicked with URL → Web verification → Rejoin server
- **Real-time API queries**: Verification status is checked via your backend API on each login
- **Highly customizable**: Kick messages, API URLs, timeout settings, and more can be configured via `config.yml`
- **Command reload**: Use `/ausweis reload` to apply config changes without restarting the server
- **Secure**: Based on Cloudflare Turnstile, no captcha interaction required, player-friendly
- **Full version compatibility**: Supports Spigot/Paper 1.8 to latest versions (tested on 1.12+, 1.21+)

---

## Installation

1. **Download the plugin**: Get the latest `AusWeis-*.jar` from the [Releases](https://github.com/Nskawa/AusWeisPlugin/releases) page
2. **Place in plugins folder**: Copy the jar file to your server's `plugins/` directory
3. **Start the server**: Start or restart your server, the plugin will automatically generate default config files
4. **Configure API and verification page**: Edit `plugins/AusWeis/config.yml` and fill in your backend API address
5. **Reload config**: Execute `/ausweis reload` in-game to apply the configuration

---

## Configuration File

```yaml
# Language file to use (e.g., en, zh). Must correspond to a messages_<lang>.yml file in the lang folder.
# 使用的语言文件（例如 en, zh）。必须与 lang 文件夹中的 messages_<语言>.yml 文件对应。
language: "en"

# API URL to check player verification status
# API 地址，用于检查玩家验证状态
api-url: "https://ausweis.lya.bz/api/check?user={player}"

# Verification page URL to show in kick message
# 验证页面地址，踢出时显示
verify-url: "https://ausweis.lya.bz?user={player}"

# HTTP request timeout in milliseconds
# HTTP 请求超时时间（毫秒）
timeout: 5000

# Debug mode, enables more detailed logs
# 调试模式，开启后会输出更多日志
debug: false
```

---

## Backend Reference Implementation

The companion verification website backend for this project is developed by [@ChineseLiyao](https://github.com/ChineseLiyao), with source code at: [ChineseLiyao/AusWeis](https://github.com/ChineseLiyao/AusWeis). You can deploy it directly or reference its implementation.

---

**Parameter Descriptions:**

- `api-url`: Your backend API address. The plugin sends GET requests to this URL, expecting a JSON response `{"verified": true/false}`.
- `verify-url`: Verification page address. Players will see this link when kicked. Ensure the page can retrieve the `user` parameter and complete Turnstile verification.
- `timeout`: API request timeout in milliseconds.
- `debug`: When enabled, more detailed debug information will be output to the console.
- `kick-message`: Kick message, supports Minecraft color codes (`§`) and multi-line text. The placeholder `{verify-url}` will be automatically replaced.

---

## Commands & Permissions

| Command | Description | Permission |
|---------|-------------|------------|
| `/ausweis reload` | Reload plugin configuration | `ausweis.reload` (default OP) |
| `/ausw reload` | Same as above (alias) | `ausweis.reload` |

---

## Building

If you want to compile the plugin yourself, ensure you have JDK 8+ and Maven installed.

```bash
git clone https://github.com/Nskawa/AusWeisPlugin.git
cd AusWeis
mvn clean package
```

The compiled jar file will be located in the `target/` directory.

---

## API Interface Specification

The plugin expects your backend to provide the following two endpoints:

### 1. Verification Status Query

- **URL**: `GET {api-url}?user={player}`
- **Response Format**: JSON

```json
{
  "verified": true,
  "user": "Steve",
  "timestamp": 1709123456.789
}
```

- **Description**: `verified` being `true` indicates the player has passed human verification, otherwise `false`.

### 2. Verification Page

- **URL**: `{verify-url}?user={player}`
- **Function**: Adds verification. After successful verification, the backend should mark the corresponding player as verified (e.g., update database), so API queries return `true`.

> You can reference the example backend project: [AusWeis-Web](https://github.com/Nskawa/AusWeisWeb)

---

## Support

Please visit our [project homepage](https://aus.lya.bz).
