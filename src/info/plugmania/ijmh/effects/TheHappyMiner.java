package info.plugmania.ijmh.effects;

import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import info.plugmania.ijmh.Util;
import info.plugmania.ijmh.ijmh;

public class TheHappyMiner {

	ijmh plugin;
	public HashMap<Integer, HashMap<String, String>> c = new HashMap<Integer, HashMap<String, String>>();
	
	public TheHappyMiner(ijmh instance){
		plugin = instance;
	}

	public void init() {
		plugin.feature.put("TheHappyMiner", "thehappyminer");
		plugin.subfeature.put("ti", "tired");
		plugin.subfeature.put("hu", "hunger");
		plugin.subfeature.put("en", "energized");
		c.put(1, plugin.util.cRow("skipworld", null, "list", null, null));
		c.put(2, plugin.util.cRow("message", "tired", "boolean", "true", "true/false/*"));
		c.put(3, plugin.util.cRow("chance", "tired", "integer", "5", "1-100"));
		c.put(4, plugin.util.cRow("chancemod", "tired", "integer", "1", "1-?"));
		c.put(5, plugin.util.cRow("multiplier", "tired", "integer", "1", "1-5"));
		c.put(6, plugin.util.cRow("duration", "tired", "integer", "10", "1-? seconds"));
		c.put(7, plugin.util.cRow("multiplier", "hunger", "integer", "1", null));
		c.put(8, plugin.util.cRow("duration", "hunger", "integer", "10", null));
		c.put(9, plugin.util.cRow("message", "energized", "boolean", "true", null));
		c.put(10, plugin.util.cRow("chance", "energized", "integer", "5", null));
		c.put(11, plugin.util.cRow("chancemod", "energized", "integer", "1", null));
		c.put(12, plugin.util.cRow("multiplier", "energized", "integer", "1", null));
		c.put(13, plugin.util.cRow("duration", "energized", "integer", "10", null));
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
		
		if(Util.config("thehappyminer",null).getBoolean("active")){

			if(e.getEventName().equalsIgnoreCase("BlockBreakEvent")) {
				BlockBreakEvent event = (BlockBreakEvent) e;
				Player player = (Player) event.getPlayer();
				
				if(!player.getGameMode().equals(GameMode.CREATIVE) && !Util.config("thehappyminer",null).getList("skip_world").contains(player.getWorld().getName())) {
					if(
						!player.hasPotionEffect(PotionEffectType.FAST_DIGGING) &&
						!player.hasPotionEffect(PotionEffectType.SLOW_DIGGING)
						){
						
						if(Util.pctChance(Util.config("thehappyminer","energized").getInt("chance"),Util.config("thehappyminer","energized").getInt("chancemod"))) {
							player.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, Util.sec2tic(Util.config("thehappyminer","energized").getInt("duration")), Util.config("thehappyminer","energized").getInt("multiplier")));
							if(Util.config("thehappyminer","energized").getBoolean("message")) player.sendMessage(ChatColor.GOLD + Util.language.getString("lan_12"));
						} 
						else if(Util.pctChance(Util.config("thehappyminer","tired").getInt("chance"),Util.config("thehappyminer","tired").getInt("chancemod")) && !player.hasPermission("ijmh.immunity.tiredminer")) {
							player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, Util.sec2tic(Util.config("thehappyminer","tired").getInt("duration")), Util.config("thehappyminer","tired").getInt("multiplier")));
							if(Util.config("thehappyminer","tired").getBoolean("message")) player.sendMessage(ChatColor.GOLD + Util.language.getString("lan_13"));
						}
						
					} 
					else if(
						player.hasPotionEffect(PotionEffectType.SLOW_DIGGING) && 
						!player.hasPotionEffect(PotionEffectType.HUNGER)
						) {
						player.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, Util.sec2tic(Util.config("thehappyminer","hunger").getInt("duration")), Util.config("thehappyminer","hunger").getInt("multiplier")));
					}
				}
			}
		}
	}
}
