package org.zzq.forgingEnhancement.domain.reposity;

import org.zzq.forgingEnhancement.domain.config.ForgingAttributePoolConfig;

import java.util.List;
import java.util.Map;

public interface IForgingAttributePoolRepository {
    Map<String, ForgingAttributePoolConfig> getForgingAttributePool();
}
