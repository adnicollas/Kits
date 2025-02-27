package br.ynicollas.kits.cache;

import br.ynicollas.kits.model.Kit;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class KitCache {

    private final Map<String, Kit> kitCache = new ConcurrentHashMap<>();

    public void addKit(Kit kit) {
        kitCache.put(kit.getId(), kit);
    }

    public Kit getKit(String kitId) {
        return kitCache.get(kitId);
    }

    public void removeKit(String kitId) {
        kitCache.remove(kitId);
    }

    public boolean containsKit(String kitId) {
        return kitCache.containsKey(kitId);
    }
}
