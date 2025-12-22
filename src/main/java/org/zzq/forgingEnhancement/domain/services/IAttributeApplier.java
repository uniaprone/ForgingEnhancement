package org.zzq.forgingEnhancement.domain.services;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.meta.ItemMeta;
import org.zzq.forgingEnhancement.domain.entity.ForgingAttribute;
import org.zzq.forgingEnhancement.managers.ConfigManager;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public interface IAttributeApplier {
    void applyBaseAttributes(ItemMeta meta, Material material);

    void applySingleAttribute(ItemMeta meta, ForgingAttribute forgingAttribute, Material material);

    void removeForgingAttributes(ItemMeta meta);

    void applyExtraAttributes(ItemMeta meta, Material material, List<ForgingAttribute> forgingAttributes);
}
