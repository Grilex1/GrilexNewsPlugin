package grilex.grilexnewsplugin;

import grilex.grilexnewsplugin.Inventories.GuiManager;
import grilex.grilexnewsplugin.command.NewsCommand;
import grilex.grilexnewsplugin.database.DatabaseConnection;
import grilex.grilexnewsplugin.database.Factories.DatabaseFactory;
import grilex.grilexnewsplugin.event.PlayerInventoryClickEvent;
import grilex.grilexnewsplugin.utils.configUtil.ConfigUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFactory;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;

public final class GrilexNewsPlugin extends JavaPlugin {
    private ConfigUtil menuConfig;
    private ConfigUtil messageConfig;
    private ConfigUtil newsConfig;
    private GuiManager guiManager;
    private DatabaseConnection data;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        loadConfigs();
        DatabaseFactory databaseFactory = new DatabaseFactory(this);
        try {
            this.data = databaseFactory.getDatabaseConnection(getConfig().getString("database.type"));
            this.data.getConnection();
            this.data.createTable();
        } catch (SQLException e) {
            throw new RuntimeException("Cannot to create table", e);
        }
        this.guiManager = new GuiManager(this);
        try {
            Bukkit.getPluginManager().registerEvents(new PlayerInventoryClickEvent(this), this);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        new NewsCommand(this);

    }

    @Override
    public void onDisable() {
        this.data.closeConnection();
        for(Player player:Bukkit.getOnlinePlayers()) {
            if (this.guiManager.hasGuiInventory((player.getOpenInventory().getTopInventory()))) {
                player.closeInventory();
            }
        }
    }

    private void loadConfigs() {
        this.menuConfig = ConfigUtil.of(this,"menu.yml");
        this.messageConfig = ConfigUtil.of(this,"message.yml");
        this.newsConfig = ConfigUtil.of(this,"news.yml");
    }
    public ConfigUtil getMenuConfig() {
        return this.menuConfig;
    }

    public ConfigUtil getMessageConfig() {
        return this.messageConfig;
    }

    public ConfigUtil getNewsConfig() {
        return this.newsConfig;
    }

    public GuiManager getGuiManager(){
        return this.guiManager;
    }
}
