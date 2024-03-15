package net.highskiesmc.hsskills.events.handlers;

import net.highskiesmc.hscore.highskies.HSListener;
import net.highskiesmc.hscore.highskies.HSPlugin;
import net.highskiesmc.hsmisc.events.events.entitydamage.PlayerDamageEntityEvent;
import net.highskiesmc.hsskills.api.HSSkillsApi;
import net.highskiesmc.hsskills.api.Skills.Skill;
import net.highskiesmc.hsskills.api.Skills.SkillType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;

import java.util.Random;

public class PvESkillHandlers extends HSListener {
    private final HSSkillsApi api;

    public PvESkillHandlers(HSPlugin main, HSSkillsApi api) {
        super(main);
        this.api = api;
    }

//    ADVENTURE_LEAVE_TIMER_DECREASE(SkillType.PVE, 50, (Skill skill) -> "-" + skill.amount + "% /adventure leave
//    timer"),
//    ADVENTURE_MOB_LOOT(SkillType.PVE, 5, "Adventure Mob Loot");
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
                if (new Random().nextDouble() > cancelChance) {
                    e.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
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
}
