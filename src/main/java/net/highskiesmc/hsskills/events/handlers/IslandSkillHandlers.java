package net.highskiesmc.hsskills.events.handlers;

import com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI;
import com.bgsoftware.superiorskyblock.api.island.Island;
import net.highskiesmc.hscore.highskies.HSListener;
import net.highskiesmc.hscore.highskies.HSPlugin;
import net.highskiesmc.hsprogression.events.events.IslandContributionEvent;
import net.highskiesmc.hsskills.api.HSSkillsApi;
import net.highskiesmc.hsskills.api.Skills.Skill;
import net.highskiesmc.nodes.events.events.IslandNodeMineEvent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.Random;

public class IslandSkillHandlers extends HSListener {
    private final HSSkillsApi api;

    public IslandSkillHandlers(HSPlugin main, HSSkillsApi api) {
        super(main);
        this.api = api;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onMobSlainOnIsland(EntityDeathEvent e) {
        Entity entity = e.getEntity();
        Player slayer = e.getEntity().getKiller();

        Island island = SuperiorSkyblockAPI.getIslandAt(entity.getLocation());

        if (island == null || slayer == null) {
            return;
        }

        if (api.hasSkill(slayer, Skill.MOB_XP_INCREASE)) {
            int xp = e.getDroppedExp();
            double modifier = 1 + ((double) Skill.MOB_XP_INCREASE.getAmount() / 100D);

            e.setDroppedExp((int) Math.round((double) xp * modifier));
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onNodeMineOnIsland(IslandNodeMineEvent e) {
        Island island = e.getIsland();

        if (island == null) {
            return;
        }

        if (api.hasSkill(e.getPlayer(), Skill.RESOURCE_NODE_INSTANT_RESPAWN_CHANCE)) {
            double chance = ((double) Skill.RESOURCE_NODE_INSTANT_RESPAWN_CHANCE.getAmount() / 100D);
            double roll = new Random().nextDouble();

            if (roll <= chance) {
                e.setRespawnTime(0);
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onIslandContribution(IslandContributionEvent e) {
        if (e.getIsland() == null) {
            return;
        }

        if (api.hasSkill(e.getPlayer(), Skill.ISLAND_SKILLS_LEVEL_FASTER)) {
            e.setAmount(e.getAmount() * 2);
        }
    }

    @EventHandler
    public void onPlayerToggleFlight(PlayerCommandPreprocessEvent e) {
        String cmd = e.getMessage().toLowerCase().replace("/", "");

        if (cmd.matches("^(essentials:)?fly")) {
            Player player = e.getPlayer();

            if (!player.hasPermission("hsskills.fly.bypass") && !api.hasSkill(player, Skill.FLY)) {
                e.setCancelled(true);
                player.sendMessage(ChatColor.RED + "You have not unlocked that skill!");
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerChangeWorld(PlayerChangedWorldEvent e) {
        // If player has permission and is flying, turn that shit off
        Player player = e.getPlayer();

        if (!player.hasPermission("hsskills.fly.bypass")) {
            player.setFlying(false);
            player.setAllowFlight(false);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerTeleport(PlayerTeleportEvent e) {
        Player player = e.getPlayer();

        if (SuperiorSkyblockAPI.getIslandAt(e.getTo()) == null && !player.hasPermission("hsskills.fly.bypass")) {
            player.setFlying(false);
            player.setAllowFlight(false);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onItemDamage(PlayerItemDamageEvent e) {
        Player player = e.getPlayer();

        if (SuperiorSkyblockAPI.getIslandAt(player.getLocation()) == null) {
            return;
        }

        if (api.hasSkill(player, Skill.DURABILITY_LOSS_DECREASE_ON_ISLAND)) {
            double chanceToCancelDurabilityLoss = Skill.DURABILITY_LOSS_DECREASE_ON_ISLAND.getAmount() / 100D;
            double roll = new Random().nextDouble();

            if (roll < chanceToCancelDurabilityLoss) {
                e.setCancelled(true);
            }
        }
    }
}

