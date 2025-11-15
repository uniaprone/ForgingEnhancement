package org.zzq.forgingEnhancement.managers;

import org.bukkit.plugin.Plugin;

public class FileManager {
    private FileManager fileManager;
    private Plugin plugin;
    private ConfigManager configManager;
    private BaseAttributeManager baseAttributeManager;
    private PlayerSettingManager playerSettingManager;

    public FileManager(Plugin plugin) {
        this.plugin = plugin;
        initialize();
    }

    private void initialize(){
        configManager = new ConfigManager(plugin);
        baseAttributeManager = new BaseAttributeManager(plugin);
        playerSettingManager = new PlayerSettingManager(plugin);
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public BaseAttributeManager getBaseAttributeManager() {
        return baseAttributeManager;
    }

    public PlayerSettingManager getplayerSettingManager(){ return playerSettingManager;}

    public void reloadFile(){
        configManager.reloadConfig();
        baseAttributeManager.reloadBaseAttribute();
        playerSettingManager.reload();
    }
}
