package net.highskiesmc.hsskills.events.handlers;

import net.highskiesmc.hscore.highskies.HSListener;
import net.highskiesmc.hscore.highskies.HSPlugin;
import net.highskiesmc.hscore.utils.DateUtil;
import net.highskiesmc.hscore.utils.TextUtils;
import net.highskiesmc.hsskills.api.HSSkillsApi;
import net.highskiesmc.hsskills.api.Skills.Skill;
import org.bukkit.*;
import org.bukkit.block.data.BlockData;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.*;

public class BleedHandler extends HSListener {
    private final HSSkillsApi api;

    public BleedHandler(HSPlugin main, HSSkillsApi api) {
        super(main);

        this.api = api;
    }

    private final Map<UUID, Integer> ready = new HashMap<>();
    private final Map<UUID, CooldownRunnable> cooldown = new HashMap<>();
    private final Map<UUID, Integer> bleed = new HashMap<>();

    private void ready(@NonNull Player attacker) {
        UUID uuid = attacker.getUniqueId();

        // Check cooldown
        if (cooldown.containsKey(uuid)) {
            String time = cooldown.get(uuid).getTimeRemaining();
            String msg = config.get("bleed.unready", String.class, "&c(!) You must wait &n{time}&c before activating " +
                    "Bleed again!");

            attacker.sendMessage(TextUtils.translateColor(
                    msg.replace("{time}", time)
            ));

            return;
        }

        // Cancel old ready
        cancelReady(uuid);

        long time = config.get("bleed.ready-duration", int.class, 5) * 20L;
        int timerId = new BukkitRunnable() {
            @Override
            public void run() {
                ready.remove(uuid);
            }
        }.runTaskLaterAsynchronously(main, time).getTaskId();

        ready.put(uuid, timerId);

        String msg = config.get("bleed.ready", String.class, "&e&L(!) Bleed - READY");

        attacker.sendMessage(TextUtils.translateColor(msg));
    }

    private void cancelReady(@NonNull UUID uuid) {
        if (ready.containsKey(uuid)) {
            Bukkit.getScheduler().cancelTask(ready.get(uuid));
            ready.remove(uuid);
        }
    }

    private void bleed(@NonNull Entity entity) {
        // Cancel previous bleed
        UUID uuid = entity.getUniqueId();
        cancelBleed(entity);

        long duration = config.get("bleed.duration", int.class, 12);
        int dmgSeconds = config.get("bleed.dmg-seconds", int.class, 2);
        Sound sound = Sound.valueOf(config.get("bleed.sound", String.class, "BLOCK_STONE_BREAK"));
        int dmg = config.get("bleed.dmg", int.class, 1);

        Particle particle = Particle.valueOf(config.get("bleed.particle.value", String.class, "BLOCK_DUST"));
        Material block = Material.valueOf(config.get("bleed.particle.block", String.class, "REDSTONE_BLOCK"));
        BlockData blockData = Bukkit.createBlockData(block);

        int bleedId = new BukkitRunnable() {
            private int seconds = 0;
            private int secondsSinceDmg = 0;

            @Override
            public void run() {
                if (seconds >= duration) {
                    cancelBleed(entity);
                    return;
                }

                if (secondsSinceDmg >= dmgSeconds) {
                    // Do damage
                    ((LivingEntity) entity).damage(dmg);
                    World world = entity.getWorld();
                    Location loc = entity.getLocation();

                    world.spawnParticle(particle, loc, 5, 0.25D, 0.5D, 0.25D, blockData);
                    world.playSound(loc, sound, 1, 1);

                    secondsSinceDmg = -1;
                }

                secondsSinceDmg++;
                seconds++;
            }
        }.runTaskTimer(main, 0, 20L).getTaskId();

        bleed.put(uuid, bleedId);
    }

    private void cancelBleed(@NonNull Entity entity) {
        UUID uuid = entity.getUniqueId();

        if (bleed.containsKey(uuid)) {
            Bukkit.getScheduler().cancelTask(bleed.get(uuid));
            bleed.remove(uuid);
        }
    }

    private void startCooldown(@NonNull Player player) {
        UUID uuid = player.getUniqueId();

        if (cooldown.containsKey(uuid)) {
            cooldown.get(uuid).cancel();
        }

        int time = config.get("bleed.cooldown", int.class, 300);

        CooldownRunnable timer = new CooldownRunnable(uuid, time);

        timer.runTaskLaterAsynchronously(main, time * 20L);
        cooldown.put(uuid, timer);

    }

    //<editor-fold desc="Handlers">
    @EventHandler(priority = EventPriority.MONITOR)
    public void onInteractWithWeapon(PlayerInteractEvent e) {
        // Check its right click with tool or weapon
        if (!Objects.equals(e.getHand(), EquipmentSlot.HAND) ||
                (!e.getAction().equals(Action.RIGHT_CLICK_AIR) && !e.getAction().equals(Action.RIGHT_CLICK_BLOCK))) {
            return;
        }
        Player player = e.getPlayer();

        ItemStack item = e.getItem();
        if (item == null || (!EnchantmentTarget.WEAPON.includes(item) && !EnchantmentTarget.TOOL.includes(item))) {
            return;
        }

        List<String> allowedWorlds = config.get("bleed.allowed-worlds", List.class, new ArrayList<>());
        World world = player.getLocation().getWorld();
        if (world != null && allowedWorlds.contains(world.getName())) {
            if (api.hasSkill(player, Skill.BLEED)) {
                // Start a runnable that says bleed is ready.
                ready(player);
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerHitEntity(EntityDamageByEntityEvent e) {
        Entity attacker = e.getDamager();

        if (attacker instanceof Player player) {
            // If ready.containsKey then the world has already been validated.
            if (ready.containsKey(player.getUniqueId())) {
                // Apply bleed (use a runnable)
                startCooldown(player);
                bleed(e.getEntity());
            }
            // Bleed config: cooldown, duration, how often it attacks them, how much dmg to do, particle effect, noise
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerSwapWorld(PlayerChangedWorldEvent e) {
        cancelReady(e.getPlayer().getUniqueId());
        cancelBleed(e.getPlayer());
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityDeath(EntityDeathEvent e) {
        cancelReady(e.getEntity().getUniqueId());
        cancelBleed(e.getEntity());
    }
    //</editor-fold>

    private class CooldownRunnable extends BukkitRunnable {
        private final UUID uuid;

        public CooldownRunnable(@NonNull UUID uuid, int seconds) {
            super();

            this.uuid = uuid;

            startDateTime = ZonedDateTime.now();
            endDateTime = startDateTime.plusSeconds(seconds);
        }

        private final ZonedDateTime startDateTime;
        private final ZonedDateTime endDateTime;

        public String getTimeRemaining() {
            ZonedDateTime now = ZonedDateTime.now();

            long ticks = Duration.between(now, endDateTime).toSeconds() * 20L;

            return DateUtil.formatTicks(ticks);
        }

        @Override
        public void run() {
            cooldown.remove(uuid);
        }
    }
}
