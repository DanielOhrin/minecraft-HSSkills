package net.highskiesmc.hsskills.api.Runnables;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class BleedRunnable extends BukkitRunnable {
    private final Player player;
    public BleedRunnable(Player player) {
        super();

        this.player = player;
    }
    @Override
    public void run() {

        cancel();
    }
}
