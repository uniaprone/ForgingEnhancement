package org.zzq.forgingEnhancement.infrastructure.minecraft.services;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.zzq.forgingEnhancement.domain.entity.ForgingAttribute;
import org.zzq.forgingEnhancement.domain.valueobject.ForgingAttributeConfig;
import org.zzq.forgingEnhancement.domain.valueobject.ForgingAttributeValue;
import org.zzq.forgingEnhancement.infrastructure.manager.KeyManager;
import org.zzq.forgingEnhancement.managers.ConfigManager;
import org.zzq.forgingEnhancement.utils.ColorUtil;
import org.zzq.forgingEnhancement.utils.RegxUtil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MinecraftItemService {
    private ForgingAttributeConfig forgingAttributeConfig;
    private KeyManager keyManager;

    public MinecraftItemService(ForgingAttributeConfig forgingAttributeConfig, KeyManager keyManager) {
        this.forgingAttributeConfig = forgingAttributeConfig;
        this.keyManager = keyManager;
    }

    public List<Component> getLore(ItemMeta itemMeta){
        if(!itemMeta.hasLore()) return new ArrayList<>();
        return itemMeta.lore();
    }

    public void clearForgingLore(List<Component> lores){
        if(lores == null || lores.isEmpty()) return;
        Iterator<Component> iterator = lores.iterator();
        while (iterator.hasNext()) {
            TextComponent line = (TextComponent) iterator.next();
            if(RegxUtil.loraDetection(line.content())){
                iterator.remove();
            }
        }
    }

    public void addPreForgingLore(ItemStack itemStack, int level){
        ItemMeta itemMeta = itemStack.getItemMeta();
        List<Component> originLore = getLore(itemMeta);
        clearForgingLore(originLore);
        List<Component> newLore = new ArrayList<>();
        NamedTextColor qualityColor = ColorUtil.getColorByLevel(level);
        Component unKnowComponent = Component.text("品质: " + "???").color(qualityColor);
        newLore.add(unKnowComponent);
        newLore.addAll(originLore);
        itemMeta.lore(newLore);
        itemStack.setItemMeta(itemMeta);
    }

    public void addForgingLore(ItemMeta itemMeta, int level, List<ForgingAttribute> forgingAttributes){
        List<Component> originLore = getLore(itemMeta);
        clearForgingLore(originLore);
        List<Component> newLore = new ArrayList<>();

        // 添加品质信息
        NamedTextColor qualityColor = ColorUtil.getColorByLevel(level);
        Component component = Component.text("品质: " + "『" + getQualityDisplayName(level) + "』").color(qualityColor).decoration(TextDecoration.ITALIC,  TextDecoration.State.FALSE);
        newLore.add(component);

        // 添加词条信息
        for (ForgingAttribute attr : forgingAttributes) {
            ForgingAttributeValue forgingAttributeValue = forgingAttributeConfig.getForgingAttributeValue(attr.getName());
            if (forgingAttributeValue != null) {
                NamedTextColor attrColor = ColorUtil.getColorByLevel(attr.getLevel());
                String valueDisplay = formatAttributeValue(forgingAttributeValue, attr.getValue());
                if(attr.getValue() > 0){
                    Component attrComponent = Component.text( forgingAttributeValue.getName() + ": +" + valueDisplay + " 『" + getQualityDisplayName(attr.getLevel()) + "』").color(attrColor).decoration(TextDecoration.ITALIC,  TextDecoration.State.FALSE);
                    newLore.add(attrComponent);
                }else{
                    Component attrComponent = Component.text(forgingAttributeValue.getName() + ": " + valueDisplay + " 『" + getQualityDisplayName(attr.getLevel()) + "』").color(attrColor).decoration(TextDecoration.ITALIC,  TextDecoration.State.FALSE);
                    newLore.add(attrComponent);
                }
            }
        }
        newLore.addAll(originLore);
        itemMeta.lore(newLore);
    }

    public boolean isForged(ItemMeta itemMeta){
        if(itemMeta == null) return false;
        return itemMeta.getPersistentDataContainer().has(keyManager.getEnhancementKey(), PersistentDataType.STRING);
    }

    public boolean isForgingStone(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;
        ItemMeta meta = item.getItemMeta();
        Byte isStone = meta.getPersistentDataContainer().get(keyManager.getForgingStoneKey(), PersistentDataType.BYTE);
        return isStone != null && isStone == 1;
    }

    public int getStoneLevel(ItemStack itemStack){
        int stoneLevel = -1;
        if (!isForgingStone(itemStack)) return stoneLevel;
        ItemMeta meta = itemStack.getItemMeta();
        if(meta.getPersistentDataContainer().has(keyManager.getStoneQualityKey(), PersistentDataType.INTEGER)){
            stoneLevel = meta.getPersistentDataContainer().get(keyManager.getStoneQualityKey(), PersistentDataType.INTEGER);
        }
        return stoneLevel;
    }

    private String formatAttributeValue(ForgingAttributeValue forgingAttributeValue, double value) {
        if ("ADD_NUMBER".equals(forgingAttributeValue.getOperation())) {
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
