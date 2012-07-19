package info.plugmania.ijmh.listeners;

import info.plugmania.ijmh.ijmh;
import info.plugmania.ijmh.effects.PlayerEffects;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.world.WorldEvent;

public class PlayerListener implements Listener {
	
	ijmh plugin;

	public PlayerListener(ijmh instance) {
		plugin = instance;
	}
	
	@EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		
		if(player.getGameMode().equals(GameMode.SURVIVAL)) {
			if(event.hasItem()) {
				PlayerEffects.addEffectInteract(event.getItem().getTypeId(), event);
			}
		}
    }	
	
	@EventHandler
    public void onEntityRegainHealth(EntityRegainHealthEvent event) {		
		if(event.getEntity() instanceof Player) {
			Player player = (Player) event.getEntity();
			if(player.getGameMode().equals(GameMode.SURVIVAL)) {
				if(event.getRegainReason().name()=="EATING") {
					PlayerEffects.addEffectRegainHealth(event.getRegainReason().name(), event);
				}
			}
		}
    }
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		
		if(player.getGameMode().equals(GameMode.SURVIVAL)) {
			PlayerEffects.addEffectMove(event);			
		}
	}
	
}
