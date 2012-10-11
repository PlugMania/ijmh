package info.plugmania.ijmh.effects;

import java.util.Date;

import info.plugmania.ijmh.Util;
import info.plugmania.ijmh.ijmh;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class OnFire {

	ijmh plugin;
	
	public OnFire(ijmh instance){
		plugin = instance;
	}
	
	public long timer = 0;
	
	public void main(Event e) {
		
		if(Util.config("fire",null).getBoolean("active")){
			
			if(e.getEventName().equalsIgnoreCase("PlayerInteractEvent")) {
				PlayerInteractEvent event = (PlayerInteractEvent) e;
				Player player = event.getPlayer();
				Date curDate = new Date();
				long curTime = curDate.getTime();
				
				if(!player.hasPermission("ijmh.immunity.fire")) {
					if(!player.getGameMode().equals(GameMode.CREATIVE) && !Util.config("fire",null).getList("skip_world").contains(player.getWorld().getName())) {
						if(player.getItemInHand().getType().equals(Material.FLINT_AND_STEEL) && event.getAction().equals(Action.RIGHT_CLICK_BLOCK)){
							Util.toLog("Fire: Active passed", true);
							if(
								!player.getWorld().getBlockAt(player.getLocation()).isLiquid() && 
								!player.getWorld().hasStorm()
								) {
								if(Util.pctChance(Util.config("fire",null).getInt("chance"),Util.config("fire",null).getInt("chancemod"))) {
									player.setFireTicks(Util.sec2tic(Util.config("fire",null).getInt("duration")));
									if(Util.config("fire",null).getBoolean("message")) player.sendMessage(ChatColor.GOLD + Util.language.getString("lan_01"));
								}
							}
						}
					}
				}
				// PUT OUT FIRE
				if(player.getFireTicks()>0) {
					if(!player.getGameMode().equals(GameMode.CREATIVE) && event.getItem().getType().equals(Material.WATER_BUCKET)) {
						player.setFireTicks(0);
						event.setCancelled(true);
						if(Util.config("fire",null).getBoolean("message")) {
							if(curTime>timer) player.sendMessage(ChatColor.AQUA + Util.language.getString("lan_02"));
							timer = (int) (curTime + 2000);
						}
					}
				}
			}
			else if(e.getEventName().equalsIgnoreCase("PlayerMoveEvent")) {
				PlayerMoveEvent event = (PlayerMoveEvent) e;
				Player player = event.getPlayer();
				Location to = event.getTo();
				Location from = event.getFrom();
				Date curDate = new Date();
				long curTime = curDate.getTime();
				
				// PUT OUT FIRE
				if(
						(to.getBlock().getType().equals(Material.WATER) || to.getBlock().getType().equals(Material.STATIONARY_WATER)) && 
						!from.getBlock().getType().equals(Material.WATER) && 
						!from.getBlock().getType().equals(Material.STATIONARY_WATER) && 
						player.getFireTicks()>0){
						
					if(curTime>timer) player.sendMessage(ChatColor.AQUA + Util.language.getString("lan_02"));
					timer = (curTime + 2000);
				}
			}
		} 
	}
}
