# EasyStarterKit

> **Built for the European Hytale survival server at `play.hyfyve.net`**

Give new players a starter inventory! Simple starter kit plugin for Hytale servers.

---

## Quick Start

1. Download the latest `EasyStarterKit.jar` from [Releases](../../releases)
2. Put it in your server's `mods` folder
3. Restart your server
4. Put items in your inventory and run `/easystarterkit set`
5. Done! New players will receive those items on first join

---

## Commands

| Command | What it does |
|---------|--------------|
| `/easystarterkit set` | Save your current inventory as the starter kit |
| `/easystarterkit give` | Give yourself the starter kit (for testing) |
| `/easystarterkit reset` | Reset your "received kit" status |

---

## How It Works

1. **Admin** puts items in their hotbar/inventory
2. **Admin** runs `/easystarterkit set`
3. **New player** joins the server for the first time
4. **New player** automatically receives the saved items

That's it!

---

## Permissions

| Permission | What it does |
|------------|--------------|
| `starterkit.admin` | Can use all `/easystarterkit` commands |

New players receive the kit automatically - no permission needed!

---

## FAQ

**Q: Where is the starter kit saved?**

In your server's `mods/cryptobench_EasyStarterKit/starterkit.json`

**Q: Can I edit the kit manually?**

Yes! Edit the JSON file and restart the server.

**Q: What if I want to test the kit?**
```
/easystarterkit reset
```
Then rejoin the server, or use `/easystarterkit give` to get the items instantly.

**Q: What gets saved in the kit?**

Only items in your hotbar and main inventory (storage). Not equipped armor or editor tools.

**Q: Will players get the kit every time they join?**

No, only on their first join. The plugin tracks who has received the kit.

---

## License

MIT - Do whatever you want with it!
