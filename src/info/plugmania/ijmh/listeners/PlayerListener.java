package info.plugmania.ijmh.listeners;

import info.plugmania.ijmh.Util;
import info.plugmania.ijmh.ijmh;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
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

public class PlayerListener implements Listener {
	
	ijmh plugin;

	public PlayerListener(ijmh instance) {
		plugin = instance;
	}
	
	
	@EventHandler
	public void join(PlayerJoinEvent event){
		Player player = event.getPlayer();
		if((player.hasPermission("ijmh.admin") || player.isOp()) && plugin.getConfig().getBoolean("update_message")){
			plugin.util.checkVersion(false, player, null);
		}
	}
	
	@EventHandler
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
		Player player = event.getPlayer();
		
		// QUICKSAND PREVENT COMMANDS 
		if(Util.config("quicksand",null).getBoolean("active") && !Util.config("quicksand",null).getList("skip_world").contains(player.getWorld().getName())) {
			if(plugin.store.quicksand.containsKey(player)) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent event) {
		Player player = event.getPlayer();
		
		// QUICKSAND PREVENT COMMANDS POSTED 
		if(plugin.store.quicksand.containsKey(player) || plugin.store.drowning.containsKey(player)) {
			if(event.getMessage().substring(0, 1)=="/") event.setCancelled(true);
			Util.toLog(event.getMessage().substring(0, 1), true);
		}
		// UNTAMED RIDE SURVIVAL
		if(plugin.store.riding.contains(player) && plugin.playerEffects.timeLimit>0) {
			if(event.getMessage().substring(0, 1)=="/") event.setCancelled(true);
			if(event.getMessage().toLowerCase().contains(Util.language.getString("lan_32").toLowerCase())) {
				event.setCancelled(true);
				plugin.playerEffects.timeLimit = 0;
				if(Util.config("ride",null).getBoolean("message")) player.sendMessage(ChatColor.GOLD + Util.language.getString("lan_33"));
			}
		}		
	}
	
	@EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
		Player player = event.getPlayer();
		if(player.getGameMode().equals(GameMode.SURVIVAL)) {
			plugin.playerEffects.addEffectInteractEntity(event);
		}
		
	}
	
	@EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		
		if(player.getGameMode().equals(GameMode.SURVIVAL)) {
			if(event.hasItem()) {
				plugin.playerEffects.addEffectInteract(event);
			}
		}
    }	
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		
		if(plugin.store.drowning.containsKey(player)) {
			if(event.getTo().getBlockX()!=event.getFrom().getBlockX() || event.getTo().getBlockZ()!=event.getFrom().getBlockZ() || event.getTo().getBlockY()>event.getFrom().getBlockY()){
				if(event.getTo().getBlockY()>event.getFrom().getBlockY()) event.setCancelled(true);
				else player.teleport(event.getFrom());
			}
		}
		
		if(plugin.store.riding.contains(player)) {
			Util.toLog("" + event.getTo(), true);
		}		
		
		if(player.getGameMode().equals(GameMode.SURVIVAL)) {
			plugin.playerEffects.addEffectMove(event);		
		}
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		
		plugin.playerEffects.addEffectPlayerJoin(event);
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
	}

	@EventHandler
	public void onPlayerGameModeChange(PlayerGameModeChangeEvent event) {
		Player player = event.getPlayer();
		
		plugin.playerEffects.addEffectPlayerGameModeChange(event);
	}	
	
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) {
		Player player = event.getEntity();

		if(plugin.store.desert.contains(player)) {
			plugin.store.drowning.remove(player);
		}
		
		if(plugin.store.riding.contains(player)) {
			plugin.store.riding.remove(player);
		}
		
		if(plugin.store.drowning.containsKey(player)) {
			plugin.store.drowning.remove(player);
			plugin.store.drowning.get(player).breakNaturally();
		}
				
		if(plugin.store.quicksand.containsKey(player)) {
			plugin.store.quicksand.remove(player);
			event.setDeathMessage(player.getName() + " " + Util.language.getString("lan_23"));
		}
		
		if(plugin.store.tnt.contains(player)) {
			plugin.store.tnt.remove(player);
			event.setDeathMessage(player.getName() + " " + Util.language.getString("lan_30"));
		}
		
		plugin.playerEffects.addEffectPlayerDeath(event);
	}
	
	@EventHandler
	public void onPlayerPickupItem(PlayerPickupItemEvent event) {
		Player player = event.getPlayer();
		
		plugin.playerEffects.addEffectPickupItem(event);
	}
	
	@EventHandler
	public void onPlayerFish(PlayerFishEvent event) {
		Player player = event.getPlayer();
		
		if(player.getGameMode().equals(GameMode.SURVIVAL)) {
			plugin.playerEffects.addEffectFish(event);			
		}
	}
	
	@EventHandler
    public void onEntityDamage(EntityDamageEvent event) {		
		if(event.getEntity() instanceof Player) {
			Player player = (Player) event.getEntity();
			if(player.getGameMode().equals(GameMode.SURVIVAL)) {
				plugin.playerEffects.addEffectDamage(event);
				
				if(event.isCancelled() && plugin.store.quicksand.containsKey(player)) {
					if(event.getCause().equals(DamageCause.SUFFOCATION)) {
						player.damage(event.getDamage());
					}
				}
			}
		} else {
			plugin.playerEffects.addEffectDamage(event);
		}
    }
	
}
