package com.github.bigibas123.dpbchunkpregen;

import io.papermc.lib.PaperLib;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

public class DPBChunkPregen extends JavaPlugin {

    @Override
    public void onEnable() {
        super.onEnable();
        PregenCommand cmd = new PregenCommand(this);
        this.registerCommand(cmd);
        PaperLib.suggestPaper(this);
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
    }



}
