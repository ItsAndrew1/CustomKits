![CustomKitsBanner](https://github.com/user-attachments/assets/6ec6c283-8387-46ce-98ba-ccd1082be3d5)

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
<img width="1293" height="688" alt="mainConfigSS" src="https://github.com/user-attachments/assets/0d86f22d-be5a-4f60-acec-452779f6edb7" />

### `kits.yml`
Contains every kit that you may create
<img width="1026" height="817" alt="kitsSS" src="https://github.com/user-attachments/assets/6dd5f6e7-19f0-4f38-83ba-a21a9e6f4565" />

### `messages.yml`
Contains the settings for the ***custom chat prefix*** and other *important messages*
<img width="869" height="364" alt="messagesSS" src="https://github.com/user-attachments/assets/7a2f2287-d28a-45bc-98be-49545736ef98" />

### `playerdata.yml`
Contains each player's cooldown data. **It is advised not to change anything here!**
<img width="822" height="820" alt="playerDataSS" src="https://github.com/user-attachments/assets/f7085dc6-c379-436a-a73f-40bad6a862d7" />

---

## ❤️Credits
Developed by **\_ItsAndrew_**

Special thanks to everyone who help, test and give feedback!

My discord: **\_itsandrew_**
