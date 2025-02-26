package br.ynicollas.kits;

import br.ynicollas.kits.commands.DeleteKitCommand;
import br.ynicollas.kits.commands.KitCommand;
import br.ynicollas.kits.commands.CreateKitCommand;
import br.ynicollas.kits.listener.InventoryCloseListener;
import br.ynicollas.kits.storage.Database;
import br.ynicollas.kits.storage.kits.KitStorage;
import br.ynicollas.kits.storage.cooldown.CooldownStorage;
import br.ynicollas.kits.cache.CooldownsCache;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class KitsPlugin extends JavaPlugin {

    @Getter
    private static KitsPlugin INSTANCE;

    private Database database;

    private CooldownStorage cooldownStorage;
    private KitStorage kitStorage;

    @Override
    public void onEnable() {
        INSTANCE = this;

        database = new Database();
        database.openConnection();

        CooldownsCache kitsCache = new CooldownsCache();

        kitStorage = new KitStorage(database);

        cooldownStorage = new CooldownStorage(database, kitsCache);

        registerCommands();
        registerListener();
    }

    @Override
    public void onDisable() {
        if (database != null) {
            database.closeConnection();
        }
    }

    private void registerCommands() {
        getCommand("createkit").setExecutor(new CreateKitCommand());

        getCommand("deletekit").setExecutor(new DeleteKitCommand(cooldownStorage, kitStorage));
        getCommand("kit").setExecutor(new KitCommand(cooldownStorage, kitStorage));
    }

    private void registerListener() {
        Bukkit.getPluginManager().registerEvents(new InventoryCloseListener(kitStorage), this);
    }
}