package org.zzq.forgingEnhancement.application;

import org.bukkit.entity.Player;
import org.zzq.forgingEnhancement.domain.aggregateroot.PlayerSettingConfig;
import org.zzq.forgingEnhancement.domain.reposity.IPlayerSettingRepository;
import org.zzq.forgingEnhancement.infrastructure.ForgingLogger;

public class TogglePlayerSettingService {
    private IPlayerSettingRepository iPlayerSettingRepository;
    private ForgingLogger logger;

    public TogglePlayerSettingService(IPlayerSettingRepository iPlayerSettingRepository, ForgingLogger logger) {
        this.iPlayerSettingRepository = iPlayerSettingRepository;
        this.logger = logger;
    }

    public boolean togglePlayerSetting(Player player){
        if(player == null) return false;
        String playerId = String.valueOf(player.getUniqueId());
        String playerName = player.getName();
        logger.debug("玩家: " + playerName + " ID: " + playerId + " 切换工作台模式");
        PlayerSettingConfig playerSettingConfig = iPlayerSettingRepository.getPlayerSettings();
        if(!playerSettingConfig.hasSetting(playerId)){
            logger.debug("玩家: " + playerName + " ID: " + playerId + " 首次切换");
            playerSettingConfig.addPlayerSetting(playerId, playerName);
        }

        boolean isEnable = playerSettingConfig.togglePlayerSetting(playerId);
        logger.debug("玩家: " + playerName + " ID: " + playerId + " 启用： " + isEnable);
        iPlayerSettingRepository.save(playerId, playerName, isEnable);
        return isEnable;
    }
}
