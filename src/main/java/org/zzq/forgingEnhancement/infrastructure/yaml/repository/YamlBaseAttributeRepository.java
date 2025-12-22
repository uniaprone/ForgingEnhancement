package org.zzq.forgingEnhancement.infrastructure.yaml.repository;

import org.zzq.forgingEnhancement.domain.reposity.IBaseAttributeRepository;
import org.zzq.forgingEnhancement.domain.aggregateroot.BaseAttributeConfig;
import org.zzq.forgingEnhancement.domain.valueobject.BaseAttribute;
import org.zzq.forgingEnhancement.infrastructure.yaml.parser.YamlBaseAttributeParser;

import java.util.Map;

public class YamlBaseAttributeRepository implements IBaseAttributeRepository {
    private YamlBaseAttributeParser yamlBaseAttributeParser;
    private BaseAttributeConfig cache;

    public YamlBaseAttributeRepository(YamlBaseAttributeParser yamlBaseAttributeParser) {
        this.yamlBaseAttributeParser = yamlBaseAttributeParser;
        this.cache = yamlBaseAttributeParser.load();
        if (this.cache == null) {
            throw new IllegalStateException("读取BaseAttribute失败");
        }
    }

    public BaseAttributeConfig getBaseAttributeConfig(){
        return cache;
    }

    public Map<String, BaseAttribute> getBaseAttributeMap(){
        return cache.getBaseAttributeConfig();
    }

    @Override
    public void reload() {
        this.cache = yamlBaseAttributeParser.load();
        if (this.cache == null) {
            throw new IllegalStateException("读取BaseAttribute失败");
        }
    }
}
