package com.division.freeforall.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerPreCheckEvent extends Event {

	private static final HandlerList handlers = new HandlerList();
	public static HandlerList getHandlerList() {
		return handlers;
	}

	private Player player;

	public PlayerPreCheckEvent(final Player player) {
		this.player = player;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public Player getPlayer(){
		return player;
	}
}
