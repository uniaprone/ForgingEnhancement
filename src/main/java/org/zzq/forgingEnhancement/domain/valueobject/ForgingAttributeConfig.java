package org.zzq.forgingEnhancement.domain.valueobject;

import org.zzq.forgingEnhancement.domain.entity.ForgingAttribute;
import org.zzq.forgingEnhancement.utils.RandomUtil;

import java.util.Map;

public class ForgingAttributeConfig {
    private Map<String, ForgingAttributeValue> forgingAttributeValueMap;

    public ForgingAttributeConfig(Map<String, ForgingAttributeValue> forgingAttributeValueMap) {
        this.forgingAttributeValueMap = forgingAttributeValueMap;
    }

    public ForgingAttributeValue getForgingAttributeValue(String attribute){
        return forgingAttributeValueMap.get(attribute);
    }

    public boolean isCommonAttribute(String attribute){
        ForgingAttributeValue forgingAttributeValue = forgingAttributeValueMap.get(attribute);
        if(forgingAttributeValue == null) return false;
        return !forgingAttributeValue.isRare();
    }

    public boolean isRareAttribute(String attribute){
        ForgingAttributeValue forgingAttributeValue = forgingAttributeValueMap.get(attribute);
        if(forgingAttributeValue == null) return false;
        return forgingAttributeValue.isRare();
    }
}
