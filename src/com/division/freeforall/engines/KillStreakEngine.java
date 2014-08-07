package com.division.freeforall.engines;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.division.freeforall.core.FreeForAll;
import com.division.freeforall.core.PlayerStorage;
import com.division.freeforall.events.PlayerDeathInArenaEvent;
import com.division.freeforall.events.PlayerDeathInArenaEvent.DeathCause;
import com.division.freeforall.events.PlayerKilledPlayerInArenaEvent;
import com.division.freeforall.events.PlayerKillstreakAwardedEvent;

@EngineInfo(author = "mastershake71",
version = "0.2.23RB",
depends = {"Storage", "Inventory"})
public class KillStreakEngine extends Engine {

	StorageEngine SE = null;
	InventoryEngine IE = null;
	public KillStreakEngine() {
	}

	public int addKill(Player killer, Player victim) {
		PlayerStorage pStorage;
		if (killer != null) {
			pStorage = SE.getStorage(killer.getName());
			if (pStorage != null) {
				pStorage.addKill(victim);
				killer.sendMessage(FreeForAll.prefix + "You killed: " + victim.getName());
				victim.sendMessage(FreeForAll.prefix + "You were killed by: " + killer.getName());
				return pStorage.getKillStreak();
			}
		}
		return -1;
	}

	@Override
	public final String getName() {
		return ("Killstreak");
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerDeathInArena(PlayerDeathInArenaEvent evt) {
		Player victim = evt.getVictim();
		if (evt.getCause() == DeathCause.ENVIRONMENT) {
			String lastAttacker = SE.getStorage(victim.getName()).getLastHit();
			@SuppressWarnings("deprecation")
			Player killer = Bukkit.getServer().getPlayer(lastAttacker);
			if (killer != null) {
				Bukkit.getServer().getPluginManager().callEvent(new PlayerKilledPlayerInArenaEvent(victim, killer));
			}
		}
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerKill(PlayerKilledPlayerInArenaEvent evt) {
		int killstreak = addKill(evt.getKiller(), evt.getVictim());
		rewardKillStreak(evt.getKiller());
		FreeForAll.getInstance().getServer().getPluginManager().callEvent(new PlayerKillstreakAwardedEvent(evt.getKiller(), killstreak, new ArrayList<ItemStack>()));
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerKillstreakRewarded(PlayerKillstreakAwardedEvent evt) {
		Player p = evt.getPlayer();
		PlayerInventory pInv = p.getInventory();
		ArrayList<ItemStack> rewards = evt.getRewards();
		PlayerStorage pStorage = SE.getStorage(p.getName());
		int killStreak = evt.getKillStreak();
		if (!pStorage.isRewarded(killStreak)) {
			if (killStreak == 1) {
				// this will switch your starting armor with what ever the armorupgrade1 is, IceWynd, i'd change it all to your need kil the killstreaks considering they're not configurable.
				pInv.remove(new ItemStack(Material.getMaterial(FreeForAll.instance().getConfig().getString("StartingArmor.Weapon1"))));
				pInv.remove(new ItemStack(Material.getMaterial(FreeForAll.instance().getConfig().getString("StartingArmor.Weapon2"))));
				rewards.add(new ItemStack(Material.getMaterial(FreeForAll.instance().getConfig().getString("ArmorUgrade1.Weapon1"))));
				rewards.add(new ItemStack(Material.getMaterial(FreeForAll.instance().getConfig().getString("ArmorUgrade1.Weapon2"))));
				pStorage.rewardKillStreak(killStreak);
				IE.addArmor(p);
			} else if (killStreak == 2) {
				// what you'll receive on the 2nd kill
				pStorage.rewardKillStreak(killStreak);
				IE.addArmor(p);
			} else if (killStreak == 3) {
				// what you'll receive on the 3rd kill
				p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1));
				pStorage.rewardKillStreak(killStreak);
				IE.addArmor(p);
			} else if (killStreak == 4) {
				// what you'll receive on the 4th kill
				pStorage.rewardKillStreak(killStreak);
				IE.addArmor1(p);
			} else if (killStreak == 5) {
				// what you'll receive on the 5th kill
				pStorage.rewardKillStreak(killStreak);
				IE.addArmor1(p);
			} else if (killStreak == 6) {
				// what you'll receive on the 6th kill
				p.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 1));
				pStorage.rewardKillStreak(killStreak);
				IE.addArmor1(p);
			} else if (killStreak == 7) {
				// what you'll receive on the 7th kill
				pStorage.rewardKillStreak(killStreak);
				IE.addArmor1(p);
			} else if (killStreak == 8) {
				// what you'll receive on the 8th kill
				pStorage.rewardKillStreak(killStreak);
				IE.addArmor2(p);
			} else if (killStreak == 9) {
				// what you'll receive on the 9th kill
				p.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, Integer.MAX_VALUE, 1));
				pStorage.rewardKillStreak(killStreak);
				IE.addArmor2(p);
			} else if (killStreak == 10) {
				// what you'll receive on the 10th kill
				pStorage.rewardKillStreak(killStreak);
				IE.addArmor2(p);
			} else if (killStreak == 11) {
				// what you'll receive on the 11th kill
				pStorage.rewardKillStreak(killStreak);
				IE.addArmor2(p);
			} else if (killStreak == 12) {
				// what you'll receive on the 12th kill
				pStorage.rewardKillStreak(killStreak);
				IE.addArmor3(p);
			} else if (killStreak == 13) {
				// what you'll receive on the 13th kill
				pStorage.rewardKillStreak(killStreak);
				IE.addArmor3(p);
			} else if (killStreak == 14) {
				// what you'll receive on the 14th kill
				pStorage.rewardKillStreak(killStreak);
				IE.addArmor3(p);
			} else if (killStreak == 15) {
				// what you'll receive on the 16th kill
				pStorage.rewardKillStreak(killStreak);
				IE.addArmor3(p);
			}
                // i'll make as many kill streaks as you want, even if you want 500 i'll take my time to add it all.
			int damageval = 16421;
			for (int i = 0; i < 2; i++)
				rewards.add(new ItemStack(Material.POTION, 1, (short)damageval));
			// this is what you get every single kill, currently it's 2 splash potion of healing II
		}
	}

	@SuppressWarnings("deprecation")
	public void rewardKillStreak(Player p) {
		PlayerStorage pStorage = SE.getStorage(p.getName());
		System.out.println(pStorage);

		int killStreak = pStorage.getKillStreak();
		double streakVal = killStreak / 3.0;
		if (streakVal >= 1) {
			if (streakVal % 1 > 0) {
				if (!pStorage.isRewarded(killStreak)) {
					// this is kinda complicated, but every time on your 3rd kill streak i believe (we can test) you'll receive money, let's say on your 3rd kill streak it'll do 3 divided by 3 * 50, for you 12th it'll do 12 divided by 3 * 50.
					FreeForAll.getEcon().depositPlayer(p.getName(), streakVal * 50);
					p.sendMessage(FreeForAll.prefix + "You have been awarded $" + streakVal * 50);
					System.out.println("[FreeForAll] Awarded " + p.getName() + " $" + streakVal * 50);
				    Bukkit.getServer().broadcastMessage(FreeForAll.prefix + p.getName() + " is on a " + killStreak + " Kill Streak! Type /ffa to stop them!");
				    // just going to remove this, i'll add how much money you want per kill streak above instead of all this complicated code.
					if (streakVal != 1) {
					}
					pStorage.rewardKillStreak(killStreak);
				}
			}
		}
	}

	@Override
	public void runStartupChecks() throws EngineException {
		Engine eng = FreeForAll.getInstance().getEngineManger().getEngine("Storage");
		if (eng != null) {
			if (eng instanceof StorageEngine) {
				this.SE = (StorageEngine) eng;
			}
		}
		eng = FreeForAll.getInstance().getEngineManger().getEngine("Inventory");
		if (eng != null) {
			if (eng instanceof InventoryEngine) {
				this.IE = (InventoryEngine) eng;
			}
		}
		if (SE == null || IE == null) {
			throw new EngineException("Missing Dependency.");
		}
	}
}