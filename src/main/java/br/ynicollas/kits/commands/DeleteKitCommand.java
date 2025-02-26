package br.ynicollas.kits.commands;

import br.ynicollas.kits.model.Kit;
import br.ynicollas.kits.storage.cooldown.CooldownStorage;
import br.ynicollas.kits.storage.kits.KitStorage;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class DeleteKitCommand implements CommandExecutor {

    private final CooldownStorage cooldownStorage;
    private final KitStorage kitStorage;

    public DeleteKitCommand(CooldownStorage cooldownStorage, KitStorage kitStorage) {
        this.cooldownStorage = cooldownStorage;
        this.kitStorage = kitStorage;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (args.length != 1) {
            sender.sendMessage(ChatColor.RED + "Uso correto: /deletekit <id>");
            return false;
        }

        if (!sender.hasPermission("kit.command.deletekit")) {
            sender.sendMessage(ChatColor.RED + "Você não tem permissão para executar este comando.");
            return false;
        }

        String id = args[0];

        Kit kit = kitStorage.getKit(id);

        if (kit == null) {
            sender.sendMessage(ChatColor.RED + "Kit não encontrado!");
            return false;
        }

        cooldownStorage.clear(id);
        kitStorage.removeKit(id);

        sender.sendMessage(ChatColor.YELLOW + "Kit deletado com sucesso.");

        return true;
    }
}