package info.plugmania.ijmh.effects;

import java.util.Date;

import info.plugmania.ijmh.Util;
import info.plugmania.ijmh.ijmh;

import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.material.Wool;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class StickyTar {
	
	ijmh plugin;
	
	public StickyTar(ijmh instance){
		plugin = instance;
	}
	
	public long timer = 0;
	
	public void main(Event e) {
		
		if(Util.config("tar",null).getBoolean("active")){
			
			if(e.getEventName().equalsIgnoreCase("PlayerMoveEvent")) {
				PlayerMoveEvent event = (PlayerMoveEvent) e;
				Player player = event.getPlayer();
				Location pUnder = player.getLocation().add(0, -1, 0);
				Location to = event.getTo();
				Location from = event.getFrom();
				Date curDate = new Date();
				long curTime = curDate.getTime();
				
				if(!Util.config("tar",null).getList("skip_world").contains(player.getWorld().getName())) {
					if(
						!player.getGameMode().equals(GameMode.CREATIVE) &&
						pUnder.getBlock().getType().equals(Material.WOOL) &&
						(
							to.getBlockX()!=from.getBlockX() ||
							to.getBlockY()!=from.getBlockY() ||
							to.getBlockZ()!=from.getBlockZ()
						)) {

						Block block = pUnder.getBlock();
						Wool wool = new Wool(block.getType(), block.getData());
						if(wool.getColor().equals(DyeColor.BLACK)) {
							player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Util.sec2tic(Util.config("tar",null).getInt("duration")), Util.config("tar",null).getInt("multiplier")));
							if(Util.config("tar",null).getBoolean("message")) {
								if(curTime>timer) player.sendMessage(ChatColor.GOLD + Util.language.getString("lan_16"));
								timer = curTime + 10000;
							}
						}
					}
				}
			}
		}
	}
}
