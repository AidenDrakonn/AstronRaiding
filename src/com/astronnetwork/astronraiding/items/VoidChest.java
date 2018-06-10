package com.astronnetwork.astronraiding.items;

import com.astronnetwork.astronraiding.AstronRaiding;
import com.astronnetwork.astronraiding.util.Util;
import com.massivecraft.factions.*;
import com.massivecraft.factions.integration.Econ;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

public class VoidChest extends RaidingItem
{
    private HashMap<Location, UUID> voidChests = new HashMap<>();
    private AstronRaiding plugin = AstronRaiding.getInstance();
    private File datafile;
    private FileConfiguration datacfg;
    private Economy economy;
    private int sellInterval;

    public VoidChest()
    {
        super("voidchest");
        this.getAliases().addAll(Arrays.asList("sellchest"));
        economy = AstronRaiding.getInstance().getEconomy();
    }

    @Override
    public ItemStack getItem()
    {
        return Util.createItem(Material.CHEST, "§6§lVoid Chest", "§7§oAutomatically sells items!");
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event)
    {
        if(event.isCancelled())
            return;

        Location location = event.getBlock().getLocation();
        if(voidChests.keySet().contains(location))
        {
            event.setCancelled(true);
            location.getBlock().setType(Material.AIR);
            location.getWorld().dropItem(location, getItem());
            voidChests.remove(location);
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event)
    {
        Player player = event.getPlayer();
        ItemStack item = event.getItemInHand();
        Block block = event.getBlock();
        if(event.isCancelled())
            return;

        if(!isItem(item))
            return;

        voidChests.put(block.getLocation(), player.getUniqueId());
    }

    public void runTimer()
    {
        Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new BukkitRunnable()
        {
            @Override
            public void run()
            {
                for(Location location : voidChests.keySet())
                {
                    Block block = location.getBlock();
                    if (!(block.getState() instanceof Chest)) {
                        System.out.println("[VoidChest] Possible error, Block is not a chest!");
                        continue;
                    }

                    Chest chest = (Chest) block.getState();

                    if (chest == null) {
                        System.out.println("[VoidChest] Possible error, chest is equal to null!");
                        continue;
                    }

                    if (chest.getInventory() == null) {
                        System.out.println("[VoidChest] Possible error, chest does not have an inventory");
                        continue;
                    }

                    Inventory chestInventory = chest.getInventory();
                    double chestWorth = 0;
                    for (ItemStack item : chestInventory.getContents())
                    {
                        if(item == null || item.getType() == Material.AIR)
                            continue;
                        BigDecimal worth = plugin.getWorth(new ItemStack(item.getType(), 1));
                        double value = worth.doubleValue() * item.getAmount();
                        chestWorth = chestWorth + value;
                        chestInventory.setItem(chestInventory.first(item), new ItemStack(Material.AIR));
                    }

                    if(chestWorth == 0)
                        continue;

                    OfflinePlayer owner = Bukkit.getOfflinePlayer(voidChests.get(location));
                    Faction faction = Board.getInstance().getFactionAt(new FLocation(location));
                    if(faction.isWilderness() || faction.isSafeZone() || faction.isWarZone()) {
                        economy.depositPlayer(owner, chestWorth);
                    }
                    else
                    {
                        Econ.deposit(faction.getAccountId(), chestWorth);
                    }
                }
            }
        }, 500, sellInterval*20);
    }

    public void loadData()
    {
        for (String locationAsString : datacfg.getStringList("chests"))
        {
            String newString = locationAsString.split(":")[0];
            Location location =  Util.stringToLocation(plugin, newString);
            UUID uuid = UUID.fromString(locationAsString.split(":")[1]);
            voidChests.put(location, uuid);
        }

        sellInterval = plugin.getConfig().getInt("voidchest.sellinterval");

        System.out.println("[VoidChest] Data has been loaded!");
    }

    public void saveData()
    {
        List<String> stringLocations = new ArrayList<>();
        for(Location location : voidChests.keySet())
        {
            String locationAsString = Util.locationToString(location);
            UUID uuid = voidChests.get(location);
            stringLocations.add(locationAsString + ":" + uuid.toString());
        }

        datacfg.set("chests", stringLocations);
        try {
            datacfg.save(datafile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("[VoidChest] Data has been saved!");
    }

    public void setup()
    {
        if(!plugin.getDataFolder().exists())
        {
            plugin.getDataFolder().mkdir();
        }

        datafile = new File(plugin.getDataFolder(), "data.yml");
        if(!datafile.exists())
        {
            try
            {
                datafile.createNewFile();
                System.out.println("[VoidChest] Data.yml did not exist, created it");
            }

            catch(IOException e)
            {
                e.printStackTrace();
            }
        }
        datacfg = YamlConfiguration.loadConfiguration(datafile);
    }
}
