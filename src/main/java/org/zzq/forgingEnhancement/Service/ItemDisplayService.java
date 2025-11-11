package org.zzq.forgingEnhancement.Service;

import org.bukkit.inventory.meta.ItemMeta;
import org.zzq.forgingEnhancement.Listener.AnvilForgingListener;
import org.zzq.forgingEnhancement.manager.ConfigManager;
import org.zzq.forgingEnhancement.model.Attribute;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ItemDisplayService {
    private PluginContext pluginContext;
    public ItemDisplayService(PluginContext pluginContext){
        this.pluginContext = pluginContext;
    }

    public void updateItemDisplay(ItemMeta newMeta, ItemMeta originalMeta, String itemQuality, List<Attribute> enhancements) {
        // 获取原有Lore
        List<String> originalLore = originalMeta.hasLore() ? originalMeta.getLore() : new ArrayList<>();
        if (originalLore == null) originalLore = new ArrayList<>();

        // 创建新的Lore列表，先添加原有Lore
        List<String> newLore = new ArrayList<>(originalLore);

        // 移除之前由本插件添加的强化信息（如果有的话）
        Iterator<String> iterator = newLore.iterator();
        while (iterator.hasNext()) {
            String line = iterator.next();
            if (line.contains("品质:") || line.contains("词条:") || line.contains("未获得词条")) {
                iterator.remove();
            }
        }

        // 添加分隔线（如果原有Lore不为空）
        if (!originalLore.isEmpty() && !enhancements.isEmpty()) {
            newLore.add("§8§m----------------------");
        }

        // 添加品质信息
        String qualityColor = getQualityColor(itemQuality);
        newLore.add(qualityColor + "品质: " + getQualityDisplayName(itemQuality));

        // 添加词条信息
        for (Attribute attr : enhancements) {
            ConfigManager.AttributeConfig config = pluginContext.getConfigManager().getAttributeConfig(attr.getName());
            if (config != null) {
                String attrColor = getQualityColor(attr.getLevel());
                String valueDisplay = formatAttributeValue(config, attr.getLevel());

                newLore.add(attrColor + config.name + ": +" + valueDisplay + " (" + getQualityDisplayName(attr.getLevel()) + ")");
            }
        }

        newMeta.setLore(newLore);
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
            case "BROKEN" -> "破旧";
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
            case 0: return "破旧";
            case 1: return "普通";
            case 2: return "优秀";
            case 3: return "史诗";
            case 4: return "传说";
            case 5: return "神话";
            default: return "普通";
        }
    }
}
