package org.zzq.forgingEnhancement.infrastructure.yaml.repository;

import org.zzq.forgingEnhancement.domain.entity.PlayerSetting;
import org.zzq.forgingEnhancement.domain.aggregateroot.PlayerSettingConfig;
import org.zzq.forgingEnhancement.domain.reposity.IPlayerSettingRepository;
import org.zzq.forgingEnhancement.infrastructure.yaml.parser.YamlPlayerSettingParser;

import java.util.HashMap;
import java.util.Map;

public class YamlPlayerSettingRepository implements IPlayerSettingRepository {
    private YamlPlayerSettingParser yamlPlayerSettingParser;
    private PlayerSettingConfig playerSettingMapCache;

    public YamlPlayerSettingRepository(YamlPlayerSettingParser yamlPlayerSettingParser) {
        this.yamlPlayerSettingParser = yamlPlayerSettingParser;
        this.playerSettingMapCache = yamlPlayerSettingParser.load();
        if (this.playerSettingMapCache == null) {
            playerSettingMapCache = new PlayerSettingConfig(new HashMap<>());
        }
    }

    @Override
    public void reload() {
        this.playerSettingMapCache = yamlPlayerSettingParser.load();
        if (this.playerSettingMapCache == null) {
            playerSettingMapCache = new PlayerSettingConfig(new HashMap<>());
        }
    }

    @Override
    public Map<String, PlayerSetting> getPlayerSettingMap() {
        return playerSettingMapCache.getPlayerSettingMap();
    }

    @Override
    public PlayerSettingConfig getPlayerSettings() {
        return playerSettingMapCache;
    }


    @Override
    public void save(String playerId, String name, boolean isEnable) {
        yamlPlayerSettingParser.save(playerId, name, isEnable);
    }

    @Override
    public void saves(Map<String, PlayerSetting> playerSettingMap) {
        yamlPlayerSettingParser.saves(playerSettingMap);
    }
}
