package info.plugmania.ijmh.effects;

import java.util.HashMap;

import info.plugmania.ijmh.Util;
import info.plugmania.ijmh.ijmh;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerFishEvent.State;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class FishermanOnHook {

	ijmh plugin;	
	public HashMap<Integer, HashMap<String, String>> c = new HashMap<Integer, HashMap<String, String>>();
	
	public FishermanOnHook(ijmh instance){
		plugin = instance;
	}
	
	public void init() {
		plugin.feature.put("FishermanOnHook", "fishermanonhook");
		plugin.subfeature.put("lu", "lucky");
		plugin.subfeature.put("sp", "spawn");
		c.put(0, plugin.util.cRow("skipworld", null, "list", null, null));
		c.put(1, plugin.util.cRow("active", "lucky", "boolean", "true", "true/false/*"));
		c.put(2, plugin.util.cRow("message", "lucky", "boolean", "true", "true/false/*"));
		c.put(3, plugin.util.cRow("chance", "lucky", "integer", "5", "1-100"));
		c.put(4, plugin.util.cRow("chancemod", "lucky", "integer", "1", "1-?"));
		c.put(5, plugin.util.cRow("items", "lucky", "list", null, null));
		c.put(6, plugin.util.cRow("active", "spawn", "boolean", "true", null));
		c.put(7, plugin.util.cRow("message", "spawn", "boolean", "true", null));
		c.put(8, plugin.util.cRow("chance", "spawn", "integer", "5", null));
		c.put(9, plugin.util.cRow("chancemod", "spawn", "integer", "1", null));
		c.put(10, plugin.util.cRow("mobs", "spawn", "list", null, null));
	}	
	
	public boolean command(CommandSender sender, String[] args) {
		if(args.length==1) {
			plugin.util.cSend(c, args, sender);
		} else {
			Util.cmdExecute(sender, args);
		} 
		return true;
	}
	
	@SuppressWarnings("deprecation")
	public void main(Event e) {
		
		if(Util.config("fishermanonhook",null).getBoolean("active")){
			
			if(e.getEventName().equalsIgnoreCase("PlayerFishEvent")) {
				PlayerFishEvent event = (PlayerFishEvent) e;
				Player player = event.getPlayer();
				
				if(!player.getGameMode().equals(GameMode.CREATIVE) && !Util.config("fishermanonhook",null).getList("skipworld").contains(player.getWorld().getName())) {
					if(event.getState().equals(State.CAUGHT_FISH)) {
						if(Util.pctChance(Util.config("fishermanonhook","lucky").getInt("chance"),Util.config("fishermanonhook","lucky").getInt("chancemod")) && Util.config("fishermanonhook","lucky").getBoolean("active")) {
							if(!Util.config("fishermanonhook","lucky").getList("items").isEmpty()) {
								Material material = Material.matchMaterial((String) Util.config("fishermanonhook","lucky").getList("items").get((int) (Util.config("fishermanonhook","lucky").getList("items").size()*Math.random())));
								short data = 0;
								int amount = 1;
								player.getInventory().addItem(new ItemStack(material, amount, data));
								player.updateInventory();
								if(Util.config("fishermanonhook","lucky").getBoolean("message")) player.sendMessage(ChatColor.GREEN + Util.chatColorText(Util.language.getString("lan_19")) + " " + material.name());
								Util.toLog(ChatColor.GOLD + Util.chatColorText(Util.language.getString("lan_19")) + " " + material.name(), true);
							}
						}
						else if(Util.pctChance(Util.config("fishermanonhook","spawn").getInt("chance"),Util.config("fishermanonhook","spawn").getInt("chancemod"))) {
							if(Util.config("fishermanonhook","spawn").getBoolean("active")) {
								if(!Util.config("fishermanonhook","spawn").getList("mobs").isEmpty()) {
									EntityType mob = EntityType.fromName((String) Util.config("fishermanonhook","spawn").getList("mobs").get((int) (Util.config("fishermanonhook","spawn").getList("mobs").size()*Math.random())));
									plugin.getServer().getWorld(player.getWorld().getName()).spawnEntity(player.getLocation().add(new Vector(2,0,0)), mob);
									if(Util.config("fishermanonhook","spawn").getBoolean("message")) player.sendMessage(ChatColor.GOLD + Util.chatColorText(Util.language.getString("lan_20")) + " " + mob.getName());
								}
						
							}
						}
					}
				}
			}
		}
	}
}
