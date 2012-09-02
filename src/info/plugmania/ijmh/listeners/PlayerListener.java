package info.plugmania.ijmh.listeners;

import info.plugmania.ijmh.Util;
import info.plugmania.ijmh.ijmh;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.BrewEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.vehicle.VehicleMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

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
		
		if(player.getGameMode().equals(GameMode.SURVIVAL)) {
			plugin.playerEffects.addEffectMove(event);			
		}
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
	}

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) {
		Player player = event.getEntity();
		
		if(plugin.store.drowning.containsKey(player)) {
			plugin.store.drowning.remove(player);
			plugin.store.drowning.get(player).breakNaturally();
		}
				
		if(plugin.store.quicksand.containsKey(player)) {
			plugin.store.quicksand.remove(player);
			event.setDeathMessage(player.getName() + " " + Util.language.getString("lan_23"));
		}
		
	}
	
	@EventHandler
	public void onPlayerFish(PlayerFishEvent event) {
		Player player = event.getPlayer();
		
		if(player.getGameMode().equals(GameMode.SURVIVAL)) {
			plugin.playerEffects.addEffectFish(event);			
		}
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		Player player = event.getPlayer();
		
		if(plugin.store.drowning.containsKey(player) && event.getBlock().getType().equals(Material.WOOD)) {
			plugin.store.drowning.remove(player);
			player.removePotionEffect(PotionEffectType.BLINDNESS);
			if(Util.config("boat",null).getBoolean("message")) player.sendMessage(ChatColor.GOLD + Util.language.getString("lan_25"));
		}
		
		if(player.getGameMode().equals(GameMode.SURVIVAL)) {
			plugin.playerEffects.addEffectBlockBreak(event);			
		}
	}
	
	@EventHandler
	public void onCraftItem(CraftItemEvent event) {
		plugin.playerEffects.addEffectCraft(event);			
	}
	
	@EventHandler
	public void onBrew(BrewEvent event) {
		plugin.playerEffects.addEffectBrew(event);			
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
    public void onVehicleMove(VehicleMoveEvent event) {	
		if(event.getVehicle().getPassenger() instanceof Player) {
			plugin.playerEffects.addEffectVehicleMove(event);
		}
	}
	
}
