package info.plugmania.ijmh.effects;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import info.plugmania.ijmh.Util;
import info.plugmania.ijmh.ijmh;

public class BuggyBlock {

	ijmh plugin;
	
	public BuggyBlock(ijmh instance){
		plugin = instance;
	}
	
	public void main(Event e) {
		
		if(Util.config("tnt",null).getBoolean("active")) {
			
			if(e.getEventName().equalsIgnoreCase("BlockPlaceEvent")) {
				BlockPlaceEvent event = (BlockPlaceEvent) e;
				Player player = (Player) event.getPlayer();
				Block block = event.getBlock();
				
				// PLACED
				if(!Util.config("buggyblock",null).getList("skip_world").contains(player.getWorld().getName())) {
					if(Util.config("buggyblock",null).getList("blocks").contains(block.getType().name())) {
						if(Util.config("buggyblock",null).getBoolean("message")) player.sendMessage(ChatColor.GOLD + Util.language.getString("lan_28"));
					}
				}
			
			} 
			else if(e.getEventName().equalsIgnoreCase("PlayerMoveEvent")) {
				PlayerMoveEvent event = (PlayerMoveEvent) e;
				Player player = event.getPlayer();
				Location pUnder = player.getLocation().add(0, -1, 0);
				Location to = event.getTo();
				Location from = event.getFrom();
				
				// BLOCK CHECK
				if(!player.getGameMode().equals(GameMode.CREATIVE) && !Util.config("buggyblock",null).getList("skip_world").contains(player.getWorld().getName())) {
					if(
						Util.config("buggyblock",null).getList("blocks").contains(pUnder.getBlock().getType().name()) &&
						(
							to.getBlockX()!=from.getBlockX() ||
							to.getBlockY()!=from.getBlockY() ||
							to.getBlockZ()!=from.getBlockZ()
						)) {
			
						if(Util.pctChance(Util.config("buggyblock",null).getInt("chance"),Util.config("buggyblock",null).getInt("chancemod"))) {
							
							pUnder.getBlock().breakNaturally();
							event.setCancelled(true);
							if(Util.config("buggyblock",null).getBoolean("message")) player.sendMessage(ChatColor.GOLD + Util.language.getString("lan_29"));
						}
						
					}
				}	
				
			}
		}
	}
}
