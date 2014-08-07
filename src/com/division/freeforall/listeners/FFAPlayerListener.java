package com.division.freeforall.listeners;

import static com.division.freeforall.util.LocationTools.toVector;

import java.util.Collection;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import com.division.freeforall.core.FreeForAll;
import com.division.freeforall.events.MoveMethod;
import com.division.freeforall.events.PlayerDamageInArenaEvent;
import com.division.freeforall.events.PlayerDeathInArenaEvent;
import com.division.freeforall.events.PlayerDeathInArenaEvent.DeathCause;
import com.division.freeforall.events.PlayerEnteredArenaEvent;
import com.division.freeforall.events.PlayerKilledPlayerInArenaEvent;
import com.division.freeforall.events.PlayerLeftArenaEvent;
import com.division.freeforall.events.PlayerPreCheckEvent;
import com.division.freeforall.events.PlayerQuitInArenaEvent;
import com.division.freeforall.regions.Arena;
import com.division.freeforall.regions.HealRegion;
import com.division.freeforall.regions.Region;
import com.division.freeforall.regions.Selection;

public class FFAPlayerListener implements Listener {
	FreeForAll FFA;

	public FFAPlayerListener(FreeForAll instance) {
		this.FFA = instance;
		FFA.getServer().getPluginManager().registerEvents(this, FFA);

	}

	@EventHandler
	public void drag(InventoryDragEvent e){
		if (e.getInventory().getName().equals(FFA.INVNAME)) return;
	}

	@EventHandler
	public void inventoryClick(final InventoryClickEvent e){
		if (!e.getInventory().getName().equals(FFA.INVNAME)) return;
		e.setCancelled(true);

		if (e.isShiftClick()) return;
		if (e.getCurrentItem() == null || e.getCurrentItem().getType().equals(Material.AIR)) return;
		if (!e.getCurrentItem().hasItemMeta() || !e.getCurrentItem().getItemMeta().hasDisplayName()) return;

		String name = e.getCurrentItem().getItemMeta().getDisplayName();
		if (FFA.hasArena(name)){
			FFA.getServer().dispatchCommand((CommandSender) e.getWhoClicked(), "ffa " + name);
		}

		new BukkitRunnable(){@Override
			public void run() {
			e.getWhoClicked().closeInventory();
		}}.runTask(FFA);
	}

	private boolean isInArena(World world, Vector tFrom) {
		return FFA.isInArena(world, tFrom);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onEntityDamage(EntityDamageEvent evt) {
		if (evt.isCancelled()) {
			return;
		}
		if (!(evt.getEntity() instanceof Player)) {
			return;
		}
		Player evtPlayer = (Player) evt.getEntity();
		if (FFA.isInTeleportQueue(evtPlayer)) {
			FFA.removePlayerFromTeleportQueue(evtPlayer);
			evtPlayer.sendMessage(FreeForAll.prefix + "Teleport has been cancelled.");
			return;
		}
		Location loc = evtPlayer.getLocation();
		World world = evtPlayer.getWorld();
		Vector pt = toVector(loc);

		if (isInArena(world, pt)) {
			if (evt instanceof EntityDamageByEntityEvent) {
				EntityDamageByEntityEvent edee = (EntityDamageByEntityEvent) evt;
				FFA.getServer().getPluginManager().callEvent(new PlayerDamageInArenaEvent(evtPlayer, edee.getDamager(), evt.getCause(), evt));
			} else {
				FFA.getServer().getPluginManager().callEvent(new PlayerDamageInArenaEvent(evtPlayer, null, evt.getCause(), evt));
			}
		}
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerBreak(BlockBreakEvent evt) {
		if (evt.getPlayer().hasPermission("FreeForAll.selection")) {
			if (evt.getPlayer().getItemInHand().getType() == Material.SULPHUR) {
				evt.setCancelled(true);
			}
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerCommandPreProcess(PlayerCommandPreprocessEvent evt) {
		if (evt.getMessage().length() < 4 || evt.getPlayer().hasPermission("FreeForAll.bypass") || evt.getMessage().subSequence(1, 4).equals("ffa")) {
			return;
		}
		World world = evt.getPlayer().getWorld();
		Vector pt = toVector(evt.getPlayer().getLocation());

		if (isInArena(world, pt)) {
			evt.setCancelled(true);
			evt.getPlayer().sendMessage(FreeForAll.prefix  + "You cannot use commands in the arena.");
		}
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerDeath(PlayerDeathEvent evt) {
		Player p = evt.getEntity();
		World world = p.getWorld();
		Vector pt = toVector(p.getLocation());

		if (isInArena(world, pt)) {
			evt.getDrops().clear();
			EntityDamageEvent ede = p.getLastDamageCause();
			if (ede instanceof EntityDamageByEntityEvent) {
				EntityDamageByEntityEvent edee = (EntityDamageByEntityEvent) ede;
				if (edee.getDamager() instanceof Player || p.getKiller() != null) {
					FFA.getServer().getPluginManager().callEvent(new PlayerKilledPlayerInArenaEvent(p, (edee.getDamager() instanceof Player) ?(Player) edee.getDamager() : p.getKiller()));
				} else {
					FFA.getServer().getPluginManager().callEvent(new PlayerDeathInArenaEvent(p, DeathCause.ENVIRONMENT));
				}
			} else {
				FFA.getServer().getPluginManager().callEvent(new PlayerDeathInArenaEvent(p, DeathCause.ENVIRONMENT));
			}
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerDrop(PlayerDropItemEvent evt) {
		World world = evt.getPlayer().getWorld();
		Vector pt = toVector(evt.getPlayer().getLocation());

		if (isInArena(world, pt)) {
			evt.setCancelled(true);
			evt.getItemDrop().remove();
			evt.getPlayer().updateInventory();
			evt.getPlayer().sendMessage(FreeForAll.prefix + "You are not allowed to drop items in this area.");
		}
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerInteract(PlayerInteractEvent evt) {
		if (evt.getPlayer().hasPermission("FreeForAll.selection")) {
			if (evt.hasBlock()) {
				Block evtBlock = evt.getClickedBlock();
				ItemStack iih = evt.getItem();
				final Material mat = FFA.WAND;
				if (iih != null) {
					if (iih.getType().equals(mat)) {
						if (evt.getAction() == Action.LEFT_CLICK_BLOCK) {
							Selection.setP2(toVector(evtBlock));
							evt.getPlayer().sendMessage(ChatColor.LIGHT_PURPLE + "Set Point 1: " + toVector(evtBlock));
							evt.setCancelled(true);
						}
						if (evt.getAction() == Action.RIGHT_CLICK_BLOCK) {
							Selection.setP1(toVector(evtBlock));
							evt.getPlayer().sendMessage(ChatColor.LIGHT_PURPLE + "Set Point 2: " + toVector(evtBlock));
							evt.setCancelled(true);
						}
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerJoin(PlayerJoinEvent evt) {
		if (FreeForAll.getInstance().isUsingLeaderBoards()) {
			FFA.getDataInterface().createPlayerAccount(evt.getPlayer().getName());
		}
		Player evtPlayer = evt.getPlayer();
		World world = evtPlayer.getWorld();
		Vector pt = toVector(evtPlayer.getLocation());

		if (!isInArena(world, pt)) {
			FFA.getServer().getPluginManager().callEvent(new PlayerPreCheckEvent(evtPlayer));
		}
		if (isInArena(world, pt)) {
			FFA.getServer().getPluginManager().callEvent(new PlayerEnteredArenaEvent(evtPlayer, null, pt, MoveMethod.JOINED));
		}
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerKick(PlayerKickEvent evt) {
		Player evtPlayer = evt.getPlayer();
		World world = evtPlayer.getWorld();
		Location loc = evtPlayer.getLocation();
		Vector pt = toVector(loc);

		if (isInArena(world, pt)) {
			FFA.getServer().getPluginManager().callEvent(new PlayerQuitInArenaEvent(evtPlayer));
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerMoveEvent(PlayerMoveEvent evt) {
		if (evt.isCancelled()) {
			return;
		}
		Location from = evt.getFrom();
		Location to = evt.getTo();

		//he didnt move?
				if (from.getBlockX() == to.getBlockX() && from.getBlockY() == to.getBlockY() && from.getBlockZ() == to.getBlockZ()) {
					return;
				}

				Player evtPlayer = evt.getPlayer();
				//check for teleport
				if (FFA.isInTeleportQueue(evtPlayer)) {
					FFA.removePlayerFromTeleportQueue(evtPlayer);
					evtPlayer.sendMessage(FreeForAll.prefix + "Teleport has been cancelled.");
					return;
				}

				Collection<Arena> arenas = FFA.getArenas().values();
				Region region;
				HealRegion healRegion;
				Vector pt, pf;
				World world;

				for (Arena a : arenas){
					region = a.getRegion();
					healRegion = a.getHealRegion();
					pt = toVector(to);
					pf = toVector(from);
					world = evtPlayer.getWorld();


					if (region.contains(world, pt)) {
						if (healRegion != null) {
							if (healRegion.contains(world, pt) && !healRegion.contains(world, pf)) {
								evtPlayer.sendMessage(FreeForAll.prefix  + "You have entered the heal region.");
							}

							if (!healRegion.contains(world, pt) && healRegion.contains(world, pf)) {
								evtPlayer.sendMessage(FreeForAll.prefix + "You have left the heal region.");
							}
						}
					}

					if (region.contains(world, pt) && !region.contains(world, pf)) {
						FreeForAll.getInstance().getServer().getPluginManager().callEvent(new PlayerEnteredArenaEvent(evtPlayer, pf, pt, MoveMethod.MOVED));
					} else if (!region.contains(world, pt) && region.contains(world, pf)) {
						FreeForAll.getInstance().getServer().getPluginManager().callEvent(new PlayerLeftArenaEvent(evtPlayer, pf, pt, MoveMethod.MOVED));
					}
				}
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerQuit(PlayerQuitEvent evt) {
		Player evtPlayer = evt.getPlayer();
		World world = evtPlayer.getWorld();
		Location loc = evtPlayer.getLocation();
		Vector pt = toVector(loc);

		if (FFA.isInTeleportQueue(evtPlayer)) {
			FFA.removePlayerFromTeleportQueue(evtPlayer);
			evtPlayer.sendMessage(FreeForAll.prefix + "Teleport has been cancelled.");
		}
		if (isInArena(world, pt)) {
			FFA.getServer().getPluginManager().callEvent(new PlayerQuitInArenaEvent(evtPlayer));
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerRespawn(PlayerRespawnEvent evt) {
		Player evtPlayer = evt.getPlayer();
		World world = evtPlayer.getWorld();
		Location loc = evt.getRespawnLocation();
		Vector rt = toVector(loc);

		if (!isInArena(world, rt)) {
			FFA.getServer().getPluginManager().callEvent(new PlayerLeftArenaEvent(evtPlayer, null, rt, MoveMethod.RESPAWNED));
		} else {
			FFA.getServer().getPluginManager().callEvent(new PlayerEnteredArenaEvent(evtPlayer, null, rt, MoveMethod.RESPAWNED));
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerTeleport(PlayerTeleportEvent evt) {
		if (!evt.isCancelled()) {
			Player evtPlayer = evt.getPlayer();
			World world = evt.getTo().getWorld();
			Vector tTo = toVector(evt.getTo());
			Vector tFrom = toVector(evt.getPlayer().getLocation());

			if (isInArena(world, tFrom)) {
				evt.setCancelled(true);
				return;
			}

			if (isInArena(world, tTo)) {
				FFA.getServer().getPluginManager().callEvent(new PlayerEnteredArenaEvent(evtPlayer, null, tTo, MoveMethod.TELEPORTED));
			} else if(isInArena(world, tFrom)) {
				FFA.getServer().getPluginManager().callEvent(new PlayerLeftArenaEvent(evtPlayer, null, tTo, MoveMethod.TELEPORTED));
			}
		}
	}
}
