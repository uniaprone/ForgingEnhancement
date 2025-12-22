package org.zzq.forgingEnhancement.domain.reposity;

import org.zzq.forgingEnhancement.domain.aggregateroot.ForgingAttributePoolConfig;
import org.zzq.forgingEnhancement.domain.valueobject.EquipmentAttribute;

import java.util.List;
import java.util.Map;

public interface IForgingAttributePoolRepository {
    ForgingAttributePoolConfig getForgingAttributePool();
    List<String> getForgableEquipmentList();
    Map<String, EquipmentAttribute> getEquipmentAttributesMap();
    void reload();
}
