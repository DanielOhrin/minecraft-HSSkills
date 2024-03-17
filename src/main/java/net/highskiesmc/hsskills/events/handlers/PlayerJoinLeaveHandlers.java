package net.highskiesmc.hsskills.events.handlers;

import net.highskiesmc.hscore.highskies.HSListener;
import net.highskiesmc.hscore.highskies.HSPlugin;
import net.highskiesmc.hsskills.HSSkills;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerJoinLeaveHandlers extends HSListener {
    public PlayerJoinLeaveHandlers(HSPlugin main) {
        super(main);
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerJoin(AsyncPlayerPreLoginEvent e) {
        HSSkills.getApi().loadPlayerData(e.getUniqueId());
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerQuit(PlayerQuitEvent e) {
        HSSkills.getApi().unloadPlayerData(e.getPlayer());
    }
}
