package info.plugmania.ijmh.effects;

import java.util.Date;
import java.util.Arrays;

import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.Event;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.inventory.BrewEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerFishEvent.State;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.vehicle.VehicleMoveEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Rails;
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

	public PlayerEffects(ijmh instance){
		plugin = instance;
	}
	
	public void addEffectCraft(CraftItemEvent event){
		Player player = (Player) event.getWhoClicked();
		
		// CRAFTTHUMB
		if(!player.hasPermission("ijmh.immunity.craftthumb")){
			if(Util.config("craftthumb",null).getBoolean("active")){
				if(!Util.config("craftthumb",null).getList("skip_world").contains(player.getWorld().getName())) {
					int moreCraft = event.getCursor().getAmount();
					if(event.getCursor().getAmount()>0) moreCraft = (event.getCursor().getAmount() / 100)^(event.getCursor().getAmount() / 2);
					if(Util.pctChance(Util.config("craftthumb",null).getInt("chance") / (1 + moreCraft),Util.config("craftthumb",null).getInt("chancemod"))) {
						player.damage(Util.config("craftthumb",null).getInt("damage"));
						if(Util.config("craftthumb",null).getBoolean("message")) player.sendMessage(ChatColor.GOLD + Util.language.getString("lan_09"));
					}
				}
			}
		}
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
		// QUICKSAND
		if(Util.config("quicksand",null).getBoolean("active") && !Util.config("quicksand",null).getList("skip_world").contains(player.getWorld().getName())) {
			if(!player.hasPermission("ijmh.immunity.quicksand")) {
				if(
						pUnder.getBlock().getType().equals(Material.SAND) &&
						!player.isInsideVehicle() &&
						!event.getTo().getBlock().isLiquid() &&
						(
							to.getBlockX()!=from.getBlockX() ||
							to.getBlockY()!=from.getBlockY() ||
							to.getBlockZ()!=from.getBlockZ()
						)) {
					if(!plugin.store.quicksand.containsKey(player) && Util.pctChance(Util.config("quicksand",null).getInt("chance"),Util.config("quicksand",null).getInt("chancemod"))) {
						plugin.store.quicksand.put(player, 0);
						
						player.teleport(pUnder);
						suffocateTime = curTime + (Util.config("quicksand",null).getInt("cooldown") * 1000);
						
						if(Util.config("quicksand",null).getBoolean("message")) player.sendMessage(ChatColor.GOLD + Util.language.getString("lan_21"));
					} else if(event.getFrom().getY() < event.getTo().getY() && plugin.store.quicksand.containsKey(player)) {
						plugin.store.quicksand.put(player, plugin.store.quicksand.get(player)+1);
						player.teleport(event.getFrom());
						if(plugin.store.quicksand.get(player)>=Util.config("quicksand",null).getInt("jumps")) {
							player.teleport(player.getLocation().add(new Vector(0,1,0)));
							if(player.getLocation().getBlock().getType().equals(Material.AIR)) {
								plugin.store.quicksand.remove(player);
								if(Util.config("quicksand",null).getBoolean("message")) player.sendMessage(ChatColor.GOLD + Util.language.getString("lan_22"));
							}
						} 
						else if(curTime>suffocateTime) {
							player.teleport(pUnder);
							suffocateTime = curTime + (Util.config("quicksand",null).getInt("cooldown") * 1000);
						}
					} else if(curTime>suffocateTime && plugin.store.quicksand.containsKey(player)) {
						player.teleport(pUnder);
						suffocateTime = curTime + (Util.config("quicksand",null).getInt("cooldown") * 1000);
					}
					
				} 
				else if(
						plugin.store.quicksand.containsKey(player) &&
						!pUnder.getBlock().getType().equals(Material.SAND) &&
						!pUnder.getBlock().getType().equals(Material.AIR) 
						){
					plugin.store.quicksand.remove(player);
				}
			}
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
		// BUGGYBLOCK
		if(Util.config("buggyblock",null).getBoolean("active") && !Util.config("buggyblock",null).getList("skip_world").contains(player.getWorld().getName())) {
			if(
				Util.config("buggyblock",null).getList("blocks").contains(pUnder.getBlock().getType().name()) &&
				(
					to.getBlockX()!=from.getBlockX() ||
					to.getBlockY()!=from.getBlockY() ||
					to.getBlockZ()!=from.getBlockZ()
				)) {
	
				if(Util.pctChance(Util.config("buggyblock",null).getInt("chance"),Util.config("buggyblock",null).getInt("chancemod"))) {
					
					pUnder.getBlock().breakNaturally();
					event.setCancelled(true);
					if(Util.config("buggyblock",null).getBoolean("message")) player.sendMessage(ChatColor.GOLD + Util.language.getString("lan_29"));
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
	
	public void addEffectFish(PlayerFishEvent event) {
		Player player = event.getPlayer();

		// FISHERMAN
		if(Util.config("fishing",null).getBoolean("active") && !Util.config("fishing",null).getList("skip_world").contains(player.getWorld().getName())) {
			// LUCKY FISHERMAN
			if(event.getState().equals(State.CAUGHT_FISH)) {
				if(Util.pctChance(Util.config("fishing","lucky").getInt("chance"),Util.config("fishing","lucky").getInt("chancemod")) && Util.config("fishing","lucky").getBoolean("active")) {
					if(!Util.config("fishing","lucky").getList("items").isEmpty()) {
						Material material = Material.matchMaterial((String) Util.config("fishing","lucky").getList("items").get((int) (Util.config("fishing","lucky").getList("items").size()*Math.random())));
						short data = 0;
						int amount = 1;
						player.getInventory().addItem(new ItemStack(material, amount, data));
						player.updateInventory();
						if(Util.config("fishing","lucky").getBoolean("message")) player.sendMessage(ChatColor.GREEN + Util.language.getString("lan_19") + material.name());
						Util.toLog(ChatColor.GOLD + Util.language.getString("lan_19") + " " + material.name(), true);
					}
				}
				else if(Util.pctChance(Util.config("fishing","spawn").getInt("chance"),Util.config("fishing","spawn").getInt("chancemod"))) {
					if(Util.config("fishing","spawn").getBoolean("active")) {
						if(!Util.config("fishing","spawn").getList("mobs").isEmpty()) {
							EntityType mob = EntityType.fromName((String) Util.config("fishing","spawn").getList("mobs").get((int) (Util.config("fishing","spawn").getList("mobs").size()*Math.random())));
							plugin.getServer().getWorld(player.getWorld().getName()).spawnEntity(player.getLocation().add(new Vector(2,0,0)), mob);
							if(Util.config("fishing","spawn").getBoolean("message")) player.sendMessage(ChatColor.GOLD + Util.language.getString("lan_20") + " " + mob.getName());
						}
				
					}
				}
			}
		}
	}
	
	public void addEffectBlockPlace(BlockPlaceEvent event) {
		Player player = (Player) event.getPlayer();
		
		// BYGGYBLOCK PLACED
		if(Util.config("buggyblock",null).getBoolean("active") && !Util.config("buggyblock",null).getList("skip_world").contains(player.getWorld().getName())) {
			if(Util.config("buggyblock",null).getList("blocks").contains(event.getBlock().getType().name())) {
				if(Util.config("buggyblock",null).getBoolean("message")) player.sendMessage(ChatColor.GOLD + Util.language.getString("lan_28"));
			}
		}
	}
	
	public void addEffectBlockBreak(BlockBreakEvent event){
		Player player = (Player) event.getPlayer();
		
		// THE HAPPY MINER
		if(Util.config("happyminer",null).getBoolean("active") && !Util.config("happyminer",null).getList("skip_world").contains(player.getWorld().getName())) {
			if(
				!player.hasPotionEffect(PotionEffectType.FAST_DIGGING) &&
				!player.hasPotionEffect(PotionEffectType.SLOW_DIGGING)
				){
				
				if(Util.pctChance(Util.config("happyminer","energized").getInt("chance"),Util.config("happyminer","energized").getInt("chancemod"))) {
					player.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, Util.sec2tic(Util.config("happyminer","energized").getInt("duration")), Util.config("happyminer","energized").getInt("multiplier")));
					if(Util.config("happyminer","energized").getBoolean("message")) player.sendMessage(ChatColor.GOLD + Util.language.getString("lan_12"));
				} 
				else if(Util.pctChance(Util.config("happyminer","tired").getInt("chance"),Util.config("happyminer","tired").getInt("chancemod")) && !player.hasPermission("ijmh.immunity.tiredminer")) {
					player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, Util.sec2tic(Util.config("happyminer","tired").getInt("duration")), Util.config("happyminer","tired").getInt("multiplier")));
					if(Util.config("happyminer","tired").getBoolean("message")) player.sendMessage(ChatColor.GOLD + Util.language.getString("lan_13"));
				}
				
			} 
			else if(
				player.hasPotionEffect(PotionEffectType.SLOW_DIGGING) && 
				!player.hasPotionEffect(PotionEffectType.HUNGER)
				) {
				player.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, Util.sec2tic(Util.config("happyminer","hunger").getInt("duration")), Util.config("happyminer","hunger").getInt("multiplier")));
			}
		}
		// QUICKSAND PREVENT BREAKOUT 
		if(Util.config("quicksand",null).getBoolean("active") && !Util.config("quicksand",null).getList("skip_world").contains(player.getWorld().getName())) {
			if(plugin.store.quicksand.containsKey(player)) {
				event.setCancelled(true);
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

	public void addEffectDamageByEntity(EntityDamageByEntityEvent event) {
		
		Player damager = null;
		
		if(event.getEntity() instanceof LivingEntity && event.getDamager() instanceof Player) {
			
			damager = (Player) event.getDamager();
			LivingEntity entity = (LivingEntity) event.getEntity();
			
			// SQUID SELFDEFENSE
			if(Util.config("squid",null).getBoolean("active") && !Util.config("squid",null).getList("skip_world").contains(damager.getWorld().getName())) {
				if(entity.getType().equals(EntityType.SQUID) && damager.getGameMode().equals(GameMode.SURVIVAL)) {
					if(Util.pctChance(Util.config("squid",null).getInt("chance"),Util.config("squid",null).getInt("chancemod"))) {
						damager.addPotionEffect(new PotionEffect(PotionEffectType.POISON, Util.sec2tic(Util.config("squid",null).getInt("duration")), Util.config("squid",null).getInt("multiplier")));
						damager.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, Util.sec2tic(Util.config("squid",null).getInt("duration")), 1));
						if(Util.config("fall",null).getBoolean("message")) damager.sendMessage(ChatColor.GOLD+ "" + ChatColor.ITALIC + Util.language.getString("lan_15"));
					}
				}
			}
		}
		// BOW BREAKS
		if(event.getDamager() instanceof Arrow && event.getEntity() instanceof Player) {
			Arrow arrow = (Arrow) event.getDamager();
			if(arrow.getShooter() instanceof Player) {
				damager = (Player) arrow.getShooter();
				if(!damager.hasPermission("ijmh.immunity.bow")) {
					if(Util.config("bow",null).getBoolean("active") && !Util.config("bow",null).getList("skip_world").contains(damager.getWorld().getName())) {
						if(Util.pctChance(Util.config("bow",null).getInt("chance"),Util.config("bow",null).getInt("chancemod"))) {
							ItemStack itemHand = (ItemStack) damager.getItemInHand();
							Inventory inv = damager.getInventory();
							inv.remove(itemHand);
							damager.damage(Util.config("bow",null).getInt("damage"));
							if(Util.config("bow",null).getBoolean("message")) damager.sendMessage(ChatColor.GOLD + Util.language.getString("lan_17"));
						}
					}
				}
			}
		}
	}
	
	public void addEffectBrew(BrewEvent event) {
		// BREW EXPLOSION
		if(Util.config("brew",null).getBoolean("active") && !Util.config("brew",null).getList("skip_world").contains(event.getBlock().getLocation().getWorld().getName())) {
			if(Util.pctChance(Util.config("brew",null).getInt("chance"),Util.config("brew",null).getInt("chancemod"))) {
				Block b = event.getBlock();
				b.getWorld().createExplosion(b.getLocation(), Util.config("brew",null).getInt("multiplier"));
				
				if(Util.config("brew",null).getBoolean("signs")) {
					if(b.getRelative(BlockFace.DOWN).getType().equals(Material.AIR)) b.getRelative(BlockFace.DOWN).setType(Material.GRASS);
		
					b.setTypeId(Material.SIGN_POST.getId());
		
					Sign sign = (Sign) b.getState();
					sign.setLine(0, "===============");
					sign.setLine(1, "BOOM");
					sign.setLine(2, "!");
					sign.setLine(3, "===============");
					sign.update();
				}
			}
		}
	}

	public void addEffectVehicleMove(VehicleMoveEvent event) {
		Date curDate = new Date();
		long curTime = curDate.getTime();
		Player player = (Player) event.getVehicle().getPassenger();

		// BUMP IN THE RAIL
		if(Util.config("rail",null).getBoolean("active") && !Util.config("rail",null).getList("skip_world").contains(player.getWorld().getName())) {
			if(event.getTo().getBlock().getType().equals(Material.RAILS) && event.getVehicle().getType().equals(EntityType.MINECART)) {
				if(!player.hasPermission("ijmh.immunity.rail")) {
					Rails rail = new Rails(event.getTo().getBlock().getType(), event.getTo().getBlock().getData());
					if(rail.isCurve() && Util.pctChance(Util.config("rail",null).getInt("chance"),Util.config("rail",null).getInt("chancemod"))) {
						event.getVehicle().eject();
						Vector vector = event.getTo().getDirection().midpoint(event.getFrom().getDirection());
						player.setVelocity(new Vector(vector.getX()+Util.config("rail",null).getInt("distance"),Util.config("rail",null).getInt("angle"),vector.getZ()+Util.config("rail",null).getInt("distance")));
						if(Util.config("rail",null).getBoolean("message")) player.sendMessage(ChatColor.GOLD + Util.language.getString("lan_18"));
					}
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