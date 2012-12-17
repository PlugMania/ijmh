package info.plugmania.ijmh.effects;

import java.util.HashMap;

import info.plugmania.ijmh.Util;
import info.plugmania.ijmh.ijmh;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

public class CrazyCombat {

	ijmh plugin;
	public HashMap<Integer, HashMap<String, String>> c = new HashMap<Integer, HashMap<String, String>>();
	
	public CrazyCombat(ijmh instance){
		plugin = instance;
	}

	public void init() {
		plugin.feature.put("CrazyCombat", "crazycombat");
		c.put(0, plugin.util.cRow("skipworld", null, "list", null, null));
		c.put(1, plugin.util.cRow("active", "sparkle", "boolean", "true", "true/false/*"));
		c.put(2, plugin.util.cRow("message", "sparkle", "boolean", "true", null));
		c.put(3, plugin.util.cRow("chance", "sparkle", "integer", "5", "1-100"));
		c.put(4, plugin.util.cRow("chancemod", "sparkle", "integer", "1", "1-?"));
		c.put(5, plugin.util.cRow("duration", "sparkle", "integer", "5", "1-? seconds"));
		c.put(6, plugin.util.cRow("active", "backfire", "boolean", "true", null));
		c.put(7, plugin.util.cRow("message", "backfire", "boolean", "true", null));
		c.put(8, plugin.util.cRow("chance", "backfire", "integer", "5", null));
		c.put(8, plugin.util.cRow("chancemod", "backfire", "integer", "1", null));
		c.put(9, plugin.util.cRow("damage", "backfire", "integer", "2", "1-?"));
	}	
	
	public boolean command(CommandSender sender, String[] args) {
		if(args.length==1) plugin.util.cSend(c, args, sender);
		else Util.cmdExecute(sender, args); 
		return true;
	}
	
	public void main(Event e) {
		if(Util.config("crazycombat",null).getBoolean("active")){
			if(e.getEventName().equalsIgnoreCase("EntityDamageByEntityEvent")) {
				EntityDamageByEntityEvent event = (EntityDamageByEntityEvent) e;
				if(event.getDamager() instanceof Player && event.getEntity() instanceof Player) {
					Player player = (Player) event.getEntity();
					Player damager = (Player) event.getDamager();
				
					if(Util.config("crazycombat","sparkle").getBoolean("active") && !Util.config("crazycombat",null).getList("skipworld").contains(player.getWorld().getName())) {
						if(event.getCause().equals(DamageCause.ENTITY_ATTACK)) {
							// SPARKLE SET FIRE TO BOTH PLAYERS
							if(Util.pctChance(Util.config("crazycombat","sparkle").getInt("chance"),Util.config("crazycombat","sparkle").getInt("chancemod"))) {
								if((damager.getItemInHand().getType().toString().contains("SWORD") || damager.getItemInHand().getType().toString().contains("AXE")) && (player.getItemInHand().getType().toString().contains("SWORD") || player.getItemInHand().getType().toString().contains("AXE"))) {
									sparkle(damager,player);
								}
							// BACKFIRE TO DAMAGER	
							} else if(Util.pctChance(Util.config("crazycombat","backfire").getInt("chance"),Util.config("crazycombat","backfire").getInt("chancemod"))) {
								backfire(damager);
							} 
						}
					} 
				}	
			}
		}
	}
	
	// SPARKLE SET FIRE TO BOTH PLAYERS
	void sparkle(Player damager, Player damaged) {
		damaged.setFireTicks(Util.sec2tic(Util.config("crazycombat","sparkle").getInt("duration")));
		if(Util.config("crazycombat",null).getBoolean("message")) damaged.sendMessage(ChatColor.GOLD + Util.language.getString("lan_36"));
		damager.setFireTicks(Util.sec2tic(Util.config("crazycombat","sparkle").getInt("duration")));
		if(Util.config("crazycombat",null).getBoolean("message")) damager.sendMessage(ChatColor.GOLD + Util.language.getString("lan_36"));
	}
	
	// BACKFIRE TO DAMAGER
	void backfire(Player damager) {
		damager.damage(Util.config("crazycombat","backfire").getInt("damage"));
		if(Util.config("onfire",null).getBoolean("message")) damager.sendMessage(ChatColor.GOLD + Util.language.getString("lan_37"));
	}
}