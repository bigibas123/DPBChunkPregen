package com.github.bigibas123.dpbchunkpregen;

import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

public class DPBChunkPregen extends JavaPlugin {

    @Override
    public void onEnable() {
        super.onEnable();
        Config.init(getConfig());
        PregenCommand cmd = new PregenCommand(this);
        this.registerCommand(cmd);
    }

    private void registerCommand(DPBChunkPregenCommand cmd) {
        PluginCommand command = this.getCommand(cmd.name);
        assert command != null;
        command.setTabCompleter(cmd);
        command.setExecutor(cmd);
    }


    @Override
    public void onDisable() {
        super.onDisable();
        Config.save(getConfig());
        saveConfig();
    }



}
