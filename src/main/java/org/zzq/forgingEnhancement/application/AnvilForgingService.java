package org.zzq.forgingEnhancement.application;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.zzq.forgingEnhancement.domain.aggregateroot.SelectAttribute;
import org.zzq.forgingEnhancement.domain.entity.EnhancementResult;
import org.zzq.forgingEnhancement.domain.entity.ForgingAttribute;
import org.zzq.forgingEnhancement.domain.services.Recast;
import org.zzq.forgingEnhancement.domain.valueobject.ForgingAttributePoolConfig;
import org.zzq.forgingEnhancement.domain.valueobject.ForgingStone;
import org.zzq.forgingEnhancement.infrastructure.minecraft.ForgingDataRepository;
import org.zzq.forgingEnhancement.infrastructure.minecraft.services.MinecraftAttributeApplier;
import org.zzq.forgingEnhancement.infrastructure.minecraft.services.MinecraftItemService;
import org.zzq.forgingEnhancement.utils.RandomUtil;
import org.zzq.forgingEnhancement.utils.SoundUtil;

import java.util.List;

public class AnvilForgingService {
    private ForgingAttributePoolConfig forgingAttributePoolConfig;
    private MinecraftItemService minecraftItemService;
    private ForgingDataRepository forgingDataRepository;
    private SelectAttribute selectAttribute;
    private MinecraftAttributeApplier minecraftAttributeApplier;
    private Recast recast;

    private List<ForgingAttribute> engravedAttributes;

    public AnvilForgingService(ForgingAttributePoolConfig forgingAttributePoolConfig, MinecraftItemService minecraftItemService, ForgingDataRepository forgingDataRepository, SelectAttribute selectAttribute, MinecraftAttributeApplier minecraftAttributeApplier, Recast recast) {
        this.forgingAttributePoolConfig = forgingAttributePoolConfig;
        this.minecraftItemService = minecraftItemService;
        this.forgingDataRepository = forgingDataRepository;
        this.selectAttribute = selectAttribute;
        this.minecraftAttributeApplier = minecraftAttributeApplier;
        this.recast = recast;
    }

    public ItemStack forgeItem(Player player, ItemStack resultItem, ItemStack forgingStone){
        ItemMeta resultItemMeta = resultItem.getItemMeta();
        String equipment = resultItem.getType().toString();
        if(!forgingAttributePoolConfig.isForgeableEquipment(equipment)) return null;
        int baseLevel = minecraftItemService.getStoneLevel(forgingStone);
        //1.判断是否是重铸
        if(minecraftItemService.isForged(resultItemMeta)){
            engravedAttributes = forgingDataRepository.removeUnengravedForgingData(resultItemMeta);
            minecraftAttributeApplier.removeForgingAttributes(resultItemMeta);
        }
        //2.强化
        int finalItemLevel = RandomUtil.normalDistribution(baseLevel, ForgingStone.values().length - 1);
        List<ForgingAttribute> attributes = selectAttribute.selectAttributes(finalItemLevel, equipment);
        EnhancementResult enhancementResult = new EnhancementResult(finalItemLevel, attributes);
        //3.判断是否有基础属性
        if (!forgingDataRepository.hasBaseAttributeData(resultItemMeta)) {
            minecraftAttributeApplier.applyBaseAttributes(resultItemMeta, resultItem.getType());
            forgingDataRepository.addBaseAttributeData(resultItemMeta);
        }

        if(engravedAttributes != null && !engravedAttributes.isEmpty()){
            //3.3 移除新增铭刻属性
            recast.removeHasEngravedAttributes(enhancementResult, engravedAttributes);
            //3.6 添加铭刻属性
            recast.addEngravedAttributes(enhancementResult, engravedAttributes);
        }
        //4.添加锻造属性
        minecraftAttributeApplier.applyExtraAttributes(resultItemMeta, resultItem.getType(), enhancementResult.getAttributeList());
        //5.存储锻造数据
        forgingDataRepository.addForgingData(resultItemMeta, enhancementResult);
        //6.更新显示
        minecraftItemService.addForgingLore(resultItemMeta, finalItemLevel, enhancementResult.getAttributeList());

        //7.应用强化
        resultItem.setItemMeta(resultItemMeta);
        //8.播放音效
        SoundUtil.playForgingSound(player);
        //8.返回强化后的物品
        return resultItem;
    }
}
