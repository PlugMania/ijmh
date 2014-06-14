package info.plugmania.ijmh.effects;

import java.util.HashMap;

import info.plugmania.ijmh.Util;
import info.plugmania.ijmh.ijmh;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.vehicle.VehicleMoveEvent;
import org.bukkit.material.Rails;
import org.bukkit.util.Vector;

public class BumpInTheRail {

	ijmh plugin;
	public HashMap<Integer, HashMap<String, String>> c = new HashMap<Integer, HashMap<String, String>>();
	
	public BumpInTheRail(ijmh instance){
		plugin = instance;
	}
	
	public void init() {
		plugin.feature.put("BumpInTheRail", "bumpintherail");
		c.put(0, plugin.util.cRow("skipworld", null, "list", null, null));
		c.put(1, plugin.util.cRow("message", null, "boolean", "true", "true/false/*"));
		c.put(2, plugin.util.cRow("chance", null, "integer", "10", "1-100"));
		c.put(3, plugin.util.cRow("chancemod", null, "integer", "1", "1-?"));
		c.put(4, plugin.util.cRow("distance", null, "integer",  "1", "1-?"));
		c.put(5, plugin.util.cRow("angle", null, "integer",  "1", "1-?"));
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
		
		if(Util.config("bumpintherail",null).getBoolean("active")){

			if(e.getEventName().equalsIgnoreCase("VehicleMoveEvent")) {
				VehicleMoveEvent event = (VehicleMoveEvent) e;
				
				if(event.getVehicle().getPassenger() instanceof Player) {
					Player player = (Player) event.getVehicle().getPassenger();
				
					if(!Util.config("bumpintherail",null).getList("skipworld").contains(player.getWorld().getName())) {
						if(event.getTo().getBlock().getType().equals(Material.RAILS) && event.getVehicle().getType().equals(EntityType.MINECART)) {
							if(!player.hasPermission("ijmh.immunity.rail")) {
								Rails rail = (Rails) event.getTo().getBlock().getState().getData();
								if(rail.isCurve() && Util.pctChance(Util.config("bumpintherail",null).getInt("chance"),Util.config("bumpintherail",null).getInt("chancemod"))) {
									event.getVehicle().eject();
									Vector vector = event.getTo().getDirection().midpoint(event.getFrom().getDirection());
									player.setVelocity(new Vector(vector.getX()+Util.config("bumpintherail",null).getInt("distance"),Util.config("bumpintherail",null).getInt("angle"),vector.getZ()+Util.config("bumpintherail",null).getInt("distance")));
									if(Util.config("bumpintherail",null).getBoolean("message")) player.sendMessage(ChatColor.GOLD + Util.chatColorText(Util.language.getString("lan_18")));
								}
							}
						}
					}
				}
			} 
		}	
	}	
}
