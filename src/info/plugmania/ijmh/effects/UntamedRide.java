package info.plugmania.ijmh.effects;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import info.plugmania.ijmh.Util;
import info.plugmania.ijmh.ijmh;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.event.vehicle.VehicleMoveEvent;
import org.bukkit.util.Vector;

public class UntamedRide {

	ijmh plugin;
	public HashMap<Integer, HashMap<String, String>> c = new HashMap<Integer, HashMap<String, String>>();
	public List<Player> riding = new ArrayList<Player>();
	public long timer = 0;
	
	public UntamedRide(ijmh instance){
		plugin = instance;
	}
	
	public void init() {
		//plugin.feature.put("UntamedRide", "untamedride");
		c.put(0, plugin.util.cRow("skipworld", null, "list", null, null));
		c.put(1, plugin.util.cRow("message", null, "boolean", "true", "true/false/*"));
		c.put(2, plugin.util.cRow("chance", null, "integer", "1", "1-100"));
		c.put(3, plugin.util.cRow("chancemod", null, "integer", "1", "1-?"));
		c.put(4, plugin.util.cRow("distance", null, "integer", "1", "1-?"));
		c.put(5, plugin.util.cRow("angle", null, "integer", "1", "1-?"));
	}	
	
	public boolean command(CommandSender sender, String[] args) {
		if(args.length==1) {
			plugin.util.cSend(c, args, sender);
		} else {
			Util.cmdExecute(sender, args);
		} 
		return true;
	}
	
	public void main(Event e) {
		
		if(Util.config("untamedride",null).getBoolean("active") && plugin.feature.containsValue("untamedride")){

			if(e.getEventName().equalsIgnoreCase("VehicleEnterEvent")) {
				VehicleEnterEvent event = (VehicleEnterEvent) e;
				if(event.getEntered() instanceof Player) {
					Player player = (Player) event.getEntered();
			
					if(!plugin.disabled.contains("ride") && Util.config("ride",null).getBoolean("active") && !Util.config("untamedride",null).getList("skipworld").contains(player.getWorld().getName())) {
					if(!player.hasPermission("ijmh.immunity.ride")) {
							if(event.getVehicle().getType().equals(EntityType.PIG)) {
								plugin.untamedride.riding.add(player);
								Util.toLog(player.getName() + " mounted " + event.getVehicle().getType(), true);
							}
						}
					}
				}	
			}
			else if(e.getEventName().equalsIgnoreCase("VehicleMoveEvent")) {
				VehicleMoveEvent event = (VehicleMoveEvent) e;
				Date curDate = new Date();
				long curTime = curDate.getTime();
				Player player = (Player) event.getVehicle().getPassenger();

				//if(event.getVehicle().getPassenger()!=null) Util.toLog("Vehicle Moving with " + event.getVehicle().getPassenger(), true);
				if(plugin.untamedride.riding.contains(player)) {
					Util.toLog(player.getName() + " is registered", true);
					if(timer>0) {
						if(curTime>timer) {
							event.getVehicle().eject();
							Vector vector = event.getTo().getDirection().midpoint(event.getFrom().getDirection());
							player.setVelocity(new Vector(vector.getX()+Util.config("untamedride",null).getInt("distance"),Util.config("untamedride",null).getInt("angle"),vector.getZ()+Util.config("untamedride",null).getInt("distance")));
							if(Util.config("untamedride",null).getBoolean("message")) player.sendMessage(ChatColor.GOLD + Util.language.getString("lan_34"));
							timer = 0;
						}
					}
					else if(Util.pctChance(Util.config("untamedride",null).getInt("chance"),Util.config("untamedride",null).getInt("chancemod"))) {
						timer = curTime + Util.config("untamedride",null).getInt("limit");
						if(Util.config("untamedride",null).getBoolean("message")) player.sendMessage(ChatColor.GOLD + Util.language.getString("lan_31"));
					}
				}
			} 
			else if(e.getEventName().equalsIgnoreCase("AsyncPlayerChatEvent")) {
				AsyncPlayerChatEvent event = (AsyncPlayerChatEvent) e;
				Player player = event.getPlayer();
				
				// SURVIVAL
				if(plugin.untamedride.riding.contains(player) && plugin.untamedride.timer>0) {
					if(event.getMessage().substring(0, 1)=="/") event.setCancelled(true);
					if(event.getMessage().toLowerCase().contains(Util.language.getString("lan_32").toLowerCase())) {
						event.setCancelled(true);
						plugin.untamedride.timer = 0;
						if(Util.config("untamedride",null).getBoolean("message")) player.sendMessage(ChatColor.GOLD + Util.language.getString("lan_33"));
					}
				}
			}
			else if(e.getEventName().equalsIgnoreCase("PlayerDeathEvent")) {
				PlayerDeathEvent event = (PlayerDeathEvent) e;
				Player player = event.getEntity();
				
				if(plugin.untamedride.riding.contains(player)) {
					plugin.untamedride.riding.remove(player);
				}
			}
			else if(e.getEventName().equalsIgnoreCase("VehicleExitEvent")) {
				VehicleExitEvent event = (VehicleExitEvent) e;
				if(event.getExited() instanceof Player) {
					Player player = (Player) event.getExited();
					
					if(plugin.untamedride.riding.contains(event.getExited())) {
						plugin.untamedride.riding.remove(event.getExited());
						Util.toLog(player.getName() + " unmounted " + event.getVehicle().getType().getName(), true);
					}	
				}	
			}
		}
	}
}
