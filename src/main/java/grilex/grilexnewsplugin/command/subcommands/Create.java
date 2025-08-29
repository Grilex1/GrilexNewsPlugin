package grilex.grilexnewsplugin.command.subcommands;

import grilex.grilexnewsplugin.GrilexNewsPlugin;
import grilex.grilexnewsplugin.utils.commandUtils.SubCommand;
import org.bukkit.entity.Player;

public class Create extends SubCommand {
    private final GrilexNewsPlugin plugin;

    public Create(GrilexNewsPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "create";
    }

    @Override
    public String getDescription() {
        return "Дает возможность создать новость";
    }

    @Override
    public String getSyntax() {
        return "/news create";
    }

    @Override
    public String getPermission() {
        return "News.create";
    }

    @Override
    public void perform(Player player, String[] args) {
        if (player.hasPermission(getPermission()) || player.isOp()) {
            player.openInventory(this.plugin.getGuiManager().getGui("edit").get(this.plugin.getGuiManager().getStandardHomePage()));
        }
    }
}
