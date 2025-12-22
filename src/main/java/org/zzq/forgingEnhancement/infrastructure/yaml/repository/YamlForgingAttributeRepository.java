package org.zzq.forgingEnhancement.infrastructure.yaml.repository;

import org.zzq.forgingEnhancement.domain.aggregateroot.ForgingAttributeConfig;
import org.zzq.forgingEnhancement.domain.reposity.IForgingAttributeRepository;
import org.zzq.forgingEnhancement.domain.valueobject.ForgingAttributeValue;
import org.zzq.forgingEnhancement.infrastructure.yaml.parser.YamlForgingAttributeParser;

import java.util.Map;

public class YamlForgingAttributeRepository implements IForgingAttributeRepository {
    private final YamlForgingAttributeParser yamlForgingAttributeParser;
    private ForgingAttributeConfig configCache;
    public YamlForgingAttributeRepository(YamlForgingAttributeParser yamlForgingAttributeParser){
        this.yamlForgingAttributeParser = yamlForgingAttributeParser;
        configCache = yamlForgingAttributeParser.load();
        if (this.configCache == null) {
            throw new IllegalStateException("读取ForgingAttributeConfig失败");
        }
    }
    @Override
    public ForgingAttributeConfig getForgingAttributeConfig() {
        return configCache;
    }

    @Override
    public Map<String, ForgingAttributeValue> getForgingAttributeMap() {
        return configCache.getForgingAttributeValueMap();
    }

    @Override
    public void reload() {
        configCache = yamlForgingAttributeParser.load();
        if (this.configCache == null) {
            throw new IllegalStateException("读取ForgingAttributeConfig失败");
        }
    }
}
