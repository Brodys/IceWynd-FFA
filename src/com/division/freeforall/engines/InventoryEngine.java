package com.division.freeforall.engines;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;

import com.division.freeforall.core.FreeForAll;
import com.division.freeforall.core.PlayerStorage;
import com.division.freeforall.events.MoveMethod;
import com.division.freeforall.events.PlayerEnteredArenaEvent;
import com.division.freeforall.events.PlayerKillstreakAwardedEvent;
import com.division.freeforall.events.PlayerLeftArenaEvent;
import com.division.freeforall.events.PlayerPreCheckEvent;

@EngineInfo(author = "mastershake71",
version = "0.2.31RB",
depends = {"OfflineStorage", "Storage"})

public class InventoryEngine extends Engine {

	OfflineStorageEngine OSE = null;
	StorageEngine SE = null;

	public InventoryEngine() {
	}
	public void addArmor(Player p) {
		PlayerInventory pInv = p.getInventory();
		pInv.setChestplate(new ItemStack(Material.getMaterial(FreeForAll.instance().getConfig().getString("StartingArmor.Chestplate"))));
		pInv.setBoots(new ItemStack(Material.getMaterial(FreeForAll.instance().getConfig().getString("StartingArmor.Boots"))));
		pInv.setHelmet(new ItemStack(Material.getMaterial(FreeForAll.instance().getConfig().getString("StartingArmor.Helmet"))));
		pInv.setLeggings(new ItemStack(Material.getMaterial(FreeForAll.instance().getConfig().getString("StartingArmor.Leggings"))));
	}

	public void addArmor1(Player p) {
		PlayerInventory pInv = p.getInventory();
		pInv.setChestplate(new ItemStack(Material.getMaterial(FreeForAll.instance().getConfig().getString("ArmorUgrade1.Chestplate"))));
		pInv.setBoots(new ItemStack(Material.getMaterial(FreeForAll.instance().getConfig().getString("ArmorUgrade1.Boots"))));
		pInv.setHelmet(new ItemStack(Material.getMaterial(FreeForAll.instance().getConfig().getString("ArmorUgrade1.Helmet"))));
		pInv.setLeggings(new ItemStack(Material.getMaterial(FreeForAll.instance().getConfig().getString("ArmorUgrade1.Leggings"))));
	}

	public void addArmor2(Player p) {
		PlayerInventory pInv = p.getInventory();
		pInv.setChestplate(new ItemStack(Material.getMaterial(FreeForAll.instance().getConfig().getString("ArmorUgrade2.Chestplate"))));
		pInv.setBoots(new ItemStack(Material.getMaterial(FreeForAll.instance().getConfig().getString("ArmorUgrade2.Boots"))));
		pInv.setHelmet(new ItemStack(Material.getMaterial(FreeForAll.instance().getConfig().getString("ArmorUgrade2.Helmet"))));
		pInv.setLeggings(new ItemStack(Material.getMaterial(FreeForAll.instance().getConfig().getString("ArmorUgrade2.Leggings"))));
	}

	public void addArmor3(Player p) {
		PlayerInventory pInv = p.getInventory();
		pInv.setChestplate(new ItemStack(Material.getMaterial(FreeForAll.instance().getConfig().getString("ArmorUgrade3.Chestplate"))));
		pInv.setBoots(new ItemStack(Material.getMaterial(FreeForAll.instance().getConfig().getString("ArmorUgrade3.Boots"))));
		pInv.setHelmet(new ItemStack(Material.getMaterial(FreeForAll.instance().getConfig().getString("ArmorUgrade3.Helmet"))));
		pInv.setLeggings(new ItemStack(Material.getMaterial(FreeForAll.instance().getConfig().getString("ArmorUgrade3.Leggings"))));
	}

	public void addItems(Player p) {
		PlayerInventory pInv = p.getInventory();
		pInv.addItem(new ItemStack(Material.getMaterial(FreeForAll.instance().getConfig().getString("StartingArmor.Weapon1"))));
		pInv.addItem(new ItemStack(Material.getMaterial(FreeForAll.instance().getConfig().getString("StartingArmor.Weapon2"))));
		pInv.addItem(new ItemStack(Material.COOKED_BEEF, 16));
		int damageval = 16421;
		for (int i = 0; i < 6; i++)
			pInv.addItem(new ItemStack(Material.POTION, 1, (short)damageval));
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void distributeRewards(PlayerKillstreakAwardedEvent evt) {
		for (ItemStack is : evt.getRewards()) {
			evt.getPlayer().getInventory().addItem(is);
		}
	}

	@Override
	public final String getName() {
		return ("Inventory");
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerEnteredArena(PlayerEnteredArenaEvent evt) {
		if (!evt.isCancelled()) {
			Player evtPlayer = evt.getPlayer();
			if (!evtPlayer.isDead() || evt.getMethod() == MoveMethod.RESPAWNED) {
				evtPlayer.getInventory().clear();
				addItems(evtPlayer);
				addArmor(evtPlayer);
				evtPlayer.setHealth(20);
				evtPlayer.setFoodLevel(20);
				evtPlayer.setGameMode(GameMode.SURVIVAL);
				evtPlayer.sendMessage(FreeForAll.prefix + "You have entered the arena. Your inventory has been saved.");
				for (PotionEffect pt : evtPlayer.getActivePotionEffects()) {
					evtPlayer.removePotionEffect(pt.getType());
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerLeftArena(PlayerLeftArenaEvent evt) {
		restoreInventory(evt.getPlayer());
		//SE.playersInArena.remove(evt.getPlayer());
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerPreCheck(PlayerPreCheckEvent evt){
		Player evtPlayer = evt.getPlayer();
		if(OSE.hasOfflineStorage(evtPlayer.getName())){
			restoreInventory(evtPlayer);
		}
	}

	public boolean restoreInventory(Player player) {
		if (OSE.hasOfflineStorage(player.getName())) {
			OSE.loadOfflineStorage(player,player.getName());
			if (SE.hasStorage(player)) {
				PlayerStorage pStorage = SE.getStorage(player.getName());
				SE.removeStorage(pStorage);
			}
			return true;
		}
		PlayerStorage pStorage = SE.getStorage(player.getName());
		if (pStorage != null) {
			pStorage.restoreInv(player);
			SE.removeStorage(pStorage);
			player.sendMessage(FreeForAll.prefix + "Your inventory has been restored.");
			return true;
		}
		return false;
	}

	@Override
	public void runStartupChecks() throws EngineException {
		Engine eng = FreeForAll.getInstance().getEngineManger().getEngine("OfflineStorage");
		if (eng != null) {
			if (eng instanceof OfflineStorageEngine) {
				this.OSE = (OfflineStorageEngine) eng;
			}
		}
		eng = FreeForAll.getInstance().getEngineManger().getEngine("Storage");
		if (eng != null) {
			if (eng instanceof StorageEngine) {
				this.SE = (StorageEngine) eng;
			}
		}
		if (OSE == null || SE == null) {
			throw new EngineException("Missing Dependency.");
		}
	}
}
