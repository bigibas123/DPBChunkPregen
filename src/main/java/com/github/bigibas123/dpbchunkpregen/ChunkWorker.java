package com.github.bigibas123.dpbchunkpregen;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Chunk;
import org.bukkit.plugin.IllegalPluginAccessException;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.stream.IntStream;

@Getter
public class ChunkWorker {

    private Queue<ChunkPos> queue;
    private BukkitScheduler scheduler;
    private Plugin plugin;
    private Consumer<ChunkWorker> doneCallback;

    @Setter( AccessLevel.PRIVATE )
    private boolean stop;
    @Setter( AccessLevel.PRIVATE )
    private boolean paused;

    private final Consumer<BukkitTask> task = (bt) -> {
        if (this.isStop()) {
            bt.cancel();
            return;
        }
        if (this.isPaused()) {
            reSchedule(300);
            return;
        }

        ArrayList<CompletableFuture<Chunk>> list = new ArrayList<>();
        IntStream.range(0, Reference.blockSize).forEach(ignored -> {
            ChunkPos c;
            if ((c = queue.poll()) != null) {
                if (!c.isGenerated()) {
                    list.add(c.gen());
                }
            }
        });
        for (CompletableFuture<Chunk> cf: list) {
            if (!cf.isDone()) {
                cf.join();
            }
        }
        if (queue.size() > 0) {
            reSchedule();
            return;
        }
        doneCallback.accept(this);
    };


    public ChunkWorker(BukkitScheduler scheduler, Plugin plugin, Queue<ChunkPos> queue, Consumer<ChunkWorker> doneCallback) {
        this.scheduler = scheduler;
        this.plugin = plugin;
        this.queue = queue;
        this.doneCallback = doneCallback;
    }

    public void go() {
        this.setPaused(false);
        this.setStop(false);
        reSchedule();
    }

    private void reSchedule() {
        reSchedule(Reference.runEvery);
    }

    private void reSchedule(int ticks) {
        try {
            scheduler.runTaskLaterAsynchronously(plugin, task, ticks);
        } catch (IllegalPluginAccessException e) {
            this.stop();
            this.getDoneCallback().accept(this);
        }
    }

    public void stop() {
        this.setStop(true);
    }

    public boolean isRunning() {
        return !this.stop && !this.paused;
    }

    public void unPause() {
        this.setPaused(false);
    }

    public void pause() {
        this.setPaused(true);
    }

}
