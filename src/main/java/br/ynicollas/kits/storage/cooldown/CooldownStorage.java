package br.ynicollas.kits.storage.cooldown;

import br.ynicollas.kits.cache.CooldownCache;
import br.ynicollas.kits.model.KitCooldown;
import br.ynicollas.kits.storage.Database;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CooldownStorage {

    private final Database database;

    private final CooldownCache cooldownCache;

    private static final Logger LOGGER = Bukkit.getLogger();

    public CooldownStorage(Database database, CooldownCache cooldownCache) {
        this.database = database;
        this.cooldownCache = cooldownCache;
    }

    public void addCooldown(Player player, String kit, KitCooldown cooldown) {
        cooldownCache.addCooldown(player, kit, cooldown.getMilliseconds());

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

    public long getCooldown(Player player, String kitId) {
        if (cooldownCache.hasCooldown(player, kitId)) {
            return cooldownCache.getCooldown(player, kitId);
        }

        String query = "SELECT expire_time FROM cooldowns WHERE player = ? AND kit = ?";

        try (PreparedStatement statement = database.getConnection().prepareStatement(query)) {
            statement.setString(1, player.getName());
            statement.setString(2, kitId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    long expireTime = resultSet.getLong("expire_time");

                    cooldownCache.addCooldown(player, kitId, expireTime - System.currentTimeMillis());

                    return expireTime;
                }
            }
        } catch (SQLException exception) {
            LOGGER.log(Level.SEVERE, "Failed to get cooldown for player", exception);
        }

        return 0;
    }

    public boolean hasCooldown(Player player, String kitId) {
        if (cooldownCache.hasCooldown(player, kitId)) {
            return cooldownCache.getCooldown(player, kitId) > System.currentTimeMillis();
        }

        long cooldown = getCooldown(player, kitId);
        return cooldown > System.currentTimeMillis();
    }

    public void removeCooldown(Player player, String kit) {
        cooldownCache.removeCooldown(player, kit);

        String query = "DELETE FROM cooldowns WHERE player = ? AND kit = ?";

        try (PreparedStatement statement = database.getConnection().prepareStatement(query)) {
            statement.setString(1, player.getName());
            statement.setString(2, kit);
            statement.executeUpdate();
        } catch (SQLException exception) {
            LOGGER.log(Level.SEVERE, "Failed to remove cooldown for player", exception);
        }
    }

    public void clear(String kitId) {
        cooldownCache.removeCooldownsForKit(kitId);

        String query = "DELETE FROM cooldowns WHERE kit = ?";

        try (PreparedStatement statement = database.getConnection().prepareStatement(query)) {
            statement.setString(1, kitId);
            statement.executeUpdate();
        } catch (SQLException exception) {
            LOGGER.log(Level.SEVERE, "Failed to clear cooldowns", exception);
        }
    }
}