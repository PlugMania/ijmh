package info.plugmania.ijmh.listeners;

import info.plugmania.ijmh.Util;
import info.plugmania.ijmh.ijmh;


import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

public class PlayerListener implements Listener {
	
	ijmh plugin;

	public PlayerListener(ijmh instance) {
		plugin = instance;
	}
	
	
	@EventHandler
	public void join(PlayerJoinEvent event){
		Player player = event.getPlayer();
		if((player.hasPermission("ijmh.admin") || player.isOp()) && plugin.getConfig().getBoolean("update_message")){
			plugin.util.checkVersion(false, player, null, plugin, plugin.getFile);
		}
		
		plugin.heavyduty.main(event); // HEAVY DUTY
	}
	
	@EventHandler
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
		plugin.quicksand.main(event); // QUICKSAND
	}

	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent event) {
		plugin.untamedride.main(event); // UNTAIMED RIDE
		plugin.rowyourboat.main(event); // ROW YOUR BOAT
		plugin.quicksand.main(event); // QUICKSAND
	}
	
	@EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
		plugin.cowsdokick.main(event); // COWS DO KICK!		
	}
	
	@EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
		plugin.dizzyinthedesert.main(event); // DIZZY IN THE DESERT
		plugin.onfire.main(event); // ON FIRE
		plugin.foodpoisoning.main(event); // FOODPOISONING
		plugin.crazycombat.main(event); // CRAZY COMBAT
    }	
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		Location to = event.getTo();
		Location from = event.getFrom();
		
		if(to.getBlockX()!=from.getBlockX() || to.getBlockY()!=from.getBlockY() || to.getBlockZ()!=from.getBlockZ()) {
			plugin.dizzyinthedesert.main(event); // DIZZY IN THE DESERT
			plugin.rowyourboat.main(event); // ROW YOUR BOAT
			plugin.concussion.main(event); // CONCUSSION
			plugin.struckbylightning.main(event); // STRUCK BY LIGHTNING
			plugin.roseshavethorns.main(event); // ROSES HAVE THORNS
			plugin.stickytar.main(event); // STICKY TAR
			plugin.electrocution.main(event); // ELECTROCUTION
			plugin.onfire.main(event); // ON FIRE
			plugin.quicksand.main(event); // QUICKSAND
			plugin.buggyblock.main(event); // BUGGY BLOCK
		}
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Util.toLog(event.getPlayer().getFlySpeed() + "", true);
		plugin.heavyduty.main(event); // HEAVY DUTY
		plugin.neardeath.main(event); // NEAR DEATH
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
	}

	@EventHandler
	public void onPlayerGameModeChange(PlayerGameModeChangeEvent event) {
		plugin.heavyduty.main(event); // HEAVY DUTY
	}	
	
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) {
		plugin.dizzyinthedesert.main(event); // DIZZY IN THE DESERT
		plugin.untamedride.main(event); // UNTAIMED RIDE
		plugin.rowyourboat.main(event); // ROW YOUR BOAT
		plugin.quicksand.main(event); // QUICKSAND
		plugin.unstabletnt.main(event); // UNSTABLE TNT
		plugin.zombienation.main(event); // ZOMBIE NATION
	}
	 
	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		Util.toLog(event.getPlayer().getFlySpeed() + "", true);
		plugin.neardeath.main(event); // NEAR DEATH
	}
	
	@EventHandler
	public void onPlayerPickupItem(PlayerPickupItemEvent event) {		
		plugin.sneakypickup.main(event); // SNEAKY PICKUP
	}
	
	@EventHandler
	public void onPlayerFish(PlayerFishEvent event) {
		plugin.fishermanonhook.main(event); // FISHERMAN ON HOOK
	}
	
	@EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {				
		plugin.crazycombat.main(event); // CRAZY COMBAT
		plugin.neardeath.main(event); // NEAR DEATH
    }
	
	@EventHandler
    public void onEntityDamage(EntityDamageEvent event) {				
		plugin.concussion.main(event); // CONCUSSION
		plugin.quicksand.main(event); // QUICKSAND
		plugin.neardeath.main(event); // NEAR DEATH
    }

	@EventHandler
    public void onEntityRegainHealth(EntityRegainHealthEvent event) {				
		plugin.neardeath.main(event); // NEAR DEATH
    }
	
}
