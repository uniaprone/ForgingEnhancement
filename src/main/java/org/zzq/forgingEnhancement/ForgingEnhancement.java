package org.zzq.forgingEnhancement;

import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.zzq.forgingEnhancement.commands.ForgingEnhancementCommand;
import org.zzq.forgingEnhancement.listeners.AnvilClickListener;
import org.zzq.forgingEnhancement.listeners.AnvilForgingListener;
import org.zzq.forgingEnhancement.listeners.CraftingForgingListener;
import org.zzq.forgingEnhancement.managers.FileManager;
import org.zzq.forgingEnhancement.managers.KeyManager;
import org.zzq.forgingEnhancement.managers.StoneManager;
import org.zzq.forgingEnhancement.services.*;

public class ForgingEnhancement extends JavaPlugin implements Listener {

    private FileManager fileManager;
    private KeyManager keyManager;
    private StoneManager stoneManager;
    private ForgingService forgingService;
    private ItemDisplayService itemDisplayService;

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
        RegxService regxService = new RegxService(this.getLogger());

        AttributeService attributeService = new AttributeService(getLogger(), fileManager.getConfigManager(), fileManager.getBaseAttributeManager(), keyManager);
        EnhancementService enhancementService = new EnhancementService(fileManager.getConfigManager(), stoneManager,this.getLogger());
        itemDisplayService = new ItemDisplayService(fileManager.getConfigManager(), regxService);
        NBTService nbtService = new NBTService(keyManager);
        this.forgingService = new ForgingService(
                attributeService,
                enhancementService,
                itemDisplayService,
                nbtService,
                fileManager.getConfigManager(),
                stoneManager,
                keyManager,
                this.getLogger());
    }

    private void registerListeners(){
        getServer().getPluginManager().registerEvents(this, this);
        getServer().getPluginManager().registerEvents(new CraftingForgingListener(fileManager.getConfigManager(), stoneManager, itemDisplayService,this.getLogger()), this);
        getServer().getPluginManager().registerEvents(new AnvilForgingListener(fileManager.getConfigManager(), stoneManager, itemDisplayService), this);
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