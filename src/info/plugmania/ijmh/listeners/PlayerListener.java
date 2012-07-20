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
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;
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
				PlayerEffects.addEffectInteract(event);
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
	
	@EventHandler
    public void onEntityRegainHealth(EntityRegainHealthEvent event) {		
		if(event.getEntity() instanceof Player) {
			Player player = (Player) event.getEntity();
			if(player.getGameMode().equals(GameMode.SURVIVAL)) {
				if(event.getRegainReason().equals(RegainReason.EATING)) {
					PlayerEffects.addEffectRegainHealth(event);
				}
			}
		}
    }
	
	@EventHandler
    public void onEntityDamage(EntityDamageEvent event) {		
		if(event.getEntity() instanceof Player) {
			Player player = (Player) event.getEntity();
			if(player.getGameMode().equals(GameMode.SURVIVAL)) {
				PlayerEffects.addEffectDamage(event);
			}
		}
    }
	
	@EventHandler
	public void onBlockRedstoneEvent(BlockRedstoneEvent event){
	plugin.playerEffects.addEffectRedstoneElectrocution(event);
	}
	
}
