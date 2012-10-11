package info.plugmania.ijmh.effects;

import info.plugmania.ijmh.Util;
import info.plugmania.ijmh.ijmh;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.sk89q.worldguard.protection.flags.DefaultFlag;

public class Concussion {

	ijmh plugin;
	
	public Concussion(ijmh instance){
		plugin = instance;
	}
	
	public long timer = 0;
	
	public void main(Event e) {
		
		if(Util.config("fall",null).getBoolean("active")){
			
			if(e.getEventName().equalsIgnoreCase("PlayerMoveEvent")) {
				PlayerMoveEvent event = (PlayerMoveEvent) e;
				Player player = event.getPlayer();
				Location pUnder = player.getLocation().add(0, -1, 0);
				
				// FALL EVENT BASED ON AIRBLOCKS
				if(!Util.config("fall",null).getList("skip_world").contains(player.getWorld().getName())) {
					if(
							!player.getGameMode().equals(GameMode.CREATIVE) &&
							!player.hasPermission("ijmh.immunity.fall") && 
							!player.getAllowFlight() && event.getPlayer().getFallDistance()>4 && 
							event.getPlayer().getLastDamage()<4 &&
							!player.hasPotionEffect(PotionEffectType.CONFUSION) &&
							!player.getLocation().getBlock().isEmpty() && !player.getLocation().getBlock().isLiquid()
							){
						Util.toLog("CONCUSSION FROM FALL EVENT BASED ON AIRBLOCKS",true);
						
						if(plugin.debug) plugin.getLogger().info("Landing block is: " + pUnder.getBlock().getType().name());
			
						boolean INVINCIBILITY = false;
						if(plugin.wg!=null) INVINCIBILITY = Util.WorldGuard(DefaultFlag.INVINCIBILITY, player.getLocation(), player);
						
						if(!INVINCIBILITY) {
							if(event.getPlayer().getFallDistance()>14){
								player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, Util.sec2tic(Util.config("fall",null).getInt("duration")), 1));
								player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, Util.sec2tic(Util.config("fall",null).getInt("duration")*3), 1));
								player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Util.sec2tic(Util.config("fall",null).getInt("duration")*2), 1));
							} else if(event.getPlayer().getFallDistance()>11){
								player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, Util.sec2tic(Util.config("fall",null).getInt("duration")*2), 1));				
								player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Util.sec2tic(Util.config("fall",null).getInt("duration")), 1));	
							} else if(event.getPlayer().getFallDistance()>6){
								player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, Util.sec2tic(Util.config("fall",null).getInt("duration")*2), 1));				
							}
							if(Util.config("fall",null).getBoolean("message") && event.getPlayer().getFallDistance()>6) player.sendMessage(ChatColor.LIGHT_PURPLE + Util.language.getString("lan_06"));
						}
					}
				}
			}
			else if(e.getEventName().equalsIgnoreCase("EntityDamageEvent")) {
				EntityDamageEvent event = (EntityDamageEvent) e;
				
				if(event.getEntity().getType().equals(EntityType.PLAYER)) {
					Player player = (Player) event.getEntity();
					
					// FALL EVENT BASED ON DAMAGE
					if(!player.hasPermission("ijmh.immunity.fall")) {
						if(!player.getGameMode().equals(GameMode.CREATIVE) && !Util.config("fall",null).getList("skip_world").contains(player.getWorld().getName())) {
							if(event.getCause().equals(DamageCause.FALL)) {
								boolean INVINCIBILITY = false;
								if(plugin.wg!=null) INVINCIBILITY = Util.WorldGuard(DefaultFlag.INVINCIBILITY, player.getLocation(), player);
								
								if(!INVINCIBILITY) {
									if(event.getDamage()>=12) {
										player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, Util.sec2tic(Util.config("fall",null).getInt("duration")), 1));
										player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, Util.sec2tic(Util.config("fall",null).getInt("duration")*3), 1));
										player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Util.sec2tic(Util.config("fall",null).getInt("duration")*2), 1));	
									} else if(event.getDamage()>=8) {
										player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, Util.sec2tic(Util.config("fall",null).getInt("duration")*2), 1));				
										player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Util.sec2tic(Util.config("fall",null).getInt("duration")), 1));	
									} else if(event.getDamage()>=4){
										player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, Util.sec2tic(Util.config("fall",null).getInt("duration")*2), 1));				
									}
									if(event.getDamage()>=4 && Util.config("fall",null).getBoolean("message")) player.sendMessage(ChatColor.LIGHT_PURPLE + Util.language.getString("lan_06"));
								}
							}
						}
					}
				} 	
			}
		}
	}
}
