package grilex.grilexnewsplugin.news;

import grilex.grilexnewsplugin.GrilexNewsPlugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.HashMap;
import java.util.Objects;

import static org.bukkit.Material.WRITTEN_BOOK;

public class NewsFactory {
    private final GrilexNewsPlugin plugin;
    private final FileConfiguration newsConfig;

    public NewsFactory(GrilexNewsPlugin plugin) {
        this.plugin = plugin;
        this.newsConfig = this.plugin.getNewsConfig().getConfig();
    }


    public void openNews(
            InventoryClickEvent event,
            String inventoryType,
            HashMap<Player, Integer> currentPageDrafts,
            HashMap<Player, Integer> currentPageNews
    ) {
        if (event.getCurrentItem() == null) {
            return;
        }
        if (Objects.requireNonNull(event.getCurrentItem()).getType().equals(WRITTEN_BOOK) &&
            (event.getClick().name().equalsIgnoreCase(this.newsConfig.getString("keywords.open_news")))
        ) {

            Player player = (Player) event.getWhoClicked();
            player.closeInventory();
            if (inventoryType.equals("draft")) {
                currentPageDrafts.put(player, 0);
            } else {
                currentPageNews.put(player, 0);
            }
            player.openBook(event.getCurrentItem());
        }
    }
}
