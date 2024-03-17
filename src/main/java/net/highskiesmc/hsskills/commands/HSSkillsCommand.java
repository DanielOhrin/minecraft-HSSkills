package net.highskiesmc.hsskills.commands;

import net.highskiesmc.hscore.commands.HSCommand;
import net.highskiesmc.hscore.highskies.HSPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class HSSkillsCommand extends HSCommand {
    public HSSkillsCommand(HSPlugin main) {
        super(main);
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
                    return reload(sender, config.get("cmd.reload", String.class, "&fSuccessfully reloaded HSSkills"),
                            config.get("no-permission", String.class, "&cInsufficient permission."));
                }
                case "givetoken" -> {
                    return giveToken(sender, args);
                }
            }
        }

        String usage = config.get("usage", String.class, "&cUnknown command");
        sender.sendMessage(usage);

        return false;
    }

    private boolean giveToken(CommandSender sender, String[] args) {
        if (args.length == 3) {

        }
    }
}
