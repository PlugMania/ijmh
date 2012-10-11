package info.plugmania.ijmh.effects;

import info.plugmania.ijmh.Util;
import info.plugmania.ijmh.ijmh;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public class CowsDoKick {

	ijmh plugin;
	
	public CowsDoKick(ijmh instance){
		plugin = instance;
	}
	
	public long timer = 0;
	
	public void main(Event e) {
		
		if(Util.config("cows",null).getBoolean("active")){
			
			if(e.getEventName().equalsIgnoreCase("PlayerInteractEntityEvent")) {
				PlayerInteractEntityEvent event = (PlayerInteractEntityEvent) e;
				Player player = event.getPlayer();
				Entity entity = event.getRightClicked();
				
				if(!player.getGameMode().equals(GameMode.CREATIVE) && !Util.config("cows",null).getList("skip_world").contains(player.getWorld().getName())) {
					if(entity.getType().equals(EntityType.COW)) {
						if(
							player.getItemInHand().getType().equals(Material.BUCKET) ||
							player.getItemInHand().getType().equals(Material.MILK_BUCKET)
							){			

							Location cowLocation = entity.getLocation();
							Location playerLocation = player.getLocation();
							
							float entityYaw = Math.abs((cowLocation.getYaw() + 180) % 360);
							float playerYaw = Math.abs((playerLocation.getYaw() + 180) % 360);
							float diff = Math.abs(playerYaw - entityYaw);
							int threshhold = 40;
							if(diff > 180 - threshhold && diff < 180 + threshhold){
								if(Util.config("cows",null).getBoolean("message")) player.sendMessage(ChatColor.GOLD + Util.language.getString("lan_10"));
								if(plugin.debug) plugin.getLogger().info("DEBUG: Front " + diff);
							}	
							else if((diff < threshhold - 10  || diff > 360 - threshhold + 10) && (!player.hasPermission("ijmh.immunity.cowskick"))) {
								player.damage(Util.config("cows","kick").getInt("damage"));
								player.setVelocity(new Vector(-entity.getLocation().getDirection().getX()-Util.config("cows","kick").getInt("backwards"),Util.config("cows","kick").getInt("upwards"),-entity.getLocation().getDirection().getZ()-Util.config("cows","kick").getInt("backwards")));
								if(Util.config("cows","kick").getBoolean("message")) player.sendMessage(ChatColor.GOLD + Util.language.getString("lan_11"));
								player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, Util.sec2tic(Util.config("cows","kick").getInt("time")), 1));
								if(plugin.debug) plugin.getLogger().info("DEBUG: Back " + diff);
							} 
							else if(plugin.debug) plugin.getLogger().info("DEBUG: Side " + diff);
						}
					}
				}
			}
		}
	}
}
