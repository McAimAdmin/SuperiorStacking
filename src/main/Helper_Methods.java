package main;

import com.wasteofplastic.askyblock.Settings;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;

import java.util.*;

public class Helper_Methods extends Helper_Variables
{
    // Returns number from string
    public int getNumberFromString(String string)
    {
        int value = 0;

        for (int i = 0; i < string.length(); i++)
        {
            if (Character.isDigit(string.charAt(i)) && i != string.length() - 1)
            {
                if (string.charAt(i + 1) == 'x' || Character.isDigit(string.charAt(i + 1)))
                {
                    value = value * 10 + Character.getNumericValue(string.charAt(i));
                }
            }
        }

        return value;
    }

    // Returns boolean if string is integer or not
    public boolean isInteger(String string)
    {
        boolean isNumber = true;

        for (int i = 0; i < string.length(); i++)
        {
            if (!Character.isDigit(string.charAt(i)))
            {
                isNumber = false;
            }
        }

        return isNumber;
    }

    // Spawner armorstand and name it (Puts hologram above spawner)
    public void spawnArmorStand(World world, Location loc, String name)
    {
        Location spawnerLocation = new Location(world, loc.getX() + 0.5, loc.getY() - 0.7, loc.getZ() + 0.5);
        ArmorStand armorStand = world.spawn(spawnerLocation, ArmorStand.class);
        armorStand.setGravity(false);
        armorStand.setCanPickupItems(false);
        armorStand.setVisible(false);
        armorStand.setCustomName(ChatColor.translateAlternateColorCodes('&', name));
        armorStand.setCustomNameVisible(true);
    }

    // Rename armor stand (Spawner hologram name)
    public void renameArmorStand(Entity armorStand, String name)
    {
        armorStand.setCustomName(ChatColor.translateAlternateColorCodes('&', name));
    }

    // Return an armor stand variable at a location
    public Entity getArmorStand(Player player, Location loc)
    {
        Entity entity = null;
        loc = new Location(player.getWorld(), loc.getX() + 0.5, loc.getY() - 0.7, loc.getZ() + 0.5);

        for (Entity e : player.getNearbyEntities(5, 5, 5))
        {
            if (e instanceof ArmorStand)
            {
                if (e.getLocation().equals(loc))
                {
                    entity = e;
                    break;
                }
            }
        }

        return entity;
    }

    // Stack Block
    public void stackBlock(Player player, Block block)
    {
        Entity entity = getArmorStand(player, block.getLocation());

        if (entity == null)
        {
            if (block.getType().equals(Material.MOB_SPAWNER))
            {
                CreatureSpawner cs = (CreatureSpawner) block.getState();
                String spawnerName = cs.getSpawnedType().toString().toLowerCase();
                spawnerName = spawnerName.substring(0, 1).toUpperCase() + spawnerName.toLowerCase().substring(1);
                spawnArmorStand(block.getWorld(), block.getLocation(), "&6&l2x&r &f" + spawnerName + " Spawner");
            }
            else
            {
                spawnArmorStand(block.getWorld(), block.getLocation(), "&6&l2x&r &f" + renameBlockType(block.getType()));
            }

            blockStacked.put(block.getLocation(), 2);
        }
        else
        {
            String entityName = entity.getCustomName();
            int value;

            if (player.getItemInHand().getItemMeta().hasDisplayName() &&
                !player.getItemInHand().getItemMeta().getDisplayName().contains(ChatColor.translateAlternateColorCodes('&', "&e")))
            {
                if (doesMatch(player.getItemInHand()))
                {
                    int blockType = getNumberFromString(player.getItemInHand().getItemMeta().getDisplayName());
                    value = getNumberFromString(entityName) + blockType;
                }
                else
                {
                    return;
                }
            }
            else
            {
                value = getNumberFromString(entityName) + 1;
            }

            if (block.getType().equals(Material.MOB_SPAWNER))
            {
                CreatureSpawner cs = (CreatureSpawner) block.getState();
                String spawnerName = cs.getSpawnedType().toString().toLowerCase();
                spawnerName = spawnerName.substring(0, 1).toUpperCase() + spawnerName.toLowerCase().substring(1);
                renameArmorStand(entity, "&6&l" + value + "x&r &f" + spawnerName + " Spawner");
            }
            else
            {
                renameArmorStand(entity, "&6&l" + value + "x&r &f" + renameBlockType(block.getType()));
            }

            if (!blockStacked.containsKey(block.getLocation()))
            {
                blockStacked.put(block.getLocation(), value);
            }
            else
            {
                blockStacked.replace(block.getLocation(), value);
            }
        }
    }

    // Remove certain amount of block
    public void removeBlockAmount(Player player, Block block, int amount)
    {
        Entity entity = getArmorStand(player, block.getLocation());
        String entityName = entity.getCustomName();
        int value = getNumberFromString(entityName) - amount;

        if (value > 1)
        {
            renameArmorStand(entity, "&6&l" + value + "x&r &f" + renameBlockType(block.getType()));

            if (!blockStacked.containsKey(block.getLocation()))
            {
                blockStacked.put(block.getLocation(), value);
            }
            else
            {
                blockStacked.replace(block.getLocation(), value);
            }
        }
        else
        {
            if (value < 1)
            {
                block.setType(Material.AIR);
                blockStacked.remove(block.getLocation());
            }

            entity.remove();
        }
    }

    // Add amount to block stack
    public void addBlockAmount(Player player, Block block, int amount)
    {
        Entity entity = getArmorStand(player, block.getLocation());
        renameArmorStand(entity, "&6&l" + amount + "x&r &f" + renameBlockType(block.getType()));

        if (!blockStacked.containsKey(block.getLocation()))
        {
            blockStacked.put(block.getLocation(), amount);
        }
        else
        {
            blockStacked.replace(block.getLocation(), amount);
        }
    }

    // Open stacked block inventory
    public void openBlockInventory(Player player)
    {
        Inventory blockInventory = Bukkit.createInventory(null, 54, "Add Blocks Here");
        player.openInventory(blockInventory);
    }

    // Get amount of items in invnetory
    public int getTotalInventoryItems(Inventory inventory, Block block)
    {
        int value = 0;

        for (ItemStack item : inventory)
        {
            if (item != null)
            {
                if (item.getItemMeta().getDisplayName() != null)
                {
                    if (doesMatch(item))
                    {
                        value += getNumberFromString(item.getItemMeta().getDisplayName()) * item.getAmount();
                    }
                }
                else
                {
                    if (item.getType().equals(block.getType()))
                    {
                        value += item.getAmount();
                    }
                }
            }
        }

        return value;
    }

    // Checking if item stack matches
    public boolean doesMatch(ItemStack itemStack)
    {
        boolean match = false;

        if (itemStack.getItemMeta().hasEnchant(Enchantment.DURABILITY))
        {
            int level = itemStack.getEnchantmentLevel(Enchantment.DURABILITY);

            if (level == 6050)
                match = true;
        }

        return match;
    }

    // Getting block player looks at
    public Block getLookedAtBlock(Player player)
    {
        final Set<Material> set = null;
        final Block block = player.getTargetBlock(set, 5);
        return block;
    }

    // If they have access to build
    public boolean accessToBuild(Player player)
    {
        try
        {
            Material material = Material.AIR;


            Block airBlock = player.getTargetBlock(Collections.singleton(material), 1);


            BlockBreakEvent blockEvent = new BlockBreakEvent(airBlock, player);


            Bukkit.getServer().getPluginManager().callEvent((Event)blockEvent);


            if (!blockEvent.isCancelled())
            {

                return true;
            }



            blockEvent.setCancelled(true);
            return false;

        }
        catch (Exception e)
        {

            e.printStackTrace();
            return false;
        }
    }

    // Used to give player a stacked block
    public void givePlayerStackedBlock(Player player, Block block, Entity entity, int amount)
    {
        ItemStack item = new ItemStack(block.getType());
        ItemMeta meta = item.getItemMeta();
        meta.addEnchant(Enchantment.DURABILITY, 6050, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

        // If dropping the item from block break
        if (amount == 0)
        {
            blockStacked.remove(block.getLocation());
            meta.setDisplayName(entity.getCustomName());
            block.setType(Material.AIR);
            entity.remove();
        }

        // If the block is given to the player via command
        if (amount > 0)
        {
            int blockValue = getNumberFromString(entity.getName());

            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&6&l" + amount + "x&r &f" + renameBlockType(block.getType())));

            if (amount <= blockValue)
            {
                removeBlockAmount(player, block, amount);
            }
            else
            {
                player.sendMessage(ChatColor.RED + "Sorry, but you can't remove that many blocks!");
                return;
            }
        }

        item.setItemMeta(meta);
        entity.getWorld().dropItemNaturally(block.getLocation(), item);
    }

    // Used to make block type of name look better
    public String renameBlockType(Material material)
    {
        String blockName = material.toString().toLowerCase();
        blockName = blockName.substring(0, 1).toUpperCase() + material.toString().toLowerCase().substring(1);
        blockName = blockName.replace("_", " ");
        return blockName;
    }

    // Returns integer value from stacked block
    public int getStackedBlockValue(Player player, Block block)
    {
        return getNumberFromString(getArmorStand(player, block.getLocation()).getCustomName());
    }

    // Returns boolean if block matches one of the listed blocks
    public boolean doesContainBlock(Material material)
    {
        boolean doesContain = false;

        for (String string : stackableBlocks)
        {
            if (material.toString().equalsIgnoreCase(string))
            {
                doesContain = true;
                break;
            }
        }

        return doesContain;
    }


    // Returning block value at a specific location
    public double getBlockValueAtLocation(UUID uuid)
    {
        Material material = null;
        int blockValue = 0;

        int islandX = aSkyBlockAPI.getIslandOwnedBy(uuid).getMinX() / 2;
        int islandZ = aSkyBlockAPI.getIslandOwnedBy(uuid).getMinZ() / 2;

        Bukkit.broadcastMessage("IslandX for " + Bukkit.getPlayer(uuid) + " = " + islandX);
        Bukkit.broadcastMessage("IslandZ for " + Bukkit.getPlayer(uuid) + " = " + islandZ);

        if (!blockStacked.isEmpty())
        {
            for (Location loc : blockStacked.keySet())
            {
                if (loc.getX() >= islandX && loc.getZ() <= islandZ)
                {
                    material = loc.getBlock().getType();
                    blockValue += (blockStacked.get(loc));
                }
            }
        }

        if (blockValue > 1)
        {
            double levelCost = Settings.levelCost;
            double blockWorth = Settings.blockValues.get(new MaterialData(material));
            return ((blockValue - 1) * blockWorth) / levelCost;
        }
        else
        {
            return 0;
        }
    }
}
