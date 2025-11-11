package org.zzq.forgingEnhancement.Service;

import org.apache.maven.artifact.repository.metadata.Plugin;
import org.bukkit.NamespacedKey;
import org.zzq.forgingEnhancement.ForgingEnhancement;
import org.zzq.forgingEnhancement.manager.BaseAttributeManager;
import org.zzq.forgingEnhancement.manager.ConfigManager;

import java.util.logging.Logger;

public class PluginContext {
    private final Logger logger;
    private final ForgingEnhancement plugin;
    private final ConfigManager configManager;
    private final BaseAttributeManager baseAttributeManager;

    public PluginContext(ForgingEnhancement plugin) {
        this.logger = plugin.getLogger();
        this.plugin = plugin;
        this.configManager = plugin.getFileManager().getConfigManager();
        this.baseAttributeManager = plugin.getFileManager().getBaseAttributeManager();
    }

    // 便捷方法
    public NamespacedKey createKey(String attributeName) {
        return new NamespacedKey(plugin,
                attributeName + System.currentTimeMillis());
    }

    // Getter方法
    public Logger getLogger() { return logger; }
    public ForgingEnhancement getPlugin() { return plugin; }
    public ConfigManager getConfigManager() { return configManager; }
    public BaseAttributeManager getBaseAttributeManager() { return baseAttributeManager; }
}
