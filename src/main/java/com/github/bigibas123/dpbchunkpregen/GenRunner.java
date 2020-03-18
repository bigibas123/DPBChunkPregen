package com.github.bigibas123.dpbchunkpregen;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;
import java.util.stream.IntStream;

class GenRunner {

    private final Plugin plugin;
    private final ArrayList<ChunkWorker> workers;
    private BukkitScheduler scheduler;
    private LinkedBlockingQueue<ChunkPos> queue;
    @Getter
    private GenRunner.Status status;
    private boolean statusUpdated;
    private int counter;
    private final Consumer<BukkitTask> task = this::run;

    public GenRunner(Plugin plugin, BukkitScheduler scheduler) {
        this.plugin = plugin;
        this.scheduler = scheduler;
        this.queue = new LinkedBlockingQueue<>();
        this.workers = new ArrayList<>();
        this.counter = 0;
    }

    public void addToQueue(World w, int startChunkX, int startChunkZ, int endChunkX, int endChunkZ) {
        IntStream.range(startChunkX, endChunkX)
                .forEach(x ->
                        IntStream.range(startChunkZ, endChunkZ)
                                .forEach(z -> this.addToQueue(new ChunkPos(x, z, w)))
                );
    }

    public void addToQueue(ChunkPos pos) {
        this.queue.add(pos);
    }

    private void run(BukkitTask bukkitTask) {

        updateStatus();

        if (statusUpdated) {
            switch (this.status) {
                case STOPPED:
                    Logger.warn("Stopping runners");
                    this.workers.stream().filter(ChunkWorker::isRunning).forEach(ChunkWorker::stop);
                    break;

                case MEMORY_PAUSED:
                case USER_PAUSED:
                    Logger.warn("Pausing runners");
                    this.workers.stream().filter(ChunkWorker::isRunning).forEach(ChunkWorker::pause);
                    break;

                case QUEUE_EMPTY:
                    Logger.info("Queue empty stopping runners");
                    this.workers.stream().filter(ChunkWorker::isRunning).forEach(ChunkWorker::stop);
                    break;

                case RUNNING:
                    Logger.info("Running...");
                    workers.stream().filter(ChunkWorker::isPaused).forEach(ChunkWorker::unPause);
                    break;
            }
            statusUpdated = false;
        }

        if (this.status == Status.RUNNING) {
            checkRunnerCount();
            if (counter >= Reference.reportMuliplier) {
                counter = 0;
                Logger.info("GenRunner " + getStatus() + " with " + this.queueSize() + " in queue and " + this.workerSize() + " workers");
            } else {
                counter++;
            }
        }


        if (this.status != Status.STOPPED && this.status != Status.USER_PAUSED) {
            reSchedule();
        } else {
            Logger.info("GenRunner pausing/shutting down");
        }
    }

    private void updateStatus() {

        if (this.status == Status.QUEUE_EMPTY) {
            if (queue.size() > 0) {
                this.status = Status.RUNNING;
                this.statusUpdated = true;
            }
        }

        if (this.status == Status.RUNNING) {
            if (queue.size() <= 0) {
                this.status = Status.QUEUE_EMPTY;
                this.statusUpdated = true;
            }
        }

        if (this.status == Status.RUNNING) {
            if (isTpsLow()) {
                Logger.warn("Tps is " + Bukkit.getTPS()[0] + " pausing runners...");
                this.status = Status.MEMORY_PAUSED;
                this.statusUpdated = true;
            } else if (memToHigh()) {
                long memp = (long) (((double) (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / Runtime.getRuntime().totalMemory()) * 100);
                Logger.warn("Mem usage is " + memp + " pausing runners...");
                this.status = Status.MEMORY_PAUSED;
                this.statusUpdated = true;
            }
        }

        if (this.status == Status.MEMORY_PAUSED) {
            if (!isTpsLow() && !memToHigh()) {
                Logger.info("Resuming runners...");
                this.status = Status.RUNNING;
                this.statusUpdated = true;
            }
        }

    }

    private void checkRunnerCount() {
        int diff = Reference.threads - this.workers.size();
        if (diff > 0) {
            Logger.info("Starting another " + diff + " runners");
            while (diff > 0) {
                ChunkWorker cw = new ChunkWorker(scheduler, plugin, queue, this.workers::remove);
                this.workers.add(cw);
                cw.go();
                diff--;
            }
        } else if (diff < 0) {
            Logger.info("Stopping " + diff * -1 + " runners");
            while (diff < 0) {
                this.workers.remove(0).stop();
                diff++;
            }
        }
    }

    public int queueSize() {
        return queue.size();
    }

    public int workerSize() {
        return workers.size();
    }

    private void reSchedule() {
        reSchedule(Reference.runEvery);
    }

    private boolean isTpsLow() {
        return Bukkit.getTPS()[0] < Reference.minTPS;
    }

    private boolean memToHigh() {
        return getMemPercent() > Reference.maxMemUsage;
    }

    private void reSchedule(int runEvery) {
        scheduler.runTaskLaterAsynchronously(this.plugin, task, runEvery);
    }

    private int getMemPercent() {
        long usedMem = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        double ratio = (double) usedMem / Runtime.getRuntime().totalMemory();
        return (int) (ratio * 100D);
    }

    public void start() {
        this.status = Status.RUNNING;
        this.statusUpdated = true;
        this.scheduler.runTaskAsynchronously(plugin, task);
    }

    public void stop() {
        this.status = Status.STOPPED;
        this.statusUpdated = true;
    }

    public void pause() {
        if (this.status != Status.USER_PAUSED) {
            this.status = Status.USER_PAUSED;
            Logger.info("Paused runner");
            this.statusUpdated = true;
        } else {
            Logger.warn("Tried to pause but runner was already paused");
        }
    }

    public void resume() {
        if (this.status == Status.USER_PAUSED) {
            this.status = Status.RUNNING;
            Logger.info("Paused runner");
            this.statusUpdated = true;
            reSchedule(0);
        } else {
            Logger.warn("Tried to resume runners but was already running!");
            Logger.debug("Current status: " + this.getStatus());
        }
    }

    private enum Status {
        RUNNING,
        MEMORY_PAUSED,
        USER_PAUSED,
        QUEUE_EMPTY,
        STOPPED,
    }

}
