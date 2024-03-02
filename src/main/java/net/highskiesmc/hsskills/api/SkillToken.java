package net.highskiesmc.hsskills.api;

import net.highskiesmc.hscore.utils.TextUtils;
import net.highskiesmc.hsskills.HSSkills;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Arrays;
import java.util.List;

public class SkillToken {
    private static final NamespacedKey KEY;
    private static final Material MATERIAL = Material.MAGMA_CREAM;
    private static final String DISPLAY_NAME;
    private static final List<String> lore = Arrays.asList(
            "&7Click to gain &6+1 &7Player",
            "&7Skill token which can",
            "&7be redeemed in /skills!"
    );

    static {
        KEY = HSSkills.getApi().SKILL_TOKEN_KEY;
        DISPLAY_NAME = TextUtils.translateColor("&6&lPlayer Skill Token (&7Right-Click&6&l)");
        lore.replaceAll(TextUtils::translateColor);
    }

    public static ItemStack get(int amount) {
        ItemStack token = new ItemStack(MATERIAL, amount);
        ItemMeta meta = token.getItemMeta();
        PersistentDataContainer pdc = meta.getPersistentDataContainer();

        pdc.set(KEY, PersistentDataType.STRING, "yep");

        meta.setDisplayName(DISPLAY_NAME);
        meta.setLore(lore);
        token.setItemMeta(meta);
        return token;
    }

    public static boolean isSkillToken(@NonNull ItemStack item) {
        ItemMeta meta = item.getItemMeta();

        if (meta == null) {
            return false;
        }

        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        return pdc.has(KEY, PersistentDataType.STRING);
    }
}
