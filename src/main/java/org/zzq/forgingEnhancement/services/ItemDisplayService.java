package org.zzq.forgingEnhancement.services;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.zzq.forgingEnhancement.managers.ConfigManager;
import org.zzq.forgingEnhancement.models.EnhancementResult;
import org.zzq.forgingEnhancement.models.ForgingAttribute;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ItemDisplayService {
    private ConfigManager configManager;
    private RegxService regxService;
    public ItemDisplayService(ConfigManager configManager, RegxService regxService){
        this.configManager = configManager;
        this.regxService = regxService;
    }

    public void updateItemDisplay(ItemMeta newMeta, ItemMeta originalMeta, int itemQualityLevel, List<ForgingAttribute> enhancements) {
        // 获取原有Lore
        List<String> originalLore = originalMeta.hasLore() ? originalMeta.getLore() : new ArrayList<>();
        if (originalLore == null) originalLore = new ArrayList<>();

        // 移除之前由本插件添加的强化信息（如果有的话）
        removeOldForgingLore(originalLore);

        List<String> newLore = new ArrayList<>();

        // 添加品质信息
        String qualityColor = getQualityColor(itemQualityLevel);
        newLore.add(qualityColor + "品质: " + "『" + getQualityDisplayName(itemQualityLevel) + "』");

        // 添加词条信息
        for (ForgingAttribute attr : enhancements) {
            ConfigManager.AttributeConfig config = configManager.getAttributeConfig(attr.getName());
            if (config != null) {
                String attrColor = getQualityColor(attr.getLevel());
                String valueDisplay = formatAttributeValue(config, attr.getValue());
                if(attr.getValue() > 0){
                    newLore.add(attrColor + config.name + ": +" + valueDisplay + " 『" + getQualityDisplayName(attr.getLevel()) + "』");
                }else{
                    newLore.add(attrColor + config.name + ": " + valueDisplay + " 『" + getQualityDisplayName(attr.getLevel()) + "』");
                }

            }
        }
        newLore.addAll(originalLore);
        newMeta.setLore(newLore);
    }

    public void guiItemDisplay(ItemMeta itemMeta, ForgingAttribute forgingAttribute) {
        List<String> newLore = new ArrayList<>();
        ConfigManager.AttributeConfig config = configManager.getAttributeConfig(forgingAttribute.getName());
        if (config != null) {
            String attrColor = getQualityColor(forgingAttribute.getLevel());
            String valueDisplay = formatAttributeValue(config, forgingAttribute.getValue());
            itemMeta.displayName(Component.text(attrColor + "属性：" + config.name));
            if(forgingAttribute.getValue() > 0){
                newLore.add(attrColor + "+" + valueDisplay + " 『" + getQualityDisplayName(forgingAttribute.getLevel()) + "』");
            }else{
                newLore.add(attrColor + valueDisplay + " 『" + getQualityDisplayName(forgingAttribute.getLevel()) + "』");
            }
            if(forgingAttribute.isEngraved()){
                newLore.add("§7已铭刻");
            }
        }
        itemMeta.setLore(newLore);
    }

    public void addEngravedLora(ItemMeta itemMeta){
        List<Component> originLore = itemMeta.lore();
        if (originLore != null) {
            originLore.add(originLore.size() ,Component.text("§7已铭刻"));
        }else{
            originLore = List.of(Component.text("§7已铭刻"));
        }
        itemMeta.lore(originLore);
//        List<String> originLore = itemMeta.getLore();
//        if (originLore != null) {
//            originLore.add(originLore.size() - 1 ,"§7已铭刻");
//        }else{
//            originLore = List.of("§7已铭刻");
//        }
//        itemMeta.setLore(originLore);
    }

    public void updatePrepareAnvilDisplay(ItemMeta newMeta, ItemMeta originalMeta, int itemQualityLevel){
        // 获取原有Lore
        List<String> originalLore = originalMeta.hasLore() ? originalMeta.getLore() : new ArrayList<>();
        if (originalLore == null) originalLore = new ArrayList<>();

        // 移除之前由本插件添加的强化信息（如果有的话）
        removeOldForgingLore(originalLore);

        List<String> newLore = new ArrayList<>();

        // 添加品质信息
        String qualityColor = getQualityColor(itemQualityLevel);
        newLore.add(qualityColor + "品质: " + "???");
        newLore.addAll(originalLore);
        newMeta.setLore(newLore);
    }

    private void removeOldForgingLore(List<String> Lore){
        Iterator<String> iterator = Lore.iterator();
        while (iterator.hasNext()) {
            String line = iterator.next();
            if(regxService.loraDetection(line)){
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

    private String getQualityColor(String quality) {
        return switch (quality) {
            case "BROKEN" -> "§8";    // 深灰
            case "COMMON" -> "§f";   // 白色
            case "UNCOMMON" -> "§a"; // 绿色
            case "EPIC" -> "§5";      // 紫色
            case "LEGENDARY" -> "§6"; // 金色
            case "MYTHIC" -> "§d";    // 粉色
            default -> "§f";
        };
    }

    private String getQualityColor(int quality) {
        return switch (quality) {
            case 0 -> "§8";    // 深灰
            case 1 -> "§f";   // 白色
            case 2 -> "§a"; // 绿色
            case 3 -> "§5";      // 紫色
            case 4 -> "§6"; // 金色
            case 5 -> "§d";    // 粉色
            default -> "§f";
        };
    }

    private String getQualityDisplayName(String quality) {
        return switch (quality) {
            case "BROKEN" -> "破损";
            case "COMMON" -> "普通";
            case "UNCOMMON" -> "优秀";
            case "EPIC" -> "史诗";
            case "LEGENDARY" -> "传说";
            case "MYTHIC" -> "神话";
            default -> quality;
        };
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

    public void setCustomName(ItemStack itemStack, Component component) {
        if (itemStack == null) return;
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) return;
        itemMeta.customName(component);
        itemStack.setItemMeta(itemMeta); // 必须保存修改
    }

    public void setLore(ItemStack itemStack, String lore){
        if(itemStack == null) return;
        if(!itemStack.hasItemMeta()) return;
        ItemMeta itemMeta = itemStack.getItemMeta();
        List<Component> loreList = new ArrayList<>();
        loreList.add(Component.text(lore));
        itemMeta.lore(loreList);
        itemStack.setItemMeta(itemMeta);
    }

    public void addLore(ItemStack itemStack, List<String> lorelist){
        if(itemStack == null) return;
        if(!itemStack.hasItemMeta()) return;
        ItemMeta itemMeta = itemStack.getItemMeta();
        if(itemMeta.hasLore()){
            List<Component> loreList = itemMeta.lore();
            for(String lore : lorelist){
                if (loreList != null) {
                    loreList.add(Component.text(lore));
                }
            }
            itemMeta.lore(loreList);
            itemStack.setItemMeta(itemMeta);
        }else{
            List<Component> loreList = new ArrayList<>();
            for(String lore : lorelist){
                loreList.add(Component.text(lore));
            }
            itemMeta.lore(loreList);
            itemStack.setItemMeta(itemMeta);
        }
    }
}
