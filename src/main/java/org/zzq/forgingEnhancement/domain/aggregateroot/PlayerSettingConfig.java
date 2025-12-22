package org.zzq.forgingEnhancement.domain.aggregateroot;

import org.zzq.forgingEnhancement.domain.entity.PlayerSetting;

import java.util.Map;

public class PlayerSettingConfig {
    private Map<String, PlayerSetting> playerSettingMap;

    public PlayerSettingConfig(Map<String, PlayerSetting> playerSettingMap) {
        this.playerSettingMap = playerSettingMap;
    }

    public boolean isWorkbenchForgingEnable(String playerId){
        if(playerId == null) return false;
        if(playerSettingMap.get(playerId) == null) return false;
        return playerSettingMap.get(playerId).isEnable();
    }

    public boolean togglePlayerSetting(String playerId){
        if(playerId == null) return false;
        PlayerSetting playerSetting = playerSettingMap.get(playerId);
        if(playerSetting == null) return false;
        return playerSetting.toggleEnable();
    }

    public boolean hasSetting(String playerId){
        return playerSettingMap.get(playerId) != null;
    }

    public void addPlayerSetting(String playerId, String name){
        playerSettingMap.put(playerId, new PlayerSetting(playerId, name, true));
    }
}
