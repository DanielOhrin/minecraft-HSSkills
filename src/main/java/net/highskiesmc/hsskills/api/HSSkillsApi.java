package net.highskiesmc.hsskills.api;

import net.highskiesmc.hsskills.HSSkills;
import net.highskiesmc.hsskills.api.Skills.Skill;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class HSSkillsApi {
    private final Database db;
    private final HSSkills main;
    private final Map<UUID, List<Skill>> playerSkills;
    public HSSkillsApi(@NonNull HSSkills main, @NonNull ConfigurationSection dbConfig) throws SQLException {
        this.main = main;
        this.db = new Database(main, dbConfig);
        this.playerSkills = new HashMap<>();
    }

    public void loadPlayerData(@NonNull Player player) {
        playerSkills.put(player.getUniqueId(), db.getPlayerSkills(player));
    }
}
