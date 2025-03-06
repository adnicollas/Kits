package br.ynicollas.kits.listeners;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class InventoryClickListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }

        String title = event.getView().getTitle();

        if (title.equals(ChatColor.DARK_GRAY + "Visualizando")) {
            event.setCancelled(true);
        }
    }
}