package grilex.grilexnewsplugin.database.mysql;

import grilex.grilexnewsplugin.database.Repository;
import grilex.grilexnewsplugin.utils.textUtil.TextUtil;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MySqlBookRepository implements Repository {
    private final MySqlDatabaseConnection connection;

    public MySqlBookRepository(MySqlDatabaseConnection connection) {
        this.connection = connection;
    }

    @Override
    public void createItem(String text, String table) {
        if (text == null || text.isEmpty()) {
            return;
        }

        String sql = "INSERT INTO " + table + " (text) VALUES (?)";
        try (PreparedStatement pstmt = this.connection.getConnection().prepareStatement(sql)) {
            pstmt.setString(1, text);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteItem(int id, String table) {
        String sql = "DELETE FROM " + table + " WHERE id = ?";
        try (PreparedStatement pstmt = this.connection.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int countItems(String table) {
        String sql = "SELECT COUNT(*) AS count FROM " + table;
        try (Statement stmt = this.connection.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt("count");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public List<String> getTextAsList(int itemId, String table) {
        String sql = "SELECT text FROM " + table + " WHERE id = ? LIMIT 1";
        List<String> lines = new ArrayList<>();

        try (PreparedStatement pstmt = this.connection.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, itemId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String text = new TextUtil().colorize(rs.getString("text"));
                    if (text != null) {
                        lines = Arrays.stream(text.split("\n"))
                                .map(String::trim)
                                .filter(line -> !line.isEmpty())
                                .collect(Collectors.toList());
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lines;
    }

    @Override
    public void post(InventoryClickEvent event,String clickType) {
        if (event.getCurrentItem() == null) {
            return;
        }

        if (event.getClick().name().equals(clickType) &&
                event.getCurrentItem().getType() == Material.WRITTEN_BOOK) {
            ItemMeta meta = event.getCurrentItem().getItemMeta();
            if (meta != null) {
                int itemId = meta.getPersistentDataContainer()
                        .get(NamespacedKey.fromString("news_id"), PersistentDataType.INTEGER);

                List<String> itemTextList = getTextAsList(itemId, "drafts");
                String itemText = String.join("\n", itemTextList);

                createItem(itemText, "clean");
                deleteItem(itemId, "drafts");
            }
            updateId("drafts");

            event.getInventory().setItem(event.getSlot(), null);
        }
    }

    @Override
    public void delete(InventoryClickEvent event,String clickType) {
        if (event.getCurrentItem() == null) {
            return;
        }

        if (event.getClick().name().equals(clickType) &&
                event.getCurrentItem().getType() == Material.WRITTEN_BOOK) {

            ItemMeta meta = event.getCurrentItem().getItemMeta();
            if (meta != null) {

                int itemId = meta.getPersistentDataContainer()
                        .get(NamespacedKey.fromString("news_id"), PersistentDataType.INTEGER);

                deleteItem(itemId, "drafts");
            }
            updateId("drafts");

            event.getInventory().setItem(event.getSlot(), null);
        }
    }

    @Override
    public void updateId(String table) {
        try (Statement stmt = connection.getConnection().createStatement()) {
            stmt.execute("SET @new_id = 0");
            stmt.execute("UPDATE " + table + " SET id = (@new_id := @new_id + 1) ORDER BY id");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}
