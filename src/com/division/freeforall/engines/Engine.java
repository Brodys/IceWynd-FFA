package com.division.freeforall.engines;

import org.bukkit.event.Listener;

public abstract class Engine implements Listener {

	public abstract String getName();

	public void runStartupChecks() throws EngineException {
	}
}
