package com.github.bigibas123.chunkpregen;

import org.bukkit.Chunk;
import org.bukkit.World;

import java.util.concurrent.CompletableFuture;

public class ChunkPos {

    private final int x;
    private final int z;
    private final World w;

    ChunkPos(int x, int z, World w) {
        this.x = x;
        this.z = z;
        this.w = w;
    }

    public CompletableFuture<Chunk> gen() {
        return w.getChunkAtAsync(x, z).whenComplete((chunk, throwable) -> {
            if (!chunk.isForceLoaded() && chunk.getPluginChunkTickets().size() <= 0) {
                chunk.unload(true);
            }
        });
    }

    public boolean isGenerated() {
        return w.isChunkGenerated(x, z);
    }

}
