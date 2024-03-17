package net.highskiesmc.hsskills.events.handlers;

import net.highskiesmc.hscore.highskies.HSListener;
import net.highskiesmc.hscore.highskies.HSPlugin;
import net.highskiesmc.hsskills.api.HSSkillsApi;
import net.highskiesmc.hsskills.api.Skills.Skill;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.Random;

public class PvPSkillHandlers extends HSListener {
    private final HSSkillsApi api;

    public PvPSkillHandlers(HSPlugin main, HSSkillsApi api) {
        super(main);
        this.api = api;
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerDamageEntityBlocking(EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Player player) {
            if (api.hasSkill(player, Skill.IGNORE_SHIELD)) {
                double chance = Skill.IGNORE_SHIELD.getAmount() / 100D;

                if (new Random().nextDouble() <= chance) {
                    e.setDamage(EntityDamageEvent.DamageModifier.BLOCKING, 1);
                }
            }
        }
    }
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onArmorDamage(PlayerItemDamageEvent e) {
        if (!EnchantmentTarget.ARMOR.includes(e.getItem())) {
            return;
        }

        Player player = e.getPlayer();

        if (api.hasSkill(player, Skill.ARMOR_DURABILITY_INCREASE)) {
            // The idea here is 1 / 2x = 50% chance to HAVE durability loss
            // 1 / 3x - 0.33% to HAVE durability loss
            // 1 / 4x = 0.25% to HAVE durability loss
            // etc...
            double chanceToNotCancelDurabilityLoss = 1D / Skill.ARMOR_DURABILITY_INCREASE.getAmount();
            double roll = new Random().nextDouble();

            if (roll > chanceToNotCancelDurabilityLoss) {
                e.setCancelled(true);
            }
        }
    }
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onDualWieldWeaponHotswap(PlayerSwapHandItemsEvent e) {
        if (!isAllowedInOffhand(e.getPlayer(), e.getOffHandItem())) {
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onManualDualWieldWeapon(InventoryClickEvent e) {
        ItemStack item = e.getCursor();

        if (e.getWhoClicked() instanceof Player player) {
            switch (e.getAction()) {
                case HOTBAR_SWAP -> {
                    item = e.getCurrentItem();
                    if (!isAllowedInOffhand(player, item)) {
                        e.setCancelled(true);
                    }
                }
                case PLACE_ALL, PLACE_ONE, PLACE_SOME, SWAP_WITH_CURSOR -> {
                    if (e.getSlotType().equals(InventoryType.SlotType.QUICKBAR) && e.getRawSlot() == 45) {
                        if (!isAllowedInOffhand(player, item)) {
                            e.setCancelled(true);
                        }
                    }
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onDragIntoOffhand(InventoryDragEvent e) {
        if (e.getWhoClicked() instanceof Player player) {
            InventoryHolder holder = e.getInventory().getHolder();

            if (holder == null) {
                return;
            }

            if (holder.toString().contains(player.getName())) {
                Map<Integer, ItemStack> items = e.getNewItems();

                if (items.containsKey(45)) {
                    if (!isAllowedInOffhand(player, items.get(45))) {
                        e.setCancelled(true);
                    }
                }
            }
        }
    }

    private boolean isAllowedInOffhand(Player player, ItemStack item) {
        // Check if it is a tool/weapon (a typical damaging item)
        if (!EnchantmentTarget.WEAPON.includes(item) && !EnchantmentTarget.TOOL.includes(item)) {
            return true;
        }

        return api.hasSkill(player, Skill.DUAL_WIELD);
    }
}
