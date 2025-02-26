package br.ynicollas.kits.cache;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class CooldownsCache {
    private final Map<String, Map<String, Long>> cooldowns = new HashMap<>();

    public void addCooldown(Player player, String kitId, long duration) {
        cooldowns.computeIfAbsent(player.getName(), key -> new HashMap<>()).put(kitId, System.currentTimeMillis() + duration);
    }

    public void removeCooldown(Player player, String kitId) {
        if (cooldowns.containsKey(player.getName())) {
            cooldowns.get(player.getName()).remove(kitId);
            if (cooldowns.get(player.getName()).isEmpty()) {
                cooldowns.remove(player.getName());
            }
        }
    }

    public void removeCooldownsForKit(String kitId) {
        for (Map<String, Long> cooldowns : cooldowns.values()) {
            cooldowns.remove(kitId);
        }
    }

    public boolean hasCooldown(Player player, String kitId) {
        return cooldowns.containsKey(player.getName()) &&
                cooldowns.get(player.getName()).getOrDefault(kitId, 0L) > System.currentTimeMillis();
    }

    public long getCooldown(Player player, String kitId) {
        return cooldowns.getOrDefault(player.getName(), new HashMap<>()).getOrDefault(kitId, 0L);
    }
}