package br.ynicollas.kits.cache;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CooldownsCache {

    private final Map<String, Map<String, Long>> cooldownsCache = new ConcurrentHashMap<>();

    public void addCooldown(Player player, String id, long duration) {
        cooldownsCache.computeIfAbsent(player.getName(), key -> new ConcurrentHashMap<>())
                .put(id, System.currentTimeMillis() + duration);
    }

    public void removeCooldown(Player player, String id) {
        cooldownsCache.computeIfPresent(player.getName(), (name, kitMap) -> {
            kitMap.remove(id);
            return kitMap.isEmpty() ? null : kitMap;
        });
    }

    public void removeCooldownsForKit(String id) {
        cooldownsCache.forEach((name, kitMap) -> kitMap.remove(id));
    }

    public boolean hasCooldown(Player player, String id) {
        return cooldownsCache.containsKey(player.getName()) &&
                cooldownsCache.get(player.getName()).getOrDefault(id, 0L) > System.currentTimeMillis();
    }

    public long getCooldown(Player player, String id) {
        return cooldownsCache.getOrDefault(player.getName(), new HashMap<>()).getOrDefault(id, 0L);
    }
}