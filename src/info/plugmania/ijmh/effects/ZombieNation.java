package info.plugmania.ijmh.effects;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

import info.plugmania.ijmh.Util;
import info.plugmania.ijmh.ijmh;

public class ZombieNation {

	ijmh plugin;
	
	public ZombieNation(ijmh instance){
		plugin = instance;
	}
	
	public void main(Event e) {
		
		if(Util.config("zombie",null).getBoolean("active")){
			
			if(e.getEventName().equalsIgnoreCase("PlayerDeathEvent")) {
				PlayerDeathEvent event = (PlayerDeathEvent) e;
				Player player = event.getEntity();
				
				if(!Util.config("zombie",null).getList("skip_world").contains(player.getWorld().getName())) {
					EntityDamageEvent deathCause = player.getLastDamageCause();
					if(deathCause != null) {
				        if(deathCause.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
				            Entity entity = ((EntityDamageByEntityEvent)deathCause).getDamager(); 
							if(
									(entity.getType().equals(EntityType.ZOMBIE) && Util.config("zombie",null).getBoolean("whenzombie")) || 
									!Util.config("zombie",null).getBoolean("whenzombie")
								) {
								if(Util.pctChance(Util.config("zombie",null).getInt("chance"),Util.config("zombie",null).getInt("chancemod"))) {
									player.getServer().getWorld(player.getWorld().getName()).spawnEntity(player.getLocation(), EntityType.ZOMBIE);
									if(Util.config("zombie",null).getBoolean("message")) event.setDeathMessage(player.getName() + " " + Util.language.getString("lan_35"));
								}
							}
				        }
					}
				}
			}
		}
	}
}
