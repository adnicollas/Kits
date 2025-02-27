package br.ynicollas.kits.cache;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CooldownCache {

    private final Map<String, Map<String, Long>> cooldowns = new ConcurrentHashMap<>();

    public void addCooldown(Player player, String kitId, long duration) {
        cooldowns.computeIfAbsent(player.getName(), key -> new ConcurrentHashMap<>())
                .put(kitId, System.currentTimeMillis() + duration);
    }

    public void removeCooldown(Player player, String kitId) {
        cooldowns.computeIfPresent(player.getName(), (name, kitMap) -> {
            kitMap.remove(kitId);
            return kitMap.isEmpty() ? null : kitMap;
        });
    }

    public void removeCooldownsForKit(String kitId) {
        cooldowns.forEach((name, kitMap) -> kitMap.remove(kitId));
    }

    public boolean hasCooldown(Player player, String kitId) {
        return cooldowns.containsKey(player.getName()) &&
                cooldowns.get(player.getName()).getOrDefault(kitId, 0L) > System.currentTimeMillis();
    }

    public long getCooldown(Player player, String kitId) {
        return cooldowns.getOrDefault(player.getName(), new HashMap<>()).getOrDefault(kitId, 0L);
    }
}