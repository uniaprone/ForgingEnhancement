package org.zzq.forgingEnhancement.services;

import com.google.gson.Gson;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.slf4j.LoggerFactory;
import org.zzq.forgingEnhancement.managers.ConfigManager;
import org.zzq.forgingEnhancement.infrastructure.manager.KeyManager;
import org.zzq.forgingEnhancement.managers.StoneManager;
import org.zzq.forgingEnhancement.models.EnhancementResult;
import org.zzq.forgingEnhancement.models.ForgingAttribute;
import org.zzq.forgingEnhancement.utils.ColorUtil;
import org.zzq.forgingEnhancement.utils.RegxUtil;
import org.zzq.forgingEnhancement.utils.SoundUtil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

public class ForgingService {
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(ForgingService.class);
    private AttributeService attributeService;
    private EnhancementService enhancementService;
    private ConfigManager configManager;
    private StoneManager stoneManager;
    private KeyManager keyManager;
    private Logger logger;
    private SoundUtil soundUtil;
    private List<ForgingAttribute> engravedAttributes;
    private final Gson gson = new Gson();
    public ForgingService(AttributeService attributeService,
                          EnhancementService enhancementService,
                          ConfigManager configManager,
                          StoneManager stoneManager,
                          KeyManager keyManager,
                          Logger logger,
                          SoundUtil soundUtil
    ){
        this.attributeService = attributeService;
        this.enhancementService = enhancementService;
        this.configManager = configManager;
        this.stoneManager = stoneManager;
        this.keyManager = keyManager;
        this.logger = logger;
        this.soundUtil = soundUtil;
    }
    public ItemStack enhanceItem(Player player, ItemStack resultItem, ItemStack forgingStone){
        ItemMeta resultItemMeta = resultItem.getItemMeta();
        String firstItemType = configManager.getEquipmentType(resultItem.getType());
        if(!configManager.isEnhanceableEquipment(firstItemType)) return null;

        int baseLevel = stoneManager.getStoneQualityLevel(forgingStone);
        //1.判断是否是重铸
        if(resultItemMeta.getPersistentDataContainer().get(keyManager.getEnhancementKey(), PersistentDataType.STRING) != null){
            List<ForgingAttribute> forgingAttributes = new ArrayList<>();
            String enhanceResultString = resultItemMeta.getPersistentDataContainer().get(keyManager.getEnhancementKey(), PersistentDataType.STRING);
            EnhancementResult enhancementResult = gson.fromJson(enhanceResultString, EnhancementResult.class);
            for(ForgingAttribute forgingAttribute: enhancementResult.getAttributeList()){
                if(forgingAttribute.isEngraved()){
                    forgingAttributes.add(forgingAttribute);
                }
            }
            resultItemMeta.getPersistentDataContainer().remove(keyManager.getEnhancementKey());
            engravedAttributes = forgingAttributes;
            attributeService.removeForgingAttributes(resultItemMeta);
        }
        //2.强化
        EnhancementResult enhancementResult = enhancementService.enhance(resultItem, baseLevel);
        //3.判断是否有基础属性
        if (resultItemMeta.getPersistentDataContainer().get(keyManager.getBaseAttributeKey(), PersistentDataType.BYTE) == null) {
            attributeService.applyBaseAttributes(resultItemMeta, resultItem.getType());
            resultItemMeta.getPersistentDataContainer().set(keyManager.getBaseAttributeKey(), PersistentDataType.BYTE, (byte) 1);
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
        String AttributesString = gson.toJson(enhancementResult);
        resultItemMeta.getPersistentDataContainer().set(keyManager.getEnhancementKey(),  PersistentDataType.STRING, AttributesString);
        //6.更新显示
        updateItemDisplay(resultItemMeta, resultItem.getItemMeta(), enhancementResult.getLevel(), enhancementResult.getAttributeList());

        //7.应用强化
        resultItem.setItemMeta(resultItemMeta);
        //8.播放音效
        soundUtil.playForgingSound(player);
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

    private void updateItemDisplay(ItemMeta newMeta, ItemMeta originalMeta, int itemQualityLevel, List<ForgingAttribute> enhancements) {
        // 获取原有Lore
        List<Component> originalLore = originalMeta.hasLore() ? originalMeta.lore() : new ArrayList<>();
        if (originalLore == null) originalLore = new ArrayList<>();

        // 移除之前由本插件添加的强化信息（如果有的话）
        removeOldForgingLore(originalLore);

        List<Component> newLore = new ArrayList<>();

        // 添加品质信息
        NamedTextColor qualityColor = ColorUtil.getColorByLevel(itemQualityLevel);
        Component component = Component.text("品质: " + "『" + getQualityDisplayName(itemQualityLevel) + "』").color(qualityColor).decoration(TextDecoration.ITALIC,  TextDecoration.State.FALSE);
        newLore.add(component);

        // 添加词条信息
        for (ForgingAttribute attr : enhancements) {
            ConfigManager.AttributeConfig config = configManager.getAttributeConfig(attr.getName());
            if (config != null) {
                NamedTextColor attrColor = ColorUtil.getColorByLevel(attr.getLevel());
                String valueDisplay = formatAttributeValue(config, attr.getValue());
                if(attr.getValue() > 0){
                    Component attrComponent = Component.text( config.name + ": +" + valueDisplay + " 『" + getQualityDisplayName(attr.getLevel()) + "』").color(attrColor).decoration(TextDecoration.ITALIC,  TextDecoration.State.FALSE);
                    newLore.add(attrComponent);
                }else{
                    Component attrComponent = Component.text(config.name + ": " + valueDisplay + " 『" + getQualityDisplayName(attr.getLevel()) + "』").color(attrColor).decoration(TextDecoration.ITALIC,  TextDecoration.State.FALSE);
                    newLore.add(attrComponent);
                }
            }
        }
        newLore.addAll(originalLore);
        newMeta.lore(newLore);
    }

    private void removeOldForgingLore(List<Component> Lore){
        Iterator<Component> iterator = Lore.iterator();
        while (iterator.hasNext()) {
            TextComponent line = (TextComponent) iterator.next();
            if(RegxUtil.loraDetection(line.content())){
                iterator.remove();
            }
        }
    }

    private String formatAttributeValue(ConfigManager.AttributeConfig config, double value) {
        if ("ADD_NUMBER".equals(config.operation)) {
            return String.format("%.1f", value);
        } else {
            return String.format("%.1f%%", value * 100);
        }
    }

    private String getQualityDisplayName(int quality) {
        switch (quality) {
            case 0: return "破损";
            case 1: return "普通";
            case 2: return "优秀";
            case 3: return "史诗";
            case 4: return "传说";
            case 5: return "神话";
            default: return "普通";
        }
    }
}
