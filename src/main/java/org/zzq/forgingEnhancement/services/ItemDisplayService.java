package org.zzq.forgingEnhancement.services;

import org.bukkit.inventory.meta.ItemMeta;
import org.zzq.forgingEnhancement.managers.ConfigManager;
import org.zzq.forgingEnhancement.models.Attribute;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

public class ItemDisplayService {
    private ConfigManager configManager;
    private RegxService regxService;
    public ItemDisplayService(ConfigManager configManager, RegxService regxService){
        this.configManager = configManager;
        this.regxService = regxService;
    }

    public void updateItemDisplay(ItemMeta newMeta, ItemMeta originalMeta, int itemQualityLevel, List<Attribute> enhancements) {
        // 获取原有Lore
        List<String> originalLore = originalMeta.hasLore() ? originalMeta.getLore() : new ArrayList<>();
        if (originalLore == null) originalLore = new ArrayList<>();

        // 移除之前由本插件添加的强化信息（如果有的话）
        removeOldForgingLore(originalLore);

        List<String> newLore = new ArrayList<>(originalLore);

        // 添加品质信息
        String qualityColor = getQualityColor(itemQualityLevel);
        newLore.add(qualityColor + "品质: " + "『" + getQualityDisplayName(itemQualityLevel) + "』");

        // 添加词条信息
        for (Attribute attr : enhancements) {
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
}
