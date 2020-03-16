package com.github.bigibas123.dpbchunkpregen;

import org.bukkit.configuration.file.FileConfiguration;

public class Config {

    public static void init(FileConfiguration configuration) {
        Reference.blockSize = configuration.getInt("blocksize", 10);
        Reference.minTPS = configuration.getInt("mintps", 15);
        Reference.runEvery = configuration.getInt("runevery", 20);
        Reference.reportMuliplier = configuration.getInt("reportmultiplier", 10);
    }

    public static void save(FileConfiguration configuration) {
        configuration.set("blocksize", Reference.blockSize);
        configuration.set("mintps", Reference.minTPS);
        configuration.set("runevery", Reference.runEvery);
        configuration.set("reportmultiplier", Reference.reportMuliplier);
    }

}
