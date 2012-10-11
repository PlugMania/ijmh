package info.plugmania.ijmh.effects;

import java.util.Date;
import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

import info.plugmania.ijmh.Util;
import info.plugmania.ijmh.ijmh;

public class Quicksand {

	ijmh plugin;
	
	public Quicksand(ijmh instance){
		plugin = instance;
	}

	// STORED PLAYERS AND REGISTERED JUMPS
	public HashMap<Player, Integer> quicksand = new HashMap<Player, Integer>();
	// TIMED EVENTS & COOLDOWNS
	public long timer = 0;
	
	public void main(Event e) {
		
		if(Util.config("quicksand",null).getBoolean("active")){
			
			if(e.getEventName().equalsIgnoreCase("PlayerMoveEvent")) {
				PlayerMoveEvent event = (PlayerMoveEvent) e;
				Player player = event.getPlayer();
				Location pUnder = player.getLocation().add(0, -1, 0);
				Location to = event.getTo();
				Location from = event.getFrom();
				Date curDate = new Date();
				long curTime = curDate.getTime();
				
				if(!player.getGameMode().equals(GameMode.CREATIVE) && !Util.config("quicksand",null).getList("skip_world").contains(player.getWorld().getName())) {
					if(!player.hasPermission("ijmh.immunity.quicksand")) {
						if(
								(pUnder.getBlock().getType().equals(Material.SAND) || pUnder.getBlock().getType().equals(Material.SANDSTONE)) &&
								!player.isInsideVehicle() &&
								!event.getTo().getBlock().isLiquid() &&
								(
									to.getBlockX()!=from.getBlockX() ||
									to.getBlockY()!=from.getBlockY() ||
									to.getBlockZ()!=from.getBlockZ()
								)) {
							if(!plugin.quicksand.quicksand.containsKey(player) && Util.pctChance(Util.config("quicksand",null).getInt("chance"),Util.config("quicksand",null).getInt("chancemod"))) {
								plugin.quicksand.quicksand.put(player, 0);
								
								player.teleport(pUnder);
								timer = curTime + (Util.config("quicksand",null).getInt("cooldown") * 1000);
								
								if(Util.config("quicksand",null).getBoolean("message")) player.sendMessage(ChatColor.GOLD + Util.language.getString("lan_21"));
							} else if(event.getFrom().getY() < event.getTo().getY() && plugin.quicksand.quicksand.containsKey(player)) {
								plugin.quicksand.quicksand.put(player, plugin.quicksand.quicksand.get(player)+1);
								Util.toLog("Jumps: " + plugin.quicksand.quicksand.get(player), true);
								player.teleport(event.getFrom());
								if(plugin.quicksand.quicksand.get(player)>=Util.config("quicksand",null).getInt("jumps")) {
									player.teleport(player.getLocation().add(new Vector(0,1,0)));
									if(player.getLocation().getBlock().getType().equals(Material.AIR)) {
										Util.toLog("Removed by AIR", true);
										plugin.quicksand.quicksand.remove(player);
										if(Util.config("quicksand",null).getBoolean("message")) player.sendMessage(ChatColor.GOLD + Util.language.getString("lan_22"));
									}
								} 
								else if(curTime>timer) {
									Util.toLog("Get back", true);
									player.teleport(pUnder);
									timer = curTime + (Util.config("quicksand",null).getInt("cooldown") * 1000);
								}
							} else if(curTime>timer && plugin.quicksand.quicksand.containsKey(player)) {
								player.teleport(pUnder);
								Util.toLog("Get back", true);
								timer = curTime + (Util.config("quicksand",null).getInt("cooldown") * 1000);
							}
							
						} 
						else if(
								plugin.quicksand.quicksand.containsKey(player) &&
								!pUnder.getBlock().getType().equals(Material.SAND) &&
								!pUnder.getBlock().getType().equals(Material.SANDSTONE) &&
								!pUnder.getBlock().getType().equals(Material.AIR) 
								){
							plugin.quicksand.quicksand.remove(player);
							Util.toLog("Removed by pUnder being: "+pUnder.getBlock().getType(), true);
						}
					}
				}
				
			} 
			else if(e.getEventName().equalsIgnoreCase("BlockBreakEvent")) {
				BlockBreakEvent event = (BlockBreakEvent) e;
				Player player = event.getPlayer();
				
				// PREVENT BREAKOUT 
				if(!player.getGameMode().equals(GameMode.CREATIVE) && !Util.config("quicksand",null).getList("skip_world").contains(player.getWorld().getName())) {
					if(plugin.quicksand.quicksand.containsKey(player)) {
						event.setCancelled(true);
					}
				}
			} 
			else if(e.getEventName().equalsIgnoreCase("PlayerCommandPreprocessEvent")) {
				PlayerCommandPreprocessEvent event = (PlayerCommandPreprocessEvent) e;
				Player player = event.getPlayer();
				
				// PREVENT COMMANDS 
				if(plugin.quicksand.quicksand.containsKey(player)) {
					event.setCancelled(true);
				}				
			} 
			else if(e.getEventName().equalsIgnoreCase("AsyncPlayerChatEvent")) {
				AsyncPlayerChatEvent event = (AsyncPlayerChatEvent) e;
				Player player = event.getPlayer();
				
				// PREVENT COMMANDS POSTED IN CHAT
				if(plugin.quicksand.quicksand.containsKey(player)) {
					if(event.getMessage().substring(0, 1)=="/") event.setCancelled(true);
					Util.toLog(event.getMessage().substring(0, 1), true);
				}
			}
			else if(e.getEventName().equalsIgnoreCase("EntityDamageEvent")) {
				EntityDamageEvent event = (EntityDamageEvent) e;
				
				if(event.getEntity() instanceof Player) {
					Player player = (Player) event.getEntity();
					if(!player.getGameMode().equals(GameMode.CREATIVE)) {
						if(event.isCancelled() && plugin.quicksand.quicksand.containsKey(player)) {
							if(event.getCause().equals(DamageCause.SUFFOCATION)) {
								player.damage(event.getDamage());
							}
						}
					}
				}
			}
			else if(e.getEventName().equalsIgnoreCase("PlayerDeathEvent")) {
				PlayerDeathEvent event = (PlayerDeathEvent) e;
				Player player = event.getEntity();
				
				// REMOVE FROM LIST WHEN DEAD
				if(plugin.quicksand.quicksand.containsKey(player)) {
					plugin.quicksand.quicksand.remove(player);
					event.setDeathMessage(player.getName() + " " + Util.language.getString("lan_23"));
				}
			}
		}
	}
}
