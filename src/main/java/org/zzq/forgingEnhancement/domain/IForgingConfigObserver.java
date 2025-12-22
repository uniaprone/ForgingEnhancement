package org.zzq.forgingEnhancement.domain;

import org.zzq.forgingEnhancement.domain.entity.ForgingConfig;

public interface IForgingConfigObserver {
    void onConfigUpdate(ForgingConfig forgingConfig);
}
