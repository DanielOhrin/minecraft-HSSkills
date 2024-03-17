package net.highskiesmc.hsskills.commands;

import net.highskiesmc.hscore.commands.HSCommand;
import net.highskiesmc.hscore.highskies.HSPlugin;
import net.highskiesmc.hscore.utils.PlayerUtils;
import net.highskiesmc.hscore.utils.TextUtils;
import net.highskiesmc.hsskills.api.HSSkillsApi;
import net.highskiesmc.hsskills.api.SkillToken;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class HSSkillsCommand extends HSCommand {
    private final HSSkillsApi api;

    public HSSkillsCommand(HSPlugin main, HSSkillsApi api) {
        super(main);

        this.api = api;
    }

    @Override
    protected String getPermissionToReload() {
        return "hsskills.cmd.reload";
    }

    @Override
    public boolean executeCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length >= 1) {
            switch (args[0].toLowerCase()) {
                case "reload" -> {
                    return reload(sender, TextUtils.translateColor(config.get("cmd.reload", String.class,
                            "&fSuccessfully reloaded HSSkills")), getNoPermission());
                }
                case "givetoken" -> {
                    return giveToken(sender, args);
                }
            }
        }

        sender.sendMessage(getUsage());

        return false;
    }

    private boolean giveToken(CommandSender sender, String[] args) {
        if (!hasPermission(sender, "hsskills.cmd.givetoken", getNoPermission())) {
            return false;
        }

        if (args.length == 3) {
            Player player = Bukkit.getPlayerExact(args[1]);

            if (player == null || !player.isOnline()) {
                sender.sendMessage(getNoPlayer());
                return false;
            }

            int amount;

            try {
                amount = Integer.parseInt(args[2]);
            } catch (NumberFormatException ignore) {
                amount = 1;
            }

            ItemStack tokens = SkillToken.get(amount);
            PlayerUtils.giveItem(player, tokens, getFullInventory());

            return true;
        }

        sender.sendMessage(getUsage());
        return false;
    }

    private String getUsage() {
        return TextUtils.translateColor(config.get("usage", String.class, "&cUnknown command."));
    }

    private String getNoPermission() {
        return TextUtils.translateColor(config.get("no-permission", String.class, "&cInsufficient permission."));
    }

    private String getNoPlayer() {
        return TextUtils.translateColor(config.get("no-player", String.class, "&cPlayer not found."));
    }

    private String getFullInventory() {
        return TextUtils.translateColor(config.get("full-inventory", String.class,
                "&cInventory full. Some items were dropped on the ground."));
    }
}
