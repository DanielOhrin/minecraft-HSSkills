package net.highskiesmc.hsskills.events.handlers;

import net.highskiesmc.hscore.highskies.HSListener;
import net.highskiesmc.hscore.highskies.HSPlugin;
import net.highskiesmc.hsskills.HSSkills;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

public class PlayerJoinLeaveHandlers extends HSListener {
    public PlayerJoinLeaveHandlers(HSPlugin main) {
        super(main);
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerFirstJoin(AsyncPlayerPreLoginEvent e) {
        HSSkills.getApi().loadPlayerData(e.getUniqueId());
    }

}
