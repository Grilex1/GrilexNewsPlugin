package grilex.grilexnewsplugin.command.subcommands;

import grilex.grilexnewsplugin.GrilexNewsPlugin;
import grilex.grilexnewsplugin.utils.commandUtils.SubCommand;
import grilex.grilexnewsplugin.utils.textUtil.TextUtil;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class Reload extends SubCommand {
    private final GrilexNewsPlugin plugin;
    private FileConfiguration config;
    private FileConfiguration messageConfig;
    private TextUtil textUtils;

    public Reload(GrilexNewsPlugin plugin) {
        this.plugin = plugin;
        this.config = this.plugin.getConfig();
        this.messageConfig = this.plugin.getMessageConfig().getConfig();
        this.textUtils = new TextUtil();
    }

    @Override
    public String getName() {
        return "reload";
    }

    @Override
    public String getDescription() {
        return "перезагрузка плагина";
    }

    @Override
    public String getSyntax() {
        return "/news reload";
    }

    @Override
    public String getPermission() {
        return "News.reload";
    }

    @Override
    public void perform(Player player, String[] args) {
        if (player.hasPermission(getPermission()) || player.isOp()) {
            this.plugin.saveConfig();
            this.plugin.getPluginLoader().disablePlugin(this.plugin);
            this.plugin.getPluginLoader().enablePlugin(this.plugin);
            player.sendMessage(this.textUtils.colorize(config.getString("prefix")) + this.textUtils.colorize(this.messageConfig.getString("command.reload")));

        }
    }
}
