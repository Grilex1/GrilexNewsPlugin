package grilex.grilexnewsplugin.command;

import grilex.grilexnewsplugin.GrilexNewsPlugin;
import grilex.grilexnewsplugin.Inventories.GuiManager;
import grilex.grilexnewsplugin.utils.commandUtils.Command;
import grilex.grilexnewsplugin.utils.commandUtils.CommandManager;
import grilex.grilexnewsplugin.utils.commandUtils.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class NewsCommand extends Command {
    private GrilexNewsPlugin plugin;
    private GuiManager guiManager;
    private String bypass = "News.bypass";

    CommandManager commandManager;

    public NewsCommand(GrilexNewsPlugin plugin) {
        super(plugin, "news", "News.use");
        this.plugin = plugin;
        this.commandManager = new CommandManager(this.plugin);
        this.guiManager = this.plugin.getGuiManager();
    }

    @Override
    public void execute(CommandSender sender, String label, String[] args, String permission) {
        if (args.length != 0) {
            for (int i = 0; i < this.commandManager.getSubCommand().size(); i++) {
                if (args[0].equalsIgnoreCase(this.commandManager.getSubCommand().get(i).getName())) {
                    if (sender instanceof Player) {
                        Player player = (Player) sender;
                        try {
                            this.commandManager.getSubCommand().get(i).perform(player, args);
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
        } else {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (player.hasPermission(permission) || player.hasPermission(this.bypass) || player.isOp()) {
                    player.openInventory(this.guiManager.getGui("news").get(this.guiManager.getStandardHomePage()));
                }
            }
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, org.bukkit.command.Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player)) {
            return List.of();
        }

        Player player = (Player) commandSender;

        if (strings.length > 1) {
            return List.of();
        }

        if (player.hasPermission(this.bypass)) {
            List<String> tabCompleter = new ArrayList<>();
            for (SubCommand subCommand : this.commandManager.getSubCommand()) {
                tabCompleter.add(subCommand.getName());
            }
            return tabCompleter;
        }
        return List.of();
    }
}
