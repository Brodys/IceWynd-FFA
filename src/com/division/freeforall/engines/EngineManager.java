package com.division.freeforall.engines;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.event.HandlerList;

import com.division.freeforall.core.FreeForAll;

public class EngineManager {

	private Map<String, Engine> activeEngines = new HashMap<String, Engine>();
	private List<Engine> engineQueue = new ArrayList<Engine>();

	public EngineManager() {
	}

	public Engine getEngine(String engineName) {
		if (activeEngines.containsKey(engineName)) {
			return activeEngines.get(engineName);
		}
		return null;
	}

	public String getEngineAuthor(String engineName) {
		Engine eng = activeEngines.get(engineName);
		final EngineInfo engDep = eng.getClass().getAnnotation(EngineInfo.class);
		return engDep.author();
	}

	public Set<String> getEngines() {
		return activeEngines.keySet();
	}

	public String getEngineVersion(String engineName) {
		Engine eng = activeEngines.get(engineName);
		final EngineInfo engDep = eng.getClass().getAnnotation(EngineInfo.class);
		return engDep.version();
	}

	public boolean isRegistered(String engineName) {
		return activeEngines.containsKey(engineName);
	}

	public void registerEngine(Engine engine) throws EngineException {
		final EngineInfo engDep = engine.getClass().getAnnotation(EngineInfo.class);
		if (engDep != null) {
			String[] depends = engDep.depends();
			for (String s : depends) {
				if (!isRegistered(s)) {
					if (!engineQueue.contains(engine)) {
						engineQueue.add(engine);
					}
					return;
				}
			}
		} else {
			throw new EngineException("Unable to find Engine Info.");
		}
		try {
			engine.runStartupChecks();
			if (!activeEngines.containsKey(engine.getName())) {
				activeEngines.put(engine.getName(), engine);
				System.out.println("[FreeForAll] Engine: " + engine.getName() + " has been started.");
				if (engineQueue.contains(engine)) {
					engineQueue.remove(engine);
				}
				FreeForAll.getInstance().getServer().getPluginManager().registerEvents(engine, FreeForAll.getInstance());
				runEngineQueue();
			} else {
				throw new EngineException("[FreeForAll] Engine with name: " + engine.getName() + " already is registered.");
			}
		} catch (EngineException ex) {
			System.out.println("[FreeForAll] Unabled to start Engine: " + engine.getName() + " Reason: " + ex.getMessage());
		}
	}

	public void runEngineQueue() {
		Engine[] tempStorage = new Engine[engineQueue.size()];
		engineQueue.toArray(tempStorage);
		for (Engine eng : tempStorage) {
			try {
				registerEngine(eng);
			} catch (EngineException ex) {
				System.out.println(ex.getMessage());
			}
		}
	}

	public void unregisterAllEngines() {
		Set<String> engines = activeEngines.keySet();
		String[] tempStorage = new String[engines.size()];
		engines.toArray(tempStorage);
		for (String s : tempStorage) {
			unregisterEngine(s);
		}
	}

	public void unregisterEngine(String engineName) {
		Engine eng = activeEngines.get(engineName);
		HandlerList.unregisterAll(eng);
		System.out.println("[FreeForAll] Engine: " + eng.getName() + " has been stopped.");
		activeEngines.remove(engineName);
	}
}
