package br.ynicollas.kits.storage.cooldown;

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

public class CooldownStorage {

    private final Database database;
    private final CooldownsCache kitsCache;

    private static final Logger LOGGER = Bukkit.getLogger();

    public CooldownStorage(Database database, CooldownsCache cache) {
        this.database = database;
        this.kitsCache = cache;
    }

    public void addCooldown(Player player, String kit, KitCooldown cooldown) {
        kitsCache.addCooldown(player, kit, cooldown.getMilliseconds());

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
        if (kitsCache.hasCooldown(player, kitId)) {
            return kitsCache.getCooldown(player, kitId);
        }

        String query = "SELECT expire_time FROM cooldowns WHERE player = ? AND kit = ?";

        try (PreparedStatement statement = database.getConnection().prepareStatement(query)) {
            statement.setString(1, player.getName());
            statement.setString(2, kitId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    long expireTime = resultSet.getLong("expire_time");

                    kitsCache.addCooldown(player, kitId, expireTime - System.currentTimeMillis());

                    return expireTime;
                }
            }
        } catch (SQLException exception) {
            LOGGER.log(Level.SEVERE, "Failed to get cooldown for player", exception);
        }

        return 0;
    }

    public boolean hasCooldown(Player player, String kitId) {
        if (kitsCache.hasCooldown(player, kitId)) {
            return kitsCache.getCooldown(player, kitId) > System.currentTimeMillis();
        }

        long cooldown = getCooldown(player, kitId);
        return cooldown > System.currentTimeMillis();
    }

    public void removeCooldown(Player player, String kit) {
        kitsCache.removeCooldown(player, kit);

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
        kitsCache.removeCooldownsForKit(kitId);

        String query = "DELETE FROM cooldowns WHERE kit = ?";

        try (PreparedStatement statement = database.getConnection().prepareStatement(query)) {
            statement.setString(1, kitId);
            statement.executeUpdate();
        } catch (SQLException exception) {
            LOGGER.log(Level.SEVERE, "Failed to clear cooldowns", exception);
        }
    }
}