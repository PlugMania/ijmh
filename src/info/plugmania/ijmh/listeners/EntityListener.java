package info.plugmania.ijmh.listeners;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.event.vehicle.VehicleMoveEvent;

import info.plugmania.ijmh.Util;
import info.plugmania.ijmh.ijmh;

public class EntityListener implements Listener {

	ijmh plugin;

	public EntityListener(ijmh instance) {
		plugin = instance;
	}
	
	@EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {		
		if(event.getEntity() instanceof Player) {
			Player player = (Player) event.getEntity();
			if(player.getGameMode().equals(GameMode.SURVIVAL)) {
				plugin.playerEffects.addEffectDamage(event);
			}
		} else {
			plugin.playerEffects.addEffectDamageByEntity(event);
		}
    }	
	
	@EventHandler
    public void onVehicleEnter(VehicleEnterEvent event) {
		
		Util.toLog(event.getEntered() + " mounted " + event.getVehicle().getType(), true);
		
		if(event.getEntered() instanceof Player) {
			Player player = (Player) event.getEntered();
	
			if(!plugin.disabled.contains("ride") && Util.config("ride",null).getBoolean("active") && !Util.config("ride",null).getList("skip_world").contains(player.getWorld().getName())) {
			if(!player.hasPermission("ijmh.immunity.ride")) {
					if(Util.config("ride",null).getList("entitytype").contains(event.getVehicle().getType())) {
						plugin.store.riding.add(player);
						Util.toLog(player.getName() + " mounted " + event.getVehicle().getType(), true);
					}
				}
			}
		}
	}
	
	@EventHandler
    public void onVehicleExit(VehicleExitEvent event) {
		if(event.getExited() instanceof Player) {
			Player player = (Player) event.getExited();
			
			if(plugin.store.riding.contains(event.getExited())) {
				plugin.store.riding.remove(event.getExited());
				Util.toLog(player.getName() + " unmounted " + event.getVehicle().getType().getName(), true);
			}	
		}		
	}
	
	@EventHandler
    public void onVehicleMove(VehicleMoveEvent event) {	
		if(event.getVehicle().getPassenger() instanceof Player) {
			plugin.playerEffects.addEffectVehicleMove(event);
		}
	}
	
}
