package org.zzq.forgingEnhancement.services;

import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.zzq.forgingEnhancement.managers.ConfigManager;
import org.zzq.forgingEnhancement.managers.KeyManager;
import org.zzq.forgingEnhancement.managers.StoneManager;
import org.zzq.forgingEnhancement.models.EnhancementResult;

public class ForgingService {
    private AttributeApplicationService attributeApplicationService;
    private EnhancementService enhancementService;
    private ItemDisplayService itemDisplayService;
    private NBTService nbtService;
    private ConfigManager configManager;
    private StoneManager stoneManager;
    private KeyManager keyManager;
    public ForgingService(AttributeApplicationService attributeApplicationService,
                          EnhancementService enhancementService,
                          ItemDisplayService itemDisplayService,
                          NBTService nbtService,
                          ConfigManager configManager,
                          StoneManager stoneManager,
                          KeyManager keyManager){
        this.attributeApplicationService = attributeApplicationService;
        this.enhancementService = enhancementService;
        this.itemDisplayService = itemDisplayService;
        this.nbtService = nbtService;
        this.configManager = configManager;
        this.stoneManager = stoneManager;
        this.keyManager = keyManager;
    }
    public ItemStack enhanceItem(ItemStack firstItem, ItemStack secondItem, ItemStack resultItem){
        ItemMeta resultItemMeta = resultItem.getItemMeta();
        if (firstItem == null || secondItem == null || !stoneManager.isForgingStone(secondItem)) {
            return firstItem;
        }
        if(configManager.isEnhanceableEquipment(firstItem.getType())) {
            String baseQuality = stoneManager.getStoneQuality(secondItem);
            int baseLevel = configManager.getLevelByQuality(baseQuality);
            EnhancementResult enhancementResult = enhancementService.enhance(firstItem, baseLevel);
            if (!nbtService.hasBaseAttributeApplied(resultItemMeta)) {
                attributeApplicationService.applyBaseAttributes(resultItemMeta, resultItem.getType());
                nbtService.markBaseAttributeApplied(resultItemMeta);
            }
            attributeApplicationService.applyExtraAttributes(resultItemMeta, resultItem.getType(), enhancementResult.getAttributeList());
            nbtService.storeForgingNBT(resultItemMeta, keyManager.getEnhancementKey(), enhancementResult);
            itemDisplayService.updateItemDisplay(resultItemMeta, resultItem.getItemMeta(), baseQuality, enhancementResult.getAttributeList());
            resultItem.setItemMeta(resultItemMeta);
            return resultItem;
        }
        return firstItem;
    }

}
