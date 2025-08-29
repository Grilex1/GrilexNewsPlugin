package grilex.grilexnewsplugin.news;

import grilex.grilexnewsplugin.GrilexNewsPlugin;
import grilex.grilexnewsplugin.database.Factories.RepositoryFactory;
import grilex.grilexnewsplugin.database.Repository;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.meta.BookMeta;

import java.sql.SQLException;

import static org.bukkit.Material.WRITABLE_BOOK;

public class DraftFactory {
    private final GrilexNewsPlugin plugin;
    private final Repository repository;

    public DraftFactory(GrilexNewsPlugin plugin) {
        this.plugin = plugin;
        this.repository = new RepositoryFactory(this.plugin).getRepository(this.plugin.getConfig().getString("database.type"));
    }


    public void createNewDrafts(InventoryClickEvent event, boolean deliveryMode) {
        if (event.getClick() == ClickType.DROP
                && deliveryMode
                && event.getCurrentItem() != null
                && event.getCurrentItem().getType().equals(WRITABLE_BOOK)) {

            BookMeta bookMeta = (BookMeta) event.getCurrentItem().getItemMeta();
            if (bookMeta != null) {
                StringBuilder bookContent = new StringBuilder();
                for (String page : bookMeta.getPages()) {
                    bookContent.append(page).append("\n");
                }
                String text = bookContent.toString().trim();
                System.out.println("create?");
                repository.createItem(text, "drafts");
                event.getWhoClicked().sendMessage("Ваш текст был сохранен в черновиках!");
            }
        }
    }
}
