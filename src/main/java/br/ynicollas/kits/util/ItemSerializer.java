package br.ynicollas.kits.util;

import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.*;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ItemSerializer {

    private static final Logger LOGGER = Bukkit.getLogger();

    public static String serialize(ItemStack[] items) {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream bukkitObjectOutputStream = new BukkitObjectOutputStream(byteArrayOutputStream);
            bukkitObjectOutputStream.writeInt(items.length);

            for (ItemStack item : items) {
                if (item != null) {
                    bukkitObjectOutputStream.writeObject(item);
                } else {
                    LOGGER.warning("Tentativa de serializar um item nulo.");
                    bukkitObjectOutputStream.writeObject(new ItemStack(0));
                }
            }

            bukkitObjectOutputStream.close();

            return Base64.getEncoder().encodeToString(byteArrayOutputStream.toByteArray());
        } catch (IOException exception) {
            LOGGER.log(Level.SEVERE, "Erro ao serializar os itens", exception);
            return null;
        }
    }

    public static ItemStack[] deserialize(String data) {
        if (data == null || data.isEmpty()) {
            LOGGER.warning("Dados de serialização vazios ou nulos.");
            return new ItemStack[0];
        }

        try {
            byte[] bytes = Base64.getDecoder().decode(data);
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
            BukkitObjectInputStream bukkitObjectInputStream = new BukkitObjectInputStream(byteArrayInputStream);
            int size = bukkitObjectInputStream.readInt();
            ItemStack[] items = new ItemStack[size];

            for (int i = 0; i < size; i++) {
                items[i] = (ItemStack) bukkitObjectInputStream.readObject();
            }

            bukkitObjectInputStream.close();
            return items;
        } catch (IOException | ClassNotFoundException exception) {
            LOGGER.log(Level.SEVERE, "Erro ao desserializar os itens", exception);
            return new ItemStack[0];
        }
    }
}