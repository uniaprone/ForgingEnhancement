package org.zzq.forgingEnhancement.Service;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.zzq.forgingEnhancement.Listener.AnvilForgingListener;
import org.zzq.forgingEnhancement.manager.ConfigManager;
import org.zzq.forgingEnhancement.model.Attribute;

import java.util.List;
import java.util.Map;

public class AttributeApplicationService {
    private PluginContext pluginContext;
    public AttributeApplicationService(PluginContext pluginContext){
        this.pluginContext = pluginContext;
    }
    public void applyExtraAttributes(ItemMeta meta, Material material, List<Attribute> attributes) {
        for (Attribute attribute : attributes) {
            applySingleAttribute(meta, attribute, material);
        }
    }

    public void applyBaseAttributes(ItemMeta meta, Material material) {
        String equipmentType = pluginContext.getConfigManager().getEquipmentType(material);
        if (equipmentType == null) {
            pluginContext.getLogger().warning("无法确定装备类型: " + material);
            return;
        }

        Map<String, Double> baseAttrs = pluginContext.getBaseAttributeManager().getBaseAttributes(material);
        if (baseAttrs.isEmpty()) {
            pluginContext.getLogger().warning("装备类型 " + equipmentType + " 没有基础属性配置");
            return;
        }

        pluginContext.getLogger().info("为 " + material + " (" + equipmentType + ") 应用 " + baseAttrs.size() + " 个基础属性");

        for (Map.Entry<String, Double> attribute : pluginContext.getBaseAttributeManager().getBaseAttributes(material).entrySet()){

            org.bukkit.attribute.Attribute bukkitAttribute = getBukkitAttribute(attribute.getKey());
            if (bukkitAttribute == null) {
                pluginContext.getLogger().warning("未知的基础属性: " + attribute);
                continue;
            }
            NamespacedKey baseAttrNamespaceKey = pluginContext.createKey("ForgingEnhancement_base_" + attribute.getKey());
            // 基础属性命名ID
            switch (attribute.getKey()) {
                case "attack_damage" -> baseAttrNamespaceKey = NamespacedKey.minecraft("base_attack_damage");
                case "attack_speed" -> baseAttrNamespaceKey = NamespacedKey.minecraft("base_attack_speed");
                case "armor" -> {
                    if (material.name().equalsIgnoreCase("_helmet")) {
                        baseAttrNamespaceKey = NamespacedKey.minecraft("armor.helmet");
                    }
                    if (material.name().equalsIgnoreCase("_chestplate")) {
                        baseAttrNamespaceKey = NamespacedKey.minecraft("armor.chestplate");
                    }
                    if (material.name().equalsIgnoreCase("_leggings")) {
                        baseAttrNamespaceKey = NamespacedKey.minecraft("armor.leggings");
                    }
                    if (material.name().equalsIgnoreCase("_boots")) {
                        baseAttrNamespaceKey = NamespacedKey.minecraft("armor.boots");
                    }
                }
            }
            AttributeModifier baseAttrModifier = new AttributeModifier(
                    baseAttrNamespaceKey,
                    attribute.getValue(),
                    AttributeModifier.Operation.ADD_NUMBER,
                    material.getEquipmentSlot().getGroup()
            );
            meta.addAttributeModifier(bukkitAttribute, baseAttrModifier);
            pluginContext.getLogger().info("应用基础属性: " + attribute.getKey() + " = " + attribute.getValue() + " (" + material.getEquipmentSlot().getGroup() + ")");
        }
    }

    private void applySingleAttribute(ItemMeta meta, Attribute attribute, Material material) {
        ConfigManager.AttributeConfig config = pluginContext.getConfigManager().getAttributeConfig(attribute.getName());
        if (config == null) {
            pluginContext.getLogger().warning("未知的属性配置: " + attribute.getName());
            return;
        }

        org.bukkit.attribute.Attribute bukkitAttribute = getBukkitAttribute(attribute.getName());
        if (bukkitAttribute == null) {
            pluginContext.getLogger().warning("未知的Bukkit属性: " + attribute.getName());
            return;
        }

        // 创建唯一标识符
        NamespacedKey modifierKey = pluginContext.createKey(attribute.getName());

        // 创建属性修饰符，使用原物品对应的槽位
        AttributeModifier modifier = new AttributeModifier(
                modifierKey,
                attribute.getValue(),
                AttributeModifier.Operation.valueOf(config.operation),
                material.getEquipmentSlot().getGroup()
        );

        // 添加属性修饰符（不会移除原有的）
        meta.addAttributeModifier(bukkitAttribute, modifier);
    }

    private org.bukkit.attribute.Attribute getBukkitAttribute(String configKey) {
        switch (configKey) {
            case "movement_speed": return org.bukkit.attribute.Attribute.MOVEMENT_SPEED;
            case "armor": return org.bukkit.attribute.Attribute.ARMOR;
            case "armor_toughness": return org.bukkit.attribute.Attribute.ARMOR_TOUGHNESS;
            case "max_health": return org.bukkit.attribute.Attribute.MAX_HEALTH;
            case "knockback_resistance": return org.bukkit.attribute.Attribute.KNOCKBACK_RESISTANCE;
            case "attack_damage": return org.bukkit.attribute.Attribute.ATTACK_DAMAGE;
            case "attack_knockback": return org.bukkit.attribute.Attribute.ATTACK_KNOCKBACK;
            case "attack_speed": return org.bukkit.attribute.Attribute.ATTACK_SPEED;
            case "gravity": return org.bukkit.attribute.Attribute.GRAVITY;
            case "burning_time": return org.bukkit.attribute.Attribute.BURNING_TIME;
            case "explosion_knockback_resistance": return org.bukkit.attribute.Attribute.EXPLOSION_KNOCKBACK_RESISTANCE;
            case "oxygen_bonus": return org.bukkit.attribute.Attribute.OXYGEN_BONUS;
            case "sneaking_speed": return  org.bukkit.attribute.Attribute.SNEAKING_SPEED;
            case "step_height": return  org.bukkit.attribute.Attribute.STEP_HEIGHT;
            case "fall_damage_multiplier": return  org.bukkit.attribute.Attribute.FALL_DAMAGE_MULTIPLIER;
            case "water_movement_efficiency": return  org.bukkit.attribute.Attribute.WATER_MOVEMENT_EFFICIENCY;
            case "movement_efficiency": return  org.bukkit.attribute.Attribute.MOVEMENT_EFFICIENCY;
            case "jump_strength": return  org.bukkit.attribute.Attribute.JUMP_STRENGTH;
            case "safe_fall_distance": return  org.bukkit.attribute.Attribute.SAFE_FALL_DISTANCE;
            case "entity_interaction_range": return  org.bukkit.attribute.Attribute.ENTITY_INTERACTION_RANGE;
            case "sweeping_damage_ratio": return org.bukkit.attribute.Attribute.SWEEPING_DAMAGE_RATIO;
            case "mining_efficiency": return org.bukkit.attribute.Attribute.MINING_EFFICIENCY;
            case "submerged_mining_speed": return org.bukkit.attribute.Attribute.SUBMERGED_MINING_SPEED;
            case "block_interaction_range": return org.bukkit.attribute.Attribute.BLOCK_INTERACTION_RANGE;
            default:
                pluginContext.getLogger().warning("不支持的属性类型: " + configKey);
                return null;
        }
    }
}
