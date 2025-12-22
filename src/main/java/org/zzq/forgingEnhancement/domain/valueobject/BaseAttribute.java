package org.zzq.forgingEnhancement.domain.valueobject;

import java.util.List;
import java.util.Map;

public class BaseAttribute {
    private String name;
    private Map<String, Double> attributes;

    public BaseAttribute(String name, Map<String, Double> attributes) {
        this.name = name;
        this.attributes = attributes;
    }

    public Map<String, Double> getAttributes() {
        return attributes;
    }

    public String getName() {
        return name;
    }
}
