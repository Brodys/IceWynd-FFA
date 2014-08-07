/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.division.freeforall.events;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

public class PlayerDamageInArenaEvent extends Event {

	private static final HandlerList handlers = new HandlerList();
	public static HandlerList getHandlerList() {
		return handlers;
	}
	private Player victim;
	private EntityDamageEvent damageEvt;
	private Entity damager;

	private DamageCause cause;

	public PlayerDamageInArenaEvent(final Player victim, final Entity damager, final DamageCause cause, final EntityDamageEvent damageEvt) {
		this.victim = victim;
		this.damageEvt = damageEvt;
		this.damager = damager;
		this.cause = cause;
	}

	public DamageCause getCause() {
		return cause;
	}

	public EntityDamageEvent getDamageEvent() {
		return damageEvt;
	}

	public Entity getDamager() {
		return damager;
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
		if (damager != null) {
			return "PDIAE: " + victim.getName() + " took " + damageEvt.getDamage() + " damage from " + damager.getType();
		} else {
			return "PDIAE: " + victim.getName() + " took " + damageEvt.getDamage() + " damage from " + cause.name();
		}
	}
}
