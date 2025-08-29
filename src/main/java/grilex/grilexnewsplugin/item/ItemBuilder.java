package grilex.grilexnewsplugin.item;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class ItemBuilder {
    private final ItemStack item;
    private final ItemMeta meta;

    public ItemBuilder(Material material) {
        this.item = new ItemStack(material);
        this.meta = item.getItemMeta();
    }

    public ItemBuilder setName(String name) {
        this.meta.setDisplayName(name);
        return this;
    }

    public ItemBuilder setAmount(int amount) {
        this.item.setAmount(amount);
        return this;
    }

    public ItemBuilder addEnchant(Enchantment enchant, int level) {
        this.meta.addEnchant(enchant, level, true);
        return this;
    }

    public ItemBuilder addFlags(ItemFlag... flags) {
        this.meta.addItemFlags(flags);
        return this;
    }

    public ItemBuilder setLore(List<String> lore) {
        this.meta.setLore(lore);
        return this;
    }


    public ItemBuilder addPersistentDataContainer(String NBT,String value){
        this.meta.getPersistentDataContainer().set(NamespacedKey.fromString(NBT), PersistentDataType.STRING,value);
        return this;
    }
    public ItemBuilder addPersistentDataContainer(String NBT,Integer value){
        this.meta.getPersistentDataContainer().set(NamespacedKey.fromString(NBT), PersistentDataType.INTEGER,value);
        return this;
    }

    public ItemBuilder setBook(String title,
                               String author,
                               List<String> pages
    ) {
        this.item.setType(Material.WRITTEN_BOOK);
        if (this.meta instanceof BookMeta) {
            BookMeta bookMeta = (BookMeta) this.meta;
            bookMeta.setTitle(title);
            bookMeta.setAuthor(author);
            List<String> cleanedPages = new ArrayList<>();
            for (String page : pages) {
                cleanedPages.add(page.replace("[", "").replace("]", ""));
            }
            //    LocalDate today = LocalDate.now();
            //    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yy");
            //     String formattedDate = today.format(formatter);
            bookMeta.setPages(cleanedPages);
        }
        return this;
    }

    public ItemBuilder setSkullOwner(String playerName) {
        if (this.meta instanceof SkullMeta) {
            SkullMeta skullMeta = (SkullMeta) this.meta;
            skullMeta.setOwningPlayer(Bukkit.getOfflinePlayer(playerName));
        }
        return this;
    }



    public ItemStack build() {
        this.item.setItemMeta(this.meta);
        return this.item;
    }

}
