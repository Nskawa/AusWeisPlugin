# AusWeis — Cloudflare Turnstile 人机验证插件

[CN](https://github.com/ChineseLiyao/AusWeisPlugin/blob/main/README_CN.md) | [EN](https://github.com/ChineseLiyao/AusWeisPlugin/blob/main/README.md)

[![License](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)
[![Bukkit](https://img.shields.io/badge/Bukkit-1.13+-orange.svg)](https://www.spigotmc.org/)
[![Paper](https://img.shields.io/badge/Paper-1.21+-yellow.svg)](https://papermc.io/)
[![Maven](https://img.shields.io/badge/Maven-3.6+-brightgreen.svg)](https://maven.apache.org/)

**AusWeis** 是一款轻量级的 Bukkit/Paper 插件，通过集成 Cloudflare Turnstile 实现玩家进入服务器前的人机验证，有效防止机器人恶意攻击。玩家首次登录会被踢出并显示验证网址，在网页完成验证后即可正常进入。

---

## 特性

- **简单的验证流程**：玩家登录 → 踢出并显示网址 → 网页验证 → 重新进入
- **实时 API 查询**：每次登录均向你的后端 API 查询验证状态
- **高度可定制**：踢出消息、API 地址、超时时间等均可通过 `config.yml` 配置
- **命令重载**：修改配置后无需重启服务器，使用 `/ausweis reload` 立即生效
- **安全**：基于 Cloudflare Turnstile，无需验证码交互，对玩家友好
- **全版本兼容**：支持 Spigot/Paper 1.8 至最新版（1.12+、1.21+ 已测试）

---

## 安装

1. **下载插件**：从 [Releases](https://github.com/Nskawa/AusWeisPlugin/releases) 页面获取最新版 `AusWeis-*.jar`
2. **放入插件文件夹**：将 jar 文件复制到服务器的 `plugins/` 目录下
3. **启动服务器**：启动或重启服务器，插件会自动生成默认配置文件
4. **配置 API 和验证页面**：编辑 `plugins/AusWeis/config.yml`，填写你的后端接口地址
5. **重载配置**：在游戏中执行 `/ausweis reload` 使配置生效

---

## 配置文件

```yaml
# API URL to check player verification status
api-url: "https://ausweis.lya.bz/api/check?user={player}"

# Verification page URL to show in kick message
verify-url: "https://ausweis.lya.bz?user={player}"

# HTTP request timeout in milliseconds
timeout: 5000

# Debug mode, enables more detailed logs
debug: false

# Kick message (supports multiple lines and color codes)
kick-message: "§c⛔ Verification Required\n§7Please visit the following link to verify:\n§b§l{verify-url}\n§7Rejoin the server after verification"

# Async thread pool size
thread-pool-size: 4
```
---

## 后端参考实现

本项目配套的验证网站后端由 [@ChineseLiyao](https://github.com/ChineseLiyao) 开发，源码位于：[ChineseLiyao/AusWeis](https://github.com/ChineseLiyao/AusWeis)。你可以直接部署使用或参考其实现。

---

**参数说明：**
- `api-url`：你的后端 API 地址，插件会向此地址发送 GET 请求，需返回 JSON `{"verified": true/false}`。
- `verify-url`：验证页面地址，玩家被踢时会看到此链接，请确保页面能获取 `user` 参数并完成 Turnstile 验证。
- `timeout`：API 请求超时时间（毫秒）。
- `debug`：开启后控制台会输出更多调试信息。
- `kick-message`：踢出消息，支持 Minecraft 颜色代码（`§`）和多行文本，占位符 `{verify-url}` 会被自动替换。

---

## 命令与权限

| 命令 | 描述 | 权限 |
|------|------|------|
| `/ausweis reload` | 重载插件配置 | `ausweis.reload`（默认 OP） |
| `/ausw reload` | 同上（别名） | `ausweis.reload` |

---

## 构建

如果你希望自行编译插件，请确保已安装 JDK 8+ 和 Maven。

```bash
git clone https://github.com/Nskawa/AusWeisPlugin.git
cd AusWeis
mvn clean package
```

编译后的 jar 文件位于 `target/` 目录下。

---

## API 接口规范

插件期望你的后端提供以下两个端点：

### 1. 验证状态查询
- **URL**：`GET {api-url}?user={player}`
- **响应格式**：JSON
  ```json
  {
    "verified": true,
    "user": "Steve",
    "timestamp": 1709123456.789
  }
  ```
- **说明**：`verified` 为 `true` 表示玩家已通过人机验证，否则为 `false`。

### 2. 验证页面
- **URL**：`{verify-url}?user={player}`
- **功能**：添加验证，验证成功后后端应将对应玩家标记为已验证（例如更新数据库），使 API 查询返回 `true`。

> 你可以参考示例后端项目：[AusWeis-Web](https://github.com/Nskawa/AusWeisWeb)

---

## 支持

请访问我们的[项目主页](https://aus.lya.bz).
