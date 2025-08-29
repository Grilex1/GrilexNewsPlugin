package grilex.grilexnewsplugin.utils.commandUtils;

import org.bukkit.entity.Player;

import java.sql.SQLException;

public abstract class SubCommand {
    public abstract String getName();

    public abstract String getDescription();

    public abstract String getSyntax();

    public abstract String getPermission();

    public abstract void perform(Player player, String args[]) throws SQLException;
}
