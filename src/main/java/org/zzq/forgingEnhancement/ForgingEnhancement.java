package org.zzq.forgingEnhancement;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.attribute.AttributeModifier.Operation;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.zzq.forgingEnhancement.Listener.AnvilForgingListener;
import org.zzq.forgingEnhancement.manager.BaseAttributeManager;
import org.zzq.forgingEnhancement.manager.ConfigManager;
import org.zzq.forgingEnhancement.manager.FileManager;

import java.util.*;

public class ForgingEnhancement extends JavaPlugin implements Listener {

    private ConfigManager configManager;
    private BaseAttributeManager baseAttributeManager;
    private FileManager fileManager;
    private Random random;
    // 锻造石与品质的映射


    @Override
    public void onEnable() {
        this.fileManager = FileManager.getInstance(this);
        this.configManager = fileManager.getConfigManager();
        this.baseAttributeManager = fileManager.getBaseAttributeManager();

        getServer().getPluginManager().registerEvents(this, this);
        getServer().getPluginManager().registerEvents(new AnvilForgingListener(this), this);
        getCommand("forgingenhancement").setExecutor(new ForgingEnhancementCommand(this));

        getLogger().info("锻造增强插件已启用!");
    }


    @Override
    public void onDisable() {
        getLogger().info("锻造增强插件已禁用!");
    }
    public FileManager getFileManager() {
        return fileManager;
    }
}