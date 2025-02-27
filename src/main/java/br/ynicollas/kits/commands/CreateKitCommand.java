package br.ynicollas.kits.commands;

import br.ynicollas.kits.model.Kit;
import br.ynicollas.kits.model.KitCooldown;
import br.ynicollas.kits.listeners.InventoryCloseListener;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class CreateKitCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Apenas jogadores podem utilizar este comando.");
            return false;
        }

        Player player = (Player) sender;

        if (args.length != 3) {
            player.sendMessage(ChatColor.RED + "Uso correto: /criarkit <id> <permissão> <cooldown>");
            return false;
        }

        if (!player.hasPermission("command.createkit")) {
            player.sendMessage(ChatColor.RED + "Você não tem permissão para executar este comando.");
            return false;
        }

        String id = args[0].toLowerCase();
        String permission = args[1];
        String cooldownStr = args[2];

        int cooldown;

        try {
            cooldown = Integer.parseInt(cooldownStr);
        } catch (NumberFormatException exception) {
            player.sendMessage(ChatColor.RED + "O cooldown deve ser um número válido.");
            return false;
        }

        Inventory kitInventory = player.getServer().createInventory(null, 54, ChatColor.DARK_GRAY + "Kit");

        player.openInventory(kitInventory);

        KitCooldown kitCooldown = new KitCooldown(0, 0, cooldown);

        Kit kit = new Kit(id, permission, kitCooldown, null);

        InventoryCloseListener.setCurrentKit(player, kit);

        player.sendMessage(ChatColor.YELLOW + "Kit criado com sucesso.");

        return true;
    }
}