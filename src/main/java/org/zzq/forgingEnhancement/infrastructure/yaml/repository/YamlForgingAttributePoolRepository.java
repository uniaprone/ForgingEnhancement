package org.zzq.forgingEnhancement.infrastructure.yaml.repository;

import org.zzq.forgingEnhancement.domain.config.ForgingAttributePoolConfig;
import org.zzq.forgingEnhancement.domain.reposity.IForgingAttributePoolRepository;
import org.zzq.forgingEnhancement.infrastructure.yaml.parser.YamlForgingAttributePoolParser;

import java.util.Map;

public class YamlForgingAttributePoolRepository implements IForgingAttributePoolRepository {
    private YamlForgingAttributePoolParser yamlForgingAttributePoolParser;
    private final Map<String, ForgingAttributePoolConfig> configChche;

    public YamlForgingAttributePoolRepository(YamlForgingAttributePoolParser yamlForgingAttributePoolParser) {
        this.yamlForgingAttributePoolParser = yamlForgingAttributePoolParser;
        this.configChche = yamlForgingAttributePoolParser.load();
        if (this.configChche == null) {
            throw new IllegalStateException("读取ForgingAttributePoolConfig失败");
        }
    }

    @Override
    public Map<String, ForgingAttributePoolConfig> getForgingAttributePool() {
        return configChche;
    }
}
