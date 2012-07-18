package info.plugmania.ijmh.listeners;

import info.plugmania.ijmh.ijmh;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public class PlayerListener implements Listener {
	
	ijmh plugin;

	public PlayerListener(ijmh instance) {
		plugin = instance;
	}
	
	int[][] items = new int[2][2];
	
	// FLINT & TINDER
	items[259][0] = 0;
	items[259][1] = 0;
	// WATER
	items[326][0] = 0;
	items[326][1] = 0;
	
	@EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		
		if(player.getGameMode().getValue()==0) {
			if(event.hasItem()) {
				int typeId = event.getItem().getTypeId();
				
			}
		}
		if(event.hasItem()) {
			
			int typeId = event.getItem().getTypeId();
			if(player.getGameMode().getValue()==0) {					

			}
		}
    }	
}
