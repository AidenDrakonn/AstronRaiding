package com.astronnetwork.astronraiding.items;

import com.astronnetwork.astronraiding.AstronRaiding;
import com.astronnetwork.astronraiding.util.Util;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class SandWand extends RaidingItem {

    public SandWand() {
        super("sandwand");
        this.getAliases().addAll(Arrays.asList("wand"));
    }

    @Override
    public ItemStack getItem() {
        return Util.createItem(Material.GOLD_HOE, "§6§lSandWand", Arrays.asList("§7§oDestroy all the sand/gravel in a pillar!","&7&oRight click to use!"));
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event)
    {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        Block block = event.getClickedBlock();

        if(!event.getPlayer().getItemInHand().equals(item))
            return;

        if(event.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;

        if (!event.hasItem() || !event.hasBlock())
            return;

        if (!isItem(item))
            return;

        if (!isValidMaterial(block.getType())) {
            event.setCancelled(true);
            player.sendMessage("§c§l(!)§c You cannot use this here!");
            return;
        }

        /* Could use a for loop for instant removal */
        new BukkitRunnable() {
            int y = block.getY()+1;

            @Override
            public void run() {
                Block nextBlock = player.getWorld().getBlockAt(block.getX(), --y, block.getZ());
                if (isValidMaterial(nextBlock.getType())) {
                    nextBlock.breakNaturally();
                } else {
                    cancel();
                }
            }
        }.runTaskTimer(AstronRaiding.getInstance(), 0, 2);
    }

    private boolean isValidMaterial(Material material) {
        return Arrays.asList(Material.SAND, Material.GRAVEL, Material.ANVIL).contains(material);
    }
}
