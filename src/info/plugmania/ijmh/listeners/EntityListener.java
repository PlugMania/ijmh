package info.plugmania.ijmh.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.event.vehicle.VehicleMoveEvent;

import info.plugmania.ijmh.ijmh;

public class EntityListener implements Listener {

	ijmh plugin;

	public EntityListener(ijmh instance) {
		plugin = instance;
	}
	
	@EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {		
			plugin.squiddefense.main(event); // SQUID DEFENSE
			plugin.bowbreaker.main(event); // BOW BREAKER
    }	
	
	@EventHandler
    public void onVehicleEnter(VehicleEnterEvent event) {
		plugin.untamedride.main(event); // UNTAIMED RIDE
	}
	
	@EventHandler
    public void onVehicleExit(VehicleExitEvent event) {
		plugin.untamedride.main(event); // UNTAIMED RIDE		
	}
	
	@EventHandler
    public void onVehicleMove(VehicleMoveEvent event) {
		plugin.untamedride.main(event); // UNTAIMED RIDE
		plugin.rowyourboat.main(event); // ROW YOUR BOAT
		plugin.bumpintherail.main(event); // BUMP IN THE RAIL
	}
	
}
