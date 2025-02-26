package br.ynicollas.kits.listener;

import br.ynicollas.kits.model.Kit;
import br.ynicollas.kits.storage.kits.KitStorage;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InventoryCloseListener implements Listener {

    private static final Map<String, Kit> currentKits = new HashMap<>();

    private final KitStorage kitStorage;

    public InventoryCloseListener(KitStorage kitStorage) {
        this.kitStorage = kitStorage;
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Inventory inventory = event.getInventory();

        if (!inventory.getName().equals(ChatColor.DARK_GRAY + "Kit")) {
            return;
        }

        Player player = (Player) event.getPlayer();

        Kit kit = getCurrentKitForPlayer(player);

        if (kit == null) {
            return;
        }

        ItemStack[] items = inventory.getContents();

        List<ItemStack> filteredItems = new ArrayList<>();

        for (ItemStack item : items) {
            if (item != null && item.getType() != Material.AIR) {
                filteredItems.add(item);
            }
        }

        kit.setItems(filteredItems.toArray(new ItemStack[0]));

        kitStorage.saveKit(kit);

        player.sendMessage(ChatColor.YELLOW + "Kit criado com sucesso.");

        currentKits.remove(player.getName());
    }

    public static void setCurrentKit(Player player, Kit kit) {
        currentKits.put(player.getName(), kit);
    }

    private Kit getCurrentKitForPlayer(Player player) {
        return currentKits.get(player.getName());
    }
}