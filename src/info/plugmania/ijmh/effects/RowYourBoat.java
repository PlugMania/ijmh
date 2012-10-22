package info.plugmania.ijmh.effects;

import java.util.HashMap;

import info.plugmania.ijmh.Util;
import info.plugmania.ijmh.ijmh;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.vehicle.VehicleMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public class RowYourBoat {

	ijmh plugin;
	
	public HashMap<Player, Block> drowning = new HashMap<Player, Block>();
	
	public RowYourBoat(ijmh instance){
		plugin = instance;
	}
	
	public void command(CommandSender sender, String[] args) {
		
		if(args.length==1) {
			HashMap<Integer, HashMap<String, String>> c = new HashMap<Integer, HashMap<String, String>>();
			c.put(0, plugin.util.cRow("skipworld", null, "list", null, null));
			c.put(1, plugin.util.cRow("message", null, "boolean", "true", "true/false"));
			c.put(2, plugin.util.cRow("chance", null, "integer", "1", "1-100"));
			c.put(3, plugin.util.cRow("chancemod", null, "integer", "1", "1-?"));
			plugin.util.cSend(c, args, sender);
		}
	}
	
	public void main(Event e) {
		
		if(Util.config("boat",null).getBoolean("active")){
			
			if(e.getEventName().equalsIgnoreCase("VehicleMoveEvent")) {
				VehicleMoveEvent event = (VehicleMoveEvent) e;
				if(event.getVehicle().getPassenger() instanceof Player) {
					Player player = (Player) event.getVehicle().getPassenger();
					
					if(!Util.config("boat",null).getList("skip_world").contains(player.getWorld().getName())) {
						if(!player.hasPermission("ijmh.immunity.boat")) {	
							if(
									event.getVehicle().getType().equals(EntityType.BOAT) &&
									(
										event.getTo().getBlockX()!=event.getFrom().getBlockX() ||
										event.getTo().getBlockY()!=event.getFrom().getBlockY() ||
										event.getTo().getBlockZ()!=event.getFrom().getBlockZ()
									)) {
								
								int y = 0;
								while(player.getLocation().add(new Vector(0,-1,0)).getBlock().isLiquid() && y<=10){
									y++;
								}
								
								if(y>2-1) {
									if(Util.pctChance(Util.config("boat",null).getInt("chance"),Util.config("boat",null).getInt("chancemod"))) {
										player.eject();
										event.getVehicle().remove();
										
										if(Util.config("boat",null).getBoolean("message")) player.sendMessage(ChatColor.GOLD + Util.language.getString("lan_24"));
										
										if(y<5) {
											Block block = player.getLocation().add(new Vector(0,-2,0)).getBlock();
											block.setType(Material.WOOD);
											player.teleport(block.getLocation());
											
											player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, Util.sec2tic(60), 1));
											plugin.rowyourboat.drowning.put(player, block);
										}
										else {
											Block block = player.getLocation().add(new Vector(0,-4,0)).getBlock();
											block.setType(Material.WOOD);
											player.teleport(block.getLocation());
											
											player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, Util.sec2tic(60), 1));
											plugin.rowyourboat.drowning.put(player, block);
										}
									}
								}
							}
						}
					}
				}
			}
			else if(e.getEventName().equalsIgnoreCase("PlayerMoveEvent")) {
				PlayerMoveEvent event = (PlayerMoveEvent) e;
				Player player = event.getPlayer();
				
				// PREVENT MOVING UP UNTIL BLOCK IS DESTROYED
				if(plugin.rowyourboat.drowning.containsKey(player)) {
					if(event.getTo().getBlockX()!=event.getFrom().getBlockX() || event.getTo().getBlockZ()!=event.getFrom().getBlockZ() || event.getTo().getBlockY()>event.getFrom().getBlockY()){
						if(event.getTo().getBlockY()>event.getFrom().getBlockY()) event.setCancelled(true);
						else player.teleport(event.getFrom());
					}
				}
			}
			else if(e.getEventName().equalsIgnoreCase("AsyncPlayerChatEvent")) {
				AsyncPlayerChatEvent event = (AsyncPlayerChatEvent) e;
				Player player = event.getPlayer();
				
				// PREVENT COMMANDS POSTED 
				if(plugin.rowyourboat.drowning.containsKey(player)) {
					if(event.getMessage().substring(0, 1)=="/") event.setCancelled(true);
					Util.toLog(event.getMessage().substring(0, 1), true);
				}
			}
			else if(e.getEventName().equalsIgnoreCase("PlayerDeathEvent")) {
				PlayerDeathEvent event = (PlayerDeathEvent) e;
				Player player = event.getEntity();
				
				if(plugin.rowyourboat.drowning.containsKey(player)) {
					plugin.rowyourboat.drowning.get(player).breakNaturally();
					plugin.rowyourboat.drowning.remove(player);
				}
			}
			else if(e.getEventName().equalsIgnoreCase("BlockBreakEvent")) {
				BlockBreakEvent event = (BlockBreakEvent) e;
				Player player = event.getPlayer();
				
				if(plugin.rowyourboat.drowning.containsKey(player) && event.getBlock().getType().equals(Material.WOOD)) {
					plugin.rowyourboat.drowning.remove(player);
					player.removePotionEffect(PotionEffectType.BLINDNESS);
					if(Util.config("boat",null).getBoolean("message")) player.sendMessage(ChatColor.GOLD + Util.language.getString("lan_25"));
				}
			}
		}
	}
}
