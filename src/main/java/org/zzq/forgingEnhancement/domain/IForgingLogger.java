package org.zzq.forgingEnhancement.domain;

public interface IForgingLogger {
    void logToFile(String message);
    void debug(String message);
    void info(String message);
    void info(String player, String message);
}
