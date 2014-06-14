package info.plugmania.ijmh.effects;

import java.util.HashMap;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.PlayerDeathEvent;

import info.plugmania.ijmh.Util;
import info.plugmania.ijmh.ijmh;

public class ZombieNation {

	ijmh plugin;
	public HashMap<Integer, HashMap<String, String>> c = new HashMap<Integer, HashMap<String, String>>();
	
	public ZombieNation(ijmh instance){
		plugin = instance;
	}
	
	public void init() {
		plugin.feature.put("ZombieNation", "zombienation");
		c.put(0, plugin.util.cRow("skipworld", null, "list", null, null));
		c.put(1, plugin.util.cRow("message", null, "boolean", "true", "true/false/*"));
		c.put(2, plugin.util.cRow("whenzombie", null, "boolean", "true", "true/false/*"));
		c.put(3, plugin.util.cRow("chance", null, "integer", "10", "1-100"));
		c.put(4, plugin.util.cRow("chancemod", null, "integer", "1", "1-?"));
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
		
		if(Util.config("zombienation",null).getBoolean("active")){
			
			if(e.getEventName().equalsIgnoreCase("PlayerDeathEvent")) {
				PlayerDeathEvent event = (PlayerDeathEvent) e;
				Player player = event.getEntity();
				
				if(!Util.config("zombienation",null).getList("skipworld").contains(player.getWorld().getName())) {
					EntityDamageEvent deathCause = player.getLastDamageCause();
					if(deathCause != null) {
				        if(deathCause.getCause().equals(DamageCause.ENTITY_ATTACK) && Util.config("zombienation",null).getBoolean("whenzombie") && ((EntityDamageByEntityEvent)deathCause).getDamager().getType().equals(EntityType.ZOMBIE)) {							
							if(Util.pctChance(Util.config("zombienation",null).getInt("chance"),Util.config("zombie",null).getInt("chancemod"))) {
								player.getServer().getWorld(player.getWorld().getName()).spawnEntity(player.getLocation(), EntityType.ZOMBIE);
								if(Util.config("zombienation",null).getBoolean("message")) event.setDeathMessage(player.getName() + " " + Util.chatColorText(Util.language.getString("lan_35")));
							}
				        } else if(!Util.config("zombienation",null).getBoolean("whenzombie")) {
				        	if(Util.pctChance(Util.config("zombienation",null).getInt("chance"),Util.config("zombie",null).getInt("chancemod"))) {
								player.getServer().getWorld(player.getWorld().getName()).spawnEntity(player.getLocation(), EntityType.ZOMBIE);
								if(Util.config("zombienation",null).getBoolean("message")) event.setDeathMessage(player.getName() + " " + Util.chatColorText(Util.language.getString("lan_35")));
							}
				        }
					}
				}
			}
		}
	}
}
