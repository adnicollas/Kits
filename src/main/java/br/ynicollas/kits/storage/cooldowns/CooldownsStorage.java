package br.ynicollas.kits.storage.cooldowns;

import br.ynicollas.kits.cache.CooldownsCache;
import br.ynicollas.kits.model.KitCooldown;
import br.ynicollas.kits.storage.Database;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CooldownsStorage {

    private final Database database;

    private final CooldownsCache cooldownsCache;

    private static final Logger LOGGER = Bukkit.getLogger();

    public CooldownsStorage(Database database, CooldownsCache cooldownsCache) {
        this.database = database;
        this.cooldownsCache = cooldownsCache;
    }

    public void addCooldown(Player player, String kit, KitCooldown cooldown) {
        cooldownsCache.addCooldown(player, kit, cooldown.getMilliseconds());

        String query = "INSERT OR REPLACE INTO cooldowns (player, kit, expire_time) VALUES (?, ?, ?)";

        try (PreparedStatement statement = database.getConnection().prepareStatement(query)) {
            statement.setString(1, player.getName());
            statement.setString(2, kit);
            statement.setLong(3, System.currentTimeMillis() + cooldown.getMilliseconds());
            statement.executeUpdate();
        } catch (SQLException exception) {
            LOGGER.log(Level.SEVERE, "Failed to set cooldown", exception);
        }
    }

    public long getCooldown(Player player, String id) {
        if (cooldownsCache.hasCooldown(player, id)) {
            return cooldownsCache.getCooldown(player, id);
        }

        String query = "SELECT expire_time FROM cooldowns WHERE player = ? AND kit = ?";

        try (PreparedStatement statement = database.getConnection().prepareStatement(query)) {
            statement.setString(1, player.getName());
            statement.setString(2, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    long expireTime = resultSet.getLong("expire_time");

                    cooldownsCache.addCooldown(player, id, expireTime - System.currentTimeMillis());

                    return expireTime;
                }
            }
        } catch (SQLException exception) {
            LOGGER.log(Level.SEVERE, "Failed to get cooldown for player", exception);
        }

        return 0;
    }

    public boolean hasCooldown(Player player, String id) {
        if (cooldownsCache.hasCooldown(player, id)) {
            return cooldownsCache.getCooldown(player, id) > System.currentTimeMillis();
        }

        long cooldown = getCooldown(player, id);
        return cooldown > System.currentTimeMillis();
    }

    public void removeCooldown(Player player, String kit) {
        cooldownsCache.removeCooldown(player, kit);

        String query = "DELETE FROM cooldowns WHERE player = ? AND kit = ?";

        try (PreparedStatement statement = database.getConnection().prepareStatement(query)) {
            statement.setString(1, player.getName());
            statement.setString(2, kit);
            statement.executeUpdate();
        } catch (SQLException exception) {
            LOGGER.log(Level.SEVERE, "Failed to remove cooldown for player", exception);
        }
    }

    public void clear(String id) {
        cooldownsCache.removeCooldownsForKit(id);

        String query = "DELETE FROM cooldowns WHERE kit = ?";

        try (PreparedStatement statement = database.getConnection().prepareStatement(query)) {
            statement.setString(1, id);
            statement.executeUpdate();
        } catch (SQLException exception) {
            LOGGER.log(Level.SEVERE, "Failed to clear cooldowns", exception);
        }
    }
}