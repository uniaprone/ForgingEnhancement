package org.zzq.forgingEnhancement.services;

import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.slf4j.LoggerFactory;
import org.zzq.forgingEnhancement.managers.ConfigManager;
import org.zzq.forgingEnhancement.managers.KeyManager;
import org.zzq.forgingEnhancement.managers.StoneManager;
import org.zzq.forgingEnhancement.models.EnhancementResult;

import java.util.Map;
import java.util.Objects;
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
    public ItemStack enhanceItem(ItemStack firstItem, ItemStack secondItem, ItemStack resultItem){
        ItemMeta resultItemMeta = resultItem.getItemMeta();
//        if(resultItemMeta != null && resultItemMeta.hasAttributeModifiers()){
//            for (Map.Entry<org.bukkit.attribute.Attribute, AttributeModifier> entry : Objects.requireNonNull(resultItemMeta.getAttributeModifiers()).entries()) {
//                logger.info(entry.getKey() + ":" + entry.getValue().getAmount());
//                logger.info(entry.getValue().getName() +"^^^"+ entry.getValue().getKey() +"^^^"+ entry.getValue());
//                logger.info(entry.getValue() + "");
//                logger.info(entry + "");
//            }
//        }
        if (firstItem == null || secondItem == null || !stoneManager.isForgingStone(secondItem)) {
            return firstItem;
        }

        if(configManager.isEnhanceableEquipment(firstItem.getType())) {
            String baseQuality = stoneManager.getStoneQuality(secondItem);
            int baseLevel = configManager.getLevelByQuality(baseQuality);
            if(nbtService.hasForgingNBT(resultItemMeta)){
                nbtService.removeForgingNBT(resultItemMeta);
                attributeService.removeForgingAttributes(resultItemMeta);
            }
            EnhancementResult enhancementResult = enhancementService.enhance(firstItem, baseLevel);
            if (!nbtService.hasBaseAttributeApplied(resultItemMeta)) {
                attributeService.applyBaseAttributes(resultItemMeta, resultItem.getType());
                nbtService.markBaseAttributeApplied(resultItemMeta);
            }
            attributeService.applyExtraAttributes(resultItemMeta, resultItem.getType(), enhancementResult.getAttributeList());
            nbtService.storeForgingNBT(resultItemMeta, keyManager.getEnhancementKey(), enhancementResult);
            itemDisplayService.updateItemDisplay(resultItemMeta, resultItem.getItemMeta(), enhancementResult.getLevel(), enhancementResult.getAttributeList());
            resultItem.setItemMeta(resultItemMeta);
            return resultItem;
        }
        return firstItem;
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
