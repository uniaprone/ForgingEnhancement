package org.zzq.forgingEnhancement.domain.aggregateroot;

import org.zzq.forgingEnhancement.domain.valueobject.BaseAttribute;

import java.util.Map;

public class BaseAttributeConfig {
    private Map<String, BaseAttribute> baseAttributeConfig;

    public BaseAttributeConfig(Map<String, BaseAttribute> baseAttributeConfig) {
        this.baseAttributeConfig = baseAttributeConfig;
    }

    public BaseAttribute getBaseAttributes(String name){
        if(name == null) return null;
        return baseAttributeConfig.get(name);
    }

    public void updateConfig(Map<String, BaseAttribute> baseAttributeConfig){
        this.baseAttributeConfig = baseAttributeConfig;
    }

    public Map<String, BaseAttribute> getBaseAttributeConfig() {
        return baseAttributeConfig;
    }
}
