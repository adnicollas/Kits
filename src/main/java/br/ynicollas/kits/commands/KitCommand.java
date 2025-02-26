package br.ynicollas.kits.commands;

import br.ynicollas.kits.model.Kit;
import br.ynicollas.kits.model.KitCooldown;
import br.ynicollas.kits.storage.cooldown.CooldownStorage;
import br.ynicollas.kits.storage.kits.KitStorage;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class KitCommand implements CommandExecutor {

    private final CooldownStorage cooldownManager;
    private final KitStorage kitStorage;

    public KitCommand(CooldownStorage cooldownManager, KitStorage kitStorage) {
        this.cooldownManager = cooldownManager;
        this.kitStorage = kitStorage;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Apenas jogadores podem utilizar este comando");
            return false;
        }

        Player player = (Player) sender;

        if (args.length != 1) {
            player.sendMessage(ChatColor.RED + "Uso: /kit <id>");
            return false;
        }

        String id = args[0].toLowerCase();

        Kit kit = kitStorage.getKit(id);

        if (kit == null) {
            player.sendMessage(ChatColor.RED + "Kit não encontrado!");
            return false;
        }

        if (!player.hasPermission(kit.getPermission())) {
            player.sendMessage(ChatColor.RED + "Você não tem permissão para usar este kit!");
            return false;
        }

        if (cooldownManager.hasCooldown(player, id)) {
            long timeRemaining = cooldownManager.getCooldown(player, id) - System.currentTimeMillis();
            player.sendMessage(ChatColor.RED + "Você precisa esperar " + timeRemaining / 1000 + " segundos para usar este kit novamente.");
            return false;
        }

        for (ItemStack item : kit.getItems()) {
            if (item != null) {
                player.getInventory().addItem(item);
            }
        }

        KitCooldown cooldown = kit.getCooldown();

        cooldownManager.removeCooldown(player, id);

        cooldownManager.addCooldown(player, id, cooldown);

        return true;
    }
}