package net.highskiesmc.hsskills.api.Skills;

import net.highskiesmc.hscore.utils.TextUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public enum SkillType {
    ISLAND("Island", "&b&l", Material.GRASS_BLOCK),
    PVE("PvE", "&e&l", Material.IRON_SWORD),
    PVP("PvP", "&c&l", Material.DIAMOND_SWORD);
    private static final String LEVEL_COLOR = "&f";
    private static final String TEXT_COLOR = "&7";
    private final String title;
    private final String color;
    private final Material material;

    SkillType(String title, String color, Material material) {
        this.title = title;
        this.color = color;
        this.material = material;
    }

    public String getColor() {
        return color;
    }

    public Material getMaterial() {
        return material;
    }

    public ItemStack getDisplayItem(int level) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(TextUtils.translateColor(color + title + " Skill"));
        List<String> lore = new ArrayList<>() {{
            add(TextUtils.translateColor(color + "Level"));
            add(TextUtils.translateColor(" " + LEVEL_COLOR + level));
            add("");
            add(TextUtils.translateColor(color + "Levels"));
        }};

        List<Skill> skills = Skill.getSkills(this);
        for (int i = 0; i < skills.size(); i++) {
            Skill skill = skills.get(i);

            lore.add(TextUtils.translateColor(String.format(
                    " %s%d: %s%s%s",
                    color,
                    i + 1,
                    TEXT_COLOR,
                    i < level ? "&m" : "",
                    skill.getDescription()
            )));
        }

        lore.add("");
        lore.add(TextUtils.translateColor(TEXT_COLOR + "Click to upgrade a perk"));

        meta.setLore(lore);
        item.setItemMeta(meta);

        return item;
    }
}
