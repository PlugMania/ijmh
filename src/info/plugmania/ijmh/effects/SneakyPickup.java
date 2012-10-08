package info.plugmania.ijmh.effects;

import info.plugmania.ijmh.Util;
import info.plugmania.ijmh.ijmh;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerPickupItemEvent;

public class SneakyPickup {

	ijmh plugin;
	
	public SneakyPickup(ijmh instance){
		plugin = instance;
	}
	
	public void main(Event e) {
		
		if(Util.config("sneaky",null).getBoolean("active")){
			
			if(e.getEventName().equalsIgnoreCase("PlayerPickupItemEvent")) {
				PlayerPickupItemEvent event = (PlayerPickupItemEvent) e;
				Player player = event.getPlayer();
				
				if(!Util.config("sneaky",null).getList("skip_world").contains(player.getWorld().getName())) {
					if(!player.hasPermission("ijmh.immunity.sneaky")) {
						if(!player.isSneaking()) event.setCancelled(true);
					}
				}
			}
		}
	}
}
