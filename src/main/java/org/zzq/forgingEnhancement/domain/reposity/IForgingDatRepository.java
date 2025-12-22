package org.zzq.forgingEnhancement.domain.reposity;

import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.zzq.forgingEnhancement.domain.entity.EnhancementResult;
import org.zzq.forgingEnhancement.domain.entity.ForgingAttribute;

import java.util.List;

public interface IForgingDatRepository {

    boolean isEngraved(ItemMeta itemMeta);

    EnhancementResult getForgingData(ItemMeta itemMeta);

    List<ForgingAttribute> removeUnengravedForgingData(ItemMeta itemMeta);

    boolean hasBaseAttributeData(ItemMeta itemMeta);

    void addBaseAttributeData(ItemMeta itemMeta);

    void addForgingData(ItemMeta itemMeta, EnhancementResult enhancementResult);
}
