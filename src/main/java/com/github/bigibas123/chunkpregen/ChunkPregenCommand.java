package com.github.bigibas123.chunkpregen;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;

public abstract class ChunkPregenCommand implements TabCompleter, CommandExecutor {

    private final ChunkPregen plugin;
    public final String name;

    public ChunkPregenCommand(ChunkPregen plugin, String name) {
        this.plugin = plugin;
        this.name = name;
    }

    protected ChunkPregen getPlugin() {
        return plugin;
    }


}
