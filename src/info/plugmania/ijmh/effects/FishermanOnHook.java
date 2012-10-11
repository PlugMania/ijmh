package info.plugmania.ijmh.effects;

import info.plugmania.ijmh.Util;
import info.plugmania.ijmh.ijmh;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerFishEvent.State;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class FishermanOnHook {

	ijmh plugin;
	
	public FishermanOnHook(ijmh instance){
		plugin = instance;
	}
	
	@SuppressWarnings("deprecation")
	public void main(Event e) {
		
		if(Util.config("fishing",null).getBoolean("active")){
			
			if(e.getEventName().equalsIgnoreCase("PlayerFishEvent")) {
				PlayerFishEvent event = (PlayerFishEvent) e;
				Player player = event.getPlayer();
				
				if(!player.getGameMode().equals(GameMode.CREATIVE) && !Util.config("fishing",null).getList("skip_world").contains(player.getWorld().getName())) {
					// LUCKY FISHERMAN
					if(event.getState().equals(State.CAUGHT_FISH)) {
						if(Util.pctChance(Util.config("fishing","lucky").getInt("chance"),Util.config("fishing","lucky").getInt("chancemod")) && Util.config("fishing","lucky").getBoolean("active")) {
							if(!Util.config("fishing","lucky").getList("items").isEmpty()) {
								Material material = Material.matchMaterial((String) Util.config("fishing","lucky").getList("items").get((int) (Util.config("fishing","lucky").getList("items").size()*Math.random())));
								short data = 0;
								int amount = 1;
								player.getInventory().addItem(new ItemStack(material, amount, data));
								player.updateInventory();
								if(Util.config("fishing","lucky").getBoolean("message")) player.sendMessage(ChatColor.GREEN + Util.language.getString("lan_19") + " " + material.name());
								Util.toLog(ChatColor.GOLD + Util.language.getString("lan_19") + " " + material.name(), true);
							}
						}
						else if(Util.pctChance(Util.config("fishing","spawn").getInt("chance"),Util.config("fishing","spawn").getInt("chancemod"))) {
							if(Util.config("fishing","spawn").getBoolean("active")) {
								if(!Util.config("fishing","spawn").getList("mobs").isEmpty()) {
									EntityType mob = EntityType.fromName((String) Util.config("fishing","spawn").getList("mobs").get((int) (Util.config("fishing","spawn").getList("mobs").size()*Math.random())));
									plugin.getServer().getWorld(player.getWorld().getName()).spawnEntity(player.getLocation().add(new Vector(2,0,0)), mob);
									if(Util.config("fishing","spawn").getBoolean("message")) player.sendMessage(ChatColor.GOLD + Util.language.getString("lan_20") + " " + mob.getName());
								}
						
							}
						}
					}
				}
			}
		}
	}
}
