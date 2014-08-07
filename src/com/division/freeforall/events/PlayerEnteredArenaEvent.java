package com.division.freeforall.events;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.util.Vector;

public class PlayerEnteredArenaEvent extends Event implements Cancellable {

	public static final HandlerList handlers = new HandlerList();
	public static HandlerList getHandlerList() {
		return handlers;
	}
	private Player player;
	private Vector from;
	private Vector to;
	private MoveMethod method;

	private boolean cancelled = false;

	public PlayerEnteredArenaEvent(Player p, Vector from, Vector to, final MoveMethod method) {
		this.player = p;
		this.from = from;
		this.to = to;
		this.method = method;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}


	public MoveMethod getMethod(){
		return method;
	}

	public Player getPlayer() {
		return player;
	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}

	@Override
	public String toString() {
		if (!cancelled) {
			return "PEAE: " + player.getName() + " " + method.toString() + " from " + ChatColor.LIGHT_PURPLE + from + ChatColor.WHITE + " to " + ChatColor.LIGHT_PURPLE + to + ChatColor.WHITE + " and entered arena";
		} else {
			return "PEAE: Cancelled " + player.getName() + " " + method.toString() + " from " + ChatColor.LIGHT_PURPLE + from + ChatColor.WHITE + " to " + ChatColor.LIGHT_PURPLE + to + ChatColor.WHITE + " and prevented entering arena";

		}
	}
}
