package com.github.bigibas123.dpbchunkpregen;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.IllegalPluginAccessException;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;

import java.util.LinkedList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;
import java.util.stream.IntStream;

class GenRunner {


    private PregenCommand pregenCommand;
    private World world;
    private BukkitScheduler scheduler;
    private final CommandSender sender;


    private LinkedBlockingQueue<ChunkPos> queue;

    private int counter = 0;

    public GenRunner(PregenCommand pregenCommand, World w, int startChunkX, int startChunkZ, int endChunkX, int endChunkZ, BukkitScheduler scheduler, CommandSender sender) {
        this.pregenCommand = pregenCommand;
        world = w;
        this.scheduler = scheduler;
        this.sender = sender;
        this.queue = new LinkedBlockingQueue<>();
        this.addToQueue(startChunkX, startChunkZ, endChunkX, endChunkZ);
        sender.sendMessage("Starting generation with: " + queue.size() + " chunks");
        this.scheduler.runTaskAsynchronously(pregenCommand.getPlugin(), task);
    }

    private void addToQueue(int startChunkX, int startChunkZ, int endChunkX, int endChunkZ) {
        IntStream.range(startChunkX, endChunkX)
                .forEach(x ->
                        IntStream.range(startChunkZ, endChunkZ)
                                .forEach(z -> queue.add(new ChunkPos(x, z)))
                );
    }

    private final Consumer<BukkitTask> task = this::accept;

    private void sync(Runnable task) {
        scheduler.runTask(pregenCommand.getPlugin(), task);
    }

    private void accept(BukkitTask bukkitTask) {
        if (Bukkit.getTPS()[0] < Reference.minTPS) {
            sender.sendMessage("Tps is " + Bukkit.getTPS()[0] + " waiting for it to be below " + Reference.minTPS);
            try {
                scheduler.runTaskLaterAsynchronously(pregenCommand.getPlugin(), task, 200);
            } catch (IllegalPluginAccessException e) {
                bukkitTask.cancel();
            }
            return;
        } else if (((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / Runtime.getRuntime().totalMemory()) * 100 > Reference.maxMemUsage) {
            sender.sendMessage("Memory usage is: " +
                    (((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / Runtime.getRuntime().totalMemory()) * 100) +
                    "% waiting for more to be freed");
        } else {
            LinkedList<CompletableFuture<Chunk>> list = new LinkedList<>();
            {
                IntStream.range(0, Reference.blockSize)
                        .forEach(ignored -> {
                                    ChunkPos c;
                                    if ((c = queue.poll()) != null) {
                                        if (!c.isGenerated(world)) {
                                            list.add(c.gen(world));
                                        }
                                    }
                                }
                        );
            }
            for (CompletableFuture<Chunk> cf: list) {
                if (!cf.isDone()) {
                    cf.join();
                }
            }
            counter++;
        }
        if (counter >= Reference.reportMuliplier) {
            counter = 0;
            sender.sendMessage("Generated another: " + Reference.blockSize * Reference.reportMuliplier);
        }
        try {
            if (queue.size() > 0) {
                scheduler.runTaskLaterAsynchronously(pregenCommand.getPlugin(), task, Reference.runEvery);
                return;
            }
        } catch (IllegalPluginAccessException e) {
            bukkitTask.cancel();
        }
        sender.sendMessage("Generating done/stopped");
    }

    private static class ChunkPos {

        private final int x;
        private final int z;

        private ChunkPos(int x, int z) {
            this.x = x;
            this.z = z;
        }

        public CompletableFuture<Chunk> gen(World w) {
            return w.getChunkAtAsync(x, z).whenComplete((chunk, throwable) -> chunk.unload());
        }

        public boolean isGenerated(World w) {
            return w.isChunkGenerated(x, z);
        }

    }


}
