package com.github.bigibas123.chunkpregen;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class PregenCommand extends ChunkPregenCommand {

    public PregenCommand(ChunkPregen plugin) {
        super(plugin, "pregen");
    }


    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1) {
            printHelp(sender);
            return true;
        }
        switch (args[0].toLowerCase()) {
            case "reload":
                getPlugin().reloadConfig();
                Config.load(getPlugin().getConfig());
                Logger.info(Reference.string());
                break;

            case "status":
                Logger.info(ChunkPregen.runner.getStatus() + " with " + ChunkPregen.runner.workerSize() + " workers generating " + ChunkPregen.runner.queueSize() + " chunks");
                break;

            case "pause":
                ChunkPregen.runner.pause();
                break;

            case "resume":
                ChunkPregen.runner.resume();
                break;

            case "square":
                String world = args[1];
                int arg0 = Integer.parseInt(args[2]);
                int arg1 = Integer.parseInt(args[3]);
                int arg2 = Integer.parseInt(args[4]);
                int arg3 = Integer.parseInt(args[5]);
                World w = Bukkit.getWorld(world);

                if (w == null) {
                    sender.sendMessage("world is not found:" + world);
                    return false;
                }
                int startChunkX = arg0 / 16;
                int startChunkZ = arg1 / 16;
                int endChunkX = arg2 / 16;
                int endChunkZ = arg3 / 16;
                ChunkPregen.runner.addToQueue(w, startChunkX, startChunkZ, endChunkX, endChunkZ);
                break;

            default:
                printHelp(sender);
                break;
        }
        return true;
    }

    private void printHelp(CommandSender sender) {
        sender.sendMessage(ChatColor.YELLOW + "===PREGEN COMMAND HELP===");
        sender.sendMessage("/pregen square <world> <startX> <startZ> <endX> <endZ>");
        sender.sendMessage("/pregen reload");
        sender.sendMessage("/pregen status");
        sender.sendMessage("/pregen pause");
        sender.sendMessage("/pregen resume");
    }

    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        ArrayList<String> suggestions = new ArrayList<>();
        if (args.length == 1) {
            if ("reload".startsWith(args[0])) {
                suggestions.add("reload");
            }
            if ("square".startsWith(args[0])) {
                suggestions.add("square");
            }
            if ("status".startsWith(args[0])) {
                suggestions.add("status");
            }
            if ("pause".startsWith(args[0])) {
                suggestions.add("status");
            }
            if ("resume".startsWith(args[0])) {
                suggestions.add("status");
            }
        } else if (args.length > 1 && "square".equalsIgnoreCase(args[0])) {
            Bukkit.getWorlds().forEach(world -> {
                if (world.getName().startsWith(args[0])) {
                    suggestions.add(world.getName());
                }
            });
        }
        return suggestions;
    }


}
