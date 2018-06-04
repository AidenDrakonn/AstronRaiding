package com.astronnetwork.astronraiding.util;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.astronnetwork.astronraiding.AstronRaiding;
import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World.Environment;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.enchantments.EnchantmentWrapper;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

public class Util {
	public static ItemStack createHead(final String owner, final String name, final String... lore) {
		final ItemStack item = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
		final SkullMeta meta = (SkullMeta) item.getItemMeta();
		meta.setOwner(owner);
		meta.setDisplayName(Util.color(name));
		final List<String> l = new ArrayList<String>();
		for (final String s : lore)
			l.add(Util.color(s));
		meta.setLore(l);
		item.setItemMeta(meta);
		return item;
	}

	public static ItemStack createItem(final Material mat, final int amt, final int durability, final String name,
			final List<String> lore) {
		final ItemStack item = new ItemStack(mat, amt);
		final ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(Util.color(name));
		meta.setLore(Util.color(lore));
		if (durability != 0)
			item.setDurability((short) durability);
		item.setItemMeta(meta);
		return item;
	}

	public static ItemStack createItem(final Material mat, final int amt, final String name, final List<String> lore) {
		return createItem(mat, amt, 0, name, lore);
	}

	public static ItemStack createItem(final Material mat, final String name, final List<String> lore) {
		return createItem(mat, 1, 0, name, lore);
	}

	public static ItemStack createItem(final Material mat, final int amt, final int durability, final String name,
			final String... lore) {
		final List<String> l = new ArrayList<String>();
		for (final String s : lore)
			l.add(s);
		return createItem(mat, amt, durability, name, l);
	}

	public static ItemStack createItem(final Material mat, final int amt, final String name, final String... lore) {
		return createItem(mat, amt, 0, name, lore);
	}

	public static ItemStack createItem(final Material mat, final String name, final String... lore) {
		return createItem(mat, 1, 0, name, lore);
	}

	public static void removeItemFromInv(final Inventory inv, final ItemStack item, final int amt) {
		int amount = 0;
		for (final ItemStack i : inv.getContents())
			if (i != null && i.getType() == item.getType())
				amount += i.getAmount();
		inv.remove(item.getType());
		if (amount > amt)
			inv.addItem(new ItemStack(item.getType(), amount - amt, item.getDurability()));
	}

	public static String caps(final String string) {
		final String[] list = string.split("-").length > 1 ? string.split("-") : string.split("_");
		String s = "";
		for (final String st : list)
			s += st.substring(0, 1).toUpperCase() + st.substring(1).toLowerCase() + "-";
		return s.substring(0, s.length() - 1);
	}

	public static boolean isInt(final String s) {
		try {
			Integer.parseInt(s);
			return true;
		} catch (final NumberFormatException e) {
			return false;
		}
	}

	public static boolean isDouble(final String s) {
		try {
			Double.parseDouble(s);
			return true;
		} catch (final NumberFormatException e) {
			return false;
		}
	}

	public static String color(final String s) {
		return ChatColor.translateAlternateColorCodes('&', s);
	}

	public static String strip(final String s) {
		return ChatColor.stripColor(s);
	}

	public static List<String> color(final List<String> list) {
		final List<String> colored = new ArrayList<String>();
		for (final String s : list)
			colored.add(color(s));
		return colored;
	}

	public static Economy setupEconomy(final Plugin p) {
		if (p.getServer().getPluginManager().getPlugin("Vault") == null)
			return null;
		final RegisteredServiceProvider<Economy> rsp = p.getServer().getServicesManager()
				.getRegistration(Economy.class);
		return rsp == null ? null : rsp.getProvider();
	}

	public static Chat setupChat(final Plugin p) {
		if (p.getServer().getPluginManager().getPlugin("Vault") == null)
			return null;
		final RegisteredServiceProvider<Chat> rsp = p.getServer().getServicesManager().getRegistration(Chat.class);
		return rsp == null ? null : rsp.getProvider();
	}

	public static Permission setupPermissions(final Plugin p) {
		if (p.getServer().getPluginManager().getPlugin("Vault") == null)
			return null;
		final RegisteredServiceProvider<Permission> rsp = p.getServer().getServicesManager()
				.getRegistration(Permission.class);
		return rsp == null ? null : rsp.getProvider();
	}

	@SuppressWarnings("deprecation")
	public static Material getMaterial(String s) {
		s = s.toUpperCase().replace(" ", "_");
		if (s.contains(":"))
			s = s.split("\\:")[0];
		try {
			return Material.getMaterial(s) != null ? Material.getMaterial(s)
					: Material.matchMaterial(s) != null ? Material.matchMaterial(s)
							: Material.valueOf(s) != null ? Material.valueOf(s)
									: isInt(s) ? Material.getMaterial(Integer.parseInt(s)) : Material.PAPER;
		} catch (final Exception ex) {
			return Material.PAPER;
		}
	}

	public static Enchantment getEnchantment(final String s) {
		final Map<String, Enchantment> enchants = new HashMap<String, Enchantment>();
		enchants.put("power", Enchantment.ARROW_DAMAGE);
		enchants.put("flame", Enchantment.ARROW_FIRE);
		enchants.put("infinite", Enchantment.ARROW_INFINITE);
		enchants.put("punch", Enchantment.ARROW_KNOCKBACK);
		enchants.put("sharp", Enchantment.DAMAGE_ALL);
		enchants.put("damage", Enchantment.DAMAGE_ALL);
		enchants.put("sharpness", Enchantment.DAMAGE_ALL);
		enchants.put("arthropod", Enchantment.DAMAGE_ARTHROPODS);
		enchants.put("arthropods", Enchantment.DAMAGE_ARTHROPODS);
		enchants.put("smite", Enchantment.DAMAGE_UNDEAD);
		enchants.put("mining", Enchantment.DIG_SPEED);
		enchants.put("efficiency", Enchantment.DIG_SPEED);
		enchants.put("unbreaking", Enchantment.DURABILITY);
		enchants.put("fire", Enchantment.FIRE_ASPECT);
		enchants.put("kb", Enchantment.KNOCKBACK);
		enchants.put("fortune", Enchantment.LOOT_BONUS_BLOCKS);
		enchants.put("loot", Enchantment.LOOT_BONUS_MOBS);
		enchants.put("looting", Enchantment.LOOT_BONUS_MOBS);
		enchants.put("water", Enchantment.OXYGEN);
		enchants.put("waterbreathing", Enchantment.OXYGEN);
		enchants.put("prot", Enchantment.PROTECTION_ENVIRONMENTAL);
		enchants.put("protection", Enchantment.PROTECTION_ENVIRONMENTAL);
		enchants.put("explosive", Enchantment.PROTECTION_EXPLOSIONS);
		enchants.put("explosions", Enchantment.PROTECTION_EXPLOSIONS);
		enchants.put("protexplosives", Enchantment.PROTECTION_EXPLOSIONS);
		enchants.put("protexplosions", Enchantment.PROTECTION_EXPLOSIONS);
		enchants.put("explosiveprot", Enchantment.PROTECTION_EXPLOSIONS);
		enchants.put("explosiveprotection", Enchantment.PROTECTION_EXPLOSIONS);
		enchants.put("fall", Enchantment.PROTECTION_FALL);
		enchants.put("feather", Enchantment.PROTECTION_FALL);
		enchants.put("falling", Enchantment.PROTECTION_FALL);
		enchants.put("featherfalling", Enchantment.PROTECTION_FALL);
		enchants.put("fireprot", Enchantment.PROTECTION_FIRE);
		enchants.put("fireprotection", Enchantment.PROTECTION_FIRE);
		enchants.put("projprot", Enchantment.PROTECTION_PROJECTILE);
		enchants.put("projprotection", Enchantment.PROTECTION_PROJECTILE);
		enchants.put("projectileprot", Enchantment.PROTECTION_PROJECTILE);
		enchants.put("arrowprotection", Enchantment.PROTECTION_PROJECTILE);
		enchants.put("projectileprotection", Enchantment.PROTECTION_PROJECTILE);
		enchants.put("silk", Enchantment.SILK_TOUCH);
		enchants.put("silktouch", Enchantment.SILK_TOUCH);
		enchants.put("watermine", Enchantment.WATER_WORKER);
		enchants.put("watermining", Enchantment.WATER_WORKER);
		enchants.put("arrowdamage", Enchantment.ARROW_DAMAGE);
		enchants.put("arrowfire", Enchantment.ARROW_FIRE);
		enchants.put("arrowinfinite", Enchantment.ARROW_INFINITE);
		enchants.put("arrowknockback", Enchantment.ARROW_KNOCKBACK);
		enchants.put("damageall", Enchantment.DAMAGE_ALL);
		enchants.put("damagearthropods", Enchantment.DAMAGE_ARTHROPODS);
		enchants.put("damageundead", Enchantment.DAMAGE_UNDEAD);
		enchants.put("digspeed", Enchantment.DIG_SPEED);
		enchants.put("fireaspect", Enchantment.FIRE_ASPECT);
		enchants.put("lootbonusblocks", Enchantment.LOOT_BONUS_BLOCKS);
		enchants.put("lootbonusmobs", Enchantment.LOOT_BONUS_MOBS);
		enchants.put("protectionenviromental", Enchantment.PROTECTION_ENVIRONMENTAL);
		enchants.put("protectionexplosions", Enchantment.PROTECTION_EXPLOSIONS);
		enchants.put("protectionfall", Enchantment.PROTECTION_FALL);
		enchants.put("protectionfire", Enchantment.PROTECTION_FIRE);
		enchants.put("protectionprojectile", Enchantment.PROTECTION_PROJECTILE);
		enchants.put("waterworker", Enchantment.WATER_WORKER);
		return Enchantment.getByName(s) != null ? Enchantment.getByName(s.toUpperCase())
				: enchants.containsKey(s.toLowerCase().replace("_", ""))
						? enchants.get(s.toLowerCase().replace("_", ""))
						: null;
	}

	public static PotionEffectType getPotionEffect(final String s) {
		final Map<String, PotionEffectType> types = new HashMap<String, PotionEffectType>();
		types.put("hearts", PotionEffectType.ABSORPTION);
		types.put("blind", PotionEffectType.BLINDNESS);
		types.put("nausea", PotionEffectType.CONFUSION);
		types.put("resistence", PotionEffectType.DAMAGE_RESISTANCE);
		types.put("haste", PotionEffectType.FAST_DIGGING);
		types.put("fireresistence", PotionEffectType.FIRE_RESISTANCE);
		types.put("damage", PotionEffectType.HARM);
		types.put("health", PotionEffectType.HEALTH_BOOST);
		types.put("healthboost", PotionEffectType.HEALTH_BOOST);
		types.put("strength", PotionEffectType.INCREASE_DAMAGE);
		types.put("regen", PotionEffectType.REGENERATION);
		types.put("food", PotionEffectType.SATURATION);
		types.put("slowness", PotionEffectType.SLOW_DIGGING);
		types.put("miningfatigue", PotionEffectType.SLOW_DIGGING);
		types.put("waterbreathing", PotionEffectType.WATER_BREATHING);
		types.put("weak", PotionEffectType.WEAKNESS);
		types.put("damageresistence", PotionEffectType.DAMAGE_RESISTANCE);
		types.put("fastdigging", PotionEffectType.FAST_DIGGING);
		types.put("increasedamage", PotionEffectType.INCREASE_DAMAGE);
		types.put("nightvision", PotionEffectType.NIGHT_VISION);
		types.put("slowdigging", PotionEffectType.SLOW_DIGGING);
		return PotionEffectType.getByName(s.toUpperCase()) != null ? PotionEffectType.getByName(s.toUpperCase())
				: types.containsKey(s.toLowerCase().replace("_", "")) ? types.get(s.toLowerCase().replace("_", ""))
						: null;
	}

	public static Location stringToLocation(final Plugin p, final String s) {
		if (p == null || s == null || s.isEmpty())
			return null;
		final String[] args = s.split(",");
		try {
			return new Location(p.getServer().getWorld(args[0].trim()), Double.parseDouble(args[1].trim()),
					Double.parseDouble(args[2].trim()), Double.parseDouble(args[3].trim()),
					(float) Double.parseDouble(args[4].trim()), (float) Double.parseDouble(args[5].trim()));
		} catch (final NullPointerException e) {
			return new Location(
					p.getServer().getWorlds().stream().filter(w -> w.getEnvironment() == Environment.NORMAL).findFirst()
							.get(),
					Double.parseDouble(args[1].trim()), Double.parseDouble(args[2].trim()),
					Double.parseDouble(args[3].trim()), (float) Double.parseDouble(args[4].trim()),
					(float) Double.parseDouble(args[5].trim()));
		} catch (final NumberFormatException | ArrayIndexOutOfBoundsException e) {
			return null;
		}
	}

	public static String locationToString(final Location l) {
		try {
			return l.getWorld().getName() + "," + round(l.getX()) + "," + round(l.getY()) + "," + round(l.getZ()) + ","
					+ round(l.getYaw()) + "," + round(l.getPitch());
		} catch (final NullPointerException e) {
			return "";
		}
	}

	public static double round(final double num) {
		return (int) (num * 100) / 100.0;
	}

	public static File getFile(final Plugin p, final String name) {
		if (!p.getDataFolder().exists())
			p.getDataFolder().mkdirs();
		final File file = new File(p.getDataFolder(), name);
		try {
			return file.exists() ? file : file.createNewFile() ? file : null;
		} catch (final IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static YamlConfiguration getConfiguration(final Plugin p, final String name) {
		return YamlConfiguration.loadConfiguration(getFile(p, name));
	}

	public static YamlConfiguration getConfiguration(final File file) {
		return file != null ? YamlConfiguration.loadConfiguration(file) : null;
	}

	public static void save(final Plugin p, final YamlConfiguration configuration, final String name) {
		try {
			configuration.save(getFile(p, name));
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	public static ItemStack getItem(final YamlConfiguration config, String path) {
		if (!path.endsWith("\\."))
			path += ".";
		final ItemStack item = new ItemStack(Material.PAPER);
		if (getMaterial(config.getString(path + "material")) != null)
			item.setType(getMaterial(config.getString(path + "material")));
		else if (getMaterial(config.getString(path + "type")) != null)
			item.setType(getMaterial(config.getString(path + "type")));
		final ItemMeta meta = item.getItemMeta();
		if (config.getInt(path + "amount") != 0)
			item.setAmount(config.getInt(path + "amount"));
		if (config.getInt(path + "durability") != 0)
			item.setDurability((short) config.getInt(path + "durability"));
		else if (config.getInt(path + "damage") != 0)
			item.setDurability((short) config.getInt(path + "damage"));
		else if (config.getString(path + "material").split(":").length > 1
				&& isInt(config.getString(path + "material").split(":")[1]))
			item.setDurability((short) Integer.parseInt(config.getString(path + "material").split(":")[1]));
		if (config.getString(path + "name") != null)
			meta.setDisplayName(color(config.getString(path + "name")));
		else if (config.getString(path + "displayname") != null)
			meta.setDisplayName(color(config.getString(path + "displayname")));
		else if (config.getString(path + "display-name") != null)
			meta.setDisplayName(color(config.getString(path + "display-name")));
		if (config.getStringList(path + "lore") != null)
			meta.setLore(color(config.getStringList(path + "lore")));
		if (config.getStringList(path + "enchants") != null)
			for (final String s : config.getStringList(path + "enchants"))
				if (getEnchantment(s.split(",")[0]) != null)
					meta.addEnchant(getEnchantment(s.split(",")[0]),
							s.split(",").length > 1 && isInt(s.split(",")[1]) ? Integer.parseInt(s.split(",")[1]) - 1
									: 0,
							true);
		item.setItemMeta(meta);
		return item;
	}

	public static LivingEntity spawnMob(final YamlConfiguration config, String path, final Location loc) {
		if (!path.endsWith("\\."))
			path += ".";
		if (Util.getEntity(config.getString(path + "type", "ZOMBIE")) != null) {
			final LivingEntity en = (LivingEntity) loc.getWorld().spawnEntity(loc,
					Util.getEntity(config.getString(path + "type", "ZOMBIE")));
			if (config.getDouble(path + "health") != 0) {
				en.setMaxHealth(config.getDouble(path + "health"));
				en.setHealth(config.getDouble(path + "health"));
			}
			if (config.getString(path + "name") != null) {
				en.setCustomName(Util.color(config.getString(path + "name")));
				en.setCustomNameVisible(true);
			}
			if (en instanceof Ageable)
				if (config.getBoolean(path + ".baby"))
					((Ageable) en).setBaby();
				else
					((Ageable) en).setAdult();
			if (config.getStringList(path + "potion_effects") != null)
				for (final String s : config.getStringList(path + "potion_effects"))
					if (getPotionEffect(s.split(",")[0]) != null)
						en.addPotionEffect(new PotionEffect(getPotionEffect(s.split(",")[0]), 100000,
								s.split(",").length > 1 && isInt(s.split(",")[1])
										? Integer.parseInt(s.split(",")[1]) - 1
										: 0));
			if (config.getStringList(path + "armour_enchants") != null) {
				final ItemStack[] armour = new ItemStack[4];
				armour[0] = new ItemStack(Material.DIAMOND_HELMET);
				armour[1] = new ItemStack(Material.DIAMOND_CHESTPLATE);
				armour[2] = new ItemStack(Material.DIAMOND_LEGGINGS);
				armour[3] = new ItemStack(Material.DIAMOND_BOOTS);
				for (final String s : config.getStringList(path + "armour_enchants"))
					for (final ItemStack item : armour)
						if (getEnchantment(s.split(",")[0]) != null)
							item.addUnsafeEnchantment(getEnchantment(s.split(",")[0]),
									s.split(",").length > 1 && isInt(s.split(",")[1])
											? Integer.parseInt(s.split(",")[1]) - 1
											: 0);
				en.getEquipment().setArmorContents(armour);
				en.getEquipment().setHelmetDropChance(0);
				en.getEquipment().setChestplateDropChance(0);
				en.getEquipment().setLeggingsDropChance(0);
				en.getEquipment().setBootsDropChance(0);
			}
			return en;
		}
		return null;
	}

	public static List<String> getFromPage(final List<Object> list, final int page, final int size) {
		final List<String> ret = new ArrayList<String>();
		for (int i = 0; i < size; i++)
			try {
				ret.add(list.get(i + page * size).toString());
			} catch (final Exception ex) {
			}
		return ret;
	}

	public static List<String> getFromPage(final List<Object> list, final int page) {
		return getFromPage(list, page, 10);
	}

	public static String timeFromMillis(final long time) {
		final long seconds = time / 1000;
		final long minutes = seconds / 60;
		final long hours = minutes / 60;
		final long days = hours / 24;
		if (days >= 1L)
			return String.format("%d day(s), %d hour(s), %d min, %d sec", days, hours - days * 24, minutes - hours * 60,
					seconds - minutes * 60);
		if (hours >= 1L)
			return String.format("%d hour(s), %d min", hours, minutes - hours * 60, seconds - minutes * 60);
		if (minutes >= 1L)
			return String.format("%d min, %d sec", minutes, seconds - minutes * 60);
		return String.format("%d sec", seconds);
	}

	public static String timeFromSec(final long time) {
		return timeFromMillis(time * 1000);
	}

	public static List<Integer> getBorder(final int size) {
		switch (size) {
		case 54:
			return Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 17, 18, 26, 27, 35, 36, 44, 45, 46, 47, 48, 49, 50, 51,
					52, 53);
		case 45:
			return Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 17, 18, 26, 27, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44);
		case 36:
			return Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 17, 18, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35);
		case 27:
			return Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26);
		default:
			return Arrays.asList();
		}
	}

	public static int roundInvSize(int size) {
		if (size >= 54)
			return 54;
		while (size < 9 || size % 9 != 0)
			size++;
		return size;
	}

	public static int getTotalExperience(final Player player) {
		int exp = Math.round(getLevelExpNew(player.getLevel()) * player.getExp());
		int currentLevel = player.getLevel();
		while (currentLevel > 0) {
			currentLevel--;
			exp += getLevelExpNew(currentLevel);
		}
		if (exp < 0)
			exp = 0;
		return exp;
	}

	public static int getTotalExperienceOld(final Player player) {
		int exp = Math.round(getLevelExpOld(player.getLevel()) * player.getExp());
		int currentLevel = player.getLevel();
		while (currentLevel > 0) {
			currentLevel--;
			exp += getLevelExpOld(currentLevel);
		}
		if (exp < 0)
			exp = 0;
		return exp;
	}

	public static int getLevelExpOld(final int level) {
		return level >= 30 ? 62 + (level - 30) * 7 : level >= 15 ? 17 + (level - 15) * 3 : 17;
	}

	public static int getLevelExpNew(final int level) {
		return level < 16 ? level * 2 + 7 : level < 31 ? level * 5 - 38 : level * 9 - 158;
	}

	public static boolean isArmour(final Material m) {
		return Enchantment.PROTECTION_ENVIRONMENTAL.canEnchantItem(new ItemStack(m));
	}

	public static boolean isDiamond(final Material m) {
		return m.toString().contains("DIAMOND");
	}

	public static boolean isGold(final Material m) {
		return m.toString().contains("GOLD");
	}

	public static boolean isIron(final Material m) {
		return m.toString().contains("IRON");
	}

	public static boolean isLeather(final Material m) {
		return m.toString().contains("LEATHER");
	}

	public static boolean isChain(final Material m) {
		return m.toString().contains("CHAIN");
	}

	public static boolean isSword(final Material m) {
		return m.toString().contains("SWORD");
	}

	public static boolean isAxe(final Material m) {
		return m.toString().endsWith("_AXE");
	}

	public static boolean isPickaxe(final Material m) {
		return m.toString().contains("PICKAXE");
	}

	public static boolean isWeapon(final Material m) {
		return Enchantment.DAMAGE_ALL.canEnchantItem(new ItemStack(m));
	}

	public static boolean isTool(final Material m) {
		return Enchantment.DIG_SPEED.canEnchantItem(new ItemStack(m));
	}

	public static String getName(final EntityType e) {
		if (e.equals(EntityType.PIG_ZOMBIE))
			return "Zombie Pigman";
		if (!e.toString().contains("_"))
			return e.toString().substring(0, 1).toUpperCase() + e.toString().substring(1).toLowerCase();
		final String[] split = e.toString().split("_");
		String name = "";
		for (final String s : split)
			name += s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase() + " ";
		return name.trim();
	}

	public static EntityType getEntity(String e) {
		if (e.equalsIgnoreCase("Zombie Pigman"))
			return EntityType.PIG_ZOMBIE;
		e = e.replaceAll(" ", "_");
		if (!e.contains("_"))
			return EntityType.valueOf(e.toUpperCase());
		final String[] split = e.toString().split(" ");
		String name = "";
		for (final String s : split)
			name += s.toUpperCase() + "_";
		try {
			return EntityType.valueOf(name.substring(0, name.length() - 1));
		} catch (final Exception ex) {
			return null;
		}
	}

	public enum Pane {
		WHITE(0), ORANGE(1), MAGENTA(2), LIGHT_BLUE(3), YELLOW(4), LIME(5), PINK(6), GRAY(7), LIGHT_GRAY(8), CYAN(
				9), PURPLE(10), BLUE(11), BROWN(12), GREEN(13), RED(14), BLACK(15);
		private final int value;

		Pane(final int value) {
			this.value = value;
		}

		public int value() {
			return value;
		}
	}

	public static class EnchantGlow extends EnchantmentWrapper {
		private static Enchantment glow = null;
		private final String name;

		public static ItemStack addGlow(final ItemStack itemstack) {
			itemstack.addEnchantment(getGlow(), 1);
			return itemstack;
		}

		public static Enchantment getGlow() {
			if (glow != null)
				return glow;
			Field field = null;
			try {
				field = Enchantment.class.getDeclaredField("acceptingNew");
			} catch (NoSuchFieldException | SecurityException e) {
				e.printStackTrace();
				return glow;
			}
			field.setAccessible(true);
			try {
				field.set(null, true);
			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}
			try {
				glow = new EnchantGlow(Enchantment.values().length + 100);
			} catch (final Exception e) {
				glow = Enchantment.getByName("Glow");
			}
			if (Enchantment.getByName("Glow") == null)
				Enchantment.registerEnchantment(glow);
			return glow;
		}

		@Override
		public String getName() {
			return name;
		}

		@Override
		public Enchantment getEnchantment() {
			return Enchantment.getByName("Glow");
		}

		@Override
		public int getMaxLevel() {
			return 1;
		}

		@Override
		public int getStartLevel() {
			return 1;
		}

		@Override
		public EnchantmentTarget getItemTarget() {
			return EnchantmentTarget.ALL;
		}

		@Override
		public boolean canEnchantItem(final ItemStack item) {
			return true;
		}

		@Override
		public boolean conflictsWith(final Enchantment other) {
			return false;
		}

		public EnchantGlow(final int i) {
			super(i);
			name = "Glow";
		}
	}

	public static class Messages {
		private Map<String, String> messages = new HashMap<String, String>();
		private File file;
		private YamlConfiguration config;

		public Messages(final Map<String, String> map, final Plugin p) {
			messages = map;
			new Messages(p);
		}

		public Messages(final Plugin p) {
			file = new File(p.getDataFolder(), "messages.yml");
			if (!file.exists())
				try {
					file.createNewFile();
				} catch (final Exception ex) {
				}
			if (file.exists())
				config = YamlConfiguration.loadConfiguration(file);
			if (config.isConfigurationSection("Messages"))
				for (final String s : config.getConfigurationSection("Messages").getValues(false).keySet())
					messages.put(s, config.getString("Messages." + s));
		}

		public String getMessage(final String s) {
			return color(messages.get(s));
		}

		public Map<String, String> getMessages() {
			return messages;
		}

		public void addMessages(final String... messages) {
			if (messages.length == 2
					&& !(messages[0].contains("-") || messages[0].contains("/") || messages[0].contains("~"))) {
				this.messages.put(messages[0], messages[1]);
				return;
			}
			for (final String s : messages)
				if (s.contains("-"))
					this.messages.put(s.split("-")[0], s.split("-")[1]);
				else if (s.contains("/"))
					this.messages.put(s.split("/")[0], s.split("/")[1]);
				else if (s.contains("~"))
					this.messages.put(s.split("~")[0], s.split("~")[1]);
		}

		public void addMessage(final String key, final String value) {
			messages.put(key, value);
		}

		public void put(final String key, final String value) {
			messages.put(key, value);
		}

		public void saveMessages() {
			try {
				for (final String s : messages.keySet())
					config.set("Messages." + s, messages.get(s));
				config.save(file);
			} catch (final Exception ex) {
			}
		}

		public void reloadMessages() {
			try {
				config = YamlConfiguration.loadConfiguration(file);
				messages.clear();
				if (config.isConfigurationSection("Messages"))
					for (final String s : config.getConfigurationSection("Messages").getValues(false).keySet())
						messages.put(s, config.getString("Messages." + s));
			} catch (final Exception ex) {
			}
		}
	}


	private static WorldGuardPlugin wp = null;
	public static ProtectedRegion getRegion(final Location l) {
		if (wp == null)
			wp = (WorldGuardPlugin) Bukkit.getServer().getPluginManager()
					.getPlugin("WorldGuard");
		if (wp == null)
			return null;
		final ApplicableRegionSet regions = wp.getRegionManager(l.getWorld()).getApplicableRegions(l);
		if (regions == null)
			return null;
		for (final ProtectedRegion reg : regions)
			if (reg != null)
				return reg;
		return null;
	}

	public static void givePlayerItem(Player player, ItemStack item)
	{
		if (player.getInventory().firstEmpty() != -1)
		{
			player.getInventory().addItem(item);
		}
		else if (getSlot(player, item.getType()) != -1)
		{
			player.getInventory().addItem(item);
		}
		else
		{
			player.sendMessage( "§c§l(!) §cYour inventory is full!");
			player.getWorld().dropItem(player.getLocation(), item);
		}
	}

	public static int getSlot(Player p, Material type)
	{
		for (int i = 0; i < p.getInventory().getSize(); i++) {
			if ((p.getInventory().getItem(i).getType() == type) && (p.getInventory().getItem(i).getAmount() < p.getInventory().getItem(i).getMaxStackSize())) {
				return i;
			}
		}
		return -1;
	}

	public static String getDirection(Player player)
	{
		double rotation = (player.getLocation().getYaw() - 90) % 360;
		if (rotation < 0) {
			rotation += 360.0;
		}
		if (0 <= rotation && rotation < 45)
			return "X";
		if (45 <= rotation && rotation < 135)
			return "Z";
		if (135 <= rotation && rotation < 225)
			return "X";
		if (225 <= rotation && rotation < 315)
			return "Z";
		if (315 <= rotation && rotation < 360.0)
			return "X";
		return null;
	}

    public static boolean canBuild(Location location, Player player)
    {
        Faction faction = Board.getInstance().getFactionAt(new FLocation(location));
        if((faction.isWilderness() || faction.equals(FPlayers.getInstance().getByPlayer(player).getFaction()))
                && AstronRaiding.getWorldGuardPlugin().canBuild(player, location))
            return true;
        return false;
    }

}