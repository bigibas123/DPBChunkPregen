package com.github.bigibas123.dpbchunkpregen;

import org.bukkit.plugin.Plugin;

import java.util.logging.Logger;

public class Reference {

    public static int blockSize;

    public static int minTPS;
    public static int runEvery;

    public static int reportMuliplier;

    public static int maxMemUsage;
    public static int threads;

    public static Plugin plugin;
    public static Logger logger;

    public static String string() {
        return "Reference{" +
                "blockSize=" + blockSize +
                ", minTPS=" + minTPS +
                ", runEvery=" + runEvery +
                ", reportMuliplier=" + reportMuliplier +
                ", maxMemUsage=" + maxMemUsage +
                ", threads=" + threads +
                '}';
    }

}
