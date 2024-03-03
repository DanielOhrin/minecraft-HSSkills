package net.highskiesmc.hsskills.inventories;

import net.highskiesmc.hscore.inventory.GUI;
import net.highskiesmc.hscore.utils.TextUtils;
import net.highskiesmc.hsskills.HSSkills;
import net.highskiesmc.hsskills.api.HSSkillsApi;
import net.highskiesmc.hsskills.api.Skills.Skill;
import net.highskiesmc.hsskills.api.Skills.SkillType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.checkerframework.checker.nullness.qual.NonNull;

public class SkillsGUI implements GUI {
    private final Player player;
    private final HSSkillsApi api;

    public SkillsGUI(@NonNull Player player) {
        this.player = player;
        this.api = HSSkills.getApi();
    }

    @Override
    public void onInventoryClick(InventoryClickEvent inventoryClickEvent) {
        // TODO: Handle upgrading skills here
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

        // TODO: Show tokens in gui title (1)
        for (SkillType skillType : SkillType.values()) {
            inventory.setItem(i, skillType.getDisplayItem(api.getSkillLevel(player, skillType)));
            i += 2;
        }
    }

    @Override
    @NonNull
    public Inventory getInventory() {
        Inventory inv = Bukkit.createInventory(this, 9,
                TextUtils.translateColor("&6&lPlayer Skills (" + api.getTokens(player) + ")"));
        // TODO: Open new GUI when successfully spending a token
        addContent(inv);

        return inv;
    }
}
