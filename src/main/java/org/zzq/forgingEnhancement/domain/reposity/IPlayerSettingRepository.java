package org.zzq.forgingEnhancement.domain.reposity;

import org.zzq.forgingEnhancement.domain.entity.PlayerSetting;
import org.zzq.forgingEnhancement.domain.aggregateroot.PlayerSettingConfig;

import java.util.Map;

public interface IPlayerSettingRepository {
    PlayerSettingConfig getPlayerSettings();
    void save(String playerId, String name, boolean isEnable);
    void saves(Map<String, PlayerSetting> playerSettingMap);
}
