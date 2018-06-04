package com.astronnetwork.astronraiding.items;

import com.astronnetwork.astronraiding.AstronRaiding;
import com.astronnetwork.astronraiding.util.Util;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class ItemNameTag extends RaidingItem {

    List<Player> waiting = new ArrayList<>();
    private int maxCharacters;
    public ItemNameTag() {
        super("itemnametag");
        this.getAliases().addAll(Arrays.asList("nametag"));
        maxCharacters = AstronRaiding.getInstance().getConfig().getInt("itemnametag.characterlimit");
    }

    @Override
    public ItemStack getItem() {
        return Util.createItem(Material.NAME_TAG, "§6§lItem Nametag", Arrays.asList("§7§oRename and customize your equipment","&7&oRight click to use!") );
    }

    @EventHandler
    public void onPlayerRightClick(PlayerInteractEvent event) {
        ItemStack item = event.getItem();
        if (!isItem(item))
            return;

        if (event.getAction() != Action.RIGHT_CLICK_BLOCK
        && event.getAction() != Action.RIGHT_CLICK_AIR)
            return;

        Player player = event.getPlayer();
        if(waiting.contains(player))
            return;

        if(!item.getItemMeta().getDisplayName().equals(getItem().getItemMeta().getDisplayName()))
        {
            player.sendMessage("§c§l(!) §cYou have already set the name of this tag, left click it on a item in your inventory to use");
            return;
        }
        else
        {
            player.sendMessage("§7--------------------------------------------------");
            player.sendMessage("§b§l                                Set Name");
            player.sendMessage("§f     To set the name of this nametag type it into chat");
            player.sendMessage("§f         To cancel type 'cancel' or wait 30 seconds");
            player.sendMessage("§7---------------------------------------------------");

            waiting.add(player);

            new BukkitRunnable() {
                @Override
                public void run() {
                if(waiting.contains(player))
                {
                    player.sendMessage("§c§l(!) §cNametag setting has been cancelled");
                    waiting.remove(player);
                }
                }
            }.runTaskLater(AstronRaiding.getInstance(), 30*20);
        }
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event)
    {
        Player player = event.getPlayer();
        if(!waiting.contains(player))
            return;

        ItemStack item = player.getItemInHand();
        if(!isItem(item))
        {
            player.sendMessage("§c§l(!) §cYou must be holding a nametag to set its name");
            waiting.remove(player);
            return;
        }

        if(!item.getItemMeta().getDisplayName().equals(getItem().getItemMeta().getDisplayName()))
        {
            player.sendMessage("§c§l(!) §cYou have already set the name of this tag, place it on a item in your inventory to use");
            return;
        }

        event.setCancelled(true);

        String message = ChatColor.translateAlternateColorCodes('&', event.getMessage());
        if(message.length() > maxCharacters)
        {
            player.sendMessage("§c§l(!) §cNames cannot exceed 50 characters");
            waiting.remove(player);
            return;
        }

        if(ChatColor.stripColor(message).equalsIgnoreCase("cancel"))
        {
            player.sendMessage("§c§l(!) §cNametag setting has been cancelled");
            waiting.remove(player);
            return;
        }

        if(item.getAmount() == 1)
        {
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(message);
            item.setItemMeta(meta);
        }
        else
        {
            ItemStack newItem = getItem();
            ItemMeta meta = newItem.getItemMeta();
            meta.setDisplayName(message);
            newItem.setItemMeta(meta);
            Util.givePlayerItem(player, newItem);
            item.setAmount(item.getAmount()-1);
            player.setItemInHand(item);
        }
        player.updateInventory();

        player.getInventory().setItemInHand(item);
        player.sendMessage("§7§l(§b§lNameTags§7§l) §fName has been set to "+ message + "§f click on a item in your inventory to apply");
        waiting.remove(player);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event)
    {
        if(!(event.getWhoClicked() instanceof Player))
            return;

        if(event.getCursor() == null || event.getCurrentItem() == null)
            return;

        if(event.getCursor().getType() == Material.AIR || event.getCurrentItem().getType() == Material.AIR)
            return;

        if(!event.getClickedInventory().equals(event.getWhoClicked().getInventory()))
            return;
        ItemStack cursorItem = event.getCursor();
        ItemStack clickedItem = event.getCurrentItem();

        if(cursorItem == null || !cursorItem.hasItemMeta()
        || !cursorItem.getItemMeta().hasDisplayName() || !cursorItem.getItemMeta().hasLore())
            return;

        if(!isItem(cursorItem))
            return;

        if(cursorItem.getItemMeta().getDisplayName().equals(getItem().getItemMeta().getDisplayName()))
            return;

        Player player = (Player)event.getWhoClicked();
        String name = cursorItem.getItemMeta().getDisplayName();

        if(Util.isArmour(clickedItem.getType()) || Util.isTool(clickedItem.getType())) {
            ItemMeta meta = clickedItem.getItemMeta();
            meta.setDisplayName(name);
            clickedItem.setItemMeta(meta);
        }
        else
        {
            player.sendMessage("§c§l(!)§c You can not set the name of that");
            return;
        }
        player.getInventory().setItem(event.getSlot(), clickedItem);

        player.sendMessage("§7§l(§b§lNameTags§7§l) §fName has been set to " + name);

        if (cursorItem.getAmount() == 1)
            player.setItemOnCursor(null);
        else {
            cursorItem.setAmount(cursorItem.getAmount() - 1);
            player.setItemOnCursor(cursorItem);
        }

        event.setCancelled(true);
        player.updateInventory();
    }


}
