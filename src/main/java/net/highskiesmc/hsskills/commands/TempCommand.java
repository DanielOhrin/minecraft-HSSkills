package net.highskiesmc.hsskills.commands;

import net.highskiesmc.hsskills.HSSkills;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TempCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        Player player = (Player) commandSender;

        HSSkills.getApi().giveSkillToken(player, 1);

        return true;
    }
}
