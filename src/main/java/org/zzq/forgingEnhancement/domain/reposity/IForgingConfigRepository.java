package org.zzq.forgingEnhancement.domain.reposity;

import org.zzq.forgingEnhancement.domain.entity.ForgingConfig;

public interface IForgingConfigRepository {
    ForgingConfig getForgingConfig();
    String getEnv();
    void reload();
}
