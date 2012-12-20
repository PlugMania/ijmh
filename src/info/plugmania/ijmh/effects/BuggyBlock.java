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
	public HashMap<Integer, HashMap<String, String>> c = new HashMap<Integer, HashMap<String, String>>();
	
	public BuggyBlock(ijmh instance){
		plugin = instance;
	}
	
	public void init() {
		plugin.feature.put("BuggyBlock", "buggyblock");
		c.put(0, plugin.util.cRow("skipworld", null, "list", null, null));
		c.put(1, plugin.util.cRow("message", null, "boolean", "true", "true/false/*"));
		c.put(2, plugin.util.cRow("chance", null, "integer", "100", "1-100"));
		c.put(3, plugin.util.cRow("chancemod", null, "integer", "1", "1-?"));
		c.put(4, plugin.util.cRow("blocks", null, "list", null, null));
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
		
		if(Util.config("buggyblock",null).getBoolean("active")) {
			
			if(e.getEventName().equalsIgnoreCase("BlockPlaceEvent")) {
				BlockPlaceEvent event = (BlockPlaceEvent) e;
				Player player = (Player) event.getPlayer();
				Block block = event.getBlock();
				
				// PLACED
				if(!Util.config("buggyblock",null).getList("skipworld").contains(player.getWorld().getName())) {
					if(Util.config("buggyblock",null).getList("blocks").contains(block.getType().name())) {
						if(Util.config("buggyblock",null).getBoolean("message")) player.sendMessage(ChatColor.GOLD + Util.chatColorText(Util.language.getString("lan_28")));
					}
				}
			
			} 
			else if(e.getEventName().equalsIgnoreCase("PlayerMoveEvent")) {
				PlayerMoveEvent event = (PlayerMoveEvent) e;
				Player player = event.getPlayer();
				Location pUnder = player.getLocation().add(0, -1, 0);
				
				// BLOCK CHECK
				if(!player.getGameMode().equals(GameMode.CREATIVE) && !Util.config("buggyblock",null).getList("skipworld").contains(player.getWorld().getName())) {
					if(Util.config("buggyblock",null).getList("blocks").contains(pUnder.getBlock().getType().name())) {
						if(Util.pctChance(Util.config("buggyblock",null).getInt("chance"),Util.config("buggyblock",null).getInt("chancemod"))) {
							pUnder.getBlock().breakNaturally();
							event.setCancelled(true);
							if(Util.config("buggyblock",null).getBoolean("message")) player.sendMessage(ChatColor.GOLD + Util.chatColorText(Util.language.getString("lan_29")));
						}
						
					}
				}	
				
			}
		}
	}
}
