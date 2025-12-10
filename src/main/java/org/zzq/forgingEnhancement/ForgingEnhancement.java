package org.zzq.forgingEnhancement;

import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.zzq.forgingEnhancement.commands.ForgingEnhancementCommand;
import org.zzq.forgingEnhancement.listeners.*;
import org.zzq.forgingEnhancement.managers.EngraveStoneManager;
import org.zzq.forgingEnhancement.managers.FileManager;
import org.zzq.forgingEnhancement.infrastructure.manager.KeyManager;
import org.zzq.forgingEnhancement.managers.StoneManager;
import org.zzq.forgingEnhancement.services.*;
import org.zzq.forgingEnhancement.services.guiService.GUIDecorateService;
import org.zzq.forgingEnhancement.services.guiService.ItemInfoGUI.ItemInfoGUIService;
import org.zzq.forgingEnhancement.services.guiService.attributeBindingGUI.AttributeBindingService;
import org.zzq.forgingEnhancement.utils.RandomUtil;
import org.zzq.forgingEnhancement.utils.RegxUtil;
import org.zzq.forgingEnhancement.utils.SoundUtil;

public class ForgingEnhancement extends JavaPlugin implements Listener {

    private FileManager fileManager;
    private KeyManager keyManager;
    private StoneManager stoneManager;
    private ForgingService forgingService;
    private ItemInfoGUIService itemInfoGUIService;
    private AttributeBindingService attributeBindingService;
    private EngraveStoneManager engraveStoneManager;
    private RandomUtil randomUtil;

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
        randomUtil = new RandomUtil();
        SoundUtil soundUtil = new SoundUtil();
        engraveStoneManager = new EngraveStoneManager(keyManager);
        GUIDecorateService guiDecorateService = new GUIDecorateService(this.getLogger(), fileManager.getConfigManager(), keyManager);
        itemInfoGUIService =  new ItemInfoGUIService(this.getLogger(), keyManager, guiDecorateService);
        AttributeService attributeService = new AttributeService(getLogger(), fileManager.getConfigManager(), fileManager.getBaseAttributeManager(), keyManager);
        EnhancementService enhancementService = new EnhancementService(fileManager.getConfigManager(), stoneManager,this.getLogger());
        attributeBindingService = new AttributeBindingService(this.getLogger(), keyManager, guiDecorateService, engraveStoneManager, soundUtil);
        this.forgingService = new ForgingService(
                attributeService,
                enhancementService,
                fileManager.getConfigManager(),
                stoneManager,
                keyManager,
                this.getLogger(),
                soundUtil
        );

    }

    private void registerListeners(){
        getServer().getPluginManager().registerEvents(this, this);
        getServer().getPluginManager().registerEvents(new CraftingForgingListener(fileManager.getConfigManager(), stoneManager,this.getLogger(), fileManager.getplayerSettingManager()), this);
        getServer().getPluginManager().registerEvents(new AnvilForgingListener(fileManager.getConfigManager(), stoneManager), this);
        getServer().getPluginManager().registerEvents(new ForgingClickListener(forgingService, itemInfoGUIService,fileManager.getplayerSettingManager()), this);
        getServer().getPluginManager().registerEvents(new ItemInfoGUIListener(this.getLogger(), itemInfoGUIService, attributeBindingService), this);
        getServer().getPluginManager().registerEvents(new AttributeBindingGUIListener(this.getLogger(), attributeBindingService, itemInfoGUIService), this);
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

    public EngraveStoneManager getEngraveStoneManager() {
        return engraveStoneManager;
    }
}