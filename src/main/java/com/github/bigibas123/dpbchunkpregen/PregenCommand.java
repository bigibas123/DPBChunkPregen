package com.github.bigibas123.dpbchunkpregen;

import io.papermc.lib.PaperLib;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class PregenCommand extends DPBChunkPregenCommand {

    public PregenCommand(DPBChunkPregen plugin) {
        super(plugin, "pregen");
    }


    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 5) {
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
                IntStream.range(startChunkX, endChunkX).filter(value -> value % 10 == 0).forEach(x ->
                        IntStream.range(startChunkZ, endChunkZ).filter(value -> value % 10 == 0).forEach(z -> {
                            sender.sendMessage("Scheduling:" + x + "," + z);
                            scheduler.runTaskAsynchronously(this.getPlugin(), () -> {
                                for (int x1 = x; x + 10 > x1; x1++) {
                                    for (int z1 = z; z + 10 > z1; z1++) {
                                        if (!PaperLib.isChunkGenerated(w, x, z)) {
                                            PaperLib.getChunkAtAsync(w, x, z).join();
                                        }
                                    }
                                }
                                scheduler.runTask(this.getPlugin(),() -> sender.sendMessage("Done generating:" + x + "," + z));

                            });
                        }));
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
        }
        return suggestions;
    }

}
