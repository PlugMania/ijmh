package info.plugmania.ijmh.effects;

import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
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
	
	public void command(CommandSender sender, String[] args) {
		try {
			if(args.length==1) {
				HashMap<Integer, HashMap<String, String>> c = new HashMap<Integer, HashMap<String, String>>();
				c.put(0, plugin.util.cRow("skipworld", null, "list", null, null));
				c.put(1, plugin.util.cRow("message", null, "boolean", "true", "true/false"));
				c.put(2, plugin.util.cRow("chance", null, "integer", "10", "1-100"));
				c.put(3, plugin.util.cRow("chancemod", null, "integer", "1", "1-?"));
				c.put(4, plugin.util.cRow("blocks", null, "list", null, null));
				plugin.util.cSend(c, args, sender);
			}
		} catch (Exception e) {
			sender.sendMessage(ChatColor.RED + "An error occured.");
		}
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
