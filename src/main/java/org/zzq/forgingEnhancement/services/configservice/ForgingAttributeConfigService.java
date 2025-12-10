package org.zzq.forgingEnhancement.services.configservice;

import org.zzq.forgingEnhancement.domain.config.ForgingAttributeConfig;
import org.zzq.forgingEnhancement.domain.reposity.IForgingAttributeRepository;

import java.util.Map;

public class ForgingAttributeConfigService {
    private IForgingAttributeRepository forgingAttributeRepository;
    public ForgingAttributeConfigService(IForgingAttributeRepository forgingAttributeRepository){
        this.forgingAttributeRepository = forgingAttributeRepository;
    }

    public ForgingAttributeConfig getForgingAttributeConfigByKey(String key){
        if(key == null) return null;
        return forgingAttributeRepository.getForgingAttributeConfig().get(key);
    }
}
