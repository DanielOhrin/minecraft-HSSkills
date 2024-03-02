package net.highskiesmc.hsskills.api;

import net.highskiesmc.hscore.data.cache.MapCache;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerSkillsCache extends MapCache<Map<UUID, PlayerSkills>> {
    public PlayerSkillsCache(Map<UUID, PlayerSkills> startingValue) {
        super(startingValue, new HashMap<>(), new HashMap<>());
    }

    public void put(UUID uuid, PlayerSkills skills, boolean isUpdate) {
        cache.put(uuid, skills);

        if (isUpdate) {
            getUpdates(false).put(uuid, skills);
        }
    }
        // TODO: Cleanup when player leaves server
    @Override
    public Map<UUID, PlayerSkills> getUpdatesClone() {
        return new HashMap<>(getUpdates(false));
    }
}
