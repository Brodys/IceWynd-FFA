/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.division.freeforall.events;

import static com.division.freeforall.util.LocationTools.toVector;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerQuitInArenaEvent extends Event {

	private static final HandlerList handlers = new HandlerList();
	public static HandlerList getHandlerList() {
		return handlers;
	}

	private Player player;

	public PlayerQuitInArenaEvent(Player p) {
		this.player = p;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public Player getPlayer() {
		return player;
	}

	@Override
	public String toString() {
		return "PQIAE: " + player.getName() + " quit arena at " + ChatColor.LIGHT_PURPLE + toVector(player.getLocation());
	}
}
