/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.division.freeforall.events;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.util.Vector;

public class PlayerLeftArenaEvent extends Event {

	private static final HandlerList handlers = new HandlerList();
	public static HandlerList getHandlerList() {
		return handlers;
	}
	private Player player;
	private Vector from;
	private Vector to;

	private MoveMethod method;

	public PlayerLeftArenaEvent(final Player p, final Vector from, final Vector to, final MoveMethod method) {
		this.player = p;
		this.from = from;
		this.to = to;
		this.method = method;
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
		return "PLAE: " + player.getName() + " " + method.toString() + " from " + ChatColor.LIGHT_PURPLE + from + ChatColor.WHITE + " to " + ChatColor.LIGHT_PURPLE + to + ChatColor.WHITE + " and left arena";
	}
}
