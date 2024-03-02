package net.highskiesmc.hsskills;

import net.highskiesmc.hscore.configuration.sources.FileConfigSource;
import net.highskiesmc.hscore.exceptions.Exception;
import net.highskiesmc.hscore.highskies.HSPlugin;
import net.highskiesmc.hsskills.api.HSSkillsApi;
import net.highskiesmc.hsskills.commands.SkillsCommand;
import net.highskiesmc.hsskills.commands.TempCommand;
import net.highskiesmc.hsskills.events.handlers.PlayerJoinLeaveHandlers;
import net.highskiesmc.hsskills.events.handlers.SkillTokenHandlers;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;

import java.sql.SQLException;

public final class HSSkills extends HSPlugin {
    private static HSSkillsApi api;
    @Override
    public void enable() {
        config.addSource(new FileConfigSource("config.yml", this));
        config.reload();

        try {
            api = new HSSkillsApi(this, config.get("my-sql", ConfigurationSection.class, null));
        } catch (SQLException ex) {
            Exception.useStackTrace(getLogger()::severe, ex);
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        getCommand("skills").setExecutor(new SkillsCommand(this));
        getCommand("getskilltoken").setExecutor(new TempCommand());

        register(new PlayerJoinLeaveHandlers(this));
        register(new SkillTokenHandlers(this, api));
    }

    @Override
    public void disable() {
        if (api != null) {
            api.dispose();
        }
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
