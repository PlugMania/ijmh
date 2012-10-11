package info.plugmania.ijmh.effects;

import info.plugmania.ijmh.Util;
import info.plugmania.ijmh.ijmh;

import java.util.Date;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class RosesHaveThorns {

	ijmh plugin;
	
	public RosesHaveThorns(ijmh instance){
		plugin = instance;
	}
	
	public long timer = 0;
	
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
								to.getBlock().getType().equals(Material.RED_ROSE) &&
								(to.getBlockX()!=from.getBlockX() ||
								 to.getBlockY()!=from.getBlockY() ||
								 to.getBlockZ()!=from.getBlockZ()
							)){
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
