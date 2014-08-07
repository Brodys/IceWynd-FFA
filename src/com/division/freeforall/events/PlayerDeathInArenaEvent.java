/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.division.freeforall.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerDeathInArenaEvent extends Event {

	public enum DeathCause {

		ENVIRONMENT,
		PLAYER,
	}
	private static final HandlerList handlers = new HandlerList();
	public static HandlerList getHandlerList() {
		return handlers;
	}

	private Player victim;

	private DeathCause cause;

	public PlayerDeathInArenaEvent(final Player victim, final DeathCause cause) {
		this.victim = victim;
		this.cause = cause;
	}

	public DeathCause getCause() {
		return cause;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public Player getVictim() {
		return victim;
	}

	@Override
	public String toString() {
		return "PDTIAE: " + victim.getName() + " died because of " + cause.name();
	}
}
