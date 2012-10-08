package info.plugmania.ijmh.effects;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import info.plugmania.ijmh.Util;
import info.plugmania.ijmh.ijmh;

public class TheHappyMiner {

	ijmh plugin;
	
	public TheHappyMiner(ijmh instance){
		plugin = instance;
	}

	public void main(Event e) {
		
		if(Util.config("happyminer",null).getBoolean("active")){

			if(e.getEventName().equalsIgnoreCase("BlockBreakEvent")) {
				BlockBreakEvent event = (BlockBreakEvent) e;
				Player player = (Player) event.getPlayer();
				
				if(!player.getGameMode().equals(GameMode.CREATIVE) && !Util.config("happyminer",null).getList("skip_world").contains(player.getWorld().getName())) {
					if(
						!player.hasPotionEffect(PotionEffectType.FAST_DIGGING) &&
						!player.hasPotionEffect(PotionEffectType.SLOW_DIGGING)
						){
						
						if(Util.pctChance(Util.config("happyminer","energized").getInt("chance"),Util.config("happyminer","energized").getInt("chancemod"))) {
							player.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, Util.sec2tic(Util.config("happyminer","energized").getInt("duration")), Util.config("happyminer","energized").getInt("multiplier")));
							if(Util.config("happyminer","energized").getBoolean("message")) player.sendMessage(ChatColor.GOLD + Util.language.getString("lan_12"));
						} 
						else if(Util.pctChance(Util.config("happyminer","tired").getInt("chance"),Util.config("happyminer","tired").getInt("chancemod")) && !player.hasPermission("ijmh.immunity.tiredminer")) {
							player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, Util.sec2tic(Util.config("happyminer","tired").getInt("duration")), Util.config("happyminer","tired").getInt("multiplier")));
							if(Util.config("happyminer","tired").getBoolean("message")) player.sendMessage(ChatColor.GOLD + Util.language.getString("lan_13"));
						}
						
					} 
					else if(
						player.hasPotionEffect(PotionEffectType.SLOW_DIGGING) && 
						!player.hasPotionEffect(PotionEffectType.HUNGER)
						) {
						player.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, Util.sec2tic(Util.config("happyminer","hunger").getInt("duration")), Util.config("happyminer","hunger").getInt("multiplier")));
					}
				}
			}
		}
	}
}
