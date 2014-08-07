/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.division.freeforall.events;

import java.util.ArrayList;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

public class PlayerKillstreakAwardedEvent extends Event {

	private static final HandlerList handlers = new HandlerList();
	public static HandlerList getHandlerList() {
		return handlers;
	}
	private int killStreak;
	private Player player;

	private ArrayList<ItemStack> rewards;

	public PlayerKillstreakAwardedEvent(final Player p, final int killstreak, ArrayList<ItemStack> rewards) {
		this.killStreak = killstreak;
		this.player = p;
		this.rewards = rewards;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public int getKillStreak() {
		return killStreak;
	}

	public Player getPlayer() {
		return player;
	}

	public ArrayList<ItemStack> getRewards() {
		return rewards;
	}

	@Override
	public String toString() {
		return "PKSAE: " + player.getName() + " achieved KS " + killStreak + " and received " + rewards;
	}
}
