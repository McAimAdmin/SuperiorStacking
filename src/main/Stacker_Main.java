package main;

import commands.BlockRemove_Command;
import listener.BlockStack_Listeners;
import listener.Island_Top_Listeners;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class Stacker_Main extends JavaPlugin
{
    // Store main class reference
    private static Stacker_Main stackerMain;

    // Return instance of plugin
    public static Stacker_Main getPluginInstance() { return stackerMain; }

    public void onEnable()
    {
        // Set instance of main class on enable
        stackerMain = this;

        // Registering listeners
        getServer().getPluginManager().registerEvents(new BlockStack_Listeners(), this);
        getServer().getPluginManager().registerEvents(new Island_Top_Listeners(), this);

        // Registered Commands
        getCommand("blockremove").setExecutor(new BlockRemove_Command());

        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                saveDefaultConfig();

                if (!getConfig().getStringList("stackedBlocks").isEmpty())
                {
                    for (String string : getConfig().getStringList("stackedBlocks"))
                    {
                        int value = Integer.parseInt(string.substring(string.indexOf(";") + 1));
                        Location location = fromString(string.substring(0, string.indexOf(";")));
                        Helper_Methods.blockStacked.put(location, value);
                    }
                }

                saveConfig();

                this.cancel();
            }

        }.runTaskLater(this, 20L);
    }

    public void onDisable()
    {
        getConfig().set("stackedBlocks", "");

        if (!Helper_Methods.blockStacked.isEmpty())
        {
            for (Location location : Helper_Methods.blockStacked.keySet())
            {
                List<String> list = getConfig().getStringList("stackedBlocks");
                list.add(toString(location) + ";" + Helper_Methods.blockStacked.get(location));
                getConfig().set("stackedBlocks", list);
            }
        }

        saveConfig();
    }

    private String format = "%world%><%x%><%y%><%z%";

    private String toString(Location loc)
    {
        String location = format
                .replaceAll("%world%", loc.getWorld().getName())
                .replaceAll("%x%", String.valueOf(loc.getX()))
                .replaceAll("%y%", String.valueOf(loc.getY()))
                .replaceAll("%z%", String.valueOf(loc.getZ()));

        return location;
    }

    private Location fromString(String loc)
    {
        String[] parts = loc.split("><");
        World world = Bukkit.getWorld(parts[0]);
        double xPos = Double.parseDouble(parts[1]);
        double yPos = Double.parseDouble(parts[2]);
        double zPos = Double.parseDouble(parts[3]);

        return new Location(world, xPos, yPos, zPos);
    }
}
