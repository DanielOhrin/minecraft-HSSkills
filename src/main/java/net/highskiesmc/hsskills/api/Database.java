package net.highskiesmc.hsskills.api;

import net.highskiesmc.hscore.data.MySQLDatabase;
import net.highskiesmc.hscore.exceptions.Exception;
import net.highskiesmc.hscore.highskies.HSPlugin;
import net.highskiesmc.hsskills.api.Skills.Skill;
import net.highskiesmc.hsskills.api.Skills.SkillType;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.sql.*;
import java.util.*;

public class Database extends MySQLDatabase {
    private final Plugin main;

    protected Database(@NonNull Plugin main, @NonNull ConfigurationSection DB_CONFIG) throws SQLException {
        super(main, DB_CONFIG);

        this.main = main;
    }

    @Override
    protected int getMaxPoolSize() {
        return 10;
    }

    @Override
    protected int getMinIdlePools() {
        return 10;
    }

    @Override
    protected int getMaxLifetime() {
        return 300000;
    }

    @Override
    protected void tryCreateTables() throws SQLException {
        try (Connection conn = getHikari().getConnection()) {
            Statement statement = conn.createStatement();

            statement.execute(
                    "CREATE TABLE IF NOT EXISTS player_skills (" +
                            "Id INT AUTO_INCREMENT, " +
                            "Player_UUID VARCHAR(36) UNIQUE, " +
                            "Tokens INT, " +
                            "Island_Level INT NOT NULL DEFAULT(0), " +
                            "PvE_Level INT NOT NULL DEFAULT(0), " +
                            "PvP_Level INT NOT NULL DEFAULT(0), " +
                            "PRIMARY KEY(Id)" +
                            ") ENGINE = INNODB;"
            );
        }
    }

    @Override
    protected boolean useConfigXml() {
        return false;
    }

    public Optional<PlayerSkills> getPlayerSkills(@NonNull UUID playerUuid) {
        Optional<PlayerSkills> skills = Optional.of(new PlayerSkills(new ArrayList<>(), 0));

        try (Connection conn = getHikari().getConnection()) {
            PreparedStatement statement = conn.prepareStatement(
                    "SELECT Tokens, Island_Level, PvE_Level, PvP_Level " +
                            "FROM player_skills WHERE Player_UUID = ?"
            );

            statement.setString(1, playerUuid.toString());
            ResultSet res = statement.executeQuery();

            if (res.next()) {
                skills.get().addSkills(Skill.getSkills(SkillType.ISLAND).subList(0, res.getInt("Island_Level")));
                skills.get().addSkills(Skill.getSkills(SkillType.PVP).subList(0, res.getInt("PvP_Level")));
                skills.get().addSkills(Skill.getSkills(SkillType.PVE).subList(0, res.getInt("PvE_Level")));
                skills.get().setTokens(res.getInt("Tokens"));
            } else {
                System.out.println("No player skills found...");
                skills = Optional.empty();
            }
        } catch (SQLException ex) {
            Exception.useStackTrace(main.getLogger()::severe, ex);
        }

        return skills;
    }

    public Optional<PlayerSkills> getPlayerSkills(@NonNull Player player) {
        return getPlayerSkills(player.getUniqueId());
    }

    public void pushCacheToDatabase(HSPlugin main, Map<UUID, PlayerSkills> skills) {
        try (Connection conn = getHikari().getConnection()) {
            PreparedStatement statement = conn.prepareStatement("INSERT INTO player_skills (Player_UUID, Tokens, " +
                    "Island_Level, PvE_Level, PvP_Level) VALUES (?, ?, ?, ?, ?) " +
                    "ON DUPLICATE KEY UPDATE Tokens = ?, Island_Level = ?, PvE_Level = ?, PvP_Level = ?");

            for (Map.Entry<UUID, PlayerSkills> skill : skills.entrySet()) {
                statement.setString(1, skill.getKey().toString());

                List<Skill> skillList = skill.getValue().getSkills();
                statement.setInt(2, skill.getValue().getTokens());
                statement.setInt(3, skillList.stream().filter(x -> x.getType().equals(SkillType.ISLAND)).toList().size());
                statement.setInt(4, skillList.stream().filter(x -> x.getType().equals(SkillType.PVE)).toList().size());
                statement.setInt(5, skillList.stream().filter(x -> x.getType().equals(SkillType.PVP)).toList().size());

                statement.setInt(6, skill.getValue().getTokens());
                statement.setInt(7, skillList.stream().filter(x -> x.getType().equals(SkillType.ISLAND)).toList().size());
                statement.setInt(8, skillList.stream().filter(x -> x.getType().equals(SkillType.PVE)).toList().size());
                statement.setInt(9, skillList.stream().filter(x -> x.getType().equals(SkillType.PVP)).toList().size());

                statement.addBatch();
            }

            int[] rowsUpdated = statement.executeBatch();

            main.getLogger().info("player_skills Updated: " + Arrays.stream(rowsUpdated).sum());
        } catch (SQLException ex) {
            Exception.useStackTrace(main.getLogger()::severe, ex);
        }
    }
}
