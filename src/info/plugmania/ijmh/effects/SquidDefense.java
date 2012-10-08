package info.plugmania.ijmh.effects;

import info.plugmania.ijmh.Util;
import info.plugmania.ijmh.ijmh;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class SquidDefense {

	ijmh plugin;
	
	public SquidDefense(ijmh instance){
		plugin = instance;
	}
	
	public void main(Event e) {
		
		if(Util.config("squid",null).getBoolean("active")){
			
			if(e.getEventName().equalsIgnoreCase("EntityDamageByEntityEvent")) {
				EntityDamageByEntityEvent event = (EntityDamageByEntityEvent) e;
				
				Player damager = null;
				
				if(event.getEntity() instanceof LivingEntity && event.getDamager() instanceof Player) {
					
					damager = (Player) event.getDamager();
					LivingEntity entity = (LivingEntity) event.getEntity();
					
					// SQUID SELFDEFENSE
					if(!Util.config("squid",null).getList("skip_world").contains(damager.getWorld().getName())) {
						if(entity.getType().equals(EntityType.SQUID) && damager.getGameMode().equals(GameMode.SURVIVAL)) {
							if(Util.pctChance(Util.config("squid",null).getInt("chance"),Util.config("squid",null).getInt("chancemod"))) {
								damager.addPotionEffect(new PotionEffect(PotionEffectType.POISON, Util.sec2tic(Util.config("squid",null).getInt("duration")), Util.config("squid",null).getInt("multiplier")));
								damager.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, Util.sec2tic(Util.config("squid",null).getInt("duration")), 1));
								if(Util.config("fall",null).getBoolean("message")) damager.sendMessage(ChatColor.GOLD+ "" + ChatColor.ITALIC + Util.language.getString("lan_15"));
							}
						}
					}
				}
			}
		}
	}
}
