package info.plugmania.ijmh.effects;

import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.inventory.CraftItemEvent;

import info.plugmania.ijmh.Util;
import info.plugmania.ijmh.ijmh;

public class CraftThumb {

	ijmh plugin;
	public HashMap<Integer, HashMap<String, String>> c = new HashMap<Integer, HashMap<String, String>>();
	
	public CraftThumb(ijmh instance){
		plugin = instance;
	}
	
	public void init() {
		plugin.feature.put("CraftThumb", "craftthumb");
		c.put(0, plugin.util.cRow("skipworld", null, "list", null, null));
		c.put(1, plugin.util.cRow("message", null, "boolean", "true", "true/false/*"));
		c.put(2, plugin.util.cRow("chance", null, "integer", "10", "1-100"));
		c.put(3, plugin.util.cRow("chancemod", null, "integer", "1", "1-?"));
		c.put(4, plugin.util.cRow("damage", null, "integer", "4", "1=½hearth"));
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
		
		if(Util.config("craftthumb",null).getBoolean("active")){
		
			if(e.getEventName().equalsIgnoreCase("CraftItemEvent")) {
				CraftItemEvent event = (CraftItemEvent) e;
				Player player = (Player) event.getWhoClicked();
				
				if(!player.hasPermission("ijmh.immunity.craftthumb")){
					if(!Util.config("craftthumb",null).getList("skipworld").contains(player.getWorld().getName())) {
						
						int moreCraft = event.getCursor().getAmount();
						if(event.getCursor().getAmount()>0) moreCraft = (event.getCursor().getAmount() / 100)^(event.getCursor().getAmount() / 2);
						if(Util.pctChance(Util.config("craftthumb",null).getInt("chance") / (1 + moreCraft),Util.config("craftthumb",null).getInt("chancemod"))) {
							player.damage(Util.config("craftthumb",null).getInt("damage"));
							if(Util.config("craftthumb",null).getBoolean("message")) player.sendMessage(ChatColor.GOLD + Util.chatColorText(Util.language.getString("lan_09")));
						}
					}
				}
			}
		}
	}
}
