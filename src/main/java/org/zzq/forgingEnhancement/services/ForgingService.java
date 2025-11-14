package org.zzq.forgingEnhancement.services;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.slf4j.LoggerFactory;
import org.zzq.forgingEnhancement.managers.ConfigManager;
import org.zzq.forgingEnhancement.managers.KeyManager;
import org.zzq.forgingEnhancement.managers.StoneManager;
import org.zzq.forgingEnhancement.models.EnhancementResult;
import org.zzq.forgingEnhancement.models.ForgingAttribute;

import java.util.List;
import java.util.logging.Logger;

public class ForgingService {
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(ForgingService.class);
    private AttributeService attributeService;
    private EnhancementService enhancementService;
    private ItemDisplayService itemDisplayService;
    private NBTService nbtService;
    private ConfigManager configManager;
    private StoneManager stoneManager;
    private KeyManager keyManager;
    private Logger logger;
    private List<ForgingAttribute> engravedAttributes;
    public ForgingService(AttributeService attributeService,
                          EnhancementService enhancementService,
                          ItemDisplayService itemDisplayService,
                          NBTService nbtService,
                          ConfigManager configManager,
                          StoneManager stoneManager,
                          KeyManager keyManager,
                          Logger logger
    ){
        this.attributeService = attributeService;
        this.enhancementService = enhancementService;
        this.itemDisplayService = itemDisplayService;
        this.nbtService = nbtService;
        this.configManager = configManager;
        this.stoneManager = stoneManager;
        this.keyManager = keyManager;
        this.logger = logger;
    }
    public ItemStack enhanceItem(ItemStack resultItem, ItemStack forgingStone){
        ItemMeta resultItemMeta = resultItem.getItemMeta();

        int baseLevel = stoneManager.getStoneQualityLevel(forgingStone);
        //1.判断是否是重铸
        if(nbtService.hasForgingNBT(resultItemMeta)){
            engravedAttributes = nbtService.removeForgingNBTReturnEngraved(resultItemMeta);
            attributeService.removeForgingAttributes(resultItemMeta);
        }
        //2.强化
        EnhancementResult enhancementResult = enhancementService.enhance(resultItem, baseLevel);
        //3.判断是否有基础属性
        if (!nbtService.hasBaseAttributeApplied(resultItemMeta)) {
            attributeService.applyBaseAttributes(resultItemMeta, resultItem.getType());
            nbtService.markBaseAttributeApplied(resultItemMeta);
        }
        if(engravedAttributes != null && !engravedAttributes.isEmpty()){
            //3.3 移除新增铭刻属性
            attributeService.removeHasEngravedResult(enhancementResult, engravedAttributes);
            //3.6 添加铭刻属性
            attributeService.addEngravedResult(enhancementResult, engravedAttributes);
        }
        //4.添加锻造属性
        attributeService.applyExtraAttributes(resultItemMeta, resultItem.getType(), enhancementResult.getAttributeList());
        //5.存储锻造数据
        nbtService.storeForgingNBT(resultItemMeta, enhancementResult);
        //6.更新显示
        itemDisplayService.updateItemDisplay(resultItemMeta, resultItem.getItemMeta(), enhancementResult.getLevel(), enhancementResult.getAttributeList());
        //7.应用强化
        resultItem.setItemMeta(resultItemMeta);
        //8.返回强化后的物品
        return resultItem;
    }

    public boolean isForgingStone(ItemStack item) {
        return stoneManager.isForgingStone(item);
    }

    public ItemStack createForgingStone(String quality, int amount) {
        return stoneManager.createForgingStone(quality, amount);
    }

    public Logger getLogger(){
        return logger;
    }

}
