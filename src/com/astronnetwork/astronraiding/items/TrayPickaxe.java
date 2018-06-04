package com.astronnetwork.astronraiding.items;

import com.astronnetwork.astronraiding.util.Util;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;


public class TrayPickaxe extends RaidingItem
{

    public TrayPickaxe() {
        super("traypickaxe");
        this.getAliases().addAll(Arrays.asList("traypick", "wallpick", "wallpickaxe"));
    }

    @Override
    public ItemStack getItem()
    {
        return Util.createItem(Material.DIAMOND_PICKAXE, "§5§lTray Pickaxe", "§7§oBuild all the walls! (like trump)");
    }


    @EventHandler
    public void onBlockBreak(BlockBreakEvent event)
    {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        ItemStack item = player.getItemInHand();
        String direction = Util.getDirection(player);
        if (event.isCancelled())
            return;

        if (!isItem(item))
            return;

        if(direction == null)
            return;

        if(direction.equals("X"))
            breakX(block, player);
        if(direction.equals("Z"))
            breakZ(block, player);

        event.setCancelled(true);
        item.setDurability((short)(item.getDurability() + 1));
    }

    public void breakX(Block block, Player player)
    {
        Block newBlock;
        for(int zOff = -1; zOff <= 1; ++zOff)
        {
            newBlock = block.getRelative(0, 0, zOff);
            if(Util.canBuild(block.getLocation(), player)) {
                if (!block.equals(newBlock))
                    newBlock.breakNaturally();
            }
        }
    }

    public void breakZ(Block block, Player player)
    {
        Block newBlock;
        for(int xOff = -1; xOff <= 1; ++xOff)
        {
            newBlock = block.getRelative(xOff, 0, 0);
            if(Util.canBuild(block.getLocation(), player)) {
                if (!block.equals(newBlock))
                    newBlock.breakNaturally();
            }
        }
    }
}
