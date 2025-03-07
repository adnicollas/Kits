# Minecraft Kits Plugin

## 📄 About
A lightweight Minecraft Kits plugin that allows players to easily access predefined kits.

## ⚡ Features
- Customizable kits;
- Easy-to-use commands for players and admins;
- Permissions support for different kit access levels.

## 📥 Installation
1. Download the latest release of the plugin;
2. Place the .jar file into your server's plugins folder;
3. Restart the server.

## 🤖 Commands & Permissions
| Command                          | Description                                | Permission   |
| -------------------------------- | ------------------------------------------ | ------------ |
| `/createkit <id> <permission> <cooldown>` | Creates a new kit with a specific ID, permission, and cooldown | `command.createkit` |
| `/deletekit <id>`                | Deletes an existing kit by its ID         | `command.deletekit` |
| `/editkit <id>`                  | Modifies the contents of an existing kit                  | `command.editkit` |
| `/givekit <player> <id>`         | Gives a specific kit to a player          | `command.givekit` |
| `/kit <id>`                      | Claims an available kit                   ||
| `/viewkit <id>`                  | Previews the contents of a kit            ||

## 🔗 Dependencies
- **Bukkit/Spigot/Paper**: Compatible with versions 1.8 to 1.16.5.

## 🛠️ Technologies Used
- **SQLite** (using [Xerial SQLite JDBC](https://github.com/xerial/sqlite-jdbc)) for storing kit data;
- **Lombok** for simplifying code with annotations like @Getter, @Setter, and @AllArgsConstructor.

## 💡 Future Plans
- GUI for selecting kits;
- Economy integration;
- More customizable options.

## 📄 License
This software is released under the **MIT** License.

## 🤝 Contributing
Contributions are welcome! Feel free to open an issue or submit a pull request.
