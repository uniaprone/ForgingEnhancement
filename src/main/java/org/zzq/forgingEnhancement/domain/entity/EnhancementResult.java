package org.zzq.forgingEnhancement.domain.entity;

import java.util.Iterator;
import java.util.List;

public class EnhancementResult {
    private int level;
    List<ForgingAttribute> forgingAttributeList;

    public EnhancementResult(int level, List<ForgingAttribute> forgingAttributeList) {
        this.level = level;
        this.forgingAttributeList = forgingAttributeList;
    }

    public List<ForgingAttribute> getAttributeList() {
        return forgingAttributeList;
    }

    public int getLevel() {
        return level;
    }

    public EnhancementResult addEngravedResult(List<ForgingAttribute> forgingAttributes){
        forgingAttributeList.addAll(forgingAttributes);
        return this;
    }

    public EnhancementResult removeUnengravedAttributes(){
        Iterator<ForgingAttribute> iterator = forgingAttributeList.iterator();
        while (iterator.hasNext()){
            ForgingAttribute forgingAttribute = iterator.next();
            if(!forgingAttribute.isEngraved()){
                iterator.remove();
            }
        }
        return this;
    }
}
