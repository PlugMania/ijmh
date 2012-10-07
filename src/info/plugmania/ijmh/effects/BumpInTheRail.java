package info.plugmania.ijmh.effects;

import info.plugmania.ijmh.Util;
import info.plugmania.ijmh.ijmh;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.vehicle.VehicleMoveEvent;
import org.bukkit.material.Rails;
import org.bukkit.util.Vector;

public class BumpInTheRail {

	ijmh plugin;
	
	public BumpInTheRail(ijmh instance){
		plugin = instance;
	}
	
	public void main(Event e) {

		Util.toLog("EventName: " + e.getEventName(), true); // DEBUG
		
		if(Util.config("rail",null).getBoolean("active")){

			if(e.getEventName().equalsIgnoreCase("VehicleMoveEvent")) {
				VehicleMoveEvent event = (VehicleMoveEvent) e;
				Player player = (Player) event.getVehicle().getPassenger();
				
				if(!Util.config("rail",null).getList("skip_world").contains(player.getWorld().getName())) {
					if(event.getTo().getBlock().getType().equals(Material.RAILS) && event.getVehicle().getType().equals(EntityType.MINECART)) {
						if(!player.hasPermission("ijmh.immunity.rail")) {
							Rails rail = new Rails(event.getTo().getBlock().getType(), event.getTo().getBlock().getData());
							if(rail.isCurve() && Util.pctChance(Util.config("rail",null).getInt("chance"),Util.config("rail",null).getInt("chancemod"))) {
								event.getVehicle().eject();
								Vector vector = event.getTo().getDirection().midpoint(event.getFrom().getDirection());
								player.setVelocity(new Vector(vector.getX()+Util.config("rail",null).getInt("distance"),Util.config("rail",null).getInt("angle"),vector.getZ()+Util.config("rail",null).getInt("distance")));
								if(Util.config("rail",null).getBoolean("message")) player.sendMessage(ChatColor.GOLD + Util.language.getString("lan_18"));
							}
						}
					}
				}
			} 
		}	
	}	
}
