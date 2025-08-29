package grilex.grilexnewsplugin.command.subcommands;

import grilex.grilexnewsplugin.GrilexNewsPlugin;
import grilex.grilexnewsplugin.utils.commandUtils.CommandManager;
import grilex.grilexnewsplugin.utils.commandUtils.SubCommand;
import grilex.grilexnewsplugin.utils.textUtil.TextUtil;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class Help extends SubCommand {
    private final GrilexNewsPlugin plugin;
    private FileConfiguration messageConfig;
    private TextUtil textUtil;
    private String before;
    private String after;

    public Help(GrilexNewsPlugin plugin) {
        this.plugin = plugin;
        this.messageConfig = this.plugin.getMessageConfig().getConfig();
        this.textUtil = new TextUtil();
        this.before = messageConfig.getString("command.help.before", "============");
        this.after = messageConfig.getString("command.help.after", "============");
        if (this.before.isEmpty()) {
            this.before = "============";
        }
        if (this.after.isEmpty()) {
            this.after = "============";
        }
    }


    @Override
    public String getName() {
        return "help";
    }

    @Override
    public String getDescription() {
        return "показывает список команд и их описание";
    }

    @Override
    public String getSyntax() {
        return "/news help";
    }

    @Override
    public String getPermission() {
        return "News.help";
    }

    @Override
    public void perform(Player player, String[] args) {
        if (player.hasPermission(getPermission()) || player.isOp()) {
            CommandManager commandManager = new CommandManager(this.plugin);

            player.sendMessage(this.textUtil.colorize(this.before));
            for (int i = 0; i < commandManager.getSubCommand().size(); i++) {
                String syntax = this.textUtil.colorize(commandManager.getSubCommand().get(i).getSyntax());
                String description = this.textUtil.colorize(commandManager.getSubCommand().get(i).getDescription());
                player.sendMessage(syntax + " - " + description);
            }
            player.sendMessage(this.textUtil.colorize(this.after));
        }
    }
}
