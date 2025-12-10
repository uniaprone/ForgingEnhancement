package org.zzq.forgingEnhancement.domain.config;

import java.util.List;

public class ForgingAttributePoolConfig {
    private String tool;
    private List<String> attributes;

    public ForgingAttributePoolConfig(String tool, List<String> attributes) {
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
