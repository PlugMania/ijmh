package info.plugmania.ijmh.listeners;

import info.plugmania.ijmh.Util;
import info.plugmania.ijmh.ijmh;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;

public class InventoryListener implements Listener {
	ijmh plugin;

	public InventoryListener(ijmh instance) {
		plugin = instance;
	}
	
	@EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {

		if(event.getPlayer() instanceof Player) {
			if(!event.getPlayer().getGameMode().equals(GameMode.CREATIVE)) {
				plugin.playerEffects.addEffectInventoryClose(event);
			}
		}
	}
}
