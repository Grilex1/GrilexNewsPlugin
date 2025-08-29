package grilex.grilexnewsplugin.Inventories;

import grilex.grilexnewsplugin.GrilexNewsPlugin;
import grilex.grilexnewsplugin.database.Factories.RepositoryFactory;
import grilex.grilexnewsplugin.database.Repository;
import grilex.grilexnewsplugin.item.ItemBuilder;
import grilex.grilexnewsplugin.utils.textUtil.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class GuiFactory {
    protected GrilexNewsPlugin plugin;
    protected String name;
    protected byte size;
    protected List<Inventory> inventory;
    protected boolean isPaginated;
    protected TextUtil textUtil;
    protected Map<Character, ItemStack> itemsMap;
    protected List<String> schema;
    protected FileConfiguration messageConfig;
    protected FileConfiguration newsConfig;
    protected int itemsPerPage;
    protected int freeSlots;
    protected Repository repository;
    protected String tableName;

    public GuiFactory(GrilexNewsPlugin plugin, String name, FileConfiguration config, String configName, boolean isPaginated, String tableName) {
        this.plugin = plugin;
        this.messageConfig = this.plugin.getMessageConfig().getConfig();
        this.isPaginated = isPaginated;
        this.textUtil = new TextUtil();
        this.name = name;
        this.newsConfig = this.plugin.getNewsConfig().getConfig();
        this.itemsMap = new HashMap<>();
        this.inventory = new ArrayList<>();
        this.tableName = tableName;
        this.repository = new RepositoryFactory(this.plugin).getRepository(this.plugin.getConfig().getString("database.type"));
        loadSchema(config.getConfigurationSection("guis." + configName));
        this.size = calculateSizeFromSchema();
        if (this.name != null) {
            this.inventory.add(Bukkit.createInventory(null, this.size, this.textUtil.colorize(name)));
        } else {
            Bukkit.getLogger().info(this.textUtil.colorize(this.messageConfig.getString("exception.menu.name_exception")));
            this.name = this.textUtil.colorize(this.messageConfig.getString("exception.menu.name_exception"));
            this.inventory.add(Bukkit.createInventory(null, this.size, this.textUtil.colorize(name)));
        }
        if (!isPaginated) {
            buildStandardInventory();
        } else {
            buildPaginatedInventory();
        }
        calculateFreeSlots();
    }

    private byte calculateSizeFromSchema() {
        if (this.schema == null || this.schema.isEmpty()) return 9;

        int rows = this.schema.size();
        if (rows > 6) {
            rows = 6;
            Bukkit.getLogger().info(
                    this.textUtil.colorize(
                            this.messageConfig.getString("exception.menu.size_exception")
                    )
            );
        }

        return (byte) (rows * 9);
    }

    public void loadSchema(ConfigurationSection config) {
        if (config == null) {
            return;
        }

        this.schema = config.getStringList("schema");

        ConfigurationSection itemsConfig = config.getConfigurationSection("items");
        if (itemsConfig != null) {
            for (String key : itemsConfig.getKeys(false)) {
                if (key == null || key.isEmpty()) continue;

                char symbol = key.charAt(0);
                ConfigurationSection itemConfig = itemsConfig.getConfigurationSection(key);
                if (itemConfig != null) {
                    ItemStack item = loadItemFromConfig(itemConfig);
                    this.itemsMap.put(symbol, item);
                }
            }
        }
    }

    private ItemStack loadItemFromConfig(ConfigurationSection config) {
        Material material = Material.valueOf(config.getString("material", "AIR"));
        ItemBuilder builder = new ItemBuilder(material);

        if (config.contains("amount")) {
            builder.setAmount(config.getInt("amount"));
        }

        if (config.contains("lore")) {
            builder.setLore(this.textUtil.colorizeList(config.getStringList("lore")));
        }

        if (config.contains("player")) {
            builder.setSkullOwner(config.getString("player"));
        }

        if (config.contains("link")) {
            builder.addPersistentDataContainer("link", config.getString("link"));
        }

        if (config.contains("link_click_type")) {
            builder.addPersistentDataContainer("link_click_type", config.getString("link_click_type"));
        }

        if (config.contains("prompt")) {
            builder.addPersistentDataContainer("prompt", config.getString("prompt"));
        }

        if (config.contains("prompt_click_type")) {
            builder.addPersistentDataContainer("prompt_click_type", config.getString("prompt_click_type"));
        }

        if (config.contains("page_offset")) {
            builder.addPersistentDataContainer("page_offset", config.getInt("page_offset"));
        }

        if (config.contains("offset_click_type")) {
            builder.addPersistentDataContainer("offset_click_type", config.getString("offset_click_type"));
        }

        if (config.contains("enchants")) {
            if (config.isString("enchants")) {
                String enchantLine = config.getString("enchants");
                String[] parts = enchantLine.split(":");
                try {
                    Enchantment enchant = Enchantment.getByName(parts[0]);
                    int level = parts.length > 1 ? Integer.parseInt(parts[1]) : 1;
                    builder.addEnchant(enchant, level);
                } catch (IllegalArgumentException e) {
                    Bukkit.getLogger().warning("Ошибка зачарования: " + enchantLine);
                }
            } else if (config.isList("enchants")) {
                for (String enchant : config.getStringList("enchants")) {
                    String[] parts = enchant.split(":");
                    builder.addEnchant(
                            Enchantment.getByName(parts[0]),
                            parts.length > 1 ? Integer.parseInt(parts[1]) : 1
                    );
                }
            }
        }

        if (config.contains("flags")) {

            if (config.isString("flags")) {
                String flagName = config.getString("flags");
                try {
                    builder.addFlags(ItemFlag.valueOf(flagName));
                } catch (IllegalArgumentException e) {
                    Bukkit.getLogger().warning("Неизвестный флаг: " + flagName);
                }
            } else if (config.isConfigurationSection("flags")) {
                Set<String> flags = config.getConfigurationSection("flags").getKeys(false);
                for (String flag : flags) {
                    if (config.getBoolean("flags." + flag)) {
                        try {
                            builder.addFlags(ItemFlag.valueOf(flag));
                        } catch (IllegalArgumentException e) {
                            Bukkit.getLogger().warning("Неизвестный флаг: " + flag);
                        }
                    }
                }
            }
        }

        if (config.contains("name")) {
            builder.setName(this.textUtil.colorize(config.getString("name")));
        }

        return builder.build();
    }

    public void buildStandardInventory() {
        if (this.schema == null) return;

        int row = 0;
        for (String line : this.schema) {
            for (int col = 0; col < line.length(); col++) {
                char symbol = line.charAt(col);
                int slot = row * 9 + col;

                if (this.itemsMap.containsKey(symbol) && slot < this.size) {
                    this.inventory.get(0).setItem(slot, this.itemsMap.get(symbol));
                }
            }
            row++;
        }
    }


    public void buildPaginatedInventory() {
        if (this.schema == null || this.repository == null) {
            return;
        }


        this.inventory.clear();

        calculateFreeSlotsFromSchema();

        int newsCount = this.repository.countItems(this.tableName);


        if (newsCount == 0) {
            Inventory emptyInventory = createPaginatedInventory(0, 1);
            this.inventory.add(emptyInventory);
            return;
        }

        int totalPages = (int) Math.ceil((double) newsCount / this.itemsPerPage);


        for (int page = 0; page < totalPages; page++) {
            Inventory inventory = createPaginatedInventory(page, totalPages);
            this.inventory.add(inventory);

        }
    }

    private Inventory createPaginatedInventory(int page, int totalPages) {
        String pageName = this.name.replace("%current_page%", String.valueOf(page + 1))
                .replace("%total_page%", String.valueOf(totalPages));
        Inventory inventory = Bukkit.createInventory(null, this.size, this.textUtil.colorize(pageName));

        fillStaticItems(inventory);
        fillContent(inventory, page);

        return inventory;
    }

    private void fillContent(Inventory inventory, int page) {
        int startIndex = page * this.itemsPerPage;

        for (int i = 0; i < this.itemsPerPage; i++) {
            int currentIndex = startIndex + i;
            int recordId = currentIndex + 1;

            List<String> itemTextList = this.repository.getTextAsList(recordId, this.tableName);

            if (itemTextList == null || itemTextList.isEmpty()) {
                continue;
            }

            ItemStack item = null;

            if (this.tableName.equals("clean")) {
                item = new ItemBuilder(Material.WRITTEN_BOOK)
                        .setBook(this.textUtil.colorize(this.newsConfig.getString("name.news_name")),
                                this.textUtil.colorize( this.newsConfig.getString("name.author")),
                                this.textUtil.colorizeList(itemTextList))
                        .setLore(this.newsConfig.getStringList("name.lore"))
                        .addPersistentDataContainer("news_id", recordId)
                        .build();

            } else if (this.tableName.equals("drafts")) {
                item = new ItemBuilder(Material.WRITTEN_BOOK)
                        .setBook(this.textUtil.colorize(this.newsConfig.getString("name.draft_name")),
                                this.textUtil.colorize( this.newsConfig.getString("name.author")),
                                itemTextList)
                        .addPersistentDataContainer("news_id", recordId)
                        .setLore(this.newsConfig.getStringList("name.lore"))
                        .build();
            }

            if (item != null) {
                int freeSlot = findFreeSlot(inventory);
                if (freeSlot != -1) {
                    inventory.setItem(freeSlot, item);
                }
            }
        }
    }

    private int findFreeSlot(Inventory inventory) {
        for (int i = 0; i < inventory.getSize(); i++) {
            ItemStack item = inventory.getItem(i);
            if (item == null || item.getType() == Material.AIR) {
                return i;
            }
        }
        return -1;
    }

    private void calculateFreeSlots() {
        this.freeSlots = 0;
        if (!this.inventory.isEmpty()) {
            Inventory firstInventory = this.inventory.get(0);
            for (ItemStack item : firstInventory.getContents()) {
                if (item == null || item.getType() == Material.AIR) {
                    this.freeSlots++;
                }
            }
        }
    }

    private void calculateFreeSlotsFromSchema() {
        this.freeSlots = 0;

        Inventory tempInventory = Bukkit.createInventory(null, this.size, "temp");
        fillStaticItems(tempInventory);

        for (int i = 0; i < tempInventory.getSize(); i++) {
            ItemStack item = tempInventory.getItem(i);
            if (item == null || item.getType() == Material.AIR) {
                this.freeSlots++;
            }
        }

        this.itemsPerPage = this.freeSlots;

    }

    private void fillStaticItems(Inventory inventory) {
        if (this.schema == null) return;

        int row = 0;
        for (String line : this.schema) {
            for (int col = 0; col < line.length(); col++) {
                char symbol = line.charAt(col);
                int slot = row * 9 + col;

                if (slot >= this.size) continue;

                if (this.itemsMap.containsKey(symbol)) {
                    inventory.setItem(slot, this.itemsMap.get(symbol));
                } else {
                    inventory.setItem(slot, new ItemStack(Material.AIR));
                }
            }
            row++;
        }
    }


    public List<Inventory> getInventory() {
        return this.inventory;
    }

}
