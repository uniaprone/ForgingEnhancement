package org.zzq.forgingEnhancement.infrastructure.yaml.repository;

import org.zzq.forgingEnhancement.domain.aggregateroot.ForgingAttributePoolConfig;
import org.zzq.forgingEnhancement.domain.reposity.IForgingAttributePoolRepository;
import org.zzq.forgingEnhancement.domain.valueobject.EquipmentAttribute;
import org.zzq.forgingEnhancement.infrastructure.yaml.parser.YamlForgingAttributePoolParser;

import java.util.List;
import java.util.Map;

public class YamlForgingAttributePoolRepository implements IForgingAttributePoolRepository {
    private YamlForgingAttributePoolParser yamlForgingAttributePoolParser;
    private ForgingAttributePoolConfig configCache;

    public YamlForgingAttributePoolRepository(YamlForgingAttributePoolParser yamlForgingAttributePoolParser) {
        this.yamlForgingAttributePoolParser = yamlForgingAttributePoolParser;
        this.configCache = yamlForgingAttributePoolParser.load();
        if (this.configCache == null) {
            throw new IllegalStateException("读取ForgingAttributePoolConfig失败");
        }
    }

    @Override
    public ForgingAttributePoolConfig getForgingAttributePool() {
        return configCache;
    }

    @Override
    public List<String> getForgableEquipmentList() {
        return configCache.getForgeableEquipments();
    }

    @Override
    public Map<String, EquipmentAttribute> getEquipmentAttributesMap() {
        return configCache.getEquipmentAttributesMap();
    }

    @Override
    public void reload() {
        this.configCache = yamlForgingAttributePoolParser.load();
        if (this.configCache == null) {
            throw new IllegalStateException("读取ForgingAttributePoolConfig失败");
        }
    }
}
