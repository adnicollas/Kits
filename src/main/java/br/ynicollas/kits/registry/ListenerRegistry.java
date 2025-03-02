package br.ynicollas.kits.registry;

import br.ynicollas.kits.listeners.InventoryClickListener;
import br.ynicollas.kits.listeners.InventoryCloseListener;
import br.ynicollas.kits.storage.kits.KitsStorage;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.List;

public class ListenerRegistry {
    private final JavaPlugin plugin;

    private final KitsStorage kits;

    public ListenerRegistry(JavaPlugin plugin, KitsStorage kits) {
        this.plugin = plugin;
        this.kits = kits;
    }

    public void registerListeners() {
        List<Listener> listeners = Arrays.asList(
                new InventoryClickListener(),
                new InventoryCloseListener(kits)
        );

        for (Listener listener : listeners) {
            plugin.getServer().getPluginManager().registerEvents(listener, plugin);
        }
    }
}