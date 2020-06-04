package commands;

import listener.BlockStack_Listeners;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BlockRemove_Command extends BlockStack_Listeners implements CommandExecutor
{
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
    {
        if (sender instanceof Player)
        {
            Player player = (Player) sender;

            if (args.length > 0)
            {
                if (getArmorStand(player, getLookedAtBlock(player).getLocation()) != null)
                {
                    if (accessToBuild(player))
                    {
                        if (isInteger(args[0]))
                        {
                            int amount = Integer.parseInt(args[0]);
                            givePlayerStackedBlock(player, getLookedAtBlock(player), getArmorStand(player, getLookedAtBlock(player).getLocation()), amount);
                        }
                        else if (args[0].equalsIgnoreCase("all"))
                        {
                            int amount = getStackedBlockValue(player, getLookedAtBlock(player));
                            givePlayerStackedBlock(player, getLookedAtBlock(player), getArmorStand(player, getLookedAtBlock(player).getLocation()), amount);
                        }
                        else
                        {
                            player.sendMessage(ChatColor.RED + "Invalid usage! please enter a number! Example Usage: /Blockremove <amount> or /Blockremove all");
                        }
                    }
                }
                else
                {
                    player.sendMessage(ChatColor.RED + "You must be starring at a stacked block for this to work!");
                }
            }
            else
            {
                player.sendMessage(ChatColor.RED + "Not enough arguments! Example Usage: /Blockremove <amount> or /Blockremove all");
            }
        }
        return false;
    }
}
