package info.plugmania.ijmh.effects;

import java.util.HashMap;

import info.plugmania.ijmh.Util;
import info.plugmania.ijmh.ijmh;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
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
	public HashMap<Integer, HashMap<String, String>> c = new HashMap<Integer, HashMap<String, String>>();
	public long timer = 0;
	
	public CowsDoKick(ijmh instance){
		plugin = instance;
	}
	
	public void init() {
		plugin.feature.put("CowsDoKick", "cowsdokick");
		plugin.subfeature.put("ki", "kick");
		c.put(0, plugin.util.cRow("skipworld", null, "list", null, null));
		c.put(1, plugin.util.cRow("message", null, "boolean", "true", "true/false/*"));
		c.put(2, plugin.util.cRow("message", "kick", "boolean", "true", null));
		c.put(3, plugin.util.cRow("damage", "kick", "integer", "4", "1=½hearth"));
		c.put(4, plugin.util.cRow("duration", "kick", "integer", "5", "1-? seconds"));
		c.put(5, plugin.util.cRow("backwards", "kick", "integer", "2", "1-?"));
		c.put(6, plugin.util.cRow("upwards", "kick", "integer", "1", "1-?"));
	}	
	
	public boolean command(CommandSender sender, String[] args) {
		if(args.length==1) {
			plugin.util.cSend(c, args, sender);
		} else {
			Util.cmdExecute(sender, args);
		} 
		return true;
	}
	
	public void main(Event e) {
		
		if(Util.config("cowsdokick",null).getBoolean("active")){
			
			if(e.getEventName().equalsIgnoreCase("PlayerInteractEntityEvent")) {
				PlayerInteractEntityEvent event = (PlayerInteractEntityEvent) e;
				Player player = event.getPlayer();
				Entity entity = event.getRightClicked();
				
				if(!player.getGameMode().equals(GameMode.CREATIVE) && !Util.config("cowsdokick",null).getList("skipworld").contains(player.getWorld().getName())) {
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
								if(Util.config("cowsdokick",null).getBoolean("message")) player.sendMessage(ChatColor.GOLD + Util.language.getString("lan_10"));
								if(plugin.debug) plugin.getLogger().info("DEBUG: Front " + diff);
							}	
							else if((diff < threshhold - 10  || diff > 360 - threshhold + 10) && (!player.hasPermission("ijmh.immunity.cowskick"))) {
								player.damage(Util.config("cowsdokick","kick").getInt("damage"));
								player.setVelocity(new Vector(-entity.getLocation().getDirection().getX()-Util.config("cowsdokick","kick").getInt("backwards"),Util.config("cowsdokick","kick").getInt("upwards"),-entity.getLocation().getDirection().getZ()-Util.config("cowsdokick","kick").getInt("backwards")));
								if(Util.config("cowsdokick","kick").getBoolean("message")) player.sendMessage(ChatColor.GOLD + Util.language.getString("lan_11"));
								player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, Util.sec2tic(Util.config("cowsdokick","kick").getInt("time")), 1));
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
