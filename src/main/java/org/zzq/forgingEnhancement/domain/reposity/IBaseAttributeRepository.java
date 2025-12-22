package org.zzq.forgingEnhancement.domain.reposity;

import org.zzq.forgingEnhancement.domain.aggregateroot.BaseAttributeConfig;
import org.zzq.forgingEnhancement.domain.valueobject.BaseAttribute;

import java.util.Map;

public interface IBaseAttributeRepository {
    BaseAttributeConfig getBaseAttributeConfig();
    Map<String, BaseAttribute> getBaseAttributeMap();
    void reload();
}
