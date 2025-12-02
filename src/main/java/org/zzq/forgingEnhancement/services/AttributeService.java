package org.zzq.forgingEnhancement.services;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.meta.ItemMeta;
import org.zzq.forgingEnhancement.managers.BaseAttributeManager;
import org.zzq.forgingEnhancement.managers.ConfigManager;
import org.zzq.forgingEnhancement.managers.KeyManager;
import org.zzq.forgingEnhancement.models.EnhancementResult;
import org.zzq.forgingEnhancement.models.ForgingAttribute;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Logger;

public class AttributeService {
    private Logger logger;
    private ConfigManager configManager;
    private BaseAttributeManager baseAttributeManager;
    private KeyManager keyManager;

    public AttributeService(Logger logger, ConfigManager configManager, BaseAttributeManager baseAttributeManager, KeyManager keyManager){
        this.logger = logger;
        this.configManager = configManager;
        this.baseAttributeManager = baseAttributeManager;
        this.keyManager = keyManager;
    }
    public void applyExtraAttributes(ItemMeta meta, Material material, List<ForgingAttribute> forgingAttributes) {
        for (ForgingAttribute forgingAttribute : forgingAttributes) {
            applySingleAttribute(meta, forgingAttribute, material);
        }
    }

    public EnhancementResult removeHasEngravedResult(EnhancementResult enhancementResult, List<ForgingAttribute> engravedForgingAttributes) {
        List<ForgingAttribute> forgingAttributes = enhancementResult.getAttributeList();
        Iterator<ForgingAttribute> iterator = forgingAttributes.iterator();
        while (iterator.hasNext()){
            ForgingAttribute forgingAttribute = iterator.next();
            for(ForgingAttribute engravedForgingAttribute : engravedForgingAttributes){
                if(forgingAttribute.getName().equals(engravedForgingAttribute.getName())){
                    iterator.remove();
                }
            }
        }
        return enhancementResult;
    }

    public void addEngravedResult(EnhancementResult enhancementResult, List<ForgingAttribute> engravedForgingAttributes) {
        List<ForgingAttribute> forgingAttributes = enhancementResult.getAttributeList();
        forgingAttributes.addAll(0, engravedForgingAttributes);
    }

    public void applyBaseAttributes(ItemMeta meta, Material material) {
        String equipmentType = configManager.getEquipmentType(material);
        if (equipmentType == null) {
            logger.warning("无法确定装备类型: " + material);
            return;
        }

        Map<String, Double> baseAttrs = baseAttributeManager.getBaseAttributes(material);
        if (baseAttrs.isEmpty()) {
            logger.warning("装备类型 " + equipmentType + " 没有基础属性配置");
            return;
        }

        for (Map.Entry<String, Double> attribute : baseAttributeManager.getBaseAttributes(material).entrySet()){
            org.bukkit.attribute.Attribute bukkitAttribute = getBukkitAttribute(attribute.getKey());
            if (bukkitAttribute == null) {
                logger.warning("未知的基础属性: " + attribute);
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
                default -> logger.warning("未知的基础属性: " + attribute);
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

    private void applySingleAttribute(ItemMeta meta, ForgingAttribute forgingAttribute, Material material) {
        ConfigManager.AttributeConfig config = configManager.getAttributeConfig(forgingAttribute.getName());
        if (config == null) {
            logger.warning("未知的属性配置: " + forgingAttribute.getName());
            return;
        }

        org.bukkit.attribute.Attribute bukkitAttribute = getBukkitAttribute(forgingAttribute.getName());
        if (bukkitAttribute == null) {
            logger.warning("未知的Bukkit属性: " + forgingAttribute.getName());
            return;
        }

        // 创建唯一标识符
        NamespacedKey modifierKey = keyManager.createUniqueKey(forgingAttribute.getName());

        // 创建属性修饰符，使用原物品对应的槽位
        AttributeModifier modifier = new AttributeModifier(
                modifierKey,
                forgingAttribute.getValue(),
                AttributeModifier.Operation.valueOf(config.operation),
                material.getEquipmentSlot().getGroup()
        );

        // 添加属性修饰符（不会移除原有的）
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

//    public void removeForgingAttributesExpectEngraved(ItemMeta meta, List<ForgingAttribute> engravedForgingAttributes) {
//        if(meta != null && meta.hasAttributeModifiers()){
//            for (Map.Entry<Attribute, AttributeModifier> entry : Objects.requireNonNull(meta.getAttributeModifiers()).entries()) {
//                for(ForgingAttribute engravedForgingAttribute : engravedForgingAttributes){
//                    String attributeName = entry.getValue().getKey().toString();
//                    logger.info("循环属性名： " + attributeName);
//                    logger.info("铭刻属性名： " + engravedForgingAttribute.getName());
//                    if(attributeName.contains("forgingenhancement") && !attributeName.contains(engravedForgingAttribute.getName())){
//                        logger.info("移除属性名： " + attributeName);
//                        meta.removeAttributeModifier(entry.getKey(),entry.getValue());
//                    }
//                }
//            }
//        }
//    }

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
                logger.warning("不支持的属性类型: " + configKey);
                return null;
        }
    }
}
