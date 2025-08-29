package grilex.grilexnewsplugin.event;

import grilex.grilexnewsplugin.GrilexNewsPlugin;
import grilex.grilexnewsplugin.Inventories.GuiManager;
import grilex.grilexnewsplugin.database.Factories.RepositoryFactory;
import grilex.grilexnewsplugin.database.Repository;
import grilex.grilexnewsplugin.news.DraftFactory;
import grilex.grilexnewsplugin.news.NewsFactory;
import grilex.grilexnewsplugin.utils.textUtil.TextUtil;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.sql.SQLException;
import java.util.HashMap;

public class PlayerInventoryClickEvent implements Listener {
    private final GrilexNewsPlugin plugin;
    private final GuiManager guiManager;
    private final FileConfiguration messageConfig;
    private final DraftFactory draftFactory;
    private final NewsFactory newsFactory;
    private final HashMap<Player, Integer> currentPageNews;
    private final HashMap<Player, Integer> currentPageDrafts;
    private boolean deliveryMode;
    private final TextUtil textUtil;
    private final Repository repository;
    private final FileConfiguration newsConfig;

    public PlayerInventoryClickEvent(GrilexNewsPlugin plugin) throws SQLException {
        this.plugin = plugin;
        this.guiManager = this.plugin.getGuiManager();
        this.messageConfig = this.plugin.getMessageConfig().getConfig();
        this.newsConfig = this.plugin.getNewsConfig().getConfig();
        this.textUtil = new TextUtil();
        this.currentPageDrafts = new HashMap<>();
        this.currentPageNews = new HashMap<>();
        this.draftFactory = new DraftFactory(this.plugin);
        this.newsFactory = new NewsFactory(this.plugin);
        this.repository = new RepositoryFactory(this.plugin).getRepository(this.plugin.getConfig().getString("database.type"));
    }

    @EventHandler
    public void clickEvent(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;

        Player player = (Player) event.getWhoClicked();
        ItemStack currentItem = event.getCurrentItem();


        if (currentItem == null || !currentItem.hasItemMeta()) return;

        if (this.guiManager.hasGuiInventory(event.getInventory())) {
            event.setCancelled(true);

            ItemMeta meta = currentItem.getItemMeta();

            if (meta == null) return;


            String link = meta.getPersistentDataContainer().get(NamespacedKey.fromString("link"), PersistentDataType.STRING);
            String linkClickType = meta.getPersistentDataContainer().get(NamespacedKey.fromString("link_click_type"), PersistentDataType.STRING);

            if (link != null && linkClickType != null && event.getClick().name().equalsIgnoreCase(linkClickType)) {
                Inventory inventory = this.guiManager.getGui(link).get(this.guiManager.getStandardHomePage());
                if (inventory != null) {
                    player.openInventory(inventory);
                }
                return;
            }

            String prompt = meta.getPersistentDataContainer().get(NamespacedKey.fromString("prompt"), PersistentDataType.STRING);
            String promptClickType = meta.getPersistentDataContainer().get(NamespacedKey.fromString("prompt_click_type"), PersistentDataType.STRING);

            if (prompt != null && promptClickType != null && event.getClick().name().equalsIgnoreCase(promptClickType)) {
                this.deliveryMode = true;
                this.messageConfig.getStringList("prompt." + prompt).forEach((x -> {
                    player.sendMessage(this.textUtil.colorize(x));
                }));
            }

            this.draftFactory.createNewDrafts(event, this.deliveryMode);

            if (event.getInventory().equals(this.guiManager.getGui("news").get(this.currentPageNews.getOrDefault(player, 0)))) {
                event.setCancelled(true);
                this.currentPageNews.putIfAbsent(player, 0);

                Integer pageOffset = meta.getPersistentDataContainer().get(NamespacedKey.fromString("page_offset"), PersistentDataType.INTEGER);
                String offsetClickType = meta.getPersistentDataContainer().get(NamespacedKey.fromString("offset_click_type"), PersistentDataType.STRING);

                if (pageOffset != null && offsetClickType != null && event.getClick().name().equalsIgnoreCase(offsetClickType)) {
                    int maxSize = this.guiManager.getGui("news").size();
                    int playerPage = this.currentPageNews.get(player);
                    int newPage = playerPage + pageOffset;

                    if (newPage < 0 || newPage >= maxSize) {
                        return;
                    }

                    this.currentPageNews.put(player, newPage);
                    player.openInventory(this.guiManager.getGui("news").get(this.currentPageNews.get(player)));
                    this.repository.delete(event,this.newsConfig.getString("keywords.delete_news"));
                }

                this.newsFactory.openNews(event, "news", this.currentPageDrafts, this.currentPageNews);
            }

            if (event.getInventory().equals(this.guiManager.getGui("draft").get(this.currentPageDrafts.getOrDefault(player, 0)))) {
                this.currentPageDrafts.putIfAbsent(player, 0);

                this.repository.post(event,this.newsConfig.getString("keywords.delete_news"));
                this.repository.delete(event,this.newsConfig.getString("keywords.delete_news"));

                Integer pageOffset = meta.getPersistentDataContainer().get(NamespacedKey.fromString("page_offset"), PersistentDataType.INTEGER);
                String offsetClickType = meta.getPersistentDataContainer().get(NamespacedKey.fromString("offset_click_type"), PersistentDataType.STRING);

                if (pageOffset != null && offsetClickType != null && event.getClick().name().equalsIgnoreCase(offsetClickType)) {
                    int maxSize = this.guiManager.getGui("draft").size();
                    int playerPage = this.currentPageDrafts.get(player);
                    int newPage = playerPage + pageOffset;

                    if (newPage < 0 || newPage >= maxSize) {
                        return;
                    }
                    this.currentPageDrafts.put(player, newPage);
                    player.openInventory(this.guiManager.getGui("draft").get(this.currentPageDrafts.get(player)));
                }

                this.newsFactory.openNews(event, "draft", this.currentPageDrafts, this.currentPageNews);
            }
        }
    }

    @EventHandler
    public void closeInventory(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player)) return;
        Player player = (Player) event.getPlayer();


        if (this.guiManager.getGui("edit") != null && !this.guiManager.getGui("edit").isEmpty()) {
            Inventory editInventory = this.guiManager.getGui("edit").get(0);
            if (event.getInventory().equals(editInventory)) {
                this.deliveryMode = false;
            }
        }


        if (this.guiManager.getGui("news") != null && !this.guiManager.getGui("news").isEmpty()) {
            if (event.getInventory().equals(this.guiManager.getGui("news").get(this.currentPageNews.getOrDefault(player, 0)))) {
                this.currentPageNews.put(player, 0);
            }
        }


        if (this.guiManager.getGui("draft") != null && !this.guiManager.getGui("draft").isEmpty()) {
            if (event.getInventory().equals(this.guiManager.getGui("draft").get(this.currentPageDrafts.getOrDefault(player, 0)))) {
                this.currentPageDrafts.put(player, 0);
            }
        }
    }
}