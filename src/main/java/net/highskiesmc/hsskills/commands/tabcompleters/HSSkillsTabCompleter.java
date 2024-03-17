package net.highskiesmc.hsskills.commands.tabcompleters;

import net.highskiesmc.hscore.highskies.HSTabCompleter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.List;

public class HSSkillsTabCompleter extends HSTabCompleter {
    @Override
    public boolean hasPermission(CommandSender sender) {
        return sender.hasPermission("hsskills.tab");
    }

    @Override
    public List<String> getResults(@NonNull CommandSender sender, @NonNull Command cmd, @NonNull String label, @NonNull String[] args) {
        List<String> result = new ArrayList<>();

        switch (args.length) {
            case 1 -> {
                result.add("reload");
                result.add("givetoken");
            }
            case 2 -> {
                if (args[0].equalsIgnoreCase("givetoken")) {
                    return matchOnlinePlayers(args[1], true);
                }
            }
            case 3 -> {
                if (args[0].equalsIgnoreCase("givetoken")) {
                    result.add("amount");
                }
            }
        }

        return result;
    }
}
