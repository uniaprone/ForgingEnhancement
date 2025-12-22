package org.zzq.forgingEnhancement.domain.services;

import org.bukkit.Material;
import org.bukkit.inventory.meta.ItemMeta;
import org.zzq.forgingEnhancement.domain.entity.ForgingAttribute;

import java.util.List;

public interface IAttributeApplier {
    void applyBaseAttributes(ItemMeta meta, Material material);

    void applySingleAttribute(ItemMeta meta, ForgingAttribute forgingAttribute, Material material);

    void removeForgingAttributes(ItemMeta meta);

    void applyExtraAttributes(ItemMeta meta, Material material, List<ForgingAttribute> forgingAttributes);
}
