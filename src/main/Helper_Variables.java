package main;

import com.wasteofplastic.askyblock.ASkyBlockAPI;
import org.bukkit.Location;
import org.bukkit.block.Block;

import java.util.HashMap;
import java.util.UUID;

public class Helper_Variables
{
    // Variable to refernce main class
    public Stacker_Main plugin = Stacker_Main.getPluginInstance();

    public static HashMap<Location, Integer> blockStacked = new HashMap<>();

    public ASkyBlockAPI aSkyBlockAPI = ASkyBlockAPI.getInstance();
    public String permission = "blockstack.use.activated";

    public HashMap<UUID, Block> stackedBlock = new HashMap<>();
    public HashMap<UUID, Integer> blockCheck = new HashMap<>();
    public String[] stackableBlocks = { "DIAMOND_BLOCK", "LAPIS_BLOCK", "EMERALD_BLOCK", "REDSTONE_BLOCK", "IRON_BLOCK", "COAL_BLOCK", "IRON_BLOCK", "GOLD_BLOCK", "DRAGON_EGG"};
}
