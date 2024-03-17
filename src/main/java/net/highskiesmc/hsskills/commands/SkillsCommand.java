package net.highskiesmc.hsskills.commands;

import net.highskiesmc.hscore.commands.HSCommand;
import net.highskiesmc.hscore.highskies.HSPlugin;
import net.highskiesmc.hsskills.inventories.SkillsGUI;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class SkillsCommand extends HSCommand {
    public SkillsCommand(HSPlugin main) {
        super(main);
    }

    @Override
    protected String getPermissionToReload() {
        return null;
    }

    @Override
    public boolean executeCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (commandSender instanceof ConsoleCommandSender) {
            commandSender.sendMessage(ChatColor.RED + ChatColor.BOLD.toString() + "That command can only be used by " +
                    "players!");

            return false;
        }

        if (commandSender instanceof Player player) {
            if (hasPermission(player, "hsskills.cmd.skills", ChatColor.RED + "Insufficient Permission")) {
                Inventory inv = new SkillsGUI(player, config).getInventory();

                player.openInventory(inv);
            }
        }

        return false;
    }
}
