package net.highskiesmc.hsskills.events.handlers;

import net.highskiesmc.hsadventure.api.HSAdventureAPI;
import net.highskiesmc.hsadventure.api.objects.Crate;
import net.highskiesmc.hsadventure.core.utils.NumberUtils;
import net.highskiesmc.hsadventure.core.utils.PlayerUtils;
import net.highskiesmc.hsalignments.events.events.AdventureWarpTimerEvent;
import net.highskiesmc.hscore.highskies.HSListener;
import net.highskiesmc.hscore.highskies.HSPlugin;
import net.highskiesmc.hscore.utils.TextUtils;
import net.highskiesmc.hsmisc.events.events.entitydamage.PlayerDamageEntityEvent;
import net.highskiesmc.hsskills.api.HSSkillsApi;
import net.highskiesmc.hsskills.api.Skills.Skill;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class PvESkillHandlers extends HSListener {
    private final HSSkillsApi api;
    private final HSAdventureAPI advApi;

    public PvESkillHandlers(HSPlugin main, HSSkillsApi api, HSAdventureAPI advApi) {
        super(main);
        this.api = api;
        this.advApi = advApi;
    }

    @EventHandler
    public void onAdventureWarpTimer(AdventureWarpTimerEvent e) {
        Player player = e.getPlayer();

        if (api.hasSkill(player, Skill.ADVENTURE_LEAVE_TIMER_DECREASE)) {
            double delay = e.getDelay();

            delay *= (Skill.ADVENTURE_LEAVE_TIMER_DECREASE.getAmount() / 100D);

            e.setDelay(delay);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerDamageEntity(PlayerDamageEntityEvent e) {
        if (api.hasSkill(e.getAttacker(), Skill.OUTGOING_PVE_DAMAGE_INCREASE)) {
            e.modifyBy(Skill.OUTGOING_PVE_DAMAGE_INCREASE.getAmount() / 100D);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onFoodLoss(FoodLevelChangeEvent e) {
        if (e.getEntity() instanceof Player player) {
            if (api.hasSkill(player, Skill.FOOD_LOSS_DECREASE)) {
                int currentFood = player.getFoodLevel();
                int newFood = e.getFoodLevel();

                if (newFood >= currentFood) {
                    return;
                }

                double cancelChance = Skill.FOOD_LOSS_DECREASE.getAmount() / 100D;
                if (new Random().nextDouble() < cancelChance) {
                    e.setCancelled(true);
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onInstantHealthPot(EntityRegainHealthEvent e) {
        if (e.getRegainReason().equals(EntityRegainHealthEvent.RegainReason.MAGIC)) {
            if (e.getEntity() instanceof Player player) {
                if (api.hasSkill(player, Skill.INSTANT_HEALTH_INCREASE)) {
                    double multiplier = 1 + Skill.INSTANT_HEALTH_INCREASE.getAmount() / 100D;
                    e.setAmount(e.getAmount() * multiplier);
                }
            }
        }
    }

    //---------------------------------------------------------------------------------------------
    // This is a workaround and should be simplified in the future (need more HSAdventureAPI methods)
    private final Map<UUID, UUID> entityLastAttackers = new HashMap<>();

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onAdventureMobKill(PlayerDamageEntityEvent e) {
        Entity victim = e.getVictim();
        Player attacker = e.getAttacker();

        if (advApi.isGuard(victim)) {
            if (api.hasSkill(attacker, Skill.ADVENTURE_MOB_LOOT)) {
                entityLastAttackers.put(victim.getUniqueId(), attacker.getUniqueId());
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onEntityDeath(EntityDeathEvent e) {
        UUID guardUuid = e.getEntity().getUniqueId();

        if (!entityLastAttackers.containsKey(guardUuid)) {
            return;
        }

        UUID playerUuid = entityLastAttackers.get(guardUuid);
        // Cleanup map
        entityLastAttackers.remove(guardUuid);

        // Check player is still online
        OfflinePlayer oPlayer = Bukkit.getOfflinePlayer(playerUuid);

        if (!oPlayer.isOnline()) {
            return;
        }

        Player player = oPlayer.getPlayer();

        // Roll chance
        if (new Random().nextDouble() < Skill.ADVENTURE_MOB_LOOT.getAmount() / 100D) {
            // Get Crate
            String crateName =
                    e.getEntity().getPersistentDataContainer().get(advApi.getGuardKey(), PersistentDataType.STRING).split(";"
                    )[0];

            Crate crate = advApi.getCrateFromName(crateName);

            if (crate == null) {
                main.getLogger().warning("Crate not found by name " + crateName);
                return;
            }

            // Give player the item!
            if (crate.getLoot() != null && !crate.getLoot().isEmpty()) {
                int randomInt = NumberUtils.randomInt(0, crate.getLoot().values().stream().toList().size() - 1);
                ItemStack item = crate.getLoot().values().stream().toList().get(randomInt).getItem();
                PlayerUtils.giveItem(player, item, TextUtils.translateColor("&4&l[!] &cYour " +
                        "inventory is full, your item was dropped on the floor!"));

                player.sendMessage(TextUtils.translateColor(
                    config.get("mob-loot", String.class, "&e+ {amount}x &f{item} &7(Adventure Mob Loot)")
                            .replace("{amount}", String.valueOf(item.getAmount()))
                            .replace("{item}", item.getItemMeta().getDisplayName())
                ));
            }
        }
    }
    //---------------------------------------------------------------------------------------------
}
