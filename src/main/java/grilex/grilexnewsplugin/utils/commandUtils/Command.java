package grilex.grilexnewsplugin.utils.commandUtils;

import grilex.grilexnewsplugin.GrilexNewsPlugin;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;

public abstract class Command implements CommandExecutor, TabCompleter {
    private final String permission;
    private final GrilexNewsPlugin plugin;

    public Command(GrilexNewsPlugin plugin, String command,String permission) {
        this.plugin = plugin;
        this.permission = permission;
        PluginCommand pluginCommand = this.plugin.getCommand(command);
        if (pluginCommand != null) {
            pluginCommand.setExecutor(this);
            pluginCommand.setTabCompleter(this);
        }

    }

    public abstract void execute(CommandSender sender, String label, String[] args, String permission);

    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        execute(sender, label, args,this.permission);
        return true;
    }
}
