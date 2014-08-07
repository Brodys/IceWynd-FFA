package com.division.freeforall.engines;

import java.util.ArrayList;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import com.division.freeforall.core.FreeForAll;
import com.division.freeforall.core.PlayerStorage;
import com.division.freeforall.events.PlayerDamageInArenaEvent;
import com.division.freeforall.events.PlayerEnteredArenaEvent;
import com.division.freeforall.events.PlayerQuitInArenaEvent;

@EngineInfo(author = "kwek20",
version = "0.2.4EB",
depends = {"OfflineStorage"})
public class StorageEngine extends Engine {

	protected int aM;

	private static ArrayList<PlayerStorage> massStorage = new ArrayList<PlayerStorage>();
	private OfflineStorageEngine OSE = null;
	public StorageEngine() {
	}

	public void addStorage(Player key) {
		PlayerStorage pStorage = new PlayerStorage(key.getName(), key.getInventory());
		massStorage.add(pStorage);
		System.out.println("added storage for player " + key.getName());
	}

	public int aO(Player p) {
		return this.l(p);
	}

	public void createOfflineStorage(PlayerStorage pStorage) {
		if (pStorage != null) {
			if (!hasOfflineStorage(pStorage.getKey())) {
				System.out.println("[FreeForAll] converting storage key: " + pStorage.getKey() + " to Offline Storage.");
				if (OSE.covertPlayerStorage(pStorage)) {
					System.out.println("[FreeForAll] storage key: " + pStorage.getKey() + " has been successfully converted.");
				} else {
					System.out.println("[FreeForAll] An error occured when converting storage key: " + pStorage.getKey());
				}
			}
		}
	}

	public double getArmorRedox(Player p, double d) {
		int j = 25 - this.aO(p);
		double k = d * j;

		this.k(p, d);
		d = k / 25;

		return d;
	}

	@Override
	public String getName() {
		return ("Storage");
	}

	public PlayerStorage getStorage(String rKey) {
		for (PlayerStorage ps : massStorage) {
			if (ps.getKey().equals(rKey)) {
				return ps;
			}
		}
		return null;
	}

	public boolean hasOfflineStorage(String key) {
		return OSE.hasOfflineStorage(key);
	}

	public boolean hasStorage(Player rkey) {
		if (getStorage(rkey.getName()) != null) {
			return true;
		}
		return false;
	}

	public void k(Player p, double d) {
		d /= 4;
		if (d < 1) {
			d = 1;
		}
		ItemStack[] armor = p.getInventory().getArmorContents();

		for (int j = 0; j < armor.length; ++j) {
			if (armor[j] != null) {
				armor[j].setDurability((short) (armor[j].getDurability() - d));
				if (armor[j].getDurability() == 0) {
					armor[j] = null;
				}
			}
		}
	}

	public int l(Player p) {
		int i = 0;
		ItemStack[] aitemstack = p.getInventory().getArmorContents();
		int j = aitemstack.length;

		for (int k = 0; k < j; ++k) {
			int l = 2;

			i += l;
		}
		return i;
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerDamagedInArena(PlayerDamageInArenaEvent evt) {
		Player victim = evt.getVictim();
		EntityDamageEvent ede = evt.getDamageEvent();
		EntityDamageByEntityEvent edee;
		if (ede instanceof EntityDamageByEntityEvent) {
			edee = (EntityDamageByEntityEvent) ede;
			Player attacker;
			if (edee.getDamager() instanceof Player) {
				attacker = (Player) edee.getDamager();
				getStorage(victim.getName()).setLastHit(attacker.getName());
				if (!evt.getDamageEvent().isCancelled()) {
					double damage = getArmorRedox(attacker, evt.getDamageEvent().getDamage());
					victim.damage(damage);
					for(ItemStack item: victim.getInventory().getArmorContents()){
						item.setDurability((short)(item.getDurability()-3));
					}
					evt.getDamageEvent().setCancelled(true);
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerEnteredArena(PlayerEnteredArenaEvent evt) {
		if (!hasStorage(evt.getPlayer())) {
			addStorage(evt.getPlayer());
		} else {
			evt.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerQuitInArena(PlayerQuitInArenaEvent evt) {
		Player evtPlayer = evt.getPlayer();
		if (hasStorage(evt.getPlayer())) {
			PlayerStorage pStorage = getStorage(evtPlayer.getName());
			if (evtPlayer.hasPotionEffect(PotionEffectType.SPEED)) {
				evtPlayer.removePotionEffect(PotionEffectType.SPEED);
			}
			if (!OSE.hasOfflineStorage(pStorage.getKey())) {
				OSE.covertPlayerStorage(pStorage);
			}
		}
	}

	public void removeStorage(PlayerStorage pStorage) {
		massStorage.remove(pStorage);
	}

	@Override
	public void runStartupChecks() throws EngineException {
		Engine eng = FreeForAll.getInstance().getEngineManger().getEngine("OfflineStorage");
		if (eng != null) {
			if (eng instanceof OfflineStorageEngine) {
				this.OSE = (OfflineStorageEngine) eng;
			}
		}
		if (OSE == null) {
			throw new EngineException("Missing Dependency.");
		}
	}

	public void safeRestore(Player p) {
		if (!p.isDead()) {
			p.teleport(p.getWorld().getSpawnLocation());
		}
	}

	public void saveAll() {
		for (PlayerStorage ps : massStorage) {
			createOfflineStorage(ps);
		}
	}
}
