package org.zzq.forgingEnhancement.application.forging;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.zzq.forgingEnhancement.domain.aggregateroot.PlayerSettingConfig;
import org.zzq.forgingEnhancement.domain.aggregateroot.SelectAttribute;
import org.zzq.forgingEnhancement.domain.entity.EnhancementResult;
import org.zzq.forgingEnhancement.domain.entity.ForgingAttribute;
import org.zzq.forgingEnhancement.domain.aggregateroot.ForgingAttributePoolConfig;
import org.zzq.forgingEnhancement.infrastructure.minecraft.ForgingDataRepository;
import org.zzq.forgingEnhancement.infrastructure.minecraft.services.MinecraftAttributeApplier;
import org.zzq.forgingEnhancement.infrastructure.minecraft.services.MinecraftItemService;
import org.zzq.forgingEnhancement.utils.SoundUtil;

import java.util.List;

public class WorkbenchForgingService {
    private ForgingAttributePoolConfig forgingAttributePoolConfig;
    private MinecraftItemService minecraftItemService;
    private ForgingDataRepository forgingDataRepository;
    private SelectAttribute selectAttribute;
    private MinecraftAttributeApplier minecraftAttributeApplier;
    private PlayerSettingConfig playerSettingConfig;

    public WorkbenchForgingService(ForgingAttributePoolConfig forgingAttributePoolConfig, MinecraftItemService minecraftItemService, ForgingDataRepository forgingDataRepository, SelectAttribute selectAttribute, MinecraftAttributeApplier minecraftAttributeApplier, PlayerSettingConfig playerSettingConfig) {
        this.forgingAttributePoolConfig = forgingAttributePoolConfig;
        this.minecraftItemService = minecraftItemService;
        this.forgingDataRepository = forgingDataRepository;
        this.selectAttribute = selectAttribute;
        this.minecraftAttributeApplier = minecraftAttributeApplier;
        this.playerSettingConfig = playerSettingConfig;
    }

    public ItemStack forgeItem(Player player, ItemStack resultItem){
        if(!playerSettingConfig.isWorkbenchForgingEnable(String.valueOf(player.getUniqueId()))) return null;
        ItemMeta resultItemMeta = resultItem.getItemMeta();
        String equipment = resultItem.getType().toString();
        if(!forgingAttributePoolConfig.isForgeableEquipment(equipment)) return null;
        int baseLevel = 1;
        //2.强化
//        int finalItemLevel = RandomUtil.normalDistribution(baseLevel, ForgingStone.values().length - 1);
        List<ForgingAttribute> attributes = selectAttribute.selectAttributes(baseLevel, equipment);
        EnhancementResult enhancementResult = new EnhancementResult(baseLevel, attributes);
        //3.应用基础属性
        minecraftAttributeApplier.applyBaseAttributes(resultItemMeta, resultItem.getType());
        //4.添加锻造属性
        minecraftAttributeApplier.applyExtraAttributes(resultItemMeta, resultItem.getType(), enhancementResult.getAttributeList());
        //5.存储锻造数据
        forgingDataRepository.addForgingData(resultItemMeta, enhancementResult);
        //6.更新显示
        minecraftItemService.addForgingLore(resultItemMeta, baseLevel, enhancementResult.getAttributeList());
        //7.应用强化
        resultItem.setItemMeta(resultItemMeta);
        //8.播放音效
        SoundUtil.playForgingSound(player);
        //8.返回强化后的物品
        return resultItem;
    }
}
