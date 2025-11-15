package org.zzq.forgingEnhancement.managers;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static java.util.UUID.fromString;

public class PlayerSettingManager {
    private Plugin plugin;
    private File playerSettingFile;
    private FileConfiguration fileConfiguration;
    private Map<UUID, Boolean> playerSettingMap = new HashMap<>();
    public PlayerSettingManager(Plugin plugin){
        this.plugin = plugin;
        loadPlayerSettingConfig();
    }

    public boolean getPlayerSetting(UUID playerUUID){
        if(playerSettingMap.containsKey(playerUUID)){
            return playerSettingMap.get(playerUUID);
        }
        return true;
    }

    public void toggleWorkBranchForging(UUID playerUUID, String name){
        if(playerSettingMap.containsKey(playerUUID)){
            boolean currentValue = playerSettingMap.get(playerUUID);
            playerSettingMap.put(playerUUID, !currentValue);
            savePlayerSetting(playerUUID, name, !currentValue);
        }else{
            playerSettingMap.put(playerUUID, false);
            savePlayerSetting(playerUUID, name, false);
        }
    }

    private void savePlayerSetting(UUID playerUUID, String name, boolean isEnable){
        fileConfiguration.set(String.valueOf(playerUUID), null);
        String UUIDPath = String.valueOf(playerUUID);
        fileConfiguration.set(UUIDPath + ".player", name);
        fileConfiguration.set(UUIDPath + ".isEnable", isEnable);
        try {
            fileConfiguration.save(playerSettingFile);
        } catch (IOException e) {
            plugin.getLogger().warning("保存玩家设置失败: " + e.getMessage());
        }
    }

    private void loadPlayerSettingConfig(){
        try{
            playerSettingFile = new File(plugin.getDataFolder(), "player_setting.yml");
            if(!playerSettingFile.exists()){
                boolean isSuccess = playerSettingFile.createNewFile();
                if(!isSuccess){
                    plugin.getLogger().warning("玩家设置文件创建失败");
                    return;
                }
            }
            fileConfiguration = YamlConfiguration.loadConfiguration(playerSettingFile);
            parsePlayerSetting();
            plugin.getLogger().info("玩家设置文件加载完成，共加载 " + playerSettingMap.size() + " 个玩家设置");
        } catch (IOException e) {
            plugin.getLogger().warning("玩家设置文件创建失败：" + e.getMessage());
        }
    }

    private void parsePlayerSetting(){
        playerSettingMap.clear();

        for(String UUID : fileConfiguration.getKeys(false)){
            String playerNamePath = UUID + ".player";
            String isEnablePath = UUID + ".isEnable";
            if(!fileConfiguration.contains(playerNamePath) || !fileConfiguration.contains(isEnablePath)){
                plugin.getLogger().info("UUID配置不全: " + UUID);
                continue;
            }
            boolean isEnable = fileConfiguration.getBoolean(isEnablePath);
            playerSettingMap.put(fromString(UUID), isEnable);
        }
    }

    public void reload(){
        loadPlayerSettingConfig();
    }
}
