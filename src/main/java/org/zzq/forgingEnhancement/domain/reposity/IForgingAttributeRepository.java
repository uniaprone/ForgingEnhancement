package org.zzq.forgingEnhancement.domain.reposity;

import org.zzq.forgingEnhancement.domain.config.ForgingAttributeConfig;

import java.util.Map;

public interface IForgingAttributeRepository {
    Map<String, ForgingAttributeConfig> getForgingAttributeConfig();
}
