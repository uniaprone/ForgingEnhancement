package org.zzq.forgingEnhancement.manager;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;

public class FileManager {
    private static volatile FileManager instance;
    private Plugin plugin;
    private ConfigManager configManager;
    private BaseAttributeManager baseAttributeManager;

    private FileManager(Plugin plugin) {
        this.plugin = plugin;
        initialize();
    }

    public static FileManager getInstance(Plugin plugin) {
        if (instance == null) {
            synchronized (FileManager.class) {
                if (instance == null) {
                    instance = new FileManager(plugin);
                }
            }
        }
        return instance;
    }

    private void initialize(){
        configManager = new ConfigManager(plugin);
        baseAttributeManager = new BaseAttributeManager(plugin);
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public BaseAttributeManager getBaseAttributeManager() {
        return baseAttributeManager;
    }

    public void reloadFile(){
        configManager.reloadConfig();
        baseAttributeManager.reloadBaseAttribute();
    }
}
