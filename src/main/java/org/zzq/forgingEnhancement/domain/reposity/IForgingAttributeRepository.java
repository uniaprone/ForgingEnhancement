package org.zzq.forgingEnhancement.domain.reposity;

import org.zzq.forgingEnhancement.domain.aggregateroot.ForgingAttributeConfig;
import org.zzq.forgingEnhancement.domain.valueobject.ForgingAttributeValue;

import java.util.Map;

public interface IForgingAttributeRepository {
    ForgingAttributeConfig getForgingAttributeConfig();
    Map<String, ForgingAttributeValue> getForgingAttributeMap();
    void reload();
}
