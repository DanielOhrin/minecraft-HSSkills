package net.highskiesmc.hsskills.inventories;

import net.highskiesmc.hscore.inventory.GUI;
import net.highskiesmc.hscore.utils.TextUtils;
import net.highskiesmc.hsskills.api.Skills.Skill;
import net.highskiesmc.hsskills.api.Skills.SkillType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.List;

public class SkillsGUI implements GUI {
    private final Player player;

    public SkillsGUI(@NonNull Player player) {
        this.player = player;
    }

    @Override
    public void onInventoryClick(InventoryClickEvent inventoryClickEvent) {

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
            inventory.setItem(i, skillType.getDisplayItem(3));
            i += 2;
        }
    }

    @Override
    @NonNull
    public Inventory getInventory() {
        Inventory inv = Bukkit.createInventory(this, 9, TextUtils.translateColor("&6&lPlayer Skills"));

        addContent(inv);

        return inv;
    }
}
