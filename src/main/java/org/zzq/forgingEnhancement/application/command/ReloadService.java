package org.zzq.forgingEnhancement.application.command;

import org.zzq.forgingEnhancement.domain.aggregateroot.BaseAttributeConfig;
import org.zzq.forgingEnhancement.domain.aggregateroot.ForgingAttributeConfig;
import org.zzq.forgingEnhancement.domain.aggregateroot.ForgingAttributePoolConfig;
import org.zzq.forgingEnhancement.domain.aggregateroot.PlayerSettingConfig;
import org.zzq.forgingEnhancement.domain.entity.ForgingConfig;
import org.zzq.forgingEnhancement.domain.reposity.*;

public class ReloadService {
    private IBaseAttributeRepository iBaseAttributeRepository;
    private IForgingConfigRepository iForgingConfigRepository;
    private IForgingAttributeRepository iForgingAttributeRepository;
    private IForgingAttributePoolRepository iForgingAttributePoolRepository;
    private IPlayerSettingRepository iPlayerSettingRepository;
    private BaseAttributeConfig baseAttributeConfig;
    private ForgingConfig forgingConfig;
    private ForgingAttributeConfig forgingAttributeConfig;
    private ForgingAttributePoolConfig forgingAttributePoolConfig;
    private PlayerSettingConfig playerSettingConfig;

    public ReloadService(IBaseAttributeRepository iBaseAttributeRepository, IForgingConfigRepository iForgingConfigRepository, IForgingAttributeRepository iForgingAttributeRepository, IForgingAttributePoolRepository iForgingAttributePoolRepository, IPlayerSettingRepository iPlayerSettingRepository, BaseAttributeConfig baseAttributeConfig, ForgingConfig forgingConfig, ForgingAttributeConfig forgingAttributeConfig, ForgingAttributePoolConfig forgingAttributePoolConfig, PlayerSettingConfig playerSettingConfig) {
        this.iBaseAttributeRepository = iBaseAttributeRepository;
        this.iForgingConfigRepository = iForgingConfigRepository;
        this.iForgingAttributeRepository = iForgingAttributeRepository;
        this.iForgingAttributePoolRepository = iForgingAttributePoolRepository;
        this.iPlayerSettingRepository = iPlayerSettingRepository;
        this.baseAttributeConfig = baseAttributeConfig;
        this.forgingConfig = forgingConfig;
        this.forgingAttributeConfig = forgingAttributeConfig;
        this.forgingAttributePoolConfig = forgingAttributePoolConfig;
        this.playerSettingConfig = playerSettingConfig;
    }

    public void reload(){
        iBaseAttributeRepository.reload();
        iForgingConfigRepository.reload();
        iForgingAttributePoolRepository.reload();
        iForgingAttributeRepository.reload();
        iPlayerSettingRepository.reload();
        baseAttributeConfig.updateConfig(iBaseAttributeRepository.getBaseAttributeMap());
        forgingConfig.updateConfig(iForgingConfigRepository.getEnv());
        forgingAttributeConfig.updateConfig(iForgingAttributeRepository.getForgingAttributeMap());
        forgingAttributePoolConfig.updateConfig(iForgingAttributePoolRepository.getForgableEquipmentList(), iForgingAttributePoolRepository.getEquipmentAttributesMap());
        playerSettingConfig.updateConfig(iPlayerSettingRepository.getPlayerSettingMap());
    }
}
