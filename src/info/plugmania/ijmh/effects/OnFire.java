package info.plugmania.ijmh.effects;

import java.util.Date;
import java.util.HashMap;

import info.plugmania.ijmh.Util;
import info.plugmania.ijmh.ijmh;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class OnFire {

	ijmh plugin;
	public HashMap<Integer, HashMap<String, String>> c = new HashMap<Integer, HashMap<String, String>>();
	public long timer = 0;
	
	public OnFire(ijmh instance){
		plugin = instance;
	}
	
	public void init() {
		plugin.feature.put("OnFire", "onfire");
		c.put(0, plugin.util.cRow("skipworld", null, "list", null, null));
		c.put(1, plugin.util.cRow("message", null, "boolean", "true", "true/false/*"));
		c.put(2, plugin.util.cRow("chance", null, "integer", "10", "1-100"));
		c.put(3, plugin.util.cRow("chancemod", null, "integer", "1", "1-?"));
		c.put(4, plugin.util.cRow("duration", null, "integer", "300", "1-? seconds"));
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
		
		if(Util.config("onfire",null).getBoolean("active")){
			
			if(e.getEventName().equalsIgnoreCase("PlayerInteractEvent")) {
				PlayerInteractEvent event = (PlayerInteractEvent) e;
				Player player = event.getPlayer();
				Date curDate = new Date();
				long curTime = curDate.getTime();
				
				if(!player.hasPermission("ijmh.immunity.fire")) {
					if(!player.getGameMode().equals(GameMode.CREATIVE) && !Util.config("onfire",null).getList("skipworld").contains(player.getWorld().getName())) {
						if(player.getItemInHand().getType().equals(Material.FLINT_AND_STEEL) && event.getAction().equals(Action.RIGHT_CLICK_BLOCK)){
							Util.toLog("Fire: Active passed", true);
							if(
								!player.getWorld().getBlockAt(player.getLocation()).isLiquid() && 
								!player.getWorld().hasStorm()
								) {
								if(Util.pctChance(Util.config("onfire",null).getInt("chance"),Util.config("onfire",null).getInt("chancemod"))) {
									player.setFireTicks(Util.sec2tic(Util.config("fire",null).getInt("duration")));
									if(Util.config("onfire",null).getBoolean("message")) player.sendMessage(ChatColor.GOLD + Util.chatColorText(Util.language.getString("lan_01")));
								}
							}
						}
					}
				}
				// PUT OUT FIRE
				if(player.getFireTicks()>0 && event.getItem()!=null) {
					if(!player.getGameMode().equals(GameMode.CREATIVE) && event.getItem().getType().equals(Material.WATER_BUCKET)) {
						player.setFireTicks(0);
						event.setCancelled(true);
						if(Util.config("onfire",null).getBoolean("message")) {
							if(curTime>timer) player.sendMessage(ChatColor.AQUA + Util.chatColorText(Util.language.getString("lan_02")));
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
						
					if(curTime>timer) player.sendMessage(ChatColor.AQUA + Util.chatColorText(Util.language.getString("lan_02")));
					timer = (curTime + 2000);
				}
			}
		} 
	}
}
