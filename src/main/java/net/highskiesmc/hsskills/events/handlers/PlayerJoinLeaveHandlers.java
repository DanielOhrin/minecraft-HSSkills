package net.highskiesmc.hsskills.events.handlers;

import net.highskiesmc.hscore.highskies.HSListener;
import net.highskiesmc.hscore.highskies.HSPlugin;
import net.highskiesmc.hsskills.HSSkills;
import net.highskiesmc.hsskills.api.HSSkillsApi;
import net.highskiesmc.hsskills.api.Rank;
import net.highskiesmc.hsskills.api.SkillToken;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerJoinLeaveHandlers extends HSListener {
    private final HSSkillsApi api;
    public PlayerJoinLeaveHandlers(HSPlugin main, HSSkillsApi api) {
        super(main);
        this.api = api;
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerJoin(AsyncPlayerPreLoginEvent e) {
        api.loadPlayerData(e.getUniqueId());
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerQuit(PlayerQuitEvent e) {
        api.unloadPlayerData(e.getPlayer());
    }

    @EventHandler(ignoreCancelled = true)
    public void giveSkillTokens(PlayerJoinEvent e) {
        Player player = e.getPlayer();

        if (!player.hasPlayedBefore()) {
            Rank rank = api.getRank(player);

            if (rank != null) {
                api.giveSkillToken(player, rank.getTokens());
            }
        }
    }
}
