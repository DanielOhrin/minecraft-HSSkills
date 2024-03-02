package net.highskiesmc.hsskills.api;

import net.highskiesmc.hscore.data.MySQLDatabase;
import net.highskiesmc.hscore.exceptions.Exception;
import net.highskiesmc.hsskills.api.Skills.Skill;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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

    }

    @Override
    protected boolean useConfigXml() {
        return false;
    }

    public List<Skill> getPlayerSkills(@NonNull Player player) {
        List<Skill> skills = new ArrayList<>();

        try (Connection conn = getHikari().getConnection()) {

        } catch (SQLException ex) {
            Exception.useStackTrace(main.getLogger()::severe, ex);
        }

        return skills;
    }
}
