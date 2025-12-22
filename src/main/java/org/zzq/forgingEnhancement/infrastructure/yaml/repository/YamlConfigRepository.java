package org.zzq.forgingEnhancement.infrastructure.yaml.repository;

import org.zzq.forgingEnhancement.domain.entity.ForgingConfig;
import org.zzq.forgingEnhancement.infrastructure.yaml.parser.YamlConfigParser;

public class YamlConfigRepository {
    private YamlConfigParser yamlConfigParser;
    private ForgingConfig configCache;

    public YamlConfigRepository(YamlConfigParser yamlConfigParser) {
        this.yamlConfigParser = yamlConfigParser;
        this.configCache = yamlConfigParser.load();
        if (this.configCache == null) {
            throw new IllegalStateException("读取Config失败");
        }
    }

    public ForgingConfig getForgingConfig(){
        return configCache;
    }

    public ForgingConfig reload(){
        ForgingConfig forgingConfig = yamlConfigParser.reload();
        forgingConfig.notifyObservers();
        return forgingConfig;
    }
}
