package org.zzq.forgingEnhancement.domain.valueobject;

import org.zzq.forgingEnhancement.utils.RandomUtil;

import java.util.List;

public class EquipmentAttribute{
    private String tool;
    private List<String> attributes;

    public EquipmentAttribute(String tool, List<String> attributes) {
        this.tool = tool;
        this.attributes = attributes;
    }

    public String getTool() {
        return tool;
    }

    public List<String> getAttributes() {
        return attributes;
    }
}
