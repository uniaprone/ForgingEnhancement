package org.zzq.forgingEnhancement.managers;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashMap;
import java.util.Map;

public class StoneManager {
    private KeyManager keyManager;
    // 品质到CustomModelData的映射
    private final Map<String, Integer> qualityToModelData = new HashMap<>();
    // 品质显示名称映射
    private final Map<String, String> qualityDisplayNames = new HashMap<>();

    public StoneManager(KeyManager keyManager){
        this.keyManager = keyManager;
        initializeQualityMappings();
    }

    private void initializeQualityMappings() {
        // CustomModelData映射
        qualityToModelData.put("BROKEN", 1001);
        qualityToModelData.put("COMMON", 1002);
        qualityToModelData.put("UNCOMMON", 1003);
        qualityToModelData.put("EPIC", 1004);
        qualityToModelData.put("LEGENDARY", 1005);
        qualityToModelData.put("MYTHIC", 1006);

        // 显示名称映射
        qualityDisplayNames.put("BROKEN", "破损");
        qualityDisplayNames.put("COMMON", "普通");
        qualityDisplayNames.put("UNCOMMON", "优秀");
        qualityDisplayNames.put("EPIC", "史诗");
        qualityDisplayNames.put("LEGENDARY", "传说");
        qualityDisplayNames.put("MYTHIC", "神话");
    }

    public ItemStack createForgingStone(String quality, int amount) {
        ItemStack stone = new ItemStack(Material.NETHER_STAR, amount);
        ItemMeta meta = stone.getItemMeta();

        if (meta == null) return stone;

        // 设置CustomModelData（客户端视觉区分）
        Integer modelData = qualityToModelData.get(quality);
        if (modelData != null) {
            meta.setCustomModelData(modelData);
        }

        // 设置显示名称和Lore
        String displayName = getQualityColor(quality) + qualityDisplayNames.get(quality) + "锻造石";
        meta.setDisplayName(displayName);

        java.util.List<String> lore = new java.util.ArrayList<>();
        lore.add("§7用于在铁砧中强化装备");
        lore.add(getQualityColor(quality) + "品质: " + qualityDisplayNames.get(quality));
        meta.setLore(lore);

        // 添加NBT标签（服务器逻辑验证）
        meta.getPersistentDataContainer().set(keyManager.getForgingStoneKey(), PersistentDataType.BYTE, (byte) 1);
        meta.getPersistentDataContainer().set(keyManager.getStoneQualityKey(), PersistentDataType.STRING, quality);

        stone.setItemMeta(meta);
        return stone;
    }

    // 获取品质颜色
    public String getQualityColor(String quality) {
        switch (quality) {
            case "BROKEN": return "§8";
            case "COMMON": return "§f";     // 白色
            case "UNCOMMON": return "§a";   // 绿色
            case "EPIC": return "§5";       // 紫色
            case "LEGENDARY": return "§6";  // 金色
            case "MYTHIC": return "§d";
            default: return "§f";
        }
    }

    public Map<String, Integer> getQualityToModelData() {
        return new HashMap<>(qualityToModelData);
    }

    public Map<String, String> getQualityDisplayNames() {
        return new HashMap<>(qualityDisplayNames);
    }

    // 验证是否为锻造石
    public boolean isForgingStone(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;
        ItemMeta meta = item.getItemMeta();
        Byte isStone = meta.getPersistentDataContainer().get(keyManager.getForgingStoneKey(), PersistentDataType.BYTE);
        return isStone != null && isStone == 1;
    }

    // 获取锻造石品质
    public String getStoneQuality(ItemStack stone) {
        if (!isForgingStone(stone)) return null;
        ItemMeta meta = stone.getItemMeta();
        return meta.getPersistentDataContainer().get(keyManager.getStoneQualityKey(), PersistentDataType.STRING);
    }
}
