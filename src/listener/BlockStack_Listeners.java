package listener;

import main.Helper_Methods;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class BlockStack_Listeners extends Helper_Methods implements Listener
{
    @EventHandler (priority = EventPriority.HIGHEST)
    public void onPlace(PlayerInteractEvent event)
    {
        Player player = event.getPlayer();

        if (!event.isCancelled())
        {
            if (event.getClickedBlock() != null && player.getItemInHand() != null)
            {
                if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK))
                {
                    // If the player right clicks on a stackable
                    // block with the stackable block in their hand
                    if (doesContainBlock(event.getClickedBlock().getType()) &&
                        player.getItemInHand().getType().equals(event.getClickedBlock().getType()))
                    {
                        if (!player.hasPermission(permission))
                            return;

                        event.setCancelled(true);
                        ItemStack itemInHand = player.getItemInHand();
                        stackBlock(player, event.getClickedBlock());

                        if (itemInHand.getAmount() > 1)
                        {
                            itemInHand.setAmount(itemInHand.getAmount() - 1);
                            player.setItemInHand(itemInHand);
                        }
                        else
                        {
                            player.setItemInHand(new ItemStack(Material.AIR));
                        }
                    }
                    else if (doesContainBlock(event.getClickedBlock().getType()))
                    {
                        if (player.hasPermission(permission))
                        {
                            if (getArmorStand(player, event.getClickedBlock().getLocation()) != null)
                            {
                                if (!stackedBlock.containsKey(player.getUniqueId()))
                                {
                                    stackedBlock.put(player.getUniqueId(), event.getClickedBlock());
                                }

                                openBlockInventory(player);
                            }
                        }
                    }
                }
                else if (event.getAction().equals(Action.LEFT_CLICK_BLOCK))
                {
                    if (event.getClickedBlock().getType().equals(Material.DRAGON_EGG))
                    {
                        if (player.getWorld().getName().equalsIgnoreCase("end"))
                        {
                            event.getClickedBlock().setType(Material.AIR);
                            player.getWorld().dropItemNaturally(event.getClickedBlock().getLocation(), new ItemStack(Material.DRAGON_EGG));
                        }
                    }
                }
            }
        }
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onBreak(BlockBreakEvent event)
    {
        Player player = event.getPlayer();

        if (!event.isCancelled())
        {
            if (doesContainBlock(event.getBlock().getType()))
            {
                Entity entity = getArmorStand(player, event.getBlock().getLocation());

                if (entity != null)
                {
                    event.setCancelled(true);

                    if (player.hasPermission(permission))
                    {
                        if (player.isSneaking())
                        {
                            givePlayerStackedBlock(player, event.getBlock(), entity, 0);
                        }
                    }
                }
            }
        }
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onPlace(BlockPlaceEvent event)
    {
        Player player = event.getPlayer();
        Block block = event.getBlock();

        if (!event.isCancelled())
        {
            if (player.hasPermission(permission))
            {
                if (doesContainBlock(block.getType()))
                {
                    if (player.getItemInHand().getItemMeta().hasDisplayName() && doesMatch(player.getItemInHand()))
                    {
                        int value = getNumberFromString(player.getItemInHand().getItemMeta().getDisplayName());
                        spawnArmorStand(player.getWorld(), block.getLocation(), "&6&l" + value + "x&r &f" + renameBlockType(block.getType()));

                        ItemStack itemInHand = player.getItemInHand();

                        if (itemInHand.getAmount() > 1)
                        {
                            itemInHand.setAmount(itemInHand.getAmount() - 1);
                            player.setItemInHand(itemInHand);
                        }
                        else
                        {
                            player.setItemInHand(new ItemStack(Material.AIR));
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
            }
        }
    }

    @EventHandler (priority = EventPriority.LOWEST)
    public void onOpen(InventoryOpenEvent event)
    {
        Player player = (Player) event.getPlayer();

        if (event.getInventory().getTitle().equalsIgnoreCase("Add Blocks Here"))
        {
            if (!blockCheck.containsKey(player.getUniqueId()))
            {
                blockCheck.put(player.getUniqueId(), getTotalInventoryItems(event.getInventory(), stackedBlock.get(player.getUniqueId())));
            }
        }
    }

    @EventHandler (priority = EventPriority.LOWEST)
    public void onClose(InventoryCloseEvent event)
    {
        Player player = (Player) event.getPlayer();

        if (event.getInventory().getTitle().equalsIgnoreCase("Add Blocks Here"))
        {
            int value = getTotalInventoryItems(event.getInventory(), stackedBlock.get(player.getUniqueId()));
            int newValue = value + getStackedBlockValue(player, stackedBlock.get(player.getUniqueId()));

            if (blockCheck.containsKey(player.getUniqueId()))
            {
                if (value > blockCheck.get(player.getUniqueId()))
                {
                    addBlockAmount(player, stackedBlock.get(player.getUniqueId()) , newValue);
                }
            }

            for (ItemStack item : event.getInventory())
            {
                if (item != null)
                {
                    if (item.getType() != stackedBlock.get(player.getUniqueId()).getType())
                    {
                        stackedBlock.get(player.getUniqueId()).getWorld().dropItemNaturally(stackedBlock.get(player.getUniqueId()).getLocation(), item);
                        item.setType(Material.AIR);
                    }
                }
            }

            blockCheck.remove(player.getUniqueId());
            stackedBlock.remove(player.getUniqueId());
        }
    }

    @EventHandler (priority = EventPriority.LOWEST)
    public void onDragonEggMove(BlockFromToEvent event)
    {
        if (event.getBlock().getType() == Material.DRAGON_EGG)
        {
            event.setCancelled(true);
        }
    }

    @EventHandler (priority = EventPriority.LOWEST)
    public void onPhysicsEvent(BlockPhysicsEvent event)
    {
        if (event.getBlock().getType().equals(Material.DRAGON_EGG))
        {
            event.setCancelled(true);
        }
    }
}
