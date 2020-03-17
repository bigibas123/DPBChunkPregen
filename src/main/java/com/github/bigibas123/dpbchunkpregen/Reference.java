package com.github.bigibas123.dpbchunkpregen;

public class Reference {
        public static int blockSize = 3;

        public static int minTPS = 15;
        public static int runEvery = 20;

        public static int reportMuliplier = 10;

        public static int maxMemUsage = 90;

        public static String string() {
                return "Reference{" +
                        "blockSize=" + blockSize +
                        ", minTPS=" + minTPS +
                        ", runEvery=" + runEvery +
                        ", reportMuliplier=" + reportMuliplier +
                        ", maxMemUsage=" + maxMemUsage +
                        '}';
        }

}
