package info.plugmania.ijmh.effects;

import java.util.Arrays;

import info.plugmania.ijmh.Util;
import info.plugmania.ijmh.ijmh;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Foodpoisoning {

	ijmh plugin;
	
	public Foodpoisoning(ijmh instance){
		plugin = instance;
	}
	
	public void main(Event e) {
		
		if(Util.config("foodpoison",null).getBoolean("active")){
			
			if(e.getEventName().equalsIgnoreCase("PlayerInteractEvent")) {
				PlayerInteractEvent event = (PlayerInteractEvent) e;
				Player player = event.getPlayer();
				
				if(event.hasItem() && 
						!player.getGameMode().equals(GameMode.CREATIVE) && 
						!player.hasPermission("ijmh.immunity.foodpoison") || 
						!player.hasPotionEffect(PotionEffectType.POISON)
						) {
					if(!Util.config("foodpoison",null).getList("skip_world").contains(player.getWorld().getName())) {
						if(player.getFoodLevel()!=20 && event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
							if(Util.pctChance(Util.config("foodpoison",null).getInt("chance"),Util.config("foodpoison",null).getInt("chancemod"))) {
								Material[] material = {Material.RAW_BEEF, Material.RAW_CHICKEN, Material.RAW_FISH, Material.ROTTEN_FLESH, Material.PORK};
								if(Arrays.asList(material).contains(event.getMaterial())) {
									player.addPotionEffect(new PotionEffect(PotionEffectType.POISON, Util.sec2tic(Util.config("foodpoison",null).getInt("duration")), Util.config("foodpoison",null).getInt("multiplier")));
									if(Util.config("foodpoison",null).getBoolean("message")) player.sendMessage(ChatColor.GREEN + Util.language.getString("lan_03"));
						
								}
							}
						}
					}
				}
				// CURE FOODPOISON
				if(player.hasPotionEffect(PotionEffectType.POISON) && event.getMaterial().equals(Material.MILK_BUCKET) && event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
					if(Util.config("foodpoison",null).getBoolean("message")) player.sendMessage(ChatColor.AQUA + Util.language.getString("lan_04"));
				} 
			}
		}
	}
}
