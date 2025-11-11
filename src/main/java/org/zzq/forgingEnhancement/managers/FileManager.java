package org.zzq.forgingEnhancement.managers;

import org.bukkit.plugin.Plugin;

public class FileManager {
    private FileManager fileManager;
    private Plugin plugin;
    private ConfigManager configManager;
    private BaseAttributeManager baseAttributeManager;

    public FileManager(Plugin plugin) {
        this.plugin = plugin;
        initialize();
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
