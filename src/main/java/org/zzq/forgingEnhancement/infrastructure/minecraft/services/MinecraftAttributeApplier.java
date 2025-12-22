package org.zzq.forgingEnhancement.infrastructure.minecraft.services;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.meta.ItemMeta;
import org.zzq.forgingEnhancement.domain.entity.ForgingAttribute;
import org.zzq.forgingEnhancement.domain.services.IAttributeApplier;
import org.zzq.forgingEnhancement.domain.valueobject.*;
import org.zzq.forgingEnhancement.infrastructure.ForgingLogger;
import org.zzq.forgingEnhancement.infrastructure.manager.KeyManager;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class MinecraftAttributeApplier implements IAttributeApplier {
    private ForgingAttributeConfig forgingAttributeConfig;
    private BaseAttributeConfig baseAttributeConfig;
    private ForgingLogger forgingLogger;
    private KeyManager keyManager;

    public MinecraftAttributeApplier(ForgingAttributeConfig forgingAttributeConfig, BaseAttributeConfig baseAttributeConfig, ForgingLogger forgingLogger, KeyManager keyManager){
        this.forgingAttributeConfig = forgingAttributeConfig;
        this.baseAttributeConfig = baseAttributeConfig;
        this.forgingLogger = forgingLogger;
        this.keyManager = keyManager;
    }

    public void applyExtraAttributes(ItemMeta meta, Material material, List<ForgingAttribute> forgingAttributes) {
        for (ForgingAttribute forgingAttribute : forgingAttributes) {
            applySingleAttribute(meta, forgingAttribute, material);
        }
    }

    public void applyBaseAttributes(ItemMeta meta, Material material) {
        BaseAttribute baseAttribute = baseAttributeConfig.getBaseAttributes(material.name().toLowerCase());
        for (Map.Entry<String, Double> attribute : baseAttribute.getAttributes().entrySet()){
            org.bukkit.attribute.Attribute bukkitAttribute = getBukkitAttribute(attribute.getKey());
            if (bukkitAttribute == null) {
                forgingLogger.debug("未知的基础属性: " + attribute);
                continue;
            }

            NamespacedKey baseAttrNamespaceKey = keyManager.createUniqueKey("ForgingEnhancement_base_" + attribute.getKey());
            // 基础属性命名ID
            switch (attribute.getKey()) {
                case "attack_damage" -> baseAttrNamespaceKey = NamespacedKey.minecraft("base_attack_damage");
                case "attack_speed" -> baseAttrNamespaceKey = NamespacedKey.minecraft("base_attack_speed");
                case "armor", "armor_toughness", "knockback_resistance" -> {
                    String materialName = material.name().toLowerCase();
                    if (materialName.contains("_helmet")) {
                        baseAttrNamespaceKey = NamespacedKey.minecraft("armor.helmet");
                    }
                    if (materialName.contains("_chestplate")) {
                        baseAttrNamespaceKey = NamespacedKey.minecraft("armor.chestplate");
                    }
                    if (materialName.contains("_leggings")) {
                        baseAttrNamespaceKey = NamespacedKey.minecraft("armor.leggings");
                    }
                    if (materialName.contains("_boots")) {
                        baseAttrNamespaceKey = NamespacedKey.minecraft("armor.boots");
                    }
                }
                default -> forgingLogger.debug("未知的基础属性: " + attribute);
            }
            AttributeModifier baseAttrModifier = new AttributeModifier(
                    baseAttrNamespaceKey,
                    attribute.getValue(),
                    AttributeModifier.Operation.ADD_NUMBER,
                    material.getEquipmentSlot().getGroup()
            );
            meta.addAttributeModifier(bukkitAttribute, baseAttrModifier);
        }
    }

    public void applySingleAttribute(ItemMeta meta, ForgingAttribute forgingAttribute, Material material) {
        ForgingAttributeValue forgingAttributeValue = forgingAttributeConfig.getForgingAttributeValue(forgingAttribute.getName());
        if (forgingAttributeValue == null) {
            forgingLogger.debug("未知的属性配置: " + forgingAttribute.getName());
            return;
        }

        org.bukkit.attribute.Attribute bukkitAttribute = getBukkitAttribute(forgingAttribute.getName());
        if (bukkitAttribute == null) {
            forgingLogger.debug("未知的Bukkit属性: " + forgingAttribute.getName());
            return;
        }

        // 创建唯一标识符
        NamespacedKey modifierKey = keyManager.createUniqueKey(forgingAttribute.getName());
        String materialName = material.name().toLowerCase();
        if(materialName.contains("_helmet")) modifierKey = keyManager.createUniqueKey("armor.helmet");
        else if (materialName.contains("_chestplate")) modifierKey = keyManager.createUniqueKey("armor.chestplate");
        else if (materialName.contains("_leggings")) modifierKey = keyManager.createUniqueKey("armor.leggings");
        else if (materialName.contains("_boots")) modifierKey = keyManager.createUniqueKey("armor.boots");
        // 创建属性修饰符，使用原物品对应的槽位
        AttributeModifier modifier = new AttributeModifier(
                modifierKey,
                forgingAttribute.getValue(),
                AttributeModifier.Operation.valueOf(forgingAttributeValue.getOperation()),
                material.getEquipmentSlot().getGroup()
        );

        // 添加属性修饰符（不会移除原有的）
        forgingLogger.debug("属性: " + bukkitAttribute.getKey() + " 修饰符: " + modifier.getAmount() + " 操作: " + modifier.getOperation() + " 名字: " + modifier.getName());
        meta.addAttributeModifier(bukkitAttribute, modifier);
    }

    public void removeForgingAttributes(ItemMeta meta) {
        if(meta != null && meta.hasAttributeModifiers()){
            for (Map.Entry<Attribute, AttributeModifier> entry : Objects.requireNonNull(meta.getAttributeModifiers()).entries()) {
                if(entry.getValue().getKey().toString().contains("forgingenhancement")){
                    meta.removeAttributeModifier(entry.getKey(),entry.getValue());
                }
            }
        }
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
                forgingLogger.debug("不支持的属性类型: " + configKey);
                return null;
        }
    }
}
