package info.plugmania.ijmh.effects;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.inventory.CraftItemEvent;

import info.plugmania.ijmh.Util;
import info.plugmania.ijmh.ijmh;

public class CraftThumb {

	ijmh plugin;
	
	public CraftThumb(ijmh instance){
		plugin = instance;
	}

	public void main(Event e) {
		
		Util.toLog("EventName: " + e.getEventName(), true); // DEBUG
		
		if(Util.config("craftthumb",null).getBoolean("active")){
		
			if(e.getEventName().equalsIgnoreCase("CraftItemEvent")) {
				CraftItemEvent event = (CraftItemEvent) e;
				Player player = (Player) event.getWhoClicked();
				
				if(!player.hasPermission("ijmh.immunity.craftthumb")){
					if(!Util.config("craftthumb",null).getList("skip_world").contains(player.getWorld().getName())) {
						
						int moreCraft = event.getCursor().getAmount();
						if(event.getCursor().getAmount()>0) moreCraft = (event.getCursor().getAmount() / 100)^(event.getCursor().getAmount() / 2);
						if(Util.pctChance(Util.config("craftthumb",null).getInt("chance") / (1 + moreCraft),Util.config("craftthumb",null).getInt("chancemod"))) {
							player.damage(Util.config("craftthumb",null).getInt("damage"));
							if(Util.config("craftthumb",null).getBoolean("message")) player.sendMessage(ChatColor.GOLD + Util.language.getString("lan_09"));
						}
					}
				}
			}
		}
	}
}
