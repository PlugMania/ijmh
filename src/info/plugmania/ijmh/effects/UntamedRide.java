package info.plugmania.ijmh.effects;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import info.plugmania.ijmh.Util;
import info.plugmania.ijmh.ijmh;

import org.bukkit.ChatColor;
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
	
	public UntamedRide(ijmh instance){
		plugin = instance;
	}
	
	public List<Player> riding = new ArrayList<Player>();
	public long timer = 0;
	
	public void main(Event e) {
		
		if(!plugin.disabled.contains("ride") && Util.config("ride",null).getBoolean("active")){

			if(e.getEventName().equalsIgnoreCase("VehicleMoveEvent")) {
				VehicleMoveEvent event = (VehicleMoveEvent) e;
				Date curDate = new Date();
				long curTime = curDate.getTime();
				Player player = (Player) event.getVehicle().getPassenger();

				if(plugin.untamedride.riding.contains(player)) {
					if(Util.config("ride",null).getList("entitytype").contains(event.getVehicle().getType())) {
						if(timer>0) {
							if(curTime>timer) {
								event.getVehicle().eject();
								Vector vector = event.getTo().getDirection().midpoint(event.getFrom().getDirection());
								player.setVelocity(new Vector(vector.getX()+Util.config("ride",null).getInt("distance"),Util.config("ride",null).getInt("angle"),vector.getZ()+Util.config("ride",null).getInt("distance")));
								if(Util.config("ride",null).getBoolean("message")) player.sendMessage(ChatColor.GOLD + Util.language.getString("lan_34"));
								timer = 0;
							}
						}
						else if(Util.pctChance(Util.config("ride",null).getInt("chance"),Util.config("ride",null).getInt("chancemod"))) {
							timer = curTime + Util.config("ride",null).getInt("limit");
							if(Util.config("ride",null).getBoolean("message")) player.sendMessage(ChatColor.GOLD + Util.language.getString("lan_31"));
						}
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
						if(Util.config("ride",null).getBoolean("message")) player.sendMessage(ChatColor.GOLD + Util.language.getString("lan_33"));
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
			else if(e.getEventName().equalsIgnoreCase("VehicleEnterEvent")) {
				VehicleEnterEvent event = (VehicleEnterEvent) e;
				if(event.getEntered() instanceof Player) {
					Player player = (Player) event.getEntered();
			
					if(!plugin.disabled.contains("ride") && Util.config("ride",null).getBoolean("active") && !Util.config("ride",null).getList("skip_world").contains(player.getWorld().getName())) {
					if(!player.hasPermission("ijmh.immunity.ride")) {
							if(Util.config("ride",null).getList("entitytype").contains(event.getVehicle().getType())) {
								plugin.untamedride.riding.add(player);
								Util.toLog(player.getName() + " mounted " + event.getVehicle().getType(), true);
							}
						}
					}
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
