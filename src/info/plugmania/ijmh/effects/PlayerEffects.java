package info.plugmania.ijmh.effects;

import java.util.Date;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import info.plugmania.ijmh.Util;
import info.plugmania.ijmh.ijmh;

public class PlayerEffects {
	
	ijmh plugin;
	public String[] effects = new String[9 + 1];
	int effect;
	public long StruckTime = 0;

	public PlayerEffects(ijmh instance){
		plugin = instance;
	
		// CATCH FIRE
		effects[1] = ChatColor.GOLD + "You caught fire, use a bucket of water or find water to put it out!";	
		// PUT OUT FIRE
		effects[2] = ChatColor.AQUA + "You really need to be careful next time.";
		// FOODPOISONING
		effects[3] = ChatColor.GREEN + "Your belly starts to rumble, that food must have been bad!? Milk Milk!!";
	    // CURE FOOD POISONING
		effects[4] = ChatColor.AQUA + "You feel better! You where lucky this time.";
	    // STRUCK BY LIGHTNING UNDER A TREE
		effects[5] = ChatColor.RED + "Struck by lightning, didn't your mom teach you not to hide under trees during a storm!?";
		// CONCUSSION FROM FALL
		effects[6] = ChatColor.LIGHT_PURPLE + "" + ChatColor.ITALIC + "You might have hit the ground a bit too hard there ...";
		// REDSTONE ELECTROCUTION
		effects[7] = ChatColor.GOLD + "You got lucky, the redstone was only carrying low voltage!";
		effects[8] = ChatColor.GOLD + "Aaaaaarggghhh, the redstone zapped you with HIGH VOLTAGE!";
		// CRAFT MESSAGES
		effects[9] = ChatColor.GOLD + "Auch! You struck your thumb.";
	}
	
	public void addEffectCraft(CraftItemEvent event){
		Player player = (Player) event.getWhoClicked();
		
		// CRAFTTHUMB
		if(!player.hasPermission("ijmh.immunity.craftthumb")){
			if(plugin.getConfig().getConfigurationSection("craftthumb").getBoolean("active")){
				int moreCraft = event.getCursor().getAmount();
				if(event.getCursor().getAmount()>0) moreCraft = (event.getCursor().getAmount() / 100)^(event.getCursor().getAmount() / 2);
				if(Util.pctChance(10 / (1 + moreCraft) )) {
					player.damage(2);
					effect = 9;
				} else {
					effect = 0;
				}
				
			}
		}
		if(effect>0) player.sendMessage(effects[effect]);
	}
	
	public void addEffectInteract(PlayerInteractEvent event){
		Player player = event.getPlayer();
		// CATCH FIRE
		if(
				plugin.getConfig().getConfigurationSection("fire").getBoolean("active") &&
				!player.hasPermission("ijmh.immunity.fire") && 
				event.getItem().getTypeId()==259 && Util.pctChance(10) && 
				player.getWorld().getBlockAt(player.getLocation()).isLiquid()==false && 
				player.getWorld().hasStorm()==false
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

	public void addEffectMove(PlayerMoveEvent event){
		Player player = event.getPlayer();
		Location to = event.getTo();
		Location from = event.getFrom();
		
		effect = 0;
		
		// ELECTRICUTION ON REDSTONE TOUCH
		if(
				plugin.getConfig().getConfigurationSection("electro").getBoolean("active") &&
				!player.hasPermission("ijmh.immunity.electro") && 
				to.getBlock().isBlockPowered() && 
				(
						to.getBlockX()!=from.getBlockX() ||
						to.getBlockY()!=from.getBlockY() ||
						to.getBlockZ()!=from.getBlockZ()
				)
			){
			if(Util.pctChance(5)) {
				player.damage(8);
				effect = 8;
			}
			else {
				player.damage(2);
				effect = 7;
			}
		}
		// CONCUSSION FROM FALL EVENT BASED ON AIRBLOCKS
		else if(
				plugin.getConfig().getConfigurationSection("fall").getBoolean("active") &&
				!player.hasPermission("ijmh.immunity.fall") && 
				!player.getAllowFlight() && event.getPlayer().getFallDistance()>4 && 
				event.getPlayer().getLastDamage()<4
				){
			if(event.getPlayer().getFallDistance()>14){
				player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, Util.sec2tic(5), 1));
				player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, Util.sec2tic(15), 1));
				player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Util.sec2tic(10), 1));
			} else if(event.getPlayer().getFallDistance()>11){
				player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, Util.sec2tic(10), 1));				
				player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Util.sec2tic(5), 1));	
			} else if(event.getPlayer().getFallDistance()>6){
				player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, Util.sec2tic(10), 1));				
			}
		}
		// PUT OUT FIRE
		else if((to.getBlock().getTypeId()==8 || to.getBlock().getTypeId()==9) && from.getBlock().getTypeId()!=8 && from.getBlock().getTypeId()!=9 && player.getFireTicks()>0){
			effect = 2;
		}
		// STRUCK BY LIGHTNING UNDER A TREE
		else if(player.getWorld().hasStorm()){
			Date curDate = new Date();
			long curTime = curDate.getTime();

			if(
					plugin.getConfig().getConfigurationSection("lightning").getBoolean("active") &&
					!player.hasPermission("ijmh.immunity.lightning") && 
					curTime>StruckTime
					) {
				int i = 0;
				boolean isHit = false;
				boolean doBreak = false;
				while(i++<=15 && !doBreak && !isHit){
					Material testBlock = player.getWorld().getBlockAt(to.getBlockX(), to.getBlockY()+i, to.getBlockZ()).getType();
					if(testBlock.equals(Material.LEAVES)) {
						isHit = true;
					} else if(!testBlock.equals(Material.AIR)) {
						doBreak = true;
						isHit = false;
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
	
	public void addEffectRegainHealth(EntityRegainHealthEvent event){
		Player player = (Player) event.getEntity();
		// FOODPOISONING
		if(
				plugin.getConfig().getConfigurationSection("foodpoison").getBoolean("active") &&
				!player.hasPermission("ijmh.immunity.foodpoison") && 
				event.getRegainReason().equals(RegainReason.EATING) && 
				Util.pctChance(10)
				) {
			player.addPotionEffect(new PotionEffect(PotionEffectType.POISON, Util.sec2tic(60), 1));
			effect = 3;
		} 
		else {
			effect = 0;
		}
		if(effect>0) player.sendMessage(effects[effect]);
	}
	
	public void addEffectDamage(EntityDamageEvent event){
		Player player = (Player) event.getEntity();
		
		// CONCUSSION FROM FALL
		if(
				plugin.getConfig().getConfigurationSection("fall").getBoolean("active") &&
				!player.hasPermission("ijmh.immunity.fall") && 
				event.getCause().equals(DamageCause.FALL)
				) {
			
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
