package grilex.grilexnewsplugin.utils.configUtil;

import com.google.common.base.Charsets;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ConfigUtil {
    private FileConfiguration config;
    private final JavaPlugin plugin;
    private final String name;
    private final File configFile;


    public static ConfigUtil of(JavaPlugin plugin, String name) {
        ConfigUtil configLoader = new ConfigUtil(plugin, name);
        configLoader.saveDefault();
        return configLoader;
    }

    public ConfigUtil(JavaPlugin plugin, String name) {
        this.plugin = plugin;
        this.name = name;
        this.configFile = new File(plugin.getDataFolder(), name);

    }

    public void saveConfig() {
        try {
            this.getConfig().save(this.configFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void saveDefault() {
        if (!configFile.exists()) {
            plugin.saveResource(name, false);
        }
    }

    public FileConfiguration getConfig() {
        if (this.config == null) {
            this.reloadConfig();
        }

        return this.config;
    }

    public void reloadConfig() {
        this.config = YamlConfiguration.loadConfiguration(this.configFile);
        InputStream defConfigStream = plugin.getResource(name);
        if (defConfigStream != null) {
            this.config.setDefaults(YamlConfiguration.loadConfiguration(new InputStreamReader(defConfigStream, Charsets.UTF_8)));
        }
    }
}
