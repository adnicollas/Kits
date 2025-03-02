package br.ynicollas.kits.storage;

import br.ynicollas.kits.KitsPlugin;
import org.bukkit.Bukkit;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SQLiteLoader {
    private static final String SQLITE_VERSION = "3.49.1.0";
    private static final String SQLITE_URL = "https://repo1.maven.org/maven2/org/xerial/sqlite-jdbc/" + SQLITE_VERSION + "/sqlite-jdbc-" + SQLITE_VERSION + ".jar";
    private static final String SQLITE_FILENAME = "sqlite-jdbc-" + SQLITE_VERSION + ".jar";

    private static final Logger LOGGER = Bukkit.getLogger();

    private final KitsPlugin plugin;

    public SQLiteLoader(KitsPlugin plugin) {
        this.plugin = plugin;
    }

    public void downloadIfNecessary() {
        Path libsFolder = plugin.getDataFolder().toPath().resolve("libs");
        Path sqliteFile = libsFolder.resolve(SQLITE_FILENAME);

        if (Files.exists(sqliteFile)) {
            LOGGER.log(Level.INFO, "SQLite já está presente: " + sqliteFile.toAbsolutePath());
            return;
        }

        try {
            Files.createDirectories(libsFolder);
            LOGGER.log(Level.INFO, "Baixando SQLite...");
            if (downloadFile(sqliteFile)) {
                LOGGER.log(Level.INFO, "SQLite baixado com sucesso: " + sqliteFile.toAbsolutePath());
            } else {
                LOGGER.log(Level.SEVERE, "Falha ao baixar SQLite.");
            }
        } catch (IOException exception) {
            LOGGER.log(Level.SEVERE, "Erro ao criar diretório libs.", exception);
        }
    }

    private boolean downloadFile(Path destination) {
        try (InputStream inputStream = new URL(SQLiteLoader.SQLITE_URL).openStream()) {
            Files.copy(inputStream, destination, StandardCopyOption.REPLACE_EXISTING);
            return true;
        } catch (IOException exception) {
            LOGGER.log(Level.SEVERE, "Erro ao baixar SQLite.", exception);
            return false;
        }
    }
}