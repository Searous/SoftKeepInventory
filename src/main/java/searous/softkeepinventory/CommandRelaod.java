package searous.softkeepinventory;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandRelaod implements CommandExecutor {
    private final SoftKeepInventory plugin;
    public CommandRelaod(SoftKeepInventory plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(args.length > 0 && args[0].equals("reload")) {
            plugin.reloadConfig();
            sender.sendMessage("Config reloaded");
            return true;
        }

        // sender.sendMessage(command.getUsage());

        return false;
    }
}
