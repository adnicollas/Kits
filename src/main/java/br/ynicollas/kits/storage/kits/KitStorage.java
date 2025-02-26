package br.ynicollas.kits.storage.kits;

import br.ynicollas.kits.model.Kit;
import br.ynicollas.kits.model.KitCooldown;
import br.ynicollas.kits.storage.Database;
import br.ynicollas.kits.util.ItemSerializer;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class KitStorage {

    private final Database database;

    private static final Logger LOGGER = Bukkit.getLogger();

    public KitStorage(Database database) {
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
        } catch (SQLException exception) {
            LOGGER.log(Level.SEVERE, "Failed to save kit", exception);
        }
    }

    public Kit getKit(String kitId) {
        String query = "SELECT * FROM kits WHERE kit = ?";
        Kit kit = null;

        try (PreparedStatement statement = database.getConnection().prepareStatement(query)) {
            statement.setString(1, kitId);

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                String permission = resultSet.getString("permission");
                long cooldownInMillis = resultSet.getLong("cooldown");
                String serializedItems = resultSet.getString("content");

                int days = (int) (cooldownInMillis / 86_400_000L);
                int hours = (int) ((cooldownInMillis % 86_400_000L) / 3_600_000L);
                int minutes = (int) ((cooldownInMillis % 3_600_000L) / 60_000L);

                ItemStack[] items = ItemSerializer.deserialize(serializedItems);

                kit = new Kit(kitId, permission, new KitCooldown(days, hours, minutes), items);
            }
        } catch (SQLException exception) {
            LOGGER.log(Level.SEVERE, "Failed to retrieve kit", exception);
        }

        return kit;
    }

    public void removeKit(String kitId) {
        String query = "DELETE FROM kits WHERE kit = ?";

        try (PreparedStatement statement = database.getConnection().prepareStatement(query)) {
            statement.setString(1, kitId);
            statement.executeUpdate();
        } catch (SQLException exception) {
            LOGGER.log(Level.SEVERE, "Failed to remove kit", exception);
        }
    }
}