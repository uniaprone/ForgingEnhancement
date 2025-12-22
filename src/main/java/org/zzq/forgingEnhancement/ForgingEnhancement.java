package org.zzq.forgingEnhancement;

import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.zzq.forgingEnhancement.application.command.GiveService;
import org.zzq.forgingEnhancement.application.command.ReloadService;
import org.zzq.forgingEnhancement.application.command.TogglePlayerSettingService;
import org.zzq.forgingEnhancement.application.forging.AnvilForgingService;
import org.zzq.forgingEnhancement.application.forging.AnvilPreForgingService;
import org.zzq.forgingEnhancement.application.forging.WorkbenchForgingService;
import org.zzq.forgingEnhancement.application.forging.WorkbenchPreForgingService;
import org.zzq.forgingEnhancement.commands.ForgingEnhancementCommand;
import org.zzq.forgingEnhancement.domain.aggregateroot.SelectAttribute;
import org.zzq.forgingEnhancement.domain.entity.ForgingConfig;
import org.zzq.forgingEnhancement.domain.aggregateroot.PlayerSettingConfig;
import org.zzq.forgingEnhancement.domain.services.Recast;
import org.zzq.forgingEnhancement.domain.aggregateroot.BaseAttributeConfig;
import org.zzq.forgingEnhancement.domain.aggregateroot.ForgingAttributeConfig;
import org.zzq.forgingEnhancement.domain.aggregateroot.ForgingAttributePoolConfig;
import org.zzq.forgingEnhancement.infrastructure.ForgingLogger;
import org.zzq.forgingEnhancement.infrastructure.minecraft.services.ForgingStoneFactory;
import org.zzq.forgingEnhancement.infrastructure.minecraft.ForgingDataRepository;
import org.zzq.forgingEnhancement.infrastructure.minecraft.services.MinecraftAttributeApplier;
import org.zzq.forgingEnhancement.infrastructure.minecraft.services.MinecraftItemService;
import org.zzq.forgingEnhancement.infrastructure.yaml.parser.*;
import org.zzq.forgingEnhancement.infrastructure.yaml.repository.*;
import org.zzq.forgingEnhancement.listeners.*;
import org.zzq.forgingEnhancement.infrastructure.manager.EngraveStoneManager;
import org.zzq.forgingEnhancement.infrastructure.manager.KeyManager;
import org.zzq.forgingEnhancement.application.guiService.GUIDecorateService;
import org.zzq.forgingEnhancement.application.guiService.ItemInfoGUI.ItemInfoGUIService;
import org.zzq.forgingEnhancement.application.guiService.attributeBindingGUI.AttributeBindingService;
import org.zzq.forgingEnhancement.utils.RandomUtil;

public class ForgingEnhancement extends JavaPlugin implements Listener {

    private KeyManager keyManager;
    private AnvilForgingService anvilForgingService;
    private ItemInfoGUIService itemInfoGUIService;
    private AttributeBindingService attributeBindingService;
    private EngraveStoneManager engraveStoneManager;
    private WorkbenchPreForgingService workbenchPreForgingService;
    private WorkbenchForgingService workbenchForgingService;
    private AnvilPreForgingService anvilPreForgingService;
    private ForgingLogger forgingLogger;
    private TogglePlayerSettingService togglePlayerSettingService;
    private GiveService giveService;
    private ForgingStoneFactory forgingStoneFactory;
    private ReloadService reloadService;
    private RandomUtil randomUtil;

    @Override
    public void onEnable() {
        this.keyManager = new KeyManager(this);

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
        YamlBaseAttributeParser yamlBaseAttributeParser = new YamlBaseAttributeParser(this);
        YamlConfigParser yamlConfigParser = new YamlConfigParser(this);
        YamlForgingAttributeParser yamlForgingAttributeParser = new YamlForgingAttributeParser(this);
        YamlForgingAttributePoolParser yamlForgingAttributePoolParser = new YamlForgingAttributePoolParser(this);
        YamlPlayerSettingParser yamlPlayerSettingParser = new YamlPlayerSettingParser(this);

        YamlBaseAttributeRepository yamlBaseAttributeRepository = new YamlBaseAttributeRepository(yamlBaseAttributeParser);
        YamlConfigRepository yamlConfigRepository = new YamlConfigRepository(yamlConfigParser);
        YamlForgingAttributeRepository yamlForgingAttributeRepository = new YamlForgingAttributeRepository(yamlForgingAttributeParser);
        YamlForgingAttributePoolRepository yamlForgingAttributePoolRepository = new YamlForgingAttributePoolRepository(yamlForgingAttributePoolParser);
        YamlPlayerSettingRepository yamlPlayerSettingRepository = new YamlPlayerSettingRepository(yamlPlayerSettingParser);

        BaseAttributeConfig baseAttributeConfig = yamlBaseAttributeRepository.getBaseAttributeConfig();
        ForgingConfig forgingConfig = yamlConfigRepository.getForgingConfig();
        ForgingAttributeConfig forgingAttributeConfig = yamlForgingAttributeRepository.getForgingAttributeConfig();
        ForgingAttributePoolConfig forgingAttributePoolConfig = yamlForgingAttributePoolRepository.getForgingAttributePool();
        PlayerSettingConfig playerSettingConfig = yamlPlayerSettingRepository.getPlayerSettings();

        randomUtil = new RandomUtil();
        engraveStoneManager = new EngraveStoneManager(keyManager);
        ForgingDataRepository forgingDataRepository = new ForgingDataRepository(keyManager);
        forgingLogger = new ForgingLogger(this,forgingConfig, forgingDataRepository);
        forgingStoneFactory = new ForgingStoneFactory(keyManager);
        MinecraftAttributeApplier minecraftAttributeApplier = new MinecraftAttributeApplier(forgingAttributeConfig, baseAttributeConfig, forgingLogger, keyManager);
        MinecraftItemService minecraftItemService = new MinecraftItemService(forgingAttributeConfig, keyManager);


        SelectAttribute selectAttribute = new SelectAttribute(forgingAttributeConfig, forgingAttributePoolConfig);

        GUIDecorateService guiDecorateService = new GUIDecorateService(this.getLogger(), forgingAttributeConfig, keyManager);
        itemInfoGUIService =  new ItemInfoGUIService(this.getLogger(), minecraftItemService, guiDecorateService, forgingDataRepository);
        attributeBindingService = new AttributeBindingService(this.getLogger(), keyManager, guiDecorateService, engraveStoneManager, forgingDataRepository);
        Recast recast = new Recast();
        workbenchPreForgingService = new WorkbenchPreForgingService(playerSettingConfig, forgingAttributePoolConfig, minecraftItemService, forgingLogger);

        anvilPreForgingService = new AnvilPreForgingService(forgingAttributePoolConfig, minecraftItemService);

        workbenchForgingService = new WorkbenchForgingService(
                forgingAttributePoolConfig,
                minecraftItemService,
                forgingDataRepository,
                selectAttribute,
                minecraftAttributeApplier,
                playerSettingConfig
        );

        this.anvilForgingService = new AnvilForgingService(
                forgingAttributePoolConfig,
                minecraftItemService,
                forgingDataRepository,
                selectAttribute,
                minecraftAttributeApplier,
                recast
        );

        togglePlayerSettingService = new TogglePlayerSettingService(yamlPlayerSettingRepository, forgingLogger);

        giveService = new GiveService(forgingStoneFactory, engraveStoneManager);

        reloadService = new ReloadService(
                yamlBaseAttributeRepository,
                yamlConfigRepository,
                yamlForgingAttributeRepository,
                yamlForgingAttributePoolRepository,
                yamlPlayerSettingRepository,
                baseAttributeConfig,
                forgingConfig,
                forgingAttributeConfig,
                forgingAttributePoolConfig,
                playerSettingConfig
        );
    }

    private void registerListeners(){
        PluginManager  pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(this, this);
        pluginManager.registerEvents(new WorkbenchPreForgingListener(workbenchPreForgingService, forgingLogger), this);
        pluginManager.registerEvents(new WorkbenchForgingListener(workbenchForgingService, forgingLogger), this);
        pluginManager.registerEvents(new AnvilPreForgingListener(anvilPreForgingService), this);
        pluginManager.registerEvents(new AnvilForgingListener(anvilForgingService, forgingLogger), this);
        pluginManager.registerEvents(new OpenItemInfoGUIListener(itemInfoGUIService), this);
        pluginManager.registerEvents(new ItemInfoGUIListener(this.getLogger(), itemInfoGUIService, attributeBindingService), this);
        pluginManager.registerEvents(new AttributeBindingGUIListener(this.getLogger(), attributeBindingService, itemInfoGUIService), this);
    }

    @Override
    public void onDisable() {
        getLogger().info("锻造增强插件已禁用!");
    }

    public EngraveStoneManager getEngraveStoneManager() {
        return engraveStoneManager;
    }

    public TogglePlayerSettingService getTogglePlayerSettingService() {
        return togglePlayerSettingService;
    }

    public GiveService getGiveService() {
        return giveService;
    }

    public ReloadService getReloadService() {
        return reloadService;
    }
}