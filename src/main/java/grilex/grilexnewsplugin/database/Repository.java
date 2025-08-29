package grilex.grilexnewsplugin.database;

import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface Repository {
    void createItem(String text, String table);
    void deleteItem(int id, String table);
    int countItems(String table);
    List<String> getTextAsList(int itemId, String table);
    void post(InventoryClickEvent event,String clickType);
    void delete(InventoryClickEvent event,String clickType);
    void updateId(String table);
}
