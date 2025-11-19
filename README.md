# CustomKits

**CustomKits** is a fully customizable plugin designed to help server *owners/admins* easily *create* and *manage* any type of kits. 

---

## ⚙️Features
- Customizable GUI
- Set the permission needed for getting a specific kit
- Real-time cooldown display in chat
- Fully configurable kits
- Custom prefix in the chat

**And many more**

---

## 🪄Commands
| Command | Description | Permission |
|----------|--------------|-------------|
| `/kitconfig create <name>` | Creates a kit in *kits.yml* | `kits.admin` |
| `/kitconfig delete <name>` | Deletes a kit from *kits.yml*| `kits.admin` |
| `/kitconfig reload` | Reloads all the *.yml files* | `kits.admin` |
| `/kitconfig help` | Gives you all the available commands | `kits.admin` |
| `/kitconfig manage <name> ...` | Main command for *managing* each kit | `kits.admin` |
| `/kits` | Opens the kits GUI | `No permission` |

#### Some heads-up:
- You can use **/kc** instead of **/kitconfig**
- Each command has a **tab completer**

#### ‼️About PERMISSIONS:
- It is advised to have a permission plugin on your server, such as LuckPerms. Otherwise, the plugin will still work, but there won't be any permissions.
- If you use **LuckPerms**, you can use this command:
 ```
 /lp group <group> permission set <permission> true
 ```

---

## 📁Configuration Files

### `config.yml`
Contains ***general settings*** for the plugin (setting the cooldown, configuring some GUI stuff and more)

### `kits.yml`
Contains every kit that you may create

### `messages.yml`
Contains the settings for the ***custom chat prefix*** and other *important messages*

### `playerdata.yml`
Contains each player's cooldown data. **It is advised not to change anything here!**

---

## ❤️Credits
Developed by **\_ItsAndrew_**

Special thanks to everyone who help, test and give feedback!

My discord: **\_itsandrew_**