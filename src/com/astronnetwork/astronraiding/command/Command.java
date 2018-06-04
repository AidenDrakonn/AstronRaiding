package com.astronnetwork.astronraiding.command;

import com.astronnetwork.astronraiding.items.RaidingItem;
import com.astronnetwork.astronraiding.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

public class Command implements CommandExecutor
{
    @Override
    public boolean onCommand(CommandSender commandSender, org.bukkit.command.Command cmd, String label, String[] args) {
        if (!cmd.getLabel().equalsIgnoreCase("astronraiding"))
            return true;

        if(commandSender instanceof Player)
        {
            Player player = (Player)commandSender;
            if(!(player.hasPermission("astronraiding.give")))
            {
                player.sendMessage("§c§l(!)§c No permission");
                return true;
            }

            if(!(args.length == 3 || args.length == 4))
            {
                player.sendMessage("§c§l(!)§c Too many or two few arguments try /astronraiding give (player) (item) [amount]");
                return true;
            }

            if(!args[0].equalsIgnoreCase("give")) {
                player.sendMessage("§c§l(!)§c Invalid command, try /astronraiding give (player) (item) [amount]");
                return true;
            }

            Player target = Bukkit.getPlayer(args[1]);
            if(target == null) {
                player.sendMessage("§c§l(!)§c " + args[1] + " is not online");
                return true;
            }

            RaidingItem raidingItem = RaidingItem.getRaidingItem(args[2]);
            if(raidingItem == null) {
                player.sendMessage("§c§l(!)§c Defined item is not available, was it spelt correctly?");
                return true;
            }

            ItemStack item = raidingItem.getItem();

            if(args.length == 4 && Util.isInt(args[3]))
                item.setAmount(Integer.valueOf(args[3]));

            Util.givePlayerItem(target, item);
            target.sendMessage("§f§lYou just got a " + item.getItemMeta().getDisplayName() + "§f§l!");
            return true;
        }

        if(!(args.length == 3 || args.length == 4))
            return true;

        if(!args[0].equalsIgnoreCase("give"))
            return true;

        Player target = Bukkit.getPlayer(args[1]);
        if(target == null)
            return true;

        ItemStack item = RaidingItem.getRaidingItem(args[2]).getItem();
        if(item == null)
            return true;

        if(args.length == 4 && Util.isInt(args[3]))
            item.setAmount(Integer.valueOf(args[3]));

        Util.givePlayerItem(target, item);
        target.sendMessage("§f§lYou just got a " + item.getItemMeta().getDisplayName() + "§f§l!");
        return true;
    }

}
