package info.plugmania.ijmh.listeners;

import info.plugmania.ijmh.ijmh;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.BrewEvent;
import org.bukkit.event.inventory.CraftItemEvent;

public class CraftListener implements Listener {

	ijmh plugin;

	public CraftListener(ijmh instance) {
		plugin = instance;
	}

	@EventHandler
	public void onCraftItem(CraftItemEvent event) {
		plugin.playerEffects.addEffectCraft(event);			
	}
	
	@EventHandler
	public void onBrew(BrewEvent event) {
		plugin.playerEffects.addEffectBrew(event);			
	}
	
}
