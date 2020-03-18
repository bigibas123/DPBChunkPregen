package com.github.bigibas123.dpbchunkpregen;

import org.bukkit.configuration.file.FileConfiguration;

public class Config {

    public static void load(FileConfiguration configuration) {
        Reference.blockSize = configuration.getInt("blocksize", 10);
        Reference.minTPS = configuration.getInt("mintps", 15);
        Reference.runEvery = configuration.getInt("runevery", 10);
        Reference.reportMuliplier = configuration.getInt("reportmultiplier", 10);
        Reference.maxMemUsage = configuration.getInt("maxmemusage", 90);
        Reference.threads = configuration.getInt("threads", 10);
    }

    public static void save(FileConfiguration configuration) {
        configuration.set("blocksize", Reference.blockSize);
        configuration.set("mintps", Reference.minTPS);
        configuration.set("runevery", Reference.runEvery);
        configuration.set("reportmultiplier", Reference.reportMuliplier);
        configuration.set("maxmemusage", Reference.maxMemUsage);
        configuration.set("threads", Reference.threads);
    }

}
