package com.division.freeforall.core;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BlockVector;
import org.bukkit.util.Vector;

import com.division.freeforall.config.FFAConfig;
import com.division.freeforall.engines.DebugEngine;
import com.division.freeforall.engines.Engine;
import com.division.freeforall.engines.EngineException;
import com.division.freeforall.engines.EngineManager;
import com.division.freeforall.engines.InventoryEngine;
import com.division.freeforall.engines.KillStreakEngine;
import com.division.freeforall.engines.LeaderboardEngine;
import com.division.freeforall.engines.OfflineStorageEngine;
import com.division.freeforall.engines.StorageEngine;
import com.division.freeforall.listeners.FFAPlayerListener;
import com.division.freeforall.mysql.DataInterface;
import com.division.freeforall.mysql.MySQLc;
import com.division.freeforall.regions.Arena;
import com.division.freeforall.regions.HealRegion;
import com.division.freeforall.regions.Region;
import com.division.freeforall.regions.Selection;

public class FreeForAll extends JavaPlugin implements Listener {

	public class Queue extends BukkitRunnable {

		@SuppressWarnings("deprecation")
		@Override
		public void run() {
			Set<String> players = teleportQueue.keySet();
			Player p;
			for (String player : players){
				String values[] = teleportQueue.get(player).split(":");

				if (Long.parseLong(values[1]) <= System.currentTimeMillis()) {
					teleportQueue.remove(player);
					if ((p = getServer().getPlayer(player)) != null){
						randomTeleport(values[0], p);
					}
				}
			}
		}
	}
	public static String prefix = ChatColor.BLUE + "[" + ChatColor.DARK_AQUA + "FreeForAll" + ChatColor.BLUE +"] " + ChatColor.BLUE;
	public Material WAND = Material.SULPHUR;
	public String INVNAME = prefix + "Arenas";
	FileConfiguration config;

	File cfile;

	private HashMap<String, Arena> arenas = new HashMap<String, Arena>();
	private static FFAConfig ffaconfig;
	private HashMap<String, String> teleportQueue = new HashMap<String, String>();
	FFAPlayerListener ffapl;
	private DataInterface DB = null;
	private boolean usingDataBaseLeaderBoards = false;
	private static Economy econ;
	private static FreeForAll instance;
	public static Economy getEcon() {
		return econ;
	}

	public static FreeForAll getInstance() {
		return instance;

	}

	public static JavaPlugin instance() {
		return instance;
	}

	private EngineManager engineManager = new EngineManager();

	private boolean isDebugMode = false;

	public void addPlayerToTeleportQueue(String arena, Player p) {
		teleportQueue.put(p.getName(), arena + ":" + (System.currentTimeMillis() + 5000));
	}

	public Arena getArena(String name) {
		return arenas.get(name);
	}

	public HashMap<String, Arena> getArenas() {
		return arenas;
	}

	private Inventory getArenasInventory(Player player) {
		Inventory i = getServer().createInventory(player, ((int) Math.floor(arenas.size() / 9) ) + (arenas.size() % 8 == 0 ? 0 : 9), INVNAME);

		List<String> list = new LinkedList<String>();
		ItemStack item = new ItemStack(Material.DIAMOND_SWORD, 1);;
		ItemMeta itemMeta = item.getItemMeta();

		for (String name : arenas.keySet()){
			list.clear();
			list.add(ChatColor.DARK_AQUA + "Click here to join the " + name + " arena!");

			itemMeta.setDisplayName(name);
			itemMeta.setLore(list);
			item.setItemMeta(itemMeta);

			i.addItem(item);
		}
		return i;
	}

	public DataInterface getDataInterface() {
		return DB;
	}

	public EngineManager getEngineManger() {
		return engineManager;
	}

	public FFAConfig getFFAConfig() {
		return ffaconfig;
	}

	public HealRegion getHealRegion(String arena) {
		return arenas.get(arena).getHealRegion();
	}

	public Region getRegion(String arena) {
		return arenas.get(arena).getRegion();
	}

	public boolean hasArena(String name) {
		return arenas.containsKey(name);
	}

	private boolean hasSpawns(String arena){
		return ffaconfig.getSpawns(arena) != null;
	}

	public boolean isDebugMode() {
		return this.isDebugMode;
	}

	public boolean isInArena(Player p){
		return isInArena(p.getWorld(), p.getLocation().toVector());
	}

	public boolean isInArena(World world, Vector tFrom) {
		Collection<Arena> arenas = getArenas().values();
		for (Arena a : arenas){
			if (a.getRegion().contains(world, tFrom)) return true;
		}
		return false;
	}

	public boolean isInTeleportQueue(Player p) {
		return teleportQueue.containsKey(p.getName());
	}

	public boolean isUsingLeaderBoards() {
		return usingDataBaseLeaderBoards;
	}

	private void loadArenas() {
		for (String s : ffaconfig.getArenas()){
			arenas.put(s, new Arena(ffaconfig.getRegion(s), ffaconfig.getHealRegion(s)));
			System.out.println("[FreeForAll] Loaded arena " + s);
		}
		this.saveDefaultConfig();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
		Player player = null;
		if (sender instanceof Player) {
			player = (Player) sender;
		}
		if (command.getName().equalsIgnoreCase("ffa")) {
			if (player != null) {
				if (args.length >= 1) {
					if (args.length > 1){
						String arenaname = args[1];

						if (args[0].equalsIgnoreCase("define") && player.hasPermission(command.getPermission() + ".define")) {
							BlockVector p1 = Selection.getP1();
							BlockVector p2 = Selection.getP2();
							if (p1 != null && p2 != null) {
								ffaconfig.setRegion(arenaname, player.getWorld(), p1, p2);
								arenas.put(arenaname, new Arena(ffaconfig.getRegion(args[1]), null));
								sender.sendMessage(prefix + "Region has been defined.");
								return true;
							} else {
								sender.sendMessage(prefix + "You need to select both points");
								return true;
							}
						}
						else if (!arenas.containsKey(arenaname)){
							sender.sendMessage(ChatColor.DARK_BLUE + "Arena not found!");
							return true;
						}

						if (args.length > 1) {
							if (args[0].equalsIgnoreCase("setspawn") && player.hasPermission(command.getPermission() + ".setspawn")) {
								ffaconfig.setSpawn(arenaname, args[2], player.getLocation());
								sender.sendMessage(prefix + "spawn " + args[2] + " has been set in arena " + arenaname + ".");
								return true;
							}
							if (args[0].equalsIgnoreCase("removespawn") && player.hasPermission(command.getPermission() + ".removespawn")) {
								if (ffaconfig.removeSpawn(arenaname, args[2])) {
									sender.sendMessage(prefix + "Spawn " + args[2] + " has been removed from arena " + arenaname + ".");
									return true;
								}
								sender.sendMessage(prefix + "Unable to find spawn " + args[2]);
								return true;
							}

							sender.sendMessage(prefix + "incorrect number of args.");
							sender.sendMessage(prefix + "/ffa setspawn/removespawn [arena] [name]");
							return true;
						}

						if (args[0].equalsIgnoreCase("healregion") && player.hasPermission(command.getPermission() + ".healregion")) {
							BlockVector p1 = Selection.getP1();
							BlockVector p2 = Selection.getP2();
							if (p1 != null && p2 != null) {
								ffaconfig.setHealRegion(arenaname, player.getWorld(), p1, p2);
								registerHealRegion(arenaname, ffaconfig.getHealRegion(arenaname));
								arenas.get(arenaname).setRegion(ffaconfig.getRegion(arenaname));
								sender.sendMessage(prefix + "Heal region has been defined.");
								return true;
							} else {
								sender.sendMessage(prefix + "You need to select both points");
								return true;
							}
						} else if (args[0].equalsIgnoreCase("spawns") && player.hasPermission(command.getPermission() + ".spawns")) {
							sender.sendMessage(ChatColor.DARK_AQUA + " Spawns from arena " + arenaname + ": " + ChatColor.BLUE + Arrays.toString(ffaconfig.getSpawnNames(arenaname).toArray()).replace("[", "").replace("]", ""));
							return true;
						}

						return false;
					} else if (args[0].equalsIgnoreCase("reload") && player.hasPermission(command.getPermission() + ".reload")) {
						config = YamlConfiguration.loadConfiguration(cfile);
						reloadConfig();
						ffaconfig.load();
						loadArenas();
						sender.sendMessage(ChatColor.DARK_AQUA + "[FreeForAll]" + ChatColor.BLUE + " config reloaded.");
						return true;
					} else if (args[0].equalsIgnoreCase("shutdown") && player.hasPermission(command.getPermission() + ".shutdown")) {
						getServer().getPluginManager().disablePlugin(this);
						sender.sendMessage(prefix + "has been shut down.");
						return true;
					} else if (args[0].equalsIgnoreCase("version")) {
						sender.sendMessage(ChatColor.DARK_AQUA + "Name: " + ChatColor.BLUE + this.getDescription().getName());
						sender.sendMessage(ChatColor.DARK_AQUA + "Description: " + ChatColor.BLUE + this.getDescription().getDescription());
						if (this.getDescription().getVersion().contains("EB")) {
							sender.sendMessage(ChatColor.DARK_AQUA + "Version: " + ChatColor.BLUE + this.getDescription().getVersion() + ChatColor.DARK_AQUA + " (" + ChatColor.DARK_BLUE + "Experimental" + ChatColor.DARK_AQUA + ")");
						} else {
							sender.sendMessage(ChatColor.DARK_AQUA + "Version: " + ChatColor.BLUE + this.getDescription().getVersion());
						}

						return true;
					} else if (args[0].equalsIgnoreCase("debug")) {
						if (this.isDebugMode) {
							this.isDebugMode = false;
							sender.sendMessage(prefix + "Debug mode disabled.");
						} else {
							this.isDebugMode = true;
							sender.sendMessage(prefix + "Debug mode enabled.");
						}
						return true;
					} else if (args[0].equalsIgnoreCase("list")){
						sender.sendMessage(prefix + "Arenas: " + Arrays.toString(arenas.keySet().toArray()).replace("[", "").replace("]", ""));
						return true;
					} else if (arenas.containsKey(args[0])){
						final Player fp = player;

						if (isInArena(fp)){
							sender.sendMessage(prefix + "You are already in an arena!");
							return true;
						} else if (!hasSpawns(args[0])){
							sender.sendMessage(prefix + "Arena " + args[0] + " does not have any spawnpoints defined!");
							sender.sendMessage(prefix + "Arenas: " + Arrays.toString(arenas.keySet().toArray()).replace("[", "").replace("]", ""));
							return true;
						}


						if(isInTeleportQueue(fp)){
							removePlayerFromTeleportQueue(fp);
						}

						addPlayerToTeleportQueue(args[0], fp);
						sender.sendMessage(prefix + "Teleporting to the " + args[0] + " arena. Please wait 5 seconds.");
						return true;

					} else if (args[0].equalsIgnoreCase("top") && player.hasPermission(command.getPermission() + ".top")) {
						String dispFormat = ChatColor.BLUE + "{0}: " + ChatColor.BLUE + "{1}" + ChatColor.DARK_AQUA + " ---- " + ChatColor.BLUE + " {2}";
						String titleFormat = (ChatColor.DARK_AQUA + "---------==[" + ChatColor.BLUE + "FFA Top {0}" + ChatColor.DARK_AQUA + "]==---------");
						String bottomFormat = ChatColor.DARK_AQUA + "-----------==[" + ChatColor.BLUE + "FreeForAll" + ChatColor.DARK_AQUA + "]==-----------";
						int count = 1;
						if (args.length == 2) {
							if (args[1].equalsIgnoreCase("kills")) {
								player.sendMessage(titleFormat.replace("{0}", "Kills"));
								ArrayList<String> top_Kills = DB.getTopKills();
								for (String name : top_Kills) {
									int player_id = DB.getPlayerId(name);
									int kill_count = DB.getKillCount(player_id);
									player.sendMessage(dispFormat.replace("{0}", "" + count).replace("{2}", name).replace("{1}", "" + kill_count));
									count++;
								}
								player.sendMessage(bottomFormat);
							}
							if (args[1].equalsIgnoreCase("streak")) {
								player.sendMessage(titleFormat.replace("{0}", "KillStreak"));
								ArrayList<String> top_Streaks = DB.getTopStreak();
								for (String name : top_Streaks) {
									int player_id = DB.getPlayerId(name);
									int kill_streak = DB.getKillStreak(player_id);
									player.sendMessage(dispFormat.replace("{0}", "" + count).replace("{2}", name).replace("{1}", "" + kill_streak));
									count++;
								}
								player.sendMessage(bottomFormat);
							}
						}
						if (args.length == 1) {
							player.sendMessage(titleFormat.replace("{0}", "Kills"));
							ArrayList<String> top_Kills = DB.getTopKills();
							for (String name : top_Kills) {
								int player_id = DB.getPlayerId(name);
								int kill_count = DB.getKillCount(player_id);
								player.sendMessage(dispFormat.replace("{0}", "" + count).replace("{2}", name).replace("{1}", "" + kill_count));
								count++;
							}
							player.sendMessage(bottomFormat);
						}
						if (args.length > 2) {
							player.sendMessage(prefix + "Invalid number of arguements.");
							player.sendMessage(prefix + "/ffa top <kills;streak>");
						}
						return true;
					} else if (args[0].equalsIgnoreCase("stats")) {
						String titleFormat = (ChatColor.DARK_AQUA + "---------==[" + ChatColor.BLUE + "{0}'s Stats" + ChatColor.DARK_AQUA + "]==---------");
						String dispFormat = ChatColor.BLUE + "{0} " + ChatColor.DARK_AQUA + ":" + ChatColor.BLUE + " {1}";
						String bottomFormat = ChatColor.DARK_AQUA + "-----------==[" + ChatColor.BLUE + "FreeForAll" + ChatColor.DARK_AQUA + "]==-----------";
						if (args.length == 2) {
							int player_id = DB.getPlayerId(args[1]);
							if (player_id != 0) {
								double kills = DB.getKillCount(player_id);
								int killstreak = DB.getKillStreak(player_id);
								double deaths = DB.getDeathCount(player_id);
								double kdr = 0;
								if (deaths > 0) {
									kdr = roundTwoDecimals(kills / deaths);
								}
								player.sendMessage(titleFormat.replace("{0}", args[1]));
								player.sendMessage(dispFormat.replace("{0}", "Kills").replace("{1}", "" + kills));
								player.sendMessage(dispFormat.replace("{0}", "Streak").replace("{1}", "" + killstreak));
								player.sendMessage(dispFormat.replace("{0}", "Deaths").replace("{1}", "" + deaths));
								player.sendMessage(dispFormat.replace("{0}", "K/D Ratio").replace("{1}", "" + kdr));
								player.sendMessage(bottomFormat);
							} else {
								player.sendMessage(ChatColor.DARK_AQUA + "[FreeForAll]" + ChatColor.BLUE + " Unable to find player.");
							}
						} else if (args.length == 1) {
							int player_id = DB.getPlayerId(player.getName());
							double kills = DB.getKillCount(player_id);
							int killstreak = DB.getKillStreak(player_id);
							double deaths = DB.getDeathCount(player_id);
							double kdr = 0;
							if (deaths > 0) {
								kdr = roundTwoDecimals(kills / deaths);
							}
							player.sendMessage(titleFormat.replace("{0}", player.getName()));
							player.sendMessage(dispFormat.replace("{0}", "Kills").replace("{1}", "" + kills));
							player.sendMessage(dispFormat.replace("{0}", "Streak").replace("{1}", "" + killstreak));
							player.sendMessage(dispFormat.replace("{0}", "Deaths").replace("{1}", "" + deaths));
							player.sendMessage(dispFormat.replace("{0}", "K/D Ratio").replace("{1}", "" + kdr));
							player.sendMessage(bottomFormat);
						}
						return true;
					}
				} else {
					player.openInventory(getArenasInventory(player));
				}
			} else {
				sender.sendMessage("[FreeForAll] requires a player.");
				return true;
			}
		}
		// this is the message it'll show when you type /ffa, going to change for you.
		sender.sendMessage(ChatColor.DARK_AQUA+ "------------------==[" + ChatColor.BLUE + "FreeForAll " + ChatColor.DARK_AQUA + "]==------------------");
		sender.sendMessage(ChatColor.DARK_AQUA + "                         IceWynd Free For All");
		sender.sendMessage("");
		sender.sendMessage(ChatColor.DARK_AQUA + "                    Plugin coded just for IceWynd! ");
		sender.sendMessage(ChatColor.DARK_AQUA + "-------------------==[" + ChatColor.BLUE + "IceWynd" + ChatColor.DARK_AQUA + "]==------------------");
		return true;
	}

	@Override
	public void onDisable() {
		System.out.println("[FreeForAll] moving RAM storage to Offline Storage.");
		Engine engine = engineManager.getEngine("Storage");
		if (engine instanceof StorageEngine) {
			StorageEngine se = (StorageEngine) engine;
			se.saveAll();
		}
		System.out.println("[FreeForAll] all RAM storage converted to Offline Storage.");
		System.out.println("[FreeForAll] Unloading engines...");
		engineManager.unregisterAllEngines();
		System.out.println("[FreeForAll] Done unloading engines.");
		System.out.println("[FreeForAll] has been disabled.");
	}

	@Override
	public void onEnable() {
		config = getConfig();
		config.options().copyDefaults(true);
		saveConfig();
		cfile = new File(getDataFolder(), "config.yml");
		instance = this;
		getServer().getPluginManager().registerEvents(this, this);
		this.saveDefaultConfig();
		if (setupEconomy()) {
			FreeForAll.instance = this;
			FreeForAll.ffaconfig = new FFAConfig(this);
			ffaconfig.load();

			loadArenas();
			new Queue().runTaskTimer(this, 20, 20);

			System.out.println("[FreeForAll] arenas loaded.");
			try {
				DB = new MySQLc(this);
				usingDataBaseLeaderBoards = true;

				try {
					engineManager.registerEngine(new LeaderboardEngine(DB));
				} catch (EngineException ex) {
					System.out.println(ex.getMessage());
				}
			} catch (Exception ex) {
				System.out.println(ex.getMessage());
				System.out.println("Can't load database");
			}
			System.out.println("[FreeForAll] Starting base engines...");
			try {
				engineManager.registerEngine(new InventoryEngine());
			} catch (EngineException ex) {
				System.out.println(ex.getMessage());
			}
			try {
				engineManager.registerEngine(new OfflineStorageEngine());
			} catch (EngineException ex) {
				System.out.println(ex.getMessage());
			}
			try {
				engineManager.registerEngine(new StorageEngine());
			} catch (EngineException ex) {
				System.out.println(ex.getMessage());
			}

			try {
				engineManager.registerEngine(new KillStreakEngine());
			} catch (EngineException ex) {
				System.out.println(ex.getMessage());
			}
			try {
				engineManager.registerEngine(new DebugEngine());
			} catch (EngineException ex) {
				System.out.println(ex.getMessage());
			}
			System.out.println("[FreeForAll] Done starting base engines.");
			ffapl = new FFAPlayerListener(this);
			System.out.println("[FreeForAll] has been enabled.");

			if (this.getDescription().getVersion().contains("EB")) {
				Bukkit.getConsoleSender().sendMessage(ChatColor.BLUE + "[FreeForAll] YOU ARE USING AN EXPERIMENTAL BUILD. USE AT YOUR OWN RISK.");
			}
		} else {
			System.out.println("[FreeForAll] unable to load vault.");
			getServer().getPluginManager().disablePlugin(this);
		}
	}

	private void randomTeleport(String arena, Player p) {
		ArrayList<Location> spawns = ffaconfig.getSpawns(arena);
		Random num = new Random();
		if (spawns != null && spawns.size() > 0) {
			p.closeInventory();
			p.teleport(spawns.get(num.nextInt(spawns.size()-1)));
		} else {
			p.sendMessage(prefix + "Unable to find spawn points :(");
		}
	}

	public void registerHealRegion(String arena, HealRegion healRegion) {
		if (getHealRegion(arena) != null) {
			getServer().getScheduler().cancelTask(getHealRegion(arena).getTimer());
		}
		if (healRegion != null) {
			arenas.get(arena).setHealRegion(healRegion);
			healRegion.startTimer();
		}
	}

	public void removePlayerFromTeleportQueue(Player p) {
		teleportQueue.remove(p.getName());
	}

	private double roundTwoDecimals(double d) {
		DecimalFormat twoDForm = new DecimalFormat("#.##");
		return Double.valueOf(twoDForm.format(d));
	}

	public void setRegion(String arena, Region region) {
		if (!arenas.containsKey(arena)) return;
		arenas.get(arena).setRegion(region);
	}

	private boolean setupEconomy() {
		if (getServer().getPluginManager().getPlugin("Vault") == null) {
			return false;
		}
		RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
		if (rsp == null) {
			return false;
		}
		econ = rsp.getProvider();
		return econ != null;
	}
}
