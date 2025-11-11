package org.zzq.forgingEnhancement.managers;

import org.bukkit.NamespacedKey;
import org.zzq.forgingEnhancement.ForgingEnhancement;

public class KeyManager {
    private final ForgingEnhancement plugin;

    // 固定Key（不需要变化）
    private final NamespacedKey forgingStoneKey;
    private final NamespacedKey stoneQualityKey;
    private final NamespacedKey enhancementKey;
    private final NamespacedKey baseAttributeKey;

    public KeyManager(ForgingEnhancement plugin) {
        this.plugin = plugin;

        // 初始化固定Key
        this.forgingStoneKey = new NamespacedKey(plugin, "forging_stone");
        this.stoneQualityKey = new NamespacedKey(plugin, "stone_quality");
        this.enhancementKey = new NamespacedKey(plugin, "forging_data");
        this.baseAttributeKey = new NamespacedKey(plugin, "base_attribute_applied");
    }

    // 创建唯一Key（用于属性修饰符等需要唯一性的场景）
    public NamespacedKey createUniqueKey(String prefix) {
        return new NamespacedKey(plugin, prefix);
    }

    // 获取固定Key
    public NamespacedKey getForgingStoneKey() { return forgingStoneKey; }
    public NamespacedKey getStoneQualityKey() { return stoneQualityKey; }
    public NamespacedKey getEnhancementKey() { return enhancementKey; }
    public NamespacedKey getBaseAttributeKey() { return baseAttributeKey; }
}
