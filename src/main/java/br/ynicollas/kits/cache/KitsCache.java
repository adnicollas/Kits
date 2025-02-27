package br.ynicollas.kits.cache;

import br.ynicollas.kits.models.Kit;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class KitsCache {

    private final Map<String, Kit> kitsCache = new ConcurrentHashMap<>();

    public void addKit(Kit kit) {
        kitsCache.put(kit.getId(), kit);
    }

    public Kit getKit(String id) {
        return kitsCache.get(id);
    }

    public void removeKit(String id) {
        kitsCache.remove(id);
    }

    public boolean containsKit(String id) {
        return kitsCache.containsKey(id);
    }
}