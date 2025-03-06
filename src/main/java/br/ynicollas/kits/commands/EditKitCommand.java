package br.ynicollas.kits.commands;

import br.ynicollas.kits.models.Kit;
import br.ynicollas.kits.listeners.InventoryCloseListener;
import br.ynicollas.kits.storage.kits.KitsStorage;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class EditKitCommand implements CommandExecutor {

    private final KitsStorage kits;

    public EditKitCommand(KitsStorage kits) {
        this.kits = kits;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Apenas jogadores podem utilizar este comando.");
            return false;
        }

        Player player = (Player) sender;

        if (args.length != 1) {
            player.sendMessage(ChatColor.RED + "Uso correto: /editarkit <id>");
            return false;
        }

        if (!player.hasPermission("command.editkit")) {
            player.sendMessage(ChatColor.RED + "Você não tem permissão para executar este comando.");
            return false;
        }

        String id = args[0].toLowerCase();

        Kit kit = kits.getKit(id);

        if (kit == null) {
            player.sendMessage(ChatColor.RED + "O kit " + id + " não existe.");
            return false;
        }

        Inventory kitInventory = player.getServer().createInventory(null, 54, ChatColor.DARK_GRAY + "Kit");

        if (kit.getItems() != null) {
            kitInventory.setContents(kit.getItems());
        }

        player.openInventory(kitInventory);

        InventoryCloseListener.setCurrentKit(player, kit);

        player.sendMessage(ChatColor.YELLOW + "Você editou o kit com sucesso!");

        return true;
    }
}