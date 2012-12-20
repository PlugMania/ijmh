package info.plugmania.ijmh.effects;

import info.plugmania.ijmh.Util;
import info.plugmania.ijmh.ijmh;

import java.util.HashMap;

import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
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
	}
	
	public boolean command(CommandSender sender, String[] args) {
		if(args.length==1) plugin.util.cSend(c, args, sender);
		else Util.cmdExecute(sender, args); 
		return true;
	}
	
	public void main(Event e) {
		if(Util.config("neardeath",null).getBoolean("active")){
			Util.toLog("active", true);
			if(e.getEventName().equalsIgnoreCase("EntityDamageEvent")) {
				EntityDamageEvent event = (EntityDamageEvent) e;
				Util.toLog("Entity", true);
				if(event.getEntity() instanceof Player) {
					Util.toLog("Player", true);
					Player player = (Player) event.getEntity();
					if(!Util.config("neardeath",null).getList("skipworld").contains(player.getWorld().getName()) && !player.getGameMode().equals(GameMode.CREATIVE)) {
						Util.toLog("Add", true);
						addEffects(player);
					}
				}
			} else if(e.getEventName().equalsIgnoreCase("PlayerInteractEvent")) {
				PlayerInteractEvent event = (PlayerInteractEvent) e;		
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
			}
		}
	}
	
	void addEffects(Player player) {
		if(player.getHealth()<=4) {
			player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS,1,999));
			player.removePotionEffect(PotionEffectType.CONFUSION);	
		}
		else if(player.getHealth()<=2) {
			player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS,5,999));
			player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION,1,999));
		} else {
			player.removePotionEffect(PotionEffectType.BLINDNESS);
			player.removePotionEffect(PotionEffectType.CONFUSION);
		}
	}
}
