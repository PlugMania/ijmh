package info.plugmania.ijmh.effects;

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
import org.bukkit.event.player.PlayerMoveEvent;

public class Electrocution {
	
	ijmh plugin;
	public HashMap<Integer, HashMap<String, String>> c = new HashMap<Integer, HashMap<String, String>>();
	public long timer = 0;
	
	public Electrocution(ijmh instance){
		plugin = instance;
	}
	
	public void init() {
		plugin.feature.put("Electrocution", "electrocution");
		plugin.subfeature.put("hi", "high");
		plugin.subfeature.put("lo", "low");
		c.put(0, plugin.util.cRow("skipworld", null, "list", null, null));
		c.put(1, plugin.util.cRow("message", "high", "boolean", "true", "true/false/*"));
		c.put(2, plugin.util.cRow("chance", "high", "integer", "5", "1-100"));
		c.put(3, plugin.util.cRow("chancemod", "high", "integer", "1", "1-?"));
		c.put(4, plugin.util.cRow("damage", "high", "integer", "8", "1=½hearth"));
		c.put(5, plugin.util.cRow("message", "low", "boolean", "true", null));
		c.put(6, plugin.util.cRow("damage", "low", "integer", "2", null));
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
		
		if(Util.config("electrocution",null).getBoolean("active")){
			
			if(e.getEventName().equalsIgnoreCase("PlayerMoveEvent")) {
				PlayerMoveEvent event = (PlayerMoveEvent) e;
				Player player = event.getPlayer();
				Location to = event.getTo();
				
				if(!player.hasPermission("ijmh.immunity.electro")) {
					if(!Util.config("electrocution",null).getList("skipworld").contains(player.getWorld().getName())) {
						if(
								!player.getGameMode().equals(GameMode.CREATIVE) &&
								!player.isInsideVehicle() &&
								to.getBlock().isBlockPowered() &&
								!(to.getBlock().getType().equals(Material.WOOD_PLATE) || to.getBlock().getType().equals(Material.STONE_PLATE))
								){
							if(Util.pctChance(Util.config("electrocution","high").getInt("chance"),Util.config("electrocution","high").getInt("chancemod"))) {
								player.damage(Util.config("electrocution","high").getInt("damage"));
								if(Util.config("electrocution","high").getBoolean("message")) player.sendMessage(ChatColor.GOLD + Util.chatColorText(Util.language.getString("lan_08")));
							}
							else {
								player.damage(Util.config("electrocution","low").getInt("chance"));
								if(Util.config("electrocution","low").getBoolean("message")) player.sendMessage(ChatColor.GOLD + Util.chatColorText(Util.language.getString("lan_07")));
							}
						}
					}
				}
			}
		}
	}
}
