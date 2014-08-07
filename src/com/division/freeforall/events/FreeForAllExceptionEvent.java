package com.division.freeforall.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class FreeForAllExceptionEvent extends Event {

	private static HandlerList handlers = new HandlerList();
	public static HandlerList getHandlerList() {
		return handlers;
	}

	private Exception ex;

	public FreeForAllExceptionEvent(final Exception ex) {
		this.ex = ex;
	}

	public Exception getException() {
		return ex;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
}
