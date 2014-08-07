package com.division.freeforall.engines;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import com.division.freeforall.core.FreeForAll;
import com.division.freeforall.core.PlayerStorage;
import com.division.freeforall.events.PlayerDeathInArenaEvent;
import com.division.freeforall.events.PlayerDeathInArenaEvent.DeathCause;
import com.division.freeforall.events.PlayerEnteredArenaEvent;
import com.division.freeforall.events.PlayerKilledPlayerInArenaEvent;
import com.division.freeforall.events.PlayerKillstreakAwardedEvent;
import com.division.freeforall.mysql.DataInterface;

@EngineInfo(author = "mastershake71",
version = "0.2.2DE",
depends = {"Storage"})
public class LeaderboardEngine extends Engine {

	private DataInterface DB;
	private StorageEngine SE = null;

	public LeaderboardEngine(DataInterface DB) {
		this.DB = DB;
	}

	@Override
	public String getName() {
		return ("Leaderboard");
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerDeathInArena(PlayerDeathInArenaEvent evt) {
		Player victim = evt.getVictim();
		if (evt.getCause() == DeathCause.ENVIRONMENT) {
			PlayerStorage storage = SE.getStorage(victim.getName());
			if (storage != null) {
				String lastAttacker = storage.getLastHit();
				DB.incrementKillCount(lastAttacker);
				DB.incrementDeathCount(victim.getName());
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerEnteredArena(PlayerEnteredArenaEvent evt) {
		DB.incrementPlayCount(evt.getPlayer().getName());
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerKilledPlayerInArena(PlayerKilledPlayerInArenaEvent evt) {
		Player victim = evt.getVictim();
		Player killer = evt.getKiller();
		DB.incrementKillCount(killer.getName());
		DB.incrementDeathCount(victim.getName());
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerKillstreakAwarded(PlayerKillstreakAwardedEvent evt) {
		DB.updateKillStreak(evt.getPlayer().getName(), evt.getKillStreak());
	}

	@Override
	public void runStartupChecks() throws EngineException {
		Engine eng = FreeForAll.getInstance().getEngineManger().getEngine("Storage");
		if (eng != null) {
			if (eng instanceof StorageEngine) {
				this.SE = (StorageEngine) eng;
			}
		}
		if (SE == null) {
			throw new EngineException("Missing Storage -Dependency.");
		}
		if (DB == null) {
			throw new EngineException("Missing database communication link.");
		}
	}
}
