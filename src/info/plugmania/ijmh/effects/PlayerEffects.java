package info.plugmania.ijmh.effects;

import java.util.Date;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import info.plugmania.ijmh.Util;
import info.plugmania.ijmh.ijmh;

public class PlayerEffects {
	
	ijmh plugin;
	public static String[] effects = new String[6 + 1];
	static int effect;
	public static long StruckTime = 0;

	public PlayerEffects(ijmh instance){
		plugin = instance;
	
		// CATCH FIRE
		effects[1] = ChatColor.GOLD + "You caught fire, use a bucket of water or find water to put it out!";	
		// PUT OUT FIRE
		effects[2] = ChatColor.AQUA + "You really need to be careful next time.";
		// FOOD POISONING
		effects[3] = ChatColor.GREEN + "Your belly starts to rumble, that food must have been bad!? Milk Milk!!";
	    // CURE FOOD POISONING
		effects[4] = ChatColor.AQUA + "You feel better! You where lucky this time.";
	    // STRUCK BY LIGHTNING UNDER A TREE
		effects[5] = ChatColor.RED + "Struck by lightning, didn't your mom teach you not to hide under trees during a storm!?";
		// CUNCUSSION FROM FALL
		effects[6] = ChatColor.LIGHT_PURPLE + "" + ChatColor.ITALIC + "You might have hit the ground a bit too hard there ...";
	}
	
	public static void addEffectInteract(PlayerInteractEvent event){
		Player player = event.getPlayer();
		// CATCH FIRE
		if(
			   event.getItem().getTypeId()==259 && Util.pctChance(10) 
			&& player.getWorld().getBlockAt(player.getLocation()).isLiquid()==false 
			&& player.getWorld().hasStorm()==false
			) {
			player.setFireTicks(Util.sec2tic(300));
			effect = 1;
		}
		// PUT OUT FIRE
		else if(event.getItem().getTypeId()==326 && player.getFireTicks()>0) {
			player.setFireTicks(0);
			event.setCancelled(true);
			effect = 2;
		}
		// CURE FOODPOISON
		else if(event.getItem().getTypeId()==326 && player.hasPotionEffect(PotionEffectType.POISON)) {
			effect = 4;
		} 
		else {
			effect = 0;
		}
		if(effect>0) player.sendMessage(effects[effect]);
	}

	public static void addEffectMove(PlayerMoveEvent event){
		Player player = event.getPlayer();
		Location to = event.getTo();
		Location from = event.getFrom();
		
		effect = 0;
		
		// PUT OUT FIRE
		if((to.getBlock().getTypeId()==8 || to.getBlock().getTypeId()==9) && from.getBlock().getTypeId()!=8 && from.getBlock().getTypeId()!=9 && player.getFireTicks()>0){
			effect = 2;
		}
		else if(player.getWorld().hasStorm()){
			Date curDate = new Date();
			long curTime = curDate.getTime();
			
			if(curTime>StruckTime) {
				int i = 0;
				boolean isHit = false;
				while(i++<=15){
					if(player.getWorld().getBlockAt(to.getBlockX(), to.getBlockY()+i+3, to.getBlockZ()).getType().name()=="LEAVES") {
						isHit = true;
					}
				}
				
				if(isHit==true && Util.pctChance(0.5)) {
					StruckTime = curTime + 10000;
					player.getLocation().getWorld().strikeLightningEffect(player.getLocation());
					player.damage(10);
					effect = 5;
				} 
			}
			
		}
		else {
			effect = 0;
		}
		if(effect>0) player.sendMessage(effects[effect]);
	}
	
	public static void addEffectRegainHealth(EntityRegainHealthEvent event){
		Player player = (Player) event.getEntity();
		// CATCH FIRE
		if(event.getRegainReason().equals(RegainReason.EATING) && Util.pctChance(10)) {
			player.addPotionEffect(new PotionEffect(PotionEffectType.POISON, Util.sec2tic(60), 1));
			effect = 3;
		} 
		else {
			effect = 0;
		}
		if(effect>0) player.sendMessage(effects[effect]);
	}
	
	public static void addEffectDamage(EntityDamageEvent event){
		Player player = (Player) event.getEntity();
		
		// CUNCUSSION FROM FALL
		if(event.getCause().equals(DamageCause.FALL)) {
			
			if(event.getDamage()>=12) {
				player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, Util.sec2tic(5), 1));
				player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, Util.sec2tic(15), 1));
				player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Util.sec2tic(10), 1));	
			} else if(event.getDamage()>=8) {
				player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, Util.sec2tic(10), 1));				
				player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Util.sec2tic(5), 1));	
			} else if(event.getDamage()>=4){
				player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, Util.sec2tic(10), 1));				
			}
			if(event.getDamage()>=4) effect = 6;
		}
		if(effect>0) player.sendMessage(effects[effect]);
	}
	
}
