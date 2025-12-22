package org.zzq.forgingEnhancement.domain.services;

import org.zzq.forgingEnhancement.domain.entity.EnhancementResult;
import org.zzq.forgingEnhancement.domain.entity.ForgingAttribute;

import java.util.Iterator;
import java.util.List;

public class Recast {
    public void removeHasEngravedAttributes(EnhancementResult enhancementResult, List<ForgingAttribute> engravedForgingAttributes){
        List<ForgingAttribute> forgingAttributes = enhancementResult.getAttributeList();
        Iterator<ForgingAttribute> iterator = forgingAttributes.iterator();
        while (iterator.hasNext()){
            ForgingAttribute forgingAttribute = iterator.next();
            for(ForgingAttribute engravedForgingAttribute : engravedForgingAttributes){
                if(forgingAttribute.getName().equals(engravedForgingAttribute.getName())){
                    iterator.remove();
                }
            }
        }
    }

    public void addEngravedAttributes(EnhancementResult enhancementResult, List<ForgingAttribute> engravedForgingAttributes) {
        List<ForgingAttribute> forgingAttributes = enhancementResult.getAttributeList();
        forgingAttributes.addAll(0, engravedForgingAttributes);
    }
}
