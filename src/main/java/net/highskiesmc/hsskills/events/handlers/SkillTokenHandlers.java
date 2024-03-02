package net.highskiesmc.hsskills.events.handlers;

import net.highskiesmc.hscore.highskies.HSListener;
import net.highskiesmc.hscore.highskies.HSPlugin;
import net.highskiesmc.hsskills.api.HSSkillsApi;
import net.highskiesmc.hsskills.api.SkillToken;
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

        // TODO: Check if player is allowed to claim more skill tokens based on their rank and current tokens claimed
        int amount = item.getAmount();

        if (api.claimSkillToken(e.getPlayer())) {
            if (amount == 1) {
                e.getPlayer().getInventory().setItemInMainHand(null);
            } else {
                item.setAmount(amount - 1);
            }
            // TODO: Player feedback
        }

    }
}
