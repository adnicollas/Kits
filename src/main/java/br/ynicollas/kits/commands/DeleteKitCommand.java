package br.ynicollas.kits.commands;

import br.ynicollas.kits.model.Kit;
import br.ynicollas.kits.storage.cooldowns.CooldownsStorage;
import br.ynicollas.kits.storage.kits.KitsStorage;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class DeleteKitCommand implements CommandExecutor {

    private final CooldownsStorage cooldowns;
    private final KitsStorage kits;

    public DeleteKitCommand(CooldownsStorage cooldowns, KitsStorage kits) {
        this.cooldowns = cooldowns;
        this.kits = kits;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (args.length != 1) {
            sender.sendMessage(ChatColor.RED + "Uso correto: /deletekit <id>");
            return false;
        }

        if (!sender.hasPermission("command.deletekit")) {
            sender.sendMessage(ChatColor.RED + "Você não tem permissão para executar este comando.");
            return false;
        }

        String id = args[0];

        Kit kit = kits.getKit(id);

        if (kit == null) {
            sender.sendMessage(ChatColor.RED + "Kit não encontrado!");
            return false;
        }

        cooldowns.clear(id);
        kits.removeKit(id);

        sender.sendMessage(ChatColor.YELLOW + "Kit deletado com sucesso.");

        return true;
    }
}