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
