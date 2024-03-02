package net.highskiesmc.hsskills;

import net.highskiesmc.hscore.configuration.sources.FileConfigSource;
import net.highskiesmc.hscore.exceptions.Exception;
import net.highskiesmc.hscore.highskies.HSPlugin;
import net.highskiesmc.hsskills.api.HSSkillsApi;
import net.highskiesmc.hsskills.commands.SkillsCommand;
import org.bukkit.configuration.ConfigurationSection;

import java.sql.SQLException;

public final class HSSkills extends HSPlugin {
    private static HSSkillsApi api;
    @Override
    public void enable() {
        config.addSource(new FileConfigSource("config.yml", this));
        config.reload();

        try {
            api = new HSSkillsApi(this, config.get("my-sql", ConfigurationSection.class));
        } catch (SQLException ex) {
            Exception.useStackTrace(getLogger()::severe, ex);
        }

        getCommand("skills").setExecutor(new SkillsCommand(this));
    }

    @Override
    public void disable() {

    }

    @Override
    public void reload() {

    }

    @Override
    protected boolean isUsingInventories() {
        return true;
    }

    public static HSSkillsApi getApi() {
        return api;
    }
}
