package info.plugmania.ijmh.effects;

import java.util.HashMap;

import info.plugmania.ijmh.Util;
import info.plugmania.ijmh.ijmh;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerPickupItemEvent;

public class SneakyPickup {

	ijmh plugin;
	public HashMap<Integer, HashMap<String, String>> c = new HashMap<Integer, HashMap<String, String>>();
	
	public SneakyPickup(ijmh instance){
		plugin = instance;
	}
	
	public void init() {
		plugin.feature.put("SneakyPickup", "sneakypickup");
		c.put(0, plugin.util.cRow("skipworld", null, "list", null, null));
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
		
		if(Util.config("sneakypickup",null).getBoolean("active")){
			if(e.getEventName().equalsIgnoreCase("PlayerPickupItemEvent")) {
				PlayerPickupItemEvent event = (PlayerPickupItemEvent) e;
				Player player = event.getPlayer();
				Location location = player.getLocation();
				
				if(!Util.config("sneakypickup",null).getList("skipworld").contains(player.getWorld().getName())) {
					if(!player.hasPermission("ijmh.immunity.sneaky")) {
						if(!player.isSneaking() && 
								!(
										location.add(1, 0, 0).getBlock().getType().equals(Material.STATIONARY_WATER) || location.add(1, 0, 0).getBlock().getType().equals(Material.WATER) ||
										location.add(-1, 0, 0).getBlock().getType().equals(Material.STATIONARY_WATER) || location.add(-1, 0, 0).getBlock().getType().equals(Material.WATER) ||
										location.add(0, 0, 1).getBlock().getType().equals(Material.STATIONARY_WATER) || location.add(0, 0, 1).getBlock().getType().equals(Material.WATER) ||
										location.add(0, 0, -1).getBlock().getType().equals(Material.STATIONARY_WATER) || location.add(0, 0, -1).getBlock().getType().equals(Material.WATER) ||
										location.add(0, -1, 0).getBlock().getType().equals(Material.STATIONARY_WATER) || location.add(0, -1, 0).getBlock().getType().equals(Material.WATER)
										
								 )) event.setCancelled(true);
					}
				}
			}
		}
	}
}
