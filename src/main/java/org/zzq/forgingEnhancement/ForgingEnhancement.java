package org.zzq.forgingEnhancement;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.zzq.forgingEnhancement.commands.ForgingEnhancementCommand;
import org.zzq.forgingEnhancement.listeners.AnvilClickListener;
import org.zzq.forgingEnhancement.listeners.AnvilForgingListener;
import org.zzq.forgingEnhancement.managers.FileManager;
import org.zzq.forgingEnhancement.managers.KeyManager;
import org.zzq.forgingEnhancement.managers.StoneManager;
import org.zzq.forgingEnhancement.services.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class ForgingEnhancement extends JavaPlugin implements Listener {

    private FileManager fileManager;
    private KeyManager keyManager;
    private StoneManager stoneManager;
    private ForgingService forgingService;

    @Override
    public void onEnable() {
        this.fileManager = new FileManager(this);
        this.keyManager = new KeyManager(this);
        this.stoneManager = new StoneManager(keyManager);

        initializeService();
        registerListeners();
        // 创建命令执行器实例
        ForgingEnhancementCommand commandExecutor = new ForgingEnhancementCommand(this);

        // 注册命令执行器和Tab补全器
        getCommand("forgingenhancement").setExecutor(commandExecutor);
        getCommand("forgingenhancement").setTabCompleter(commandExecutor);

        getLogger().info("锻造增强插件已启用!");
    }
    private void initializeService(){
        AttributeApplicationService attributeApplicationService = new AttributeApplicationService(getLogger(), fileManager.getConfigManager(), fileManager.getBaseAttributeManager(), keyManager);
        EnhancementService enhancementService = new EnhancementService(fileManager.getConfigManager());
        ItemDisplayService itemDisplayService = new ItemDisplayService(fileManager.getConfigManager());
        NBTService nbtService = new NBTService(keyManager);
        this.forgingService = new ForgingService(
                attributeApplicationService,
                enhancementService,
                itemDisplayService,
                nbtService,
                fileManager.getConfigManager(),
                stoneManager,
                keyManager);
    }

    private void registerListeners(){
        getServer().getPluginManager().registerEvents(this, this);
        getServer().getPluginManager().registerEvents(new AnvilForgingListener(fileManager.getConfigManager(), stoneManager), this);
        getServer().getPluginManager().registerEvents(new AnvilClickListener(forgingService), this);
    }

    @Override
    public void onDisable() {
        getLogger().info("锻造增强插件已禁用!");
    }

    public FileManager getFileManager() {
        return fileManager;
    }

    public StoneManager getStoneManager() {
        return stoneManager;
    }
}