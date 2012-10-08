package info.plugmania.ijmh.effects;

import java.util.Date;
import java.util.Arrays;

import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.vehicle.VehicleMoveEvent;
import org.bukkit.material.Wool;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.flags.StateFlag;

import info.plugmania.ijmh.Util;
import info.plugmania.ijmh.ijmh;

public class PlayerEffects {
	
	ijmh plugin;
	int effect;
	public long StruckTime = 0;
	public long HitTime = 0;
	public long SlowTime = 0;
	public long FireTime = 0;
	public long suffocateTime = 0;
	public long drowningTime = 0;
	public long timeLimit = 0;

	public PlayerEffects(ijmh instance){
		plugin = instance;
	}
	
	public void addEffectInteract(PlayerInteractEvent event){
		Player player = event.getPlayer();
		Date curDate = new Date();
		long curTime = curDate.getTime();
		
		// FOODPOISONING
		if(!player.hasPermission("ijmh.immunity.foodpoison") || !player.hasPotionEffect(PotionEffectType.POISON)) {
			if(
				Util.config("foodpoison",null).getBoolean("active") &&
				!Util.config("foodpoison",null).getList("skip_world").contains(player.getWorld().getName())
				) {
				if(player.getFoodLevel()!=20 && event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
					if(Util.pctChance(Util.config("foodpoison",null).getInt("chance"),Util.config("foodpoison",null).getInt("chancemod"))) {
						Material[] material = {Material.RAW_BEEF, Material.RAW_CHICKEN, Material.RAW_FISH, Material.ROTTEN_FLESH, Material.PORK};
						if(Arrays.asList(material).contains(event.getMaterial())) {
							player.addPotionEffect(new PotionEffect(PotionEffectType.POISON, Util.sec2tic(Util.config("foodpoison",null).getInt("duration")), Util.config("foodpoison",null).getInt("multiplier")));
							if(Util.config("foodpoison",null).getBoolean("message")) player.sendMessage(ChatColor.GREEN + Util.language.getString("lan_03"));
				
						}
					}
				}
			}
		}		
		// CATCH FIRE
		if(!player.hasPermission("ijmh.immunity.fire")) {
			if(Util.config("fire",null).getBoolean("active") && !Util.config("fire",null).getList("skip_world").contains(player.getWorld().getName())) {
				if(player.getItemInHand().getType().equals(Material.FLINT_AND_STEEL) && event.getAction().equals(Action.RIGHT_CLICK_BLOCK)){
					Util.toLog("Fire: Active passed", true);
					if(
						!player.getWorld().getBlockAt(player.getLocation()).isLiquid() && 
						!player.getWorld().hasStorm()
						) {
						if(Util.pctChance(Util.config("fire",null).getInt("chance"),Util.config("fire",null).getInt("chancemod"))) {
							player.setFireTicks(Util.sec2tic(Util.config("fire",null).getInt("duration")));
							if(Util.config("fire",null).getBoolean("message")) player.sendMessage(ChatColor.GOLD + Util.language.getString("lan_01"));
						}
					}
				}
			}
		}
		// PUT OUT FIRE
		if(player.getFireTicks()>0) {
			if(event.getItem().getType().equals(Material.WATER_BUCKET)) {
				player.setFireTicks(0);
				event.setCancelled(true);
				if(Util.config("fire",null).getBoolean("message")) {
					if(curTime>FireTime) player.sendMessage(ChatColor.AQUA + Util.language.getString("lan_02"));
					FireTime = curTime + 2000;
				}
			}
		}
		// CURE DIZZY IN THE DESERT
		if(plugin.store.desert.contains(player)) {
			if(event.getItem().getType().equals(Material.WATER_BUCKET)) {
				plugin.store.desert.remove(player);
				player.removePotionEffect(PotionEffectType.SLOW);
				player.removePotionEffect(PotionEffectType.CONFUSION);
				if(Util.config("desert",null).getBoolean("message")) player.sendMessage(ChatColor.GOLD + Util.language.getString("lan_27"));
			}
		}		
		plugin.store.desert.remove(player);
		player.removePotionEffect(PotionEffectType.SLOW);
		player.removePotionEffect(PotionEffectType.CONFUSION);
		
		// CURE FOODPOISON
		if(player.hasPotionEffect(PotionEffectType.POISON) && event.getMaterial().equals(Material.MILK_BUCKET) && event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
			if(Util.config("foodpoison",null).getBoolean("message")) player.sendMessage(ChatColor.AQUA + Util.language.getString("lan_04"));
		} 
	}

	public void addEffectInteractEntity(PlayerInteractEntityEvent event){
		Player player = event.getPlayer();
		Entity entity = event.getRightClicked();
		
		// MILKING A COW
		if(Util.config("cows",null).getBoolean("active") && !Util.config("cows",null).getList("skip_world").contains(player.getWorld().getName())) {
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
						if(Util.config("cows",null).getBoolean("message")) player.sendMessage(ChatColor.GOLD + Util.language.getString("lan_10"));
						if(plugin.debug) plugin.getLogger().info("DEBUG: Front " + diff);
					}	
					else if((diff < threshhold - 10  || diff > 360 - threshhold + 10) && (!player.hasPermission("ijmh.immunity.cowskick"))) {
						player.damage(Util.config("cows","kick").getInt("damage"));
						player.setVelocity(new Vector(-entity.getLocation().getDirection().getX()-Util.config("cows","kick").getInt("backwards"),Util.config("cows","kick").getInt("upwards"),-entity.getLocation().getDirection().getZ()-Util.config("cows","kick").getInt("backwards")));
						if(Util.config("cows","kick").getBoolean("message")) player.sendMessage(ChatColor.GOLD + Util.language.getString("lan_11"));
						player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, Util.sec2tic(Util.config("cows","kick").getInt("time")), 1));
						if(plugin.debug) plugin.getLogger().info("DEBUG: Back " + diff);
					} 
					else if(plugin.debug) plugin.getLogger().info("DEBUG: Side " + diff);
				}
			}
		}
	}
	
	public void addEffectMove(PlayerMoveEvent event){
		Player player = event.getPlayer();
		Location pUnder = player.getLocation().add(0, -1, 0);
		Location to = event.getTo();
		Location from = event.getFrom();
		Date curDate = new Date();
		long curTime = curDate.getTime();
		
		// ELECTRICUTION ON REDSTONE TOUCH
		if(!player.hasPermission("ijmh.immunity.electro")) {
			if(
				Util.config("electro",null).getBoolean("active") && !Util.config("electro",null).getList("skip_world").contains(player.getWorld().getName())) {
				if(
						!player.isInsideVehicle() &&
						to.getBlock().isBlockPowered() &&
						!(to.getBlock().getType().equals(Material.WOOD_PLATE) || to.getBlock().getType().equals(Material.STONE_PLATE)) &&
						(
						to.getBlockX()!=from.getBlockX() ||
						to.getBlockY()!=from.getBlockY() ||
						to.getBlockZ()!=from.getBlockZ()
						)
						){
					if(Util.pctChance(Util.config("electro","high").getInt("chance"),Util.config("electro","high").getInt("chancemod"))) {
						player.damage(Util.config("electro","high").getInt("damage"));
						if(Util.config("electro","high").getBoolean("message")) player.sendMessage(ChatColor.GOLD + Util.language.getString("lan_08"));
					}
					else {
						player.damage(Util.config("electro","low").getInt("chance"));
						if(Util.config("electro","low").getBoolean("message")) player.sendMessage(ChatColor.GOLD + Util.language.getString("lan_07"));
					}
				}
			}
		}
		// CONCUSSION FROM FALL EVENT BASED ON AIRBLOCKS
		if(Util.config("fall",null).getBoolean("active") && !Util.config("fall",null).getList("skip_world").contains(player.getWorld().getName())) {
			if(
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
		// PUT OUT FIRE
		if(
				(to.getBlock().getType().equals(Material.WATER) || to.getBlock().getType().equals(Material.STATIONARY_WATER)) && 
				!from.getBlock().getType().equals(Material.WATER) && 
				from.getBlock().getType().equals(Material.STATIONARY_WATER) && 
				player.getFireTicks()>0){
				
			if(curTime>FireTime) player.sendMessage(ChatColor.AQUA + Util.language.getString("lan_02"));
			FireTime = curTime + 2000;
		}
		// DIZZY IN THE DESERT
		if(Util.config("desert",null).getBoolean("active") && !Util.config("desert",null).getList("skip_world").contains(player.getWorld().getName())) {
			if(
					!player.hasPermission("ijmh.immunity.desert") && 
					!player.getWorld().isThundering() && 
					player.getWorld().getTime()<Util.sec2tic(600) &&
					(Util.config("desert",null).getBoolean("whendesert") && player.getLocation().getBlock().getBiome().equals(Biome.DESERT))
					) {
				if(!plugin.store.desert.contains(player)) {
					if(
							pUnder.getBlock().getType().equals(Material.SAND) &&
							!player.isInsideVehicle() &&
							!event.getTo().getBlock().isLiquid() &&
							(
								to.getBlockX()!=from.getBlockX() ||
								to.getBlockY()!=from.getBlockY() ||
								to.getBlockZ()!=from.getBlockZ()
							)) {
						if(Util.pctChance(Util.config("desert",null).getInt("chance"),Util.config("desert",null).getInt("chancemod"))) {
							Util.toLog("was here " + player.getWorld().getTime(), true);
							plugin.store.desert.add(player);
							player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Util.sec2tic(60), Util.config("desert",null).getInt("multiplier")));
							player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, Util.sec2tic(60), Util.config("desert",null).getInt("multiplier")));
							if(Util.config("desert",null).getBoolean("message")) player.sendMessage(ChatColor.GOLD + Util.language.getString("lan_26"));
						}
					}
				}
			}	
		}
		if(
				plugin.store.desert.contains(player) &&
				(
						to.getBlockX()!=from.getBlockX() ||
						to.getBlockY()!=from.getBlockY() ||
						to.getBlockZ()!=from.getBlockZ()
					)) {
			if(player.getWorld().isThundering() || !pUnder.getBlock().getType().equals(Material.SAND)) {
				Util.toLog("was here too " + player.getWorld().getTime(), true);
				plugin.store.desert.remove(player);
				player.removePotionEffect(PotionEffectType.SLOW);
				player.removePotionEffect(PotionEffectType.CONFUSION);
			}
		} 
		else if(plugin.store.desert.contains(player) && !player.hasPotionEffect(PotionEffectType.CONFUSION)) {
			player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Util.sec2tic(60), Util.config("desert",null).getInt("multiplier")));
			player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, Util.sec2tic(60), Util.config("desert",null).getInt("multiplier")));	
		}
		// TAR
		if(Util.config("tar",null).getBoolean("active") && !Util.config("tar",null).getList("skip_world").contains(player.getWorld().getName())) {
			if(
				pUnder.getBlock().getType().equals(Material.WOOL) &&
				(
					to.getBlockX()!=from.getBlockX() ||
					to.getBlockY()!=from.getBlockY() ||
					to.getBlockZ()!=from.getBlockZ()
				)) {

				Block block = pUnder.getBlock();
				Wool wool = new Wool(block.getType(), block.getData());
				if(wool.getColor().equals(DyeColor.BLACK)) {
					player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Util.sec2tic(Util.config("tar",null).getInt("duration")), Util.config("tar",null).getInt("multiplier")));
					if(Util.config("tar",null).getBoolean("message")) {
						if(curTime>SlowTime) player.sendMessage(ChatColor.GOLD + Util.language.getString("lan_16"));
						SlowTime = curTime + 10000;
					}
				}
			}
		}	
		// WALK ON RED ROSES
		if(!player.hasPermission("ijmh.immunity.roses")) {
			if(Util.config("roses",null).getBoolean("active") && !Util.config("roses",null).getList("skip_world").contains(player.getWorld().getName())){
				if(
						to.getBlock().getType().equals(Material.RED_ROSE) &&
						(to.getBlockX()!=from.getBlockX() ||
						 to.getBlockY()!=from.getBlockY() ||
						 to.getBlockZ()!=from.getBlockZ()
					)){
					player.damage(Util.config("roses",null).getInt("damage"));
					player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Util.sec2tic(Util.config("roses",null).getInt("duration")), Util.config("roses",null).getInt("multiplier")));
					if(
							Util.config("roses",null).getBoolean("message") && 
							to.getBlock().getType()!=from.getBlock().getType()
							) {
						if(curTime>HitTime) player.sendMessage(ChatColor.GOLD + Util.language.getString("lan_14"));
						HitTime = curTime + 10000;
					}		
				}
			}
		}
		// STRUCK BY LIGHTNING UNDER A TREE
		if(player.getWorld().hasStorm()){
			if(!player.hasPermission("ijmh.immunity.lightning")) {
				if(Util.config("lightning",null).getBoolean("active") && !Util.config("fall",null).getList("skip_world").contains(player.getWorld().getName())) {
					if(
							!Util.config("lightning",null).getList("skip_biome").contains(player.getLocation().getBlock().getBiome().name()) &&
							(plugin.mazeMania==null || (plugin.mazeMania!=null && !plugin.mazeMania.arena.playing.contains(player))) &&
							curTime>StruckTime
							) {
						int i = 0;
						boolean isHit = false;
						boolean doBreak = false;
						while(i++<=10 && !doBreak && !isHit){
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
							if(Util.config("lightning",null).getBoolean("message")) player.sendMessage(ChatColor.GOLD + Util.language.getString("lan_05"));
						}
					}
				}
			}	
		}
	}
		
	public void addEffectDamage(EntityDamageEvent event){
		
		if(event.getEntity().getType().equals(EntityType.PLAYER)) {
		
			Player player = (Player) event.getEntity();
			
			// CONCUSSION FROM FALL
			if(!player.hasPermission("ijmh.immunity.fall")) {
				if(Util.config("fall",null).getBoolean("active") && !Util.config("fall",null).getList("skip_world").contains(player.getWorld().getName())) {
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
		
	public void addEffectVehicleMove(VehicleMoveEvent event) {
		Date curDate = new Date();
		long curTime = curDate.getTime();
		Player player = (Player) event.getVehicle().getPassenger();

		// UNTAMED RIDE
		if(plugin.store.riding.contains(player)) {
			if(Util.config("ride",null).getList("entitytype").contains(event.getVehicle().getType())) {
				if(timeLimit>0) {
					if(curTime>timeLimit) {
						event.getVehicle().eject();
						Vector vector = event.getTo().getDirection().midpoint(event.getFrom().getDirection());
						player.setVelocity(new Vector(vector.getX()+Util.config("ride",null).getInt("distance"),Util.config("ride",null).getInt("angle"),vector.getZ()+Util.config("ride",null).getInt("distance")));
						if(Util.config("ride",null).getBoolean("message")) player.sendMessage(ChatColor.GOLD + Util.language.getString("lan_34"));
						timeLimit = 0;
					}
				}
				else if(Util.pctChance(Util.config("ride",null).getInt("chance"),Util.config("ride",null).getInt("chancemod"))) {
					timeLimit = curTime + Util.config("ride",null).getInt("limit");
					if(Util.config("ride",null).getBoolean("message")) player.sendMessage(ChatColor.GOLD + Util.language.getString("lan_31"));
				}
			}
		}
		// ROW YOUR BOAT
		if(Util.config("boat",null).getBoolean("active") && !Util.config("boat",null).getList("skip_world").contains(player.getWorld().getName())) {
			if(!player.hasPermission("ijmh.immunity.boat")) {	
				if(
						event.getVehicle().getType().equals(EntityType.BOAT) &&
						(
							event.getTo().getBlockX()!=event.getFrom().getBlockX() ||
							event.getTo().getBlockY()!=event.getFrom().getBlockY() ||
							event.getTo().getBlockZ()!=event.getFrom().getBlockZ()
						)) {
					
					int y = 0;
					while(player.getLocation().add(new Vector(0,-1,0)).getBlock().isLiquid() && y<=10){
						y++;
					}
					
					if(y>2-1) {
						if(Util.pctChance(Util.config("boat",null).getInt("chance"),Util.config("boat",null).getInt("chancemod"))) {
							player.eject();
							event.getVehicle().remove();
							
							if(Util.config("boat",null).getBoolean("message")) player.sendMessage(ChatColor.GOLD + Util.language.getString("lan_24"));
							
							if(y<5) {
								Block block = player.getLocation().add(new Vector(0,-2,0)).getBlock();
								block.setType(Material.WOOD);
								
								player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, Util.sec2tic(60), 1));
								plugin.store.drowning.put(player, block);
							}
							else {
								Block block = player.getLocation().add(new Vector(0,-4,0)).getBlock();
								block.setType(Material.WOOD);
								
								player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, Util.sec2tic(60), 1));
								plugin.store.drowning.put(player, block);
							}
						}
					}
				}
			}
		}
	}

}