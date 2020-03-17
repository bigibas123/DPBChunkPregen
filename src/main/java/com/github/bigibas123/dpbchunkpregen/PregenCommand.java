package com.github.bigibas123.dpbchunkpregen;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.ArrayList;
import java.util.List;

public class PregenCommand extends DPBChunkPregenCommand {

    public PregenCommand(DPBChunkPregen plugin) {
        super(plugin, "pregen");
    }


    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1) {
            String subcmd = args[0];
            if (subcmd.equalsIgnoreCase("reload")) {
                getPlugin().reloadConfig();
                Config.load(getPlugin().getConfig());
                sender.sendMessage("Config reloaded");
                sender.sendMessage(Reference.string());
            }else{
                sender.sendMessage(ChatColor.RED+"Unknown subcommand, either use reload or <world> <startx> <startz> <endx> <endz>");
            }
            return true;
        } else if (args.length < 5) {
            return false;
        } else {

            try {
                BukkitScheduler scheduler = Bukkit.getScheduler();

                String world = args[0];
                int arg0 = Integer.parseInt(args[1]);
                int arg1 = Integer.parseInt(args[2]);
                int arg2 = Integer.parseInt(args[3]);
                int arg3 = Integer.parseInt(args[4]);
                World w = Bukkit.getWorld(world);

                if (w == null) {
                    sender.sendMessage("world is not found:" + world);
                    return false;
                }
                int startChunkX = arg0 / 16;
                int startChunkZ = arg1 / 16;
                int endChunkX = arg2 / 16;
                int endChunkZ = arg3 / 16;


                GenRunner runner = new GenRunner(this, w, startChunkX, startChunkZ, endChunkX, endChunkZ, scheduler, sender);


                return true;
            } catch (NumberFormatException e) {
                sender.sendMessage("one of the numbers is wrong");
                return false;
            }
        }

    }

    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        ArrayList<String> suggestions = new ArrayList<>();
        if (args.length == 1) {
            Bukkit.getWorlds().forEach(world -> {
                if (world.getName().startsWith(args[0])) {
                    suggestions.add(world.getName());
                }
            });
            if ("reload".startsWith(args[0])) {
                suggestions.add("reload");
            }
        }
        return suggestions;
    }


}
