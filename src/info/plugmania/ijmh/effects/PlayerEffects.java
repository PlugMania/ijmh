package info.plugmania.ijmh.effects;

import java.util.Date;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import info.plugmania.ijmh.Util;
import info.plugmania.ijmh.ijmh;

public class PlayerEffects {
	
	ijmh plugin;
	public String[] effects = new String[13 + 1];
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
		// MILKING A COW
		effects[10] = ChatColor.GOLD + "Seriously ... Do you know any cows that can be milked like that?!";
		effects[11] = ChatColor.GOLD + "That might leave a mark ...!";
		// THE HAPPY MINER
		effects[12] = ChatColor.GOLD + "This is fun! you feel energized!!";
		effects[13] = ChatColor.GOLD + "So tired... must slow down ...";	
	}
	
	public void addEffectCraft(CraftItemEvent event){
		Player player = (Player) event.getWhoClicked();
		
		// CRAFTTHUMB
		if(!player.hasPermission("ijmh.immunity.craftthumb")){
			if(Util.config("craftthumb",null).getBoolean("active")){
				int moreCraft = event.getCursor().getAmount();
				if(event.getCursor().getAmount()>0) moreCraft = (event.getCursor().getAmount() / 100)^(event.getCursor().getAmount() / 2);
				if(Util.pctChance(Util.config("craftthumb",null).getInt("chance") / (1 + moreCraft),Util.config("craftthumb",null).getInt("chancemod"))) {
					player.damage(Util.config("craftthumb",null).getInt("damage"));
					if(Util.config("craftthumb",null).getBoolean("message")) player.sendMessage(effects[9]);
				} 
				
			}
		}
	}
	
	public void addEffectInteract(PlayerInteractEvent event){
		Player player = event.getPlayer();
		// CATCH FIRE
		if(!player.hasPermission("ijmh.immunity.fire")) {
			if(event.getItem().equals(Material.FLINT_AND_STEEL)){
				if(Util.config("fire",null).getBoolean("active")) {
					if(
						Util.pctChance(Util.config("fire",null).getInt("chance"),Util.config("fire",null).getInt("chancemod")) && 
						player.getWorld().getBlockAt(player.getLocation()).isLiquid()==false && 
						player.getWorld().hasStorm()==false
						) {
						player.setFireTicks(Util.sec2tic(Util.config("fire",null).getInt("duration")));
						if(Util.config("fire",null).getBoolean("message")) player.sendMessage(effects[1]);
					}
				}
			}
		}
		// PUT OUT FIRE
		if(player.getFireTicks()>0) {
			if(event.getItem().equals(Material.WATER_BUCKET)) {
				player.setFireTicks(0);
				event.setCancelled(true);
				if(Util.config("fire",null).getBoolean("message")) player.sendMessage(effects[2]);
			}
		}
		// CURE FOODPOISON
		if(player.hasPotionEffect(PotionEffectType.POISON)) {
			if(Util.config("foodpoison",null).getBoolean("message")) player.sendMessage(effects[4]);
		} 
	}

	public void addEffectInteractEntity(PlayerInteractEntityEvent event){
		Player player = event.getPlayer();
		Entity entity = event.getRightClicked();
		
		// MILKING A COW
		if(Util.config("cow",null).getBoolean("active")) {
			if(entity.getType().equals(EntityType.COW)) {
				if(
					player.getItemInHand().getType().equals(Material.BUCKET) ||
					player.getItemInHand().getType().equals(Material.MILK_BUCKET)
					){			

					Location cowLocation = entity.getLocation();
					Location playerLocation = player.getLocation();
					
					float entityYaw = Math.abs((cowLocation.getYaw() + 180) % 360);
					float playerYaw = Math.abs((playerLocation.getYaw() + 180) % 360);
					float diff = Math.abs(playerYaw - entityYaw);
					int threshhold = 40;
					if(diff > 180 - threshhold && diff < 180 + threshhold){
						if(Util.config("cow",null).getBoolean("message")) player.sendMessage(effects[10]);
						if(plugin.debug) plugin.getLogger().info("DEBUG: Front " + diff);
					}	
					else if((diff < threshhold - 10  || diff > 360 - threshhold + 10) && (!player.hasPermission("ijmh.immunity.cowskick"))) {
						player.damage(Util.config("cow","kick").getInt("damage"));
						player.setVelocity(new Vector(-entity.getLocation().getDirection().getX()-Util.config("cow","kick").getInt("backwards"),Util.config("cow","kick").getInt("upwards"),-entity.getLocation().getDirection().getZ()-Util.config("cow","kick").getInt("backwards")));
						if(Util.config("cow","kick").getBoolean("message")) player.sendMessage(effects[11]);
						player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, Util.sec2tic(Util.config("cow","kick").getInt("time")), 1));
						if(plugin.debug) plugin.getLogger().info("DEBUG: Back " + diff);
					} 
					else if(plugin.debug) plugin.getLogger().info("DEBUG: Side " + diff);
				}
			}
		}
	}
	
	public void addEffectMove(PlayerMoveEvent event){
		Player player = event.getPlayer();
		Location to = event.getTo();
		Location from = event.getFrom();
		
		// ELECTRICUTION ON REDSTONE TOUCH
		if(!player.hasPermission("ijmh.immunity.electro")) {
			if(
				Util.config("electro",null).getBoolean("active") && 
				to.getBlock().isBlockPowered() && 
				(
						to.getBlockX()!=from.getBlockX() ||
						to.getBlockY()!=from.getBlockY() ||
						to.getBlockZ()!=from.getBlockZ()
				)
				){
				if(Util.pctChance(Util.config("electro","high").getInt("chance"),Util.config("electro","high").getInt("chancemod"))) {
					player.damage(Util.config("electro","high").getInt("damage"));
					if(Util.config("electro","high").getBoolean("message")) player.sendMessage(effects[8]);
				}
				else {
					player.damage(Util.config("electro","low").getInt("chance"));
					if(Util.config("electro","low").getBoolean("message")) player.sendMessage(effects[7]);
				}
			}
		}
		// CONCUSSION FROM FALL EVENT BASED ON AIRBLOCKS
		if(
			Util.config("fall",null).getBoolean("active") &&
			!player.hasPermission("ijmh.immunity.fall") && 
			!player.getAllowFlight() && event.getPlayer().getFallDistance()>4 && 
			event.getPlayer().getLastDamage()<4 &&
			!player.hasPotionEffect(PotionEffectType.CONFUSION) &&
			!player.getLocation().getBlock().isEmpty() && !player.getLocation().getBlock().isLiquid()
			){
			Location pLoc = player.getLocation().add(0, -1, 0);
			
			if(plugin.debug) plugin.getLogger().info("Landing block is: " + pLoc.getBlock().getType().name());
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
			if(Util.config("fall",null).getBoolean("message") && event.getPlayer().getFallDistance()>6) player.sendMessage(effects[6]);
		}
		// PUT OUT FIRE
		if((to.getBlock().getTypeId()==8 || to.getBlock().getTypeId()==9) && from.getBlock().getTypeId()!=8 && from.getBlock().getTypeId()!=9 && player.getFireTicks()>0){
			if(Util.config("fire",null).getBoolean("message")) player.sendMessage(effects[2]);
		}
		// QUICKSAND
		if(to.getBlock().getType().equals(Material.SAND)) {
			// YET TO COME
		}
		// STRUCK BY LIGHTNING UNDER A TREE
		if(player.getWorld().hasStorm()){
			Date curDate = new Date();
			long curTime = curDate.getTime();
			
			if(!player.hasPermission("ijmh.immunity.lightning")) {
				if(
					Util.config("lightning",null).getBoolean("active") &&
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
				
					if(isHit==true && Util.pctChance(Util.config("lightning",null).getInt("chance"),Util.config("lightning",null).getInt("chancemod"))) {
						StruckTime = curTime + (Util.config("lightning",null).getInt("cooldown") * 1000);
						player.getLocation().getWorld().strikeLightningEffect(player.getLocation());
						player.damage(Util.config("lightning",null).getInt("damage"));
						if(Util.config("lightning",null).getBoolean("message")) player.sendMessage(effects[5]);
					}
				}
			}	
		}
	}
	
	public void addEffectRegainHealth(EntityRegainHealthEvent event){
		Player player = (Player) event.getEntity();
		// FOODPOISONING
		if(!player.hasPermission("ijmh.immunity.foodpoison")) {
			if(
				Util.config("foodpoison",null).getBoolean("active") &&
				event.getRegainReason().equals(RegainReason.EATING) && 
				Util.pctChance(Util.config("foodpoison",null).getInt("chance"),Util.config("foodpoison",null).getInt("chancemod"))
				) {
				player.addPotionEffect(new PotionEffect(PotionEffectType.POISON, Util.sec2tic(Util.config("foodpoison",null).getInt("duration")), Util.config("foodpoison",null).getInt("multiplier")));
				if(Util.config("foodpoison",null).getBoolean("message")) player.sendMessage(effects[3]);
			}
		}
	}
	
	public void addEffectBlockBreak(BlockBreakEvent event){
		Player player = (Player) event.getPlayer();
		
		// THE HAPPY MINER
		if(Util.config("happyminer",null).getBoolean("active")) {
			if(
				!player.hasPotionEffect(PotionEffectType.FAST_DIGGING) &&
				!player.hasPotionEffect(PotionEffectType.SLOW_DIGGING)
				){
				
				if(Util.pctChance(Util.config("happyminer","energized").getInt("chance"),Util.config("happyminer","energized").getInt("chancemod"))) {
					player.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, Util.sec2tic(Util.config("happyminer","energized").getInt("duration")), Util.config("happyminer","energized").getInt("multiplier")));
					if(Util.config("happyminer","tired").getBoolean("message")) player.sendMessage(effects[12]);
				} else if(Util.pctChance(Util.config("happyminer","tired").getInt("chance"),Util.config("happyminer","tired").getInt("chancemod")) && !player.hasPermission("ijmh.ummunity.lazyminer")) {
					player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, Util.sec2tic(Util.config("happyminer","tired").getInt("duration")), Util.config("happyminer","tired").getInt("multiplier")));
					if(Util.config("happyminer","tired").getBoolean("message")) player.sendMessage(effects[13]);
				}
				
			} 
			else if(
				player.hasPotionEffect(PotionEffectType.SLOW_DIGGING) && 
				!player.hasPotionEffect(PotionEffectType.HUNGER)
				) {
				player.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, Util.sec2tic(Util.config("happyminer","hunger").getInt("duration")), Util.config("happyminer","hunger").getInt("multiplier")));
			}
		}
	}
	
	public void addEffectDamage(EntityDamageEvent event){
		Player player = (Player) event.getEntity();
		
		// CONCUSSION FROM FALL
		if(!player.hasPermission("ijmh.immunity.fall")) {
			if(
				Util.config("fall",null).getBoolean("active") &&
				event.getCause().equals(DamageCause.FALL)
				) {
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
				if(event.getDamage()>=4 && Util.config("fall",null).getBoolean("message")) player.sendMessage(effects[6]);
			}
		}
	}
	
}
