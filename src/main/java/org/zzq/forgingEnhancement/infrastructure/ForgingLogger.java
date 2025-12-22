package org.zzq.forgingEnhancement.infrastructure;

import org.bukkit.plugin.Plugin;
import org.zzq.forgingEnhancement.domain.IForgingLogger;
import org.zzq.forgingEnhancement.domain.entity.ForgingConfig;
import org.zzq.forgingEnhancement.domain.IForgingConfigObserver;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Logger;

public class ForgingLogger implements IForgingLogger, IForgingConfigObserver {
    private Plugin plugin;
    private Logger logger;
    private File logFile;
    private ForgingConfig forgingConfig;
    private String env;

    public ForgingLogger(Plugin plugin, ForgingConfig forgingConfig) {
        this.logger = plugin.getLogger();
        this.forgingConfig = forgingConfig;
        this.env = forgingConfig.getEnv();
        logFile = new File(plugin.getDataFolder(), "forging.log");
        ensureLogFileExists();
        this.forgingConfig.addObserver(this);
    }

    @Override
    public void logToFile(String message) {
        try{
            FileWriter fileWriter = new FileWriter(logFile, true);
            fileWriter.write(message + "\n");
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to write to log file: " + e.getMessage());
        }
    }

    @Override
    public void debug(String message) {
        if(this.env.equals("DEBUG")){
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
}
