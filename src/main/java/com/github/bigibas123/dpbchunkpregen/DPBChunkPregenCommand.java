package com.github.bigibas123.dpbchunkpregen;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;

public abstract class DPBChunkPregenCommand implements TabCompleter, CommandExecutor {

    private final DPBChunkPregen plugin;
    public final String name;

    public DPBChunkPregenCommand(DPBChunkPregen plugin,String name) {
        this.plugin = plugin;
        this.name = name;
    }

    protected DPBChunkPregen getPlugin() {
        return plugin;
    }


}
