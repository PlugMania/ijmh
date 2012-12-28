package info.plugmania.ijmh.effects;

import info.plugmania.ijmh.Util;
import info.plugmania.ijmh.ijmh;

import java.util.HashMap;

import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class NearDeath {

	ijmh plugin;
	public HashMap<Integer, HashMap<String, String>> c = new HashMap<Integer, HashMap<String, String>>();
	
	public NearDeath(ijmh instance){
		plugin = instance;
	}

	public void init() {
		plugin.feature.put("NearDeath", "neardeath");
		c.put(0, plugin.util.cRow("skipworld", null, "list", null, null));
		c.put(1, plugin.util.cRow("min", null, "integer", "3", "1-max"));
		c.put(2, plugin.util.cRow("max", null, "integer", "6", "min-20"));
		c.put(3, plugin.util.cRow("modifier", null, "integer", "3", "1-5"));
	}
	
	public boolean command(CommandSender sender, String[] args) {
		if(args.length==1) plugin.util.cSend(c, args, sender);
		else Util.cmdExecute(sender, args); 
		return true;
	}
	
	public void main(Event e) {
		if(Util.config("neardeath",null).getBoolean("active")){
			if(e.getEventName().equalsIgnoreCase("EntityDamageEvent")) {
				EntityDamageEvent event = (EntityDamageEvent) e;
				if(event.getEntity() instanceof Player) {
					Player player = (Player) event.getEntity();
					if(!Util.config("neardeath",null).getList("skipworld").contains(player.getWorld().getName()) && !player.getGameMode().equals(GameMode.CREATIVE)) {
						addEffects(player);
					}
				}
			} else 	if(e.getEventName().equalsIgnoreCase("EntityDamageByEntityEvent")) {
				EntityDamageByEntityEvent event = (EntityDamageByEntityEvent) e;
				if(event.getEntity() instanceof Player) {
					Player player = (Player) event.getEntity();
					if(!Util.config("neardeath",null).getList("skipworld").contains(player.getWorld().getName()) && !player.getGameMode().equals(GameMode.CREATIVE)) {
						addEffects(player);
					}
				}		
			} else if(e.getEventName().equalsIgnoreCase("PlayerJoinEvent")) {
				PlayerJoinEvent event = (PlayerJoinEvent) e;
				Player player = event.getPlayer();
				if(!Util.config("neardeath",null).getList("skipworld").contains(player.getWorld().getName()) && !player.getGameMode().equals(GameMode.CREATIVE)) {
					addEffects(player);
				}
			} else if(e.getEventName().equalsIgnoreCase("EntityRegainHealthEvent")) {
				EntityRegainHealthEvent event = (EntityRegainHealthEvent) e;
				if(event.getEntity() instanceof Player) {
					Player player = (Player) event.getEntity();
					if(!Util.config("neardeath",null).getList("skipworld").contains(player.getWorld().getName()) && !player.getGameMode().equals(GameMode.CREATIVE)) {
						addEffects(player);
					}
				}
			} else if(e.getEventName().equalsIgnoreCase("PlayerRespawnEvent")) {
				PlayerRespawnEvent event = (PlayerRespawnEvent) e;
				Player player = event.getPlayer();
				if(!Util.config("neardeath",null).getList("skipworld").contains(player.getWorld().getName()) && !player.getGameMode().equals(GameMode.CREATIVE)) {
					addEffects(player);
				}
			}
		}
	}
	
	void addEffects(Player player) {
		if(player.getHealth()>Util.config("neardeath",null).getInt("min") && player.getHealth()<=Util.config("neardeath",null).getInt("max")) {
			player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION,Util.sec2tic(999),Util.config("neardeath",null).getInt("modifier")));
			player.removePotionEffect(PotionEffectType.BLINDNESS);	
		}
		else if(player.getHealth()<=Util.config("neardeath",null).getInt("min")) {
			player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS,Util.sec2tic(999),Util.config("neardeath",null).getInt("modifier")));
			player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION,Util.sec2tic(999),Util.config("neardeath",null).getInt("modifier")));
		} else {
			player.removePotionEffect(PotionEffectType.BLINDNESS);
			player.removePotionEffect(PotionEffectType.CONFUSION);
		}
	}
}
