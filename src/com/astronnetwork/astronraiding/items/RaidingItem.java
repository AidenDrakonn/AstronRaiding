package com.astronnetwork.astronraiding.items;

import com.google.common.collect.Lists;
import lombok.Getter;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.List;


@Getter
public abstract class RaidingItem implements Listener {

    @Getter
    private static List<RaidingItem> raidingItems = Lists.newArrayList();

    private String name;
    private List<String> aliases;

    public RaidingItem(String name) {
        this.name = name;
        this.aliases = Lists.newArrayList();
        raidingItems.add(this);
    }

    public boolean isItem(ItemStack item)
    {
        if(item != null && item.hasItemMeta() && item.getItemMeta().hasLore()) {
            List<String> itemLore = item.getItemMeta().getLore();
            List<String> targetLore = getItem().getItemMeta().getLore();
            if (itemLore.equals(targetLore)
            && item.getType().equals(getItem().getType()))
                return true;
        }
        return false;
    }

    public abstract ItemStack getItem();

    public static final RaidingItem getRaidingItem(String name) {
        return raidingItems.stream().filter(raidingItem -> raidingItem.getName().equalsIgnoreCase(name) || raidingItem.getAliases().contains(name.toLowerCase()))
                .findFirst().orElse(null);
    }
}
