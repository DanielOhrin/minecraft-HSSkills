package net.highskiesmc.hsskills.inventories;

import net.highskiesmc.hscore.inventory.GUI;
import net.highskiesmc.hscore.utils.TextUtils;
import net.highskiesmc.hsskills.HSSkills;
import net.highskiesmc.hsskills.api.HSSkillsApi;
import net.highskiesmc.hsskills.api.Rank;
import net.highskiesmc.hsskills.api.Skills.Skill;
import net.highskiesmc.hsskills.api.Skills.SkillType;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.checkerframework.checker.nullness.qual.NonNull;

public class SkillsGUI implements GUI {
    private final Player player;
    private final HSSkillsApi api;
    private final int tokens;

    public SkillsGUI(@NonNull Player player) {
        this.player = player;
        this.api = HSSkills.getApi();
        this.tokens = api.getTokens(player);
    }

    @Override
    public void onInventoryClick(InventoryClickEvent inventoryClickEvent) {
        if (tokens <= 0) {
            return;
        }

        switch (inventoryClickEvent.getRawSlot()) {
            case 2:
                if (api.upgradeSkill(player, SkillType.ISLAND)) {
                    player.closeInventory();
                    player.openInventory(new SkillsGUI(player).getInventory());
                }
                break;
            case 4:
                if (api.upgradeSkill(player, SkillType.PVE)) {
                    player.closeInventory();
                    player.openInventory(new SkillsGUI(player).getInventory());
                }
                break;
            case 6:
                if (api.upgradeSkill(player, SkillType.PVP)) {
                    if (api.getSkillLevel(player, SkillType.PVP) == 1) {
                        AttributeInstance maxHealth = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
                        maxHealth.setBaseValue(maxHealth.getBaseValue() + 2);
                    }

                    player.closeInventory();
                    player.openInventory(new SkillsGUI(player).getInventory());
                }
                break;
            default:
                break;
        }

    }

    @Override
    public void onInventoryOpen(InventoryOpenEvent inventoryOpenEvent) {

    }

    @Override
    public void onInventoryClose(InventoryCloseEvent inventoryCloseEvent) {

    }

    @Override
    public void addContent(Inventory inventory) {
        int i = 2;

        for (SkillType skillType : SkillType.values()) {
            inventory.setItem(i, skillType.getDisplayItem(api.getSkillLevel(player, skillType)));
            i += 2;
        }
    }
    // TODO: Feedback when unlocking a new skill!
    @Override
    @NonNull
    public Inventory getInventory() {
        Inventory inv = Bukkit.createInventory(this, 9,
                TextUtils.translateColor("&6&lPlayer Skills (" + tokens + ")"));

        addContent(inv);
        
        return inv;
    }
}
