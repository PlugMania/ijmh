package info.plugmania.ijmh.effects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import info.plugmania.ijmh.Util;
import info.plugmania.ijmh.ijmh;

public class DizzyInTheDesert {

	ijmh plugin;
	
	public List<Player> desert = new ArrayList<Player>();
	
	public DizzyInTheDesert(ijmh instance){
		plugin = instance;
	}

	public void command(CommandSender sender, String[] args) {
		
		if(args.length==1) {
			HashMap<Integer, HashMap<String, String>> c = new HashMap<Integer, HashMap<String, String>>();
			c.put(0, plugin.util.cRow("skipworld", null, "list", null, null));
			c.put(1, plugin.util.cRow("whendesert", null, "boolean", "true", "true/false"));
			c.put(2, plugin.util.cRow("message", null, "boolean", "true", "true/false"));
			c.put(3, plugin.util.cRow("chance", null, "integer", "1", "1-100"));
			c.put(4, plugin.util.cRow("chancemod", null, "integer", "1", "1-?"));
			c.put(5, plugin.util.cRow("multiplier", null, "integer", "2", "1-5"));
			plugin.util.cSend(c, args, sender);
		}
	}
	
	public void main(Event e) {
		
		if(Util.config("desert",null).getBoolean("active")){
			
			if(e.getEventName().equalsIgnoreCase("PlayerInteractEvent")) {
				PlayerInteractEvent event = (PlayerInteractEvent) e;	
				Player player = event.getPlayer();
				if(!player.getGameMode().equals(GameMode.CREATIVE) && event.hasItem()) {		
					// CURE
					if(plugin.dizzyinthedesert.desert.contains(player)) {
						if(event.getItem().getType().equals(Material.WATER_BUCKET)) {
							plugin.dizzyinthedesert.desert.remove(player);
							player.removePotionEffect(PotionEffectType.SLOW);
							player.removePotionEffect(PotionEffectType.CONFUSION);
							if(Util.config("desert",null).getBoolean("message")) player.sendMessage(ChatColor.GOLD + Util.language.getString("lan_27"));
						}
					}
				}
			}
			else if(e.getEventName().equalsIgnoreCase("PlayerDeathEvent")) {
				PlayerDeathEvent event = (PlayerDeathEvent) e;
				Player player = event.getEntity();

				if(plugin.dizzyinthedesert.desert.contains(player)) {
					plugin.dizzyinthedesert.desert.remove(player);
				}
			}
			else if(e.getEventName().equalsIgnoreCase("PlayerMoveEvent")) {
				PlayerMoveEvent event = (PlayerMoveEvent) e;
				Player player = event.getPlayer();
				Location pUnder = player.getLocation().add(0, -1, 0);
			
				if(!player.getGameMode().equals(GameMode.CREATIVE) && !Util.config("desert",null).getList("skip_world").contains(player.getWorld().getName())) {
					if(
							!player.hasPermission("ijmh.immunity.desert") && 
							!player.getWorld().isThundering() && 
							player.getWorld().getTime()<Util.sec2tic(600) &&
							(Util.config("desert",null).getBoolean("whendesert") && player.getLocation().getBlock().getBiome().equals(Biome.DESERT))
							) {
						if(!plugin.dizzyinthedesert.desert.contains(player)) {
							if(
									pUnder.getBlock().getType().equals(Material.SAND) &&
									!player.isInsideVehicle() &&
									!event.getTo().getBlock().isLiquid()
									) {
								if(Util.pctChance(Util.config("desert",null).getInt("chance"),Util.config("desert",null).getInt("chancemod"))) {
									Util.toLog("was here " + player.getWorld().getTime(), true);
									plugin.dizzyinthedesert.desert.add(player);
									player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Util.sec2tic(60), Util.config("desert",null).getInt("multiplier")));
									player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, Util.sec2tic(60), Util.config("desert",null).getInt("multiplier")));
									if(Util.config("desert",null).getBoolean("message")) player.sendMessage(ChatColor.GOLD + Util.language.getString("lan_26"));
								}
							}
						}
					}	
				}
				if(plugin.dizzyinthedesert.desert.contains(player)) {
					if(player.getWorld().isThundering() || !pUnder.getBlock().getType().equals(Material.SAND)) {
						Util.toLog("was here too " + player.getWorld().getTime(), true);
						plugin.dizzyinthedesert.desert.remove(player);
						player.removePotionEffect(PotionEffectType.SLOW);
						player.removePotionEffect(PotionEffectType.CONFUSION);
					}
				} 
				else if(plugin.dizzyinthedesert.desert.contains(player) && !player.hasPotionEffect(PotionEffectType.CONFUSION)) {
					player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Util.sec2tic(60), Util.config("desert",null).getInt("multiplier")));
					player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, Util.sec2tic(60), Util.config("desert",null).getInt("multiplier")));	
				}
			}
		}
	}
}
