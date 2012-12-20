package info.plugmania.ijmh.effects;

import java.util.Arrays;
import java.util.HashMap;

import info.plugmania.ijmh.Util;
import info.plugmania.ijmh.ijmh;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Foodpoisoning {

	ijmh plugin;
	public HashMap<Integer, HashMap<String, String>> c = new HashMap<Integer, HashMap<String, String>>();
	
	public Foodpoisoning(ijmh instance){
		plugin = instance;
	}
	
	public void init() {
		plugin.feature.put("Foodpoisoning", "foodpoisoning");
		c.put(0, plugin.util.cRow("skipworld", null, "list", null, null));
		c.put(1, plugin.util.cRow("message", null, "boolean", "true", "true/false/*"));
		c.put(2, plugin.util.cRow("chance", null, "integer", "10", "1-100"));
		c.put(3, plugin.util.cRow("chancemod", null, "integer", "1", "1-?"));
		c.put(4, plugin.util.cRow("multiplier", null, "integer", "1", "1-5"));
		c.put(5, plugin.util.cRow("duration", null, "integer", "5", "1-? seconds"));
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
		
		if(Util.config("foodpoisoning",null).getBoolean("active")){
			
			if(e.getEventName().equalsIgnoreCase("PlayerInteractEvent")) {
				PlayerInteractEvent event = (PlayerInteractEvent) e;
				Player player = event.getPlayer();
				
				if(event.hasItem() && 
						!player.getGameMode().equals(GameMode.CREATIVE) && 
						!player.hasPermission("ijmh.immunity.foodpoison") || 
						!player.hasPotionEffect(PotionEffectType.POISON)
						) {
					if(!Util.config("foodpoisoning",null).getList("skipworld").contains(player.getWorld().getName())) {
						if(player.getFoodLevel()!=20 && event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
							if(Util.pctChance(Util.config("foodpoisoning",null).getInt("chance"),Util.config("foodpoisoning",null).getInt("chancemod"))) {
								Material[] material = {Material.RAW_BEEF, Material.RAW_CHICKEN, Material.RAW_FISH, Material.ROTTEN_FLESH, Material.PORK};
								if(Arrays.asList(material).contains(event.getMaterial())) {
									player.addPotionEffect(new PotionEffect(PotionEffectType.POISON, Util.sec2tic(Util.config("foodpoisoning",null).getInt("duration")), Util.config("foodpoisoning",null).getInt("multiplier")));
									if(Util.config("foodpoisoning",null).getBoolean("message")) player.sendMessage(ChatColor.GREEN + Util.chatColorText(Util.language.getString("lan_03")));
						
								}
							}
						}
					}
				}
				// CURE FOODPOISON
				if(player.hasPotionEffect(PotionEffectType.POISON) && event.getMaterial().equals(Material.MILK_BUCKET) && event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
					if(Util.config("foodpoisoning",null).getBoolean("message")) player.sendMessage(ChatColor.AQUA + Util.chatColorText(Util.language.getString("lan_04")));
				} 
			}
		}
	}
}
