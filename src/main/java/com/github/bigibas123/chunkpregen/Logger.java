package com.github.bigibas123.chunkpregen;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.util.logging.Level;

public class Logger {

    public static void error(String message) {
        log(Level.SEVERE, message);
    }

    public static void log(Level l, String message) {
        Bukkit.getScheduler().runTask(Reference.plugin, () -> Reference.logger.log(l, LevelColor.getString(l) + message));
    }

    public static void warn(String message) {
        log(Level.WARNING, message);
    }

    public static void info(String message) {
        log(Level.INFO, message);
    }

    public static void debug(String message) {
        log(Level.FINE, message);
    }

    public static void trace(String message) {
        log(Level.FINER, message);
    }

    private enum LevelColor {

        ERROR(Level.SEVERE, "ERROR", ChatColor.RED),
        WARN(Level.WARNING, "WARN", ChatColor.YELLOW),
        INFO(Level.INFO, "INFO", ChatColor.WHITE),
        DEBUG(Level.FINE, "DEBUG", ChatColor.GRAY),
        TRACE(Level.FINER, "TRACE", ChatColor.DARK_GRAY);

        private final Level l;
        private final String name;
        private final ChatColor color;

        LevelColor(Level l, String name, ChatColor color) {
            this.l = l;
            this.name = name;
            this.color = color;
        }

        public static String getString(Level l) {
            for (LevelColor lc: LevelColor.values()) {
                if (lc.l.equals(l)) {
                    return lc.toString();
                }
            }
            return INFO.toString();
        }

        @Override
        public String toString() {
            return color.toString();
        }

        public String s() {
            return this.toString();
        }
    }

}
