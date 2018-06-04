package com.astronnetwork.astronraiding.items;

import com.astronnetwork.astronraiding.AstronRaiding;
import com.astronnetwork.astronraiding.util.Util;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.math.BigDecimal;
import java.util.Arrays;

public class HarvesterHoe extends RaidingItem
{
    private String state;
    private Economy economy;
    public HarvesterHoe()
    {
        super("harvesterhoe");
        this.getAliases().addAll(Arrays.asList("hoe", "sellhoe", "sugarcanehoe"));
        state = "sell";
        economy = AstronRaiding.getInstance().getEconomy();
    }

    public ItemStack getItem()
    {
        return Util.createItem(Material.WOOD_HOE, "§2§lHarvester Hoe", "§7§oAutomatically collect or sell sugar cane!");
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event)
    {
        Player player = event.getPlayer();
        ItemStack item = player.getItemInHand();

        if(!isItem(item))
            return;

        if(!event.getPlayer().getItemInHand().equals(item))
            return;

        if(event.getAction() != Action.RIGHT_CLICK_AIR
        && event.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;


        changeState();
        player.sendMessage("§7§l(§b§lHarvester§7§l) §fMode changed to §2" + state);
        event.setCancelled(true);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event)
    {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        ItemStack item = player.getItemInHand();

        if(event.isCancelled())
            return;

        if(!isItem(item))
            return;

        if(block.getType() != Material.SUGAR_CANE_BLOCK)
            return;


        int amount = 0;
        event.setCancelled(true);
        Location currLoc = event.getBlock().getLocation();
        while (currLoc.getBlock().getType() == Material.SUGAR_CANE_BLOCK)
        {
            amount++;
            currLoc = new Location(currLoc.getWorld(), currLoc.getBlockX(), currLoc.getBlockY() + 1, currLoc.getBlockZ());
        }

        currLoc = new Location(currLoc.getWorld(), currLoc.getBlockX(), currLoc.getBlockY() - 1, currLoc.getBlockZ());
        while (currLoc.getBlockY() >= event.getBlock().getY())
        {
            currLoc.getBlock().setType(Material.AIR);
            currLoc = new Location(currLoc.getWorld(), currLoc.getBlockX(), currLoc.getBlockY() - 1, currLoc.getBlockZ());
        }

        if(state.equals("sell"))
        {
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(player.getUniqueId());
            sellItems(offlinePlayer, amount);
        }

        if(state.equals("collect"))
        {
            Util.givePlayerItem(player, new ItemStack(Material.SUGAR_CANE, amount));
        }
    }

    private void changeState()
    {
        if(state.equals("collect")) {
            state = "sell";
            return;
        }

        if(state.equals("sell")) {
            state = "collect";
            return;
        }
    }

    private void sellItems(OfflinePlayer player, int amount)
    {
        BigDecimal price = AstronRaiding.getInstance().getWorth(new ItemStack(Material.SUGAR_CANE, 1));
        double worth = price.doubleValue() * amount;

        economy.depositPlayer(player, worth);
    }



    /**
     *
     *  A harvester hoe is used to farm sugar cane, it will break the sugar can and add it directly to the players inventory.
     *
     *  We want 2 different modes:
     *
     *
     *   Mode #1: Destroy - Add the sugarcane to the players inventory.
     *
     *   Mode #2: Sell - Sell the sugarcane instantly when its broken.
     *
     *   Right Click to change the mode.
     *
     */

}
