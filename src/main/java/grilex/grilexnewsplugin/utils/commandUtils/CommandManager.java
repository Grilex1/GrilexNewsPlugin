package grilex.grilexnewsplugin.utils.commandUtils;

import grilex.grilexnewsplugin.GrilexNewsPlugin;
import grilex.grilexnewsplugin.command.subcommands.Create;
import grilex.grilexnewsplugin.command.subcommands.Help;
import grilex.grilexnewsplugin.command.subcommands.Reload;

import java.util.ArrayList;

public class CommandManager {
    private final ArrayList<SubCommand> subCommands = new ArrayList<>();
    private final GrilexNewsPlugin plugin;

    public CommandManager(GrilexNewsPlugin plugin) {
        this.plugin = plugin;
        subCommands.add(new Create(this.plugin));
        subCommands.add(new Help(this.plugin));
        subCommands.add(new Reload(this.plugin));
    }

    public ArrayList<SubCommand> getSubCommand() {
        return subCommands;
    }

}
