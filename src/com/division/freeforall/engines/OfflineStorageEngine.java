package com.division.freeforall.engines;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import com.division.freeforall.core.FreeForAll;
import com.division.freeforall.core.PlayerStorage;

@EngineInfo(author = "mastershake71", version = "0.2.39RB")
public class OfflineStorageEngine extends Engine {

	FreeForAll FFA;
	File storagedir;

	public OfflineStorageEngine() {
		this.FFA = FreeForAll.getInstance();
		storagedir = new File(FFA.getDataFolder() + "/Offline_Storage/");
		if (!storagedir.exists()) {
			storagedir.mkdir();
		}

	}

	@SuppressWarnings("deprecation")
	public boolean covertPlayerStorage(PlayerStorage pStorage) {
		File storagefile = new File(storagedir + "/" + pStorage.getKey() + ".yml");
		YamlConfiguration offstor = new YamlConfiguration();
		for (ItemStack is : pStorage.getContents()) {
			if (is != null && !is.getType().equals(Material.AIR)) {
				String path = "inventory.contents.inventory." + is.getType().name() + ":0";
				if (offstor.contains(path)) {
					int inc = incrementItem(is.getType().name(), offstor.saveToString());
					path = "inventory.contents.inventory." + is.getType().name() + ":" + inc;
				}
				offstor.set(path + ".amount", is.getAmount());
				offstor.set(path + ".durrability", is.getDurability());
				Set<Enchantment> enchants = is.getEnchantments().keySet();
				for (Enchantment ench : enchants) {
					int enchLevel = is.getEnchantments().get(ench);
					offstor.set(path + ".enchantments." + ench.getId(), enchLevel);
				}
			}
		}
		if (pStorage.getChestpiece() != null) {
			ItemStack item = pStorage.getChestpiece();
			String path = "inventory.contents.armor.chest." + item.getType().name();
			offstor.set(path + ".amount", item.getAmount());
			offstor.set(path + ".durrability", item.getDurability());
			Set<Enchantment> enchants = item.getEnchantments().keySet();
			for (Enchantment ench : enchants) {
				int enchLevel = item.getEnchantments().get(ench);
				offstor.set(path + ".enchantments." + ench.getId(), enchLevel);
			}
		}
		if (pStorage.getHelmet() != null) {
			ItemStack item = pStorage.getHelmet();
			String path = "inventory.contents.armor.helm." + item.getType().name();
			offstor.set(path + ".amount", item.getAmount());
			offstor.set(path + ".durrability", item.getDurability());
			Set<Enchantment> enchants = item.getEnchantments().keySet();
			for (Enchantment ench : enchants) {
				int enchLevel = item.getEnchantments().get(ench);
				offstor.set(path + ".enchantments." + ench.getId(), enchLevel);
			}
		}
		if (pStorage.getLeggings() != null) {
			ItemStack item = pStorage.getLeggings();
			String path = "inventory.contents.armor.legs." + item.getType().name();
			offstor.set(path + ".amount", item.getAmount());
			offstor.set(path + ".durrability", item.getDurability());
			Set<Enchantment> enchants = item.getEnchantments().keySet();
			for (Enchantment ench : enchants) {
				int enchLevel = item.getEnchantments().get(ench);
				offstor.set(path + ".enchantments." + ench.getId(), enchLevel);
			}
		}
		if (pStorage.getBoots() != null) {
			ItemStack item = pStorage.getBoots();
			String path = "inventory.contents.armor.boots." + item.getType().name();
			offstor.set(path + ".amount", item.getAmount());
			offstor.set(path + ".durrability", item.getDurability());
			Set<Enchantment> enchants = item.getEnchantments().keySet();
			for (Enchantment ench : enchants) {
				int enchLevel = item.getEnchantments().get(ench);
				offstor.set(path + ".enchantments." + ench.getId(), enchLevel);
			}
		}
		try {
			offstor.save(storagefile);
		} catch (IOException ex) {
			return false;
		}
		return true;
	}

	@Override
	public String getName() {
		return ("OfflineStorage");
	}

	public boolean hasOfflineStorage(String key) {
		File storagefile = new File(storagedir + "/" + key + ".yml");
		return storagefile.exists();
	}

	private int incrementItem(String item, String storage) {
		String[] split = storage.split(" ");
		int count = 0;
		for (String s : split) {
			if (s.contains(item)) {
				count++;
			}
		}
		return count;
	}

	@SuppressWarnings("deprecation")
	public boolean loadOfflineStorage(Player player, String key) {
		if (hasOfflineStorage(key)) {
			File storagefile = new File(storagedir + "/" + key + ".yml");
			YamlConfiguration offstor = new YamlConfiguration();
			try {
				offstor.load(storagefile);
				if (player != null) {
					PlayerInventory inv = player.getInventory();
					inv.clear();
					if (offstor.contains("inventory.contents.inventory")) {
						Set<String> contents = offstor.getConfigurationSection("inventory.contents.inventory").getKeys(false);
						for (String rawItem : contents) {
							String item = rawItem.split(":")[0];
							String path = "inventory.contents.inventory." + rawItem;
							int amount = offstor.getInt(path + ".amount");
							int durability = offstor.getInt(path + ".durrability");
							ItemStack newItem = new ItemStack(Material.getMaterial(item), amount, (short) durability);
							if (offstor.contains(path + ".enchantments")) {
								Set<String> enchants = offstor.getConfigurationSection(path + ".enchantments").getKeys(false);
								for (String id : enchants) {
									Enchantment ench = Enchantment.getById(Integer.parseInt(id));
									int level = offstor.getInt(path + ".enchantments." + id);
									newItem.addEnchantment(ench, level);
								}
							}
							inv.addItem(newItem);
						}
					}
					if (offstor.contains("inventory.contents.armor.chest")) {
						Set<String> chestpiece = offstor.getConfigurationSection("inventory.contents.armor.chest").getKeys(false);
						for (String s : chestpiece) {
							String path = "inventory.contents.armor.chest." + s;
							int amount = offstor.getInt(path + ".amount");
							int durability = offstor.getInt(path + ".durrability");
							ItemStack chest = new ItemStack(Material.valueOf(s), amount, (short) durability);
							if (offstor.contains(path + ".enchantments")) {
								Set<String> enchants = offstor.getConfigurationSection(path + ".enchantments").getKeys(false);
								for (String id : enchants) {
									Enchantment ench = Enchantment.getById(Integer.parseInt(id));
									int level = offstor.getInt(path + ".enchantments." + id);
									chest.addEnchantment(ench, level);
								}
							}
							inv.setChestplate(chest);
						}
					} else {
						inv.setChestplate(new ItemStack(Material.AIR));
					}
					if (offstor.contains("inventory.contents.armor.helm")) {
						Set<String> helmet = offstor.getConfigurationSection("inventory.contents.armor.helm").getKeys(false);
						for (String s : helmet) {
							String path = "inventory.contents.armor.helm." + s;
							int amount = offstor.getInt(path + ".amount");
							int durability = offstor.getInt(path + ".durrability");
							ItemStack helm = new ItemStack(Material.valueOf(s), amount, (short) durability);
							if (offstor.contains(path + ".enchantments")) {
								Set<String> enchants = offstor.getConfigurationSection(path + ".enchantments").getKeys(false);
								for (String id : enchants) {
									Enchantment ench = Enchantment.getById(Integer.parseInt(id));
									int level = offstor.getInt(path + ".enchantments." + id);
									helm.addEnchantment(ench, level);
								}
							}
							inv.setHelmet(helm);
						}
					} else {
						inv.setHelmet(new ItemStack(Material.AIR));
					}
					if (offstor.contains("inventory.contents.armor.legs")) {
						Set<String> legset = offstor.getConfigurationSection("inventory.contents.armor.legs").getKeys(false);
						for (String s : legset) {
							String path = "inventory.contents.armor.legs." + s;
							int amount = offstor.getInt(path + ".amount");
							int durability = offstor.getInt(path + ".durrability");
							ItemStack legs = new ItemStack(Material.valueOf(s), amount, (short) durability);
							if (offstor.contains(path + ".enchantments")) {
								Set<String> enchants = offstor.getConfigurationSection(path + ".enchantments").getKeys(false);
								for (String id : enchants) {
									Enchantment ench = Enchantment.getById(Integer.parseInt(id));
									int level = offstor.getInt(path + ".enchantments." + id);
									legs.addEnchantment(ench, level);
								}
							}
							inv.setLeggings(legs);
						}
					} else {
						inv.setLeggings(new ItemStack(Material.AIR));
					}
					if (offstor.contains("inventory.contents.armor.boots")) {
						Set<String> boots = offstor.getConfigurationSection("inventory.contents.armor.boots").getKeys(false);
						for (String s : boots) {
							String path = "inventory.contents.armor.boots." + s;
							int amount = offstor.getInt(path + ".amount");
							int durability = offstor.getInt(path + ".durrability");
							ItemStack feet = new ItemStack(Material.valueOf(s), amount, (short) durability);
							if (offstor.contains(path + ".enchantments")) {
								Set<String> enchants = offstor.getConfigurationSection(path + ".enchantments").getKeys(false);
								for (String id : enchants) {
									Enchantment ench = Enchantment.getById(Integer.parseInt(id));
									int level = offstor.getInt(path + ".enchantments." + id);
									feet.addEnchantment(ench, level);
								}
							}
							inv.setBoots(feet);
						}
					} else {
						inv.setBoots(new ItemStack(Material.AIR));
					}
					player.sendMessage(FreeForAll.prefix + "Your offline inventory has been restored.");
					return storagefile.delete();
				}
			} catch (Exception ex) {
				ex.printStackTrace();
				return false;
			}
		}
		return false;
	}
}
