package info.plugmania.ijmh.listeners;

import info.plugmania.ijmh.ijmh;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.material.Door;

public class PlayerListener implements Listener {
	
	ijmh plugin;

	public PlayerListener(ijmh instance) {
		plugin = instance;
	}
	
	
	@EventHandler()
	public void join(PlayerJoinEvent event){
		Player player = event.getPlayer();
		if(player.hasPermission("ijmh.admin") || player.isOp()){
			plugin.util.checkVersion(false, player, null);
		}
	}
	
	@EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		// Block block = event.getClickedBlock();
		
		if(player.getGameMode().equals(GameMode.SURVIVAL)) {
			if(event.hasItem()) {
				plugin.playerEffects.addEffectInteract(event);
			}
			/*else if(block.getType() == Material.WOODEN_DOOR){
				Door door = (Door) block.getState().getData();
				if(plugin.debug) plugin.getLogger().info("Door state is " + door.);
			} */
		}
    }	
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		
		if(player.getGameMode().equals(GameMode.SURVIVAL)) {
			plugin.playerEffects.addEffectMove(event);			
		}
	}

	@EventHandler
	public void onCraftItem(CraftItemEvent event) {
		plugin.playerEffects.addEffectCraft(event);			
	}
	
	@EventHandler
    public void onEntityRegainHealth(EntityRegainHealthEvent event) {		
		if(event.getEntity() instanceof Player) {
			Player player = (Player) event.getEntity();
			if(player.getGameMode().equals(GameMode.SURVIVAL)) {
				if(event.getRegainReason().equals(RegainReason.EATING)) {
					plugin.playerEffects.addEffectRegainHealth(event);
				}
			}
		}
    }
	
	@EventHandler
    public void onEntityDamage(EntityDamageEvent event) {		
		if(event.getEntity() instanceof Player) {
			Player player = (Player) event.getEntity();
			if(player.getGameMode().equals(GameMode.SURVIVAL)) {
				plugin.playerEffects.addEffectDamage(event);
			}
		}
    }
	
}
