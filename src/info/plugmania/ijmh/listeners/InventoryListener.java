package info.plugmania.ijmh.listeners;

import info.plugmania.ijmh.ijmh;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class InventoryListener implements Listener {
	ijmh plugin;

	public InventoryListener(ijmh instance) {
		plugin = instance;
	}
	
	@EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
		if(event.getPlayer() instanceof Player) {
			// HEAVY ARMOR
			plugin.heavyduty.main(event);
		}
	}
}
