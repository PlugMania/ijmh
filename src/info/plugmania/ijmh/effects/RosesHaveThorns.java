package info.plugmania.ijmh.effects;

import info.plugmania.ijmh.Util;
import info.plugmania.ijmh.ijmh;

import java.util.Date;
import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class RosesHaveThorns {

	ijmh plugin;
	
	public long timer = 0;
	
	public RosesHaveThorns(ijmh instance){
		plugin = instance;
	}
	
	public void command(CommandSender sender, String[] args) {
		
		if(args.length==1) {
			HashMap<Integer, HashMap<String, String>> c = new HashMap<Integer, HashMap<String, String>>();
			c.put(0, plugin.util.cRow("skipworld", null, "list", null, null));
			c.put(1, plugin.util.cRow("message", null, "boolean", "true", "true/false"));
			c.put(2, plugin.util.cRow("damage", null, "integer", "1", "1-?"));
			c.put(3, plugin.util.cRow("multiplier", null, "integer", "3", "1-5"));
			c.put(4, plugin.util.cRow("duration", null, "integer", "2", "1-? seconds"));
			plugin.util.cSend(c, args, sender);
		}			
	}
	
	public void main(Event e) {
		
		if(Util.config("roses",null).getBoolean("active")){
			
			if(e.getEventName().equalsIgnoreCase("PlayerMoveEvent")) {
				PlayerMoveEvent event = (PlayerMoveEvent) e;
				Player player = event.getPlayer();
				Location to = event.getTo();
				Location from = event.getFrom();
				Date curDate = new Date();
				long curTime = curDate.getTime();
				
				if(!player.hasPermission("ijmh.immunity.roses")) {
					if(!Util.config("roses",null).getList("skip_world").contains(player.getWorld().getName())){
						if(
								!player.getGameMode().equals(GameMode.CREATIVE) &&
								to.getBlock().getType().equals(Material.RED_ROSE)
								){
							player.damage(Util.config("roses",null).getInt("damage"));
							player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Util.sec2tic(Util.config("roses",null).getInt("duration")), Util.config("roses",null).getInt("multiplier")));
							if(
									Util.config("roses",null).getBoolean("message") && 
									to.getBlock().getType()!=from.getBlock().getType()
									) {
								if(curTime>timer) player.sendMessage(ChatColor.GOLD + Util.language.getString("lan_14"));
								timer = curTime + 10000;
							}		
						}
					}
				}
			}
		}
	}
}
