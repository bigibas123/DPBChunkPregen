package com.github.bigibas123.chunkpregen;

import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

public class ChunkPregen extends JavaPlugin {

    public static GenRunner runner;

    @Override
    public void onLoad() {
        super.onLoad();
        Reference.plugin = this;
        Reference.logger = this.getLogger();
    }

    @Override
    public void onEnable() {
        super.onEnable();
        Config.load(getConfig());
        PregenCommand cmd = new PregenCommand(this);
        this.registerCommand(cmd);
        Config.save(getConfig());
        saveConfig();
        runner = new GenRunner(this, Bukkit.getScheduler());
        runner.start();
    }

    private void registerCommand(ChunkPregenCommand cmd) {
        PluginCommand command = this.getCommand(cmd.name);
        assert command != null;
        command.setTabCompleter(cmd);
        command.setExecutor(cmd);
    }


    @Override
    public void onDisable() {
        super.onDisable();
        runner.stop();
        Config.save(getConfig());
        saveConfig();
    }



}
