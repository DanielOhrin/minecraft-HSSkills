package net.highskiesmc.hsskills.events.handlers;

import net.highskiesmc.hscore.highskies.HSListener;
import net.highskiesmc.hscore.highskies.HSPlugin;
import net.highskiesmc.hscore.utils.TextUtils;
import net.highskiesmc.hsskills.api.HSSkillsApi;
import net.highskiesmc.hsskills.api.SkillToken;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

public class SkillTokenHandlers extends HSListener {
    private final HSSkillsApi api;

    public SkillTokenHandlers(HSPlugin main, HSSkillsApi api) {
        super(main);
        this.api = api;
    }

    @EventHandler
    public void onClaimSkillToken(PlayerInteractEvent e) {
        if (!e.hasItem() || !Arrays.asList(Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK).contains(e.getAction()) || e.getHand() != (EquipmentSlot.HAND)) {
            return;
        }

        ItemStack item = e.getItem();

        assert item != null;
        if (!SkillToken.isSkillToken(item)) {
            return;
        }

        int amount = item.getAmount();

        Player player = e.getPlayer();
        if (api.claimSkillToken(player)) {
            if (amount == 1) {
                player.getInventory().setItemInMainHand(null);
            } else {
                item.setAmount(amount - 1);
            }

            Sound sound = Sound.valueOf(config.get("skill-token.claim-sound", String.class,
                    "ENTITY_EXPERIENCE_ORB_PICKUP"));
            String msg = config.get("skill-token.claim", String.class, "&f&l+1 &6&lSkill Token");

            player.playSound(player.getLocation(), sound, 1, 1);
            player.sendMessage(TextUtils.translateColor(msg));
        } else {
            player.sendMessage(TextUtils.translateColor(
                    config.get("skill-token.cannot-claim", String.class, "&cYou cannot claim more skill tokens!")
            ));
        }

    }
}
