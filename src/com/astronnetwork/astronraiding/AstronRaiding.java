package com.astronnetwork.astronraiding;

import com.astronnetwork.astronraiding.command.Command;
import com.astronnetwork.astronraiding.items.*;
import com.astronnetwork.astronraiding.util.Util;
import com.earth2me.essentials.Essentials;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import lombok.Getter;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.math.BigDecimal;

public class AstronRaiding extends JavaPlugin {

    /**
     * ItemName tags, A command to get them. Basically allow people to rename their gear with colors and stuff.
     * <p>
     * SandWand - Will remove all the sand/gravel/anvils (going straight down) on right click.
     * <p>
     * GenBuckets - Ignore for now.
     * <p>
     * Void Chests - All the items that go into this chest will be automatically sold and put into the faction bank balance.
     * <p>
     * Infusion Pickaxe breaks blocks in a 5x5, Shockwave breaks 3x3.
     * <p>
     * Trench Pickaxe / Tray Pickaxe - Not to sure what these are to be honest.
     * <p>
     * Creeper Log.
     */

    @Getter
    private static AstronRaiding instance;
    @Getter
    private Economy economy;
    @Getter
    private static WorldGuardPlugin worldGuardPlugin;
    private VoidChest voidChest;

    public void onEnable()
    {
        instance = this;
        economy = Util.setupEconomy(this);
        worldGuardPlugin = setupWorldGaurd();
        voidChest = new VoidChest();
        voidChest.setup();
        voidChest.loadData();
        voidChest.runTimer();
        this.getCommand("astronraiding").setExecutor(new Command());
        registerListeners();
        saveDefaultConfig();

    }

    public void onDisable()
    {
        voidChest.saveData();
    }

    public void registerListeners()
    {
        getServer().getPluginManager().registerEvents(new HarvesterHoe(), this);
        getServer().getPluginManager().registerEvents(new InfusionPickaxe(), this);
        getServer().getPluginManager().registerEvents(new SandWand(), this);
        getServer().getPluginManager().registerEvents(new ShockwavePickaxe(), this);
        getServer().getPluginManager().registerEvents(new TrayPickaxe(), this);
        getServer().getPluginManager().registerEvents(voidChest, this);
        getServer().getPluginManager().registerEvents(new ItemNameTag(), this);
    }

    public Essentials getEssentials() {
        return (Essentials) Bukkit.getPluginManager().getPlugin("Essentials");
    }

    private WorldGuardPlugin setupWorldGaurd()
    {
        Plugin plugin = getServer().getPluginManager().getPlugin("WorldGuard");
        if (plugin == null || !(plugin instanceof WorldGuardPlugin)) {
            return null;
        }
        return (WorldGuardPlugin) plugin;
    }

    public BigDecimal getWorth(ItemStack item)
    {
        BigDecimal worth = getEssentials().getWorth().getPrice(item);
        if(worth == null) {
            return BigDecimal.ZERO;
        }
        return worth;
    }
}
