package net.highskiesmc.hsskills.api;

import net.highskiesmc.hscore.utils.PlayerUtils;
import net.highskiesmc.hsskills.HSSkills;
import net.highskiesmc.hsskills.api.Skills.SkillType;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public class HSSkillsApi {
    public final NamespacedKey SKILL_TOKEN_KEY;
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

        SKILL_TOKEN_KEY = new NamespacedKey(main, "is-skill-token");
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

    /**
     * @param player Player
     * @return Current Unspent tokens the player has
     */
    public int getTokens(@NonNull Player player) {
        return getPlayerSkills(player).getTokens();
    }

    /**
     * Gives the player physical token(s)
     *
     * @param player Player
     * @param amount Amount of tokens
     */
    public void giveSkillToken(@NonNull Player player, int amount) {
        // This will give them the skill-token item
        PlayerUtils.giveItem(player, SkillToken.get(amount), "Some items were dropped at your feet...");
    }

    /**
     * Gives the player a virtual token unless they already have the max amount of tokens FOR THEIR RANK
     *
     * @param playerUuid UUID of player claiming the token
     * @return Whether the token was given or not
     */
    private boolean claimSkillToken(@NonNull UUID playerUuid) {
        //TODO: Add checking to see if their tokens surpass the maximum allowed FOR THEIR RANK

        // This will give them a virtual token
        PlayerSkills skills = cache.getCache().get(playerUuid);

        skills.setTokens(skills.getTokens() + 1);
        System.out.println("New Tokens: " + skills.getTokens());
        cache.put(playerUuid, skills, true);

        return true;
    }

    public boolean claimSkillToken(@NonNull Player player) {
        Rank rank = getRank(player);

        if (rank == null) {
            return false;
        }

        int maxTokens = (rank.ordinal() + 1) * SkillType.values().length;
        PlayerSkills skills = getPlayerSkills(player);
        int tokens = skills.getTokens() + skills.getSkills().size();

        if (tokens >= maxTokens) {
            return false;
        }

        return claimSkillToken(player.getUniqueId());
    }

    private PlayerSkills getPlayerSkills(@NonNull Player player) {
        return cache.getCache().get(player.getUniqueId());
    }

    public boolean upgradeSkill(@NonNull UUID playerUuid, @NonNull SkillType skillType) {
        // TODO: Check their rank, they should only be able to claim up to the skill-level of their rank.

        return true;
    }

    public boolean upgradeSkill(@NonNull Player player, @NonNull SkillType skillType) {
        if (false) {
            return false;
        }

        return upgradeSkill(player.getUniqueId(), skillType);
    }

    public int getSkillLevel(@NonNull Player player, @NonNull SkillType skillType) {
        return cache.getCache().get(player.getUniqueId()).getSkills().stream().filter(x -> x.getType() == skillType).toList().size();
    }

    /**
     * @param player Player's rank to observe
     * @return Highest Rank of the player, or null if they have none
     */
    @Nullable
    public Rank getRank(@NonNull Player player) {
        for (Rank rank : Arrays.stream(Rank.values()).sorted(Comparator.reverseOrder()).toList()) {
            if (player.hasPermission(rank.getPermission())) {
                return rank;
            }
        }

        return null;
    }
}
