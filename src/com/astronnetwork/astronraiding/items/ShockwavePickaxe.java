package com.astronnetwork.astronraiding.items;

import com.astronnetwork.astronraiding.AstronRaiding;
import com.astronnetwork.astronraiding.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.HashMap;

public class ShockwavePickaxe extends RaidingItem {

    private HashMap<Block, BlockFace> blockFace = new HashMap<>();

    public ShockwavePickaxe()
    {
        super("shockwavepickaxe");
        this.getAliases().addAll(Arrays.asList("3x3pickaxe", "shockwavepick", "3x3pick", "shockpick", "shockpickaxe"));
    }

    @Override
    public ItemStack getItem()
    {
        return Util.createItem(Material.DIAMOND_PICKAXE, "§2§lShockwave Pickaxe", "§7§oMine blocks in a 3x3 radius!");
    }

    @EventHandler
    public void getBlockFace(PlayerInteractEvent event)
    {
        if(event.getAction() != Action.LEFT_CLICK_BLOCK)
            return;

        BlockFace face = event.getBlockFace();
        if(!(face == BlockFace.UP || face == BlockFace.DOWN))
            return;

        Block block = event.getClickedBlock();
        blockFace.put(block, face);
        new BukkitRunnable() {
            @Override
            public void run()
            {
                if(blockFace.keySet().contains(block)) {
                    if(blockFace.get(block).equals(face)) {
                        blockFace.remove(block);
                    }
                }
            }
        }.runTaskLater(AstronRaiding.getInstance(), 30*20);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event)
    {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        ItemStack item = player.getItemInHand();
        String direction = Util.getDirection(player);
        BlockFace face = blockFace.get(block);

        if(event.isCancelled())
            return;

        if(!isItem(item))
            return;

        if(direction == null)
            return;

        if(face != null) {
            if (face == BlockFace.DOWN || face == BlockFace.UP) {
                breakXZ(block, player);
                return;
            }
        }

        if(direction.equals("X"))
        {
            breakXY(block, player);
            return;
        }

        if(direction.equals("Z"))
        {
            breakZY(block, player);
            return;
        }
    }


    public void breakXY(Block block, Player player)
    {
        Block newBlock;
        for(int yOff = -1; yOff <= 1; ++yOff)
        {
            for(int zOff = -1; zOff <= 1; ++zOff)
            {
                newBlock = block.getRelative(0, yOff, zOff);
                if(Util.canBuild(block.getLocation(), player)) {
                    if (!block.equals(newBlock))
                        newBlock.breakNaturally();
                }
            }
        }
    }

    public void breakZY(Block block, Player player)
    {
        Block newBlock;
        for(int xOff = -1; xOff <= 1; ++xOff)
        {
            for(int yOff = -1; yOff <= 1; ++yOff)
            {
                newBlock = block.getRelative(xOff, yOff, 0);
                if(Util.canBuild(block.getLocation(), player)) {
                    if (!block.equals(newBlock))
                        newBlock.breakNaturally();
                }
            }
        }
    }

    public void breakXZ(Block block, Player player)
    {
        Block newBlock;
        for(int xOff = -1; xOff <= 1; ++xOff)
        {
            for(int zOff = -1; zOff <= 1; ++zOff)
            {
                newBlock = block.getRelative(xOff, 0, zOff);
                if(Util.canBuild(block.getLocation(), player)) {
                    if (!block.equals(newBlock))
                        newBlock.breakNaturally();
                }
            }
        }
    }
}
