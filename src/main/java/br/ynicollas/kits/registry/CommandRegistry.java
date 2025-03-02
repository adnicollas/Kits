package br.ynicollas.kits.registry;

import br.ynicollas.kits.commands.*;
import br.ynicollas.kits.storage.cooldowns.CooldownsStorage;
import br.ynicollas.kits.storage.kits.KitsStorage;
import org.bukkit.command.CommandExecutor;
import org.bukkit.plugin.java.JavaPlugin;

public class CommandRegistry {
    private final JavaPlugin plugin;

    private final CooldownsStorage cooldowns;
    private final KitsStorage kits;

    public CommandRegistry(JavaPlugin plugin, CooldownsStorage cooldowns, KitsStorage kits) {
        this.plugin = plugin;
        this.cooldowns = cooldowns;
        this.kits = kits;
    }

    public void registerCommands() {
        register("createkit", new CreateKitCommand());
        register("givekit", new GiveKitCommand(kits));
        register("deletekit", new DeleteKitCommand(cooldowns, kits));
        register("editkit", new EditKitCommand(kits));
        register("kit", new KitCommand(cooldowns, kits));
        register("viewkit", new ViewKitCommand(kits));
    }

    private void register(String command, CommandExecutor executor) {
        if (plugin.getCommand(command) != null) {
            plugin.getCommand(command).setExecutor(executor);
        } else {
            plugin.getLogger().warning("Comando '" + command + "' não registrado no plugin.yml!");
        }
    }
}