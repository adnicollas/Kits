package br.ynicollas.kits.storage;

import br.ynicollas.kits.KitsPlugin;
import lombok.Getter;
import org.bukkit.Bukkit;

import java.io.File;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

@Getter
public class Database {

    private Connection connection;

    private static final Logger LOGGER = Bukkit.getLogger();
    private static final String DATABASE_URL = getDatabaseUrl();

    private static String getDatabaseUrl() {
        File folder = KitsPlugin.getINSTANCE().getDataFolder();

        if (!folder.exists() && !folder.mkdirs()) {
            LOGGER.log(Level.SEVERE, "Failed to create plugin data folder: " + folder.getAbsolutePath());
        }

        File databaseFile = new File(folder, "kits.db");
        return "jdbc:sqlite:" + databaseFile.getAbsolutePath();
    }

    public void openConnection() {
        synchronized (this) {
            try {
                if (connection == null || connection.isClosed()) {
                    Class.forName("org.sqlite.JDBC");

                    connection = DriverManager.getConnection(DATABASE_URL);

                    createTables();
                }
            } catch (ClassNotFoundException | SQLException exception) {
                LOGGER.log(Level.SEVERE, "Failed to open database connection.", exception);
            }
        }
    }

    public void closeConnection() {
        synchronized (this) {
            try {
                if (connection != null && !connection.isClosed()) {
                    connection.close();
                    connection = null;
                }

            } catch (SQLException exception) {
                LOGGER.log(Level.SEVERE, "Failed to close database connection.", exception);
            }
        }
    }

    private void createTables() {
        if (connection == null) {
            LOGGER.log(Level.SEVERE, "Cannot create tables: No database connection.");
            return;
        }

        try (Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE IF NOT EXISTS kits (kit TEXT PRIMARY KEY, permission TEXT, cooldown INTEGER, content TEXT)");
            statement.execute("CREATE TABLE IF NOT EXISTS cooldowns (player TEXT, kit TEXT, expire_time INTEGER, PRIMARY KEY(player, kit))");
        } catch (SQLException exception) {
            LOGGER.log(Level.SEVERE, "Failed to create database tables.", exception);
        }
    }
}