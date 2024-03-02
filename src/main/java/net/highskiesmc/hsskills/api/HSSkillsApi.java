package net.highskiesmc.hsskills.api;

import net.highskiesmc.hsskills.HSSkills;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.sql.SQLException;
import java.util.*;

public class HSSkillsApi {
    private static final Long CACHE_PUSH_INTERVAL = 6000L;
    private final Database db;
    private final HSSkills main;
    private final PlayerSkillsCache cache;
    private final int taskId;

    public HSSkillsApi(@NonNull HSSkills main, @NonNull ConfigurationSection dbConfig) throws SQLException {
        this.main = main;
        this.db = new Database(main, dbConfig);
        cache = new PlayerSkillsCache(new HashMap<>());

        this.taskId = Bukkit.getScheduler().runTaskTimerAsynchronously(
                this.main,
                this::uploadCacheToDatabaseAsync,
                CACHE_PUSH_INTERVAL,
                CACHE_PUSH_INTERVAL
        ).getTaskId();
    }

    /**
     * Uploads cache to DB (Async)
     */
    private void uploadCacheToDatabaseAsync() {
        Bukkit.getScheduler().runTaskAsynchronously(this.main, this::uploadCacheToDatabase);
    }

    /**
     * Uploads cache to DB (Sync)
     */
    private void uploadCacheToDatabase() {
        db.pushCacheToDatabase(main, cache.getUpdates(true));
    }

    public void dispose() {
        Bukkit.getScheduler().cancelTask(taskId);
        uploadCacheToDatabase();
        db.disconnect();
        cache.dispose();
    }

    public void loadPlayerData(@NonNull UUID playerUuid) {
        Optional<PlayerSkills> skills = db.getPlayerSkills(playerUuid);
        boolean isUpdate = false;

        if (skills.isEmpty()) {
            skills = Optional.of(createPlayerData());
            isUpdate = true;
        }

        cache.put(playerUuid, skills.get(), isUpdate);
    }

    public void loadPlayerData(@NonNull Player player) {
        loadPlayerData(player.getUniqueId());
    }

    private PlayerSkills createPlayerData() {
        return new PlayerSkills(new ArrayList<>(), 0);
    }
}
