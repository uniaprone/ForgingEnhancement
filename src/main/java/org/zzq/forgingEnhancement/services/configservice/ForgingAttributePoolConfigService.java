package org.zzq.forgingEnhancement.services.configservice;

import org.zzq.forgingEnhancement.domain.config.ForgingAttributePoolConfig;
import org.zzq.forgingEnhancement.domain.reposity.IForgingAttributePoolRepository;

public class ForgingAttributePoolConfigService {
    private IForgingAttributePoolRepository forgingAttributePoolRepository;

    public ForgingAttributePoolConfigService(IForgingAttributePoolRepository forgingAttributePoolRepository) {
        this.forgingAttributePoolRepository = forgingAttributePoolRepository;
    }

    public ForgingAttributePoolConfig getForgingAttributePoolConfigByKey(String key){
        if(key == null) return null;
        return forgingAttributePoolRepository.getForgingAttributePool().get(key);
    }
}
