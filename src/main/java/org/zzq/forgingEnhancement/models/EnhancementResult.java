package org.zzq.forgingEnhancement.models;

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
}
