package grilex.grilexnewsplugin.Inventories;

import grilex.grilexnewsplugin.GrilexNewsPlugin;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;
import java.util.List;

public class GuiManager {
    private GrilexNewsPlugin plugin;
    private HashMap<String, List<Inventory>> guis;
    private FileConfiguration menuConfig;
    private Integer standartHomePage;

    public GuiManager(GrilexNewsPlugin plugin) {
        this.plugin = plugin;
        this.guis = new HashMap<>();
        this.menuConfig = this.plugin.getMenuConfig().getConfig();
        this.standartHomePage = 0;
        loadAllGuis();
    }


    public void loadAllGuis() {
        ConfigurationSection guisSection = this.menuConfig.getConfigurationSection("guis");
        if (guisSection == null) return;

        for (String guiKey : guisSection.getKeys(false)) {
            String name = guisSection.getString(guiKey + ".name");
            boolean is_paginated = guisSection.getBoolean(guiKey + ".is_paginated", false);
            String tableName = guisSection.getString(guiKey + ".table");
            GuiFactory factory = new GuiFactory(this.plugin, name, this.menuConfig, guiKey, is_paginated, tableName);
            List<Inventory> inventoryList = factory.getInventory();
            this.guis.put(guiKey, inventoryList);
        }

    }


    public List<Inventory> getGui(String name) {
        return this.guis.get(name);
    }

    public Integer getStandardHomePage() {
        return this.standartHomePage;
    }

    public boolean hasGuiInventory(Inventory inventory) {
        for (List<Inventory> inventoryList : this.guis.values()) {
            if (inventoryList.contains(inventory)) {
                return true;
            }
        }
        return false;
    }

}
