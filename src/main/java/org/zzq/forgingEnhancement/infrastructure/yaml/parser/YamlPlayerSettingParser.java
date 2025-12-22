package org.zzq.forgingEnhancement.infrastructure.yaml.parser;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.zzq.forgingEnhancement.domain.entity.PlayerSetting;
import org.zzq.forgingEnhancement.domain.aggregateroot.PlayerSettingConfig;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class YamlPlayerSettingParser {
    private Plugin plugin;
    private File file;
    private FileConfiguration fileConfiguration;

    public YamlPlayerSettingParser(Plugin plugin){
        this.plugin = plugin;
    }

    public PlayerSettingConfig load(){
        try{
            file = new File(plugin.getDataFolder(), "player_setting.yml");
            if(!file.exists()){
                boolean isSuccess = file.createNewFile();
                if(!isSuccess){
                    plugin.getLogger().warning("玩家设置文件创建失败");
                }
            }
            fileConfiguration = YamlConfiguration.loadConfiguration(file);
            PlayerSettingConfig playerSettingMap = parse();
            plugin.getLogger().info("玩家设置文件加载完成");
            return playerSettingMap;
        } catch (IOException e) {
            plugin.getLogger().warning("玩家设置文件创建失败：" + e.getMessage());
            return null;
        }
    }

    public void save(String playerId, String name, boolean isEnable){
        fileConfiguration.set(playerId, null);
        fileConfiguration.set(playerId + ".player", name);
        fileConfiguration.set(playerId + ".isEnable", isEnable);
        try {
            fileConfiguration.save(file);
        } catch (IOException e) {
            plugin.getLogger().warning("保存玩家设置失败: " + e.getMessage());
        }
    }

    public void saves(Map<String, PlayerSetting> playerSettingMap){
        playerSettingMap.forEach((s, playerSetting) -> save(playerSetting.getUUID(), playerSetting.getName(), playerSetting.isEnable()));
    }

    private PlayerSettingConfig parse(){
        Map<String, PlayerSetting> playerSettingMap = new HashMap<>();
        for(String UUID : fileConfiguration.getKeys(false)){
            String playerNamePath = UUID + ".player";
            String isEnablePath = UUID + ".isEnable";
            if(!fileConfiguration.contains(playerNamePath) || !fileConfiguration.contains(isEnablePath)){
                plugin.getLogger().info("UUID配置不全: " + UUID);
                continue;
            }
            String name = fileConfiguration.getString(playerNamePath);
            boolean isEnable = fileConfiguration.getBoolean(isEnablePath);
            playerSettingMap.put(UUID, new PlayerSetting(UUID, name, isEnable));
        }
        return new PlayerSettingConfig(playerSettingMap);
    }

    public PlayerSettingConfig reload(){
        return load();
    }
}
