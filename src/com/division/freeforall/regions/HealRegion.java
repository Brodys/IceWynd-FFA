package com.division.freeforall.regions;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockVector;
import org.bukkit.util.Vector;

import com.division.freeforall.core.FreeForAll;

public class HealRegion extends Region {

	private FreeForAll FFA;
	private int timer;

	public HealRegion(World world, BlockVector p1, BlockVector p2, FreeForAll instance) {
		super(world, p1, p2);
		this.FFA = instance;
	}

	public List<Player> getPlayersInRegion() {
		List<Player> rangeList = new ArrayList<Player>();
		for (Player p : FFA.getServer().getOnlinePlayers()) {
			Location loc = p.getLocation();
			Vector vec = new Vector(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
			if (this.contains(p.getWorld(), vec)) {
				rangeList.add(p);
			}
		}
		return rangeList;
	}

	public int getTimer() {
		return timer;
	}

	public void heal() {
		List<Player> rangeList = getPlayersInRegion();
		for (Player p : rangeList) {
			if (p.getHealth() != 20) {
				if (!p.isDead()) {
					p.setHealth(p.getHealth() + 1);
				}
			}
		}
	}

	public void startTimer() {
		timer = FFA.getServer().getScheduler().scheduleSyncRepeatingTask(FFA, new Runnable() {

			@Override
			public void run() {
				heal();
			}
		}, 60L, 60L);
	}
}
