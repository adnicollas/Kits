package br.ynicollas.kits.models;

import lombok.Getter;

import lombok.NonNull;
import lombok.Setter;
import org.bukkit.inventory.ItemStack;

@Getter
public class Kit {

    private final String id;
    private final String permission;

    private final KitCooldown cooldown;

    @Setter
    @NonNull
    private ItemStack[] items;

    public Kit(String id, String permission, KitCooldown cooldown, @NonNull ItemStack[] items) {
        this.id = id;
        this.permission = permission;
        this.cooldown = cooldown;
        this.items = items;
    }
}