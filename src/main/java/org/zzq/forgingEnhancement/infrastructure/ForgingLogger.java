package org.zzq.forgingEnhancement.infrastructure;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.zzq.forgingEnhancement.domain.IForgingLogger;
import org.zzq.forgingEnhancement.domain.entity.ForgingConfig;
import org.zzq.forgingEnhancement.domain.IForgingConfigObserver;
import org.zzq.forgingEnhancement.infrastructure.minecraft.ForgingDataRepository;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class ForgingLogger implements IForgingLogger, IForgingConfigObserver {
    private Plugin plugin;
    private Logger logger;
    private File logFile;
    private ForgingConfig forgingConfig;
    private ForgingDataRepository forgingDataRepository;
    private String env;

    public ForgingLogger(Plugin plugin, ForgingConfig forgingConfig, ForgingDataRepository forgingDataRepository) {
        this.logger = plugin.getLogger();
        this.forgingConfig = forgingConfig;
        this.forgingDataRepository = forgingDataRepository;
        this.env = forgingConfig.getEnv();
        logFile = new File(plugin.getDataFolder(), "forging.log");
        ensureLogFileExists();
        this.forgingConfig.addObserver(this);
    }

    @Override
    public void logToFile(String message) {
        try(FileWriter fileWriter = new FileWriter(logFile, true)) {
            fileWriter.write(message + "\n");
            fileWriter.flush();
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to write to log file: " + e.getMessage());
        }
    }

    @Override
    public void debug(String message) {
        if("DEBUG".equalsIgnoreCase(this.env.trim())){  // 去除空格并忽略大小写
            String output = "[DEBUG] " + message;
            this.logger.info(output);
            logToFile(output);
        }
    }


    @Override
    public void info(String message) {
        if(this.env.equals("PRODUCTION")){
            String output = "[INFO] " + message;
            this.logger.info(output);
            logToFile(output);
        }
    }

    public void info(String player, String message) {
        String output = formatLog("INFO", message);
        this.logger.info(output);
        logToFile(output);
    }

    private void ensureLogFileExists() {
        if (!logFile.exists()) {
            try {
                logFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Failed to create log file: " + e.getMessage());
            }
        }
    }

    @Override
    public void onConfigUpdate(ForgingConfig forgingConfig) {
        this.env = forgingConfig.getEnv();
    }

    public void logForgingResult(String type, Player player, ItemStack item) {
        // 获取物品信息
        String itemName = getItemDisplayName(item);
        String itemType = item.getType().name();
        String loreText = getItemLoreText(item);

        // 格式化日志
        String logMessage = String.format(
                "[%s锻造结果] [玩家: %s] [类型: %s] [名称: %s] [属性: %s]",
                type,
                (player != null ? player.getName() : "SYSTEM"),
                itemType,
                itemName.isEmpty() ? "未命名" : itemName,
                loreText.isEmpty() ? "无" : loreText
        );

        String output = formatLog("INFO", logMessage);
        if(forgingDataRepository.getForgingItemLevel(item.getItemMeta()) >= 2){ this.logger.info(output); }
        logToFile(output);
    }

    // 辅助方法：获取物品显示名称
    private String getItemDisplayName(ItemStack item) {
        if (!item.hasItemMeta()) return "";
        ItemMeta meta = item.getItemMeta();
        return meta.hasDisplayName()
                ? PlainTextComponentSerializer.plainText().serialize(meta.displayName())
                : "";
    }

    // 辅助方法：获取物品lore文本
    private String getItemLoreText(ItemStack item) {
        List<Component> lores = item.lore();
        if (lores == null) return "";

        return lores.stream()
                .map(component -> PlainTextComponentSerializer.plainText().serialize(component))
                .collect(Collectors.joining(" | "));
    }


    private String formatLog(String level, String message) {
        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        return String.format("[%s] [%s] %s",
                timestamp,
                level,
                message);
    }

}
