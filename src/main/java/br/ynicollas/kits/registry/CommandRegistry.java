package br.ynicollas.kits.registry;

import br.ynicollas.kits.KitsPlugin;
import br.ynicollas.kits.commands.*;
import br.ynicollas.kits.storage.cooldowns.CooldownsStorage;
import br.ynicollas.kits.storage.kits.KitsStorage;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;

public class CommandRegistry {
    private final KitsPlugin plugin;

    private final CooldownsStorage cooldowns;
    private final KitsStorage kits;

    public CommandRegistry(KitsPlugin plugin, CooldownsStorage cooldowns, KitsStorage kits) {
        this.plugin = plugin;
        this.cooldowns = cooldowns;
        this.kits = kits;
    }

    public void registerCommands() {
        register("createkit", new CreateKitCommand(kits));
        register("givekit", new GiveKitCommand(kits));
        register("deletekit", new DeleteKitCommand(cooldowns, kits));
        register("editkit", new EditKitCommand(kits));
        register("kit", new KitCommand(cooldowns, kits));
        register("viewkit", new ViewKitCommand(kits));
    }

    private void register(String command, CommandExecutor executor) {
        PluginCommand pluginCommand = plugin.getCommand(command);

        if (pluginCommand == null) {
            plugin.getLogger().warning("Comando " + command + " não registrado no plugin.yml!");
            return;
        }

        pluginCommand.setExecutor(executor);
    }
}