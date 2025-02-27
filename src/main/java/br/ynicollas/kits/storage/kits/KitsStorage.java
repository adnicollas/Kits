package br.ynicollas.kits.storage.kits;

import br.ynicollas.kits.cache.KitsCache;
import br.ynicollas.kits.models.Kit;
import br.ynicollas.kits.models.KitCooldown;
import br.ynicollas.kits.storage.Database;
import br.ynicollas.kits.util.ItemSerializer;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class KitsStorage {

    private final Database database;

    private final KitsCache kitsCache;

    private static final Logger LOGGER = Bukkit.getLogger();

    public KitsStorage(Database database, KitsCache kitsCache) {
        this.kitsCache = kitsCache;
        this.database = database;
    }

    public void saveKit(Kit kit) {
        String query = "INSERT OR REPLACE INTO kits (kit, permission, cooldown, content) VALUES (?, ?, ?, ?)";

        try (PreparedStatement statement = database.getConnection().prepareStatement(query)) {
            statement.setString(1, kit.getId());
            statement.setString(2, kit.getPermission());
            statement.setLong(3, kit.getCooldown().getMilliseconds());
            statement.setString(4, ItemSerializer.serialize(kit.getItems()));
            statement.executeUpdate();

            kitsCache.addKit(kit);
        } catch (SQLException exception) {
            LOGGER.log(Level.SEVERE, "Failed to save kit", exception);
        }
    }

    public Kit getKit(String id) {
        if (kitsCache.containsKit(id)) {
            return kitsCache.getKit(id);
        }

        String query = "SELECT * FROM kits WHERE kit = ?";
        Kit kit = null;

        try (PreparedStatement statement = database.getConnection().prepareStatement(query)) {
            statement.setString(1, id);

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                String permission = resultSet.getString("permission");
                long cooldownInMillis = resultSet.getLong("cooldown");
                String serializedItems = resultSet.getString("content");

                int days = (int) (cooldownInMillis / 86_400_000L);
                int hours = (int) ((cooldownInMillis % 86_400_000L) / 3_600_000L);
                int minutes = (int) ((cooldownInMillis % 3_600_000L) / 60_000L);

                ItemStack[] items = ItemSerializer.deserialize(serializedItems);

                kit = new Kit(id, permission, new KitCooldown(days, hours, minutes), items);

                kitsCache.addKit(kit);
            }
        } catch (SQLException exception) {
            LOGGER.log(Level.SEVERE, "Failed to retrieve kit", exception);
        }

        return kit;
    }

    public void removeKit(String id) {
        String query = "DELETE FROM kits WHERE kit = ?";

        try (PreparedStatement statement = database.getConnection().prepareStatement(query)) {
            statement.setString(1, id);
            statement.executeUpdate();

            kitsCache.removeKit(id);
        } catch (SQLException exception) {
            LOGGER.log(Level.SEVERE, "Failed to remove kit", exception);
        }
    }
}