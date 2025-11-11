package org.zzq.forgingEnhancement.model;

import java.util.List;

public class EnhancementResult {
    private int level;
    List<Attribute> attributeList;

    public EnhancementResult(int level, List<Attribute> attributeList) {
        this.level = level;
        this.attributeList = attributeList;
    }

    public List<Attribute> getAttributeList() {
        return attributeList;
    }
}
