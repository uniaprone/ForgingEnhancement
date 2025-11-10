package org.zzq.forgingEnhancement;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.zzq.forgingEnhancement.Listener.AnvilForgingListener;
import org.zzq.forgingEnhancement.manager.FileManager;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class ForgingEnhancement extends JavaPlugin implements Listener {

    private FileManager fileManager;
    private Random random;

    // NBT标签键
    private NamespacedKey forgingStoneKey;
    private NamespacedKey stoneQualityKey;

    // 品质到CustomModelData的映射
    private final Map<String, Integer> qualityToModelData = new HashMap<>();
    // 品质显示名称映射
    private final Map<String, String> qualityDisplayNames = new HashMap<>();

    @Override
    public void onEnable() {
        this.fileManager = FileManager.getInstance(this);
        this.random = new Random();

        // 初始化NBT键
        this.forgingStoneKey = new NamespacedKey(this, "forging_stone");
        this.stoneQualityKey = new NamespacedKey(this, "stone_quality");

        // 初始化品质映射
        initializeQualityMappings();

        getServer().getPluginManager().registerEvents(this, this);
        getServer().getPluginManager().registerEvents(new AnvilForgingListener(this), this);

        // 创建命令执行器实例
        ForgingEnhancementCommand commandExecutor = new ForgingEnhancementCommand(this);

        // 注册命令执行器和Tab补全器
        getCommand("forgingenhancement").setExecutor(commandExecutor);
        getCommand("forgingenhancement").setTabCompleter(commandExecutor);

        getLogger().info("锻造增强插件已启用!");
    }

    private void initializeQualityMappings() {
        // CustomModelData映射
        qualityToModelData.put("COMMON", 1001);
        qualityToModelData.put("UNCOMMON", 1002);
        qualityToModelData.put("EPIC", 1003);
        qualityToModelData.put("LEGENDARY", 1004);

        // 显示名称映射
        qualityDisplayNames.put("COMMON", "普通");
        qualityDisplayNames.put("UNCOMMON", "优秀");
        qualityDisplayNames.put("EPIC", "史诗");
        qualityDisplayNames.put("LEGENDARY", "传说");
    }

    @Override
    public void onDisable() {
        getLogger().info("锻造增强插件已禁用!");
    }

    public FileManager getFileManager() {
        return fileManager;
    }

    // 创建自定义锻造石
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
        lore.add("§7品质: " + qualityDisplayNames.get(quality));
        lore.add("§8ID: " + quality.toLowerCase());
        meta.setLore(lore);

        // 添加NBT标签（服务器逻辑验证）
        meta.getPersistentDataContainer().set(forgingStoneKey, PersistentDataType.BYTE, (byte) 1);
        meta.getPersistentDataContainer().set(stoneQualityKey, PersistentDataType.STRING, quality);

        stone.setItemMeta(meta);
        return stone;
    }

    // 验证是否为锻造石
    public boolean isForgingStone(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;

        ItemMeta meta = item.getItemMeta();
        Byte isStone = meta.getPersistentDataContainer().get(forgingStoneKey, PersistentDataType.BYTE);
        return isStone != null && isStone == 1;
    }

    // 获取锻造石品质
    public String getStoneQuality(ItemStack stone) {
        if (!isForgingStone(stone)) return null;

        ItemMeta meta = stone.getItemMeta();
        return meta.getPersistentDataContainer().get(stoneQualityKey, PersistentDataType.STRING);
    }

    // 获取品质颜色
    private String getQualityColor(String quality) {
        switch (quality) {
            case "COMMON": return "§f";     // 白色
            case "UNCOMMON": return "§a";   // 绿色
            case "EPIC": return "§5";       // 紫色
            case "LEGENDARY": return "§6";  // 金色
            default: return "§f";
        }
    }

    public Random getRandom() {
        return random;
    }

    public NamespacedKey getForgingStoneKey() {
        return forgingStoneKey;
    }

    public NamespacedKey getStoneQualityKey() {
        return stoneQualityKey;
    }

    public Map<String, Integer> getQualityToModelData() {
        return new HashMap<>(qualityToModelData);
    }

    public Map<String, String> getQualityDisplayNames() {
        return new HashMap<>(qualityDisplayNames);
    }
}