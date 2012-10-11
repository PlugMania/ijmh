package info.plugmania.ijmh.effects;

import info.plugmania.ijmh.Util;
import info.plugmania.ijmh.ijmh;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerMoveEvent;

public class Electrocution {
	
	ijmh plugin;
	
	public Electrocution(ijmh instance){
		plugin = instance;
	}
	
	public long timer = 0;
	
	public void main(Event e) {
		
		if(Util.config("electro",null).getBoolean("active")){
			
			if(e.getEventName().equalsIgnoreCase("PlayerMoveEvent")) {
				PlayerMoveEvent event = (PlayerMoveEvent) e;
				Player player = event.getPlayer();
				Location to = event.getTo();
				Location from = event.getFrom();
				
				if(!player.hasPermission("ijmh.immunity.electro")) {
					if(!Util.config("electro",null).getList("skip_world").contains(player.getWorld().getName())) {
						if(
								!player.getGameMode().equals(GameMode.CREATIVE) &&
								!player.isInsideVehicle() &&
								to.getBlock().isBlockPowered() &&
								!(to.getBlock().getType().equals(Material.WOOD_PLATE) || to.getBlock().getType().equals(Material.STONE_PLATE)) &&
								(
								to.getBlockX()!=from.getBlockX() ||
								to.getBlockY()!=from.getBlockY() ||
								to.getBlockZ()!=from.getBlockZ()
								)
								){
							if(Util.pctChance(Util.config("electro","high").getInt("chance"),Util.config("electro","high").getInt("chancemod"))) {
								player.damage(Util.config("electro","high").getInt("damage"));
								if(Util.config("electro","high").getBoolean("message")) player.sendMessage(ChatColor.GOLD + Util.language.getString("lan_08"));
							}
							else {
								player.damage(Util.config("electro","low").getInt("chance"));
								if(Util.config("electro","low").getBoolean("message")) player.sendMessage(ChatColor.GOLD + Util.language.getString("lan_07"));
							}
						}
					}
				}
			}
		}
	}
}
