package info.plugmania.ijmh.effects;

import java.util.HashMap;

import info.plugmania.ijmh.Util;
import info.plugmania.ijmh.ijmh;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class CrazyCombat {

	ijmh plugin;
	public HashMap<Integer, HashMap<String, String>> c = new HashMap<Integer, HashMap<String, String>>();
	
	public CrazyCombat(ijmh instance){
		plugin = instance;
	}

	public void init() {
		plugin.feature.put("CrazyCombat", "crazycombat");
		c.put(0, plugin.util.cRow("skipworld", null, "list", null, null));
		c.put(1, plugin.util.cRow("active", "fairplay", "boolean", "true", "true/false/*"));
		c.put(2, plugin.util.cRow("active", "sparkle", "boolean", "true", null));
		c.put(3, plugin.util.cRow("message", "sparkle", "boolean", "true", null));
		c.put(4, plugin.util.cRow("chance", "sparkle", "integer", "5", "1-100"));
		c.put(5, plugin.util.cRow("chancemod", "sparkle", "integer", "1", "1-?"));
		c.put(6, plugin.util.cRow("duration", "sparkle", "integer", "5", "1-? seconds"));
		c.put(7, plugin.util.cRow("active", "backfire", "boolean", "true", null));
		c.put(8, plugin.util.cRow("message", "backfire", "boolean", "true", null));
		c.put(8, plugin.util.cRow("chance", "backfire", "integer", "5", null));
		c.put(9, plugin.util.cRow("chancemod", "backfire", "integer", "1", null));
		c.put(10, plugin.util.cRow("damage", "backfire", "integer", "2", "1-?"));
		c.put(11, plugin.util.cRow("active", "splinter", "boolean", "true", null));
		c.put(12, plugin.util.cRow("message", "splinter", "boolean", "true", null));
		c.put(13, plugin.util.cRow("chance", "splinter", "integer", "5", null));
		c.put(14, plugin.util.cRow("chancemod", "splinter", "integer", "1", null));
		c.put(15, plugin.util.cRow("damage", "splinter", "integer", "2", null));
		c.put(16, plugin.util.cRow("active", "broken", "boolean", "true", null));
		c.put(17, plugin.util.cRow("message", "broken", "boolean", "true", null));
		c.put(18, plugin.util.cRow("chance", "broken", "integer", "1", null));
		c.put(19, plugin.util.cRow("chancemod", "broken", "integer", "10", null));
		c.put(20, plugin.util.cRow("active", "killrush", "boolean", "true", null));
		c.put(21, plugin.util.cRow("message", "killrush", "boolean", "true", null));
		c.put(22, plugin.util.cRow("chance", "killrush", "integer", "1", null));
		c.put(23, plugin.util.cRow("chancemod", "killrush", "integer", "10", null));
		c.put(24, plugin.util.cRow("modifier", "killrush", "integer", "3", "1-5"));
		c.put(25, plugin.util.cRow("duration", "killrush", "integer", "20", null));
		c.put(26, plugin.util.cRow("active", "bowsnaps", "boolean", "true", null));
		c.put(27, plugin.util.cRow("message", "bowsnaps", "boolean", "true", null));
		c.put(28, plugin.util.cRow("chance", "bowsnaps", "integer", "5", null));
		c.put(29, plugin.util.cRow("chancemod", "bowsnaps", "integer", "1", null));
		c.put(30, plugin.util.cRow("damage", "bowsnaps", "integer", "2", null));
		
	}	
	
	public boolean command(CommandSender sender, String[] args) {
		if(args.length==1) plugin.util.cSend(c, args, sender);
		else Util.cmdExecute(sender, args); 
		return true;
	}
	
	public void main(Event e) {
		if(Util.config("crazycombat",null).getBoolean("active")){
			if(e.getEventName().equalsIgnoreCase("EntityDamageByEntityEvent")) {
				EntityDamageByEntityEvent event = (EntityDamageByEntityEvent) e;
				if(event.getDamager() instanceof Player && event.getEntity() instanceof Player) {
					Player damaged = (Player) event.getEntity();
					Player damager = (Player) event.getDamager();
				
					if(!Util.config("crazycombat",null).getList("skipworld").contains(damaged.getWorld().getName()) && !damaged.getGameMode().equals(GameMode.CREATIVE)) {
						// FAIR PLAY
						if(Util.config("crazycombat","fairplay").getBoolean("active") && damager.getGameMode().equals(GameMode.CREATIVE)) {
							damager.setGameMode(GameMode.SURVIVAL);
						}
						// KILLRUSH
						if(damaged.getHealth() - event.getDamage() < 0 && Util.config("crazycombat","killrush").getBoolean("active")) {
							if(Util.pctChance(Util.config("crazycombat","killrush").getInt("chance"),Util.config("crazycombat","killrush").getInt("chancemod"))) {
								killrush(damager);	
							}
						} else if(event.getCause().equals(DamageCause.ENTITY_ATTACK)) {
							// SPARKLE SET FIRE TO BOTH PLAYERS
							if(Util.config("crazycombat","sparkle").getBoolean("active")) {
								if(damager.getItemInHand().getType().toString().contains("SWORD") && damaged.getItemInHand().getType().toString().contains("SWORD")) {	
									if(Util.pctChance(Util.config("crazycombat","sparkle").getInt("chance"),Util.config("crazycombat","sparkle").getInt("chancemod"))) {
										sparkle(damager,damaged);
									}
								}
							// BACKFIRE TO DAMAGER	
							} else if(Util.config("crazycombat","backfire").getBoolean("active") && !damager.getGameMode().equals(GameMode.CREATIVE))  {
								if(Util.pctChance(Util.config("crazycombat","backfire").getInt("chance"),Util.config("crazycombat","backfire").getInt("chancemod"))) {
									backfire(damager);
								}
								// WEAPON BREAKS	
							} else if(Util.config("crazycombat","broken").getBoolean("active") && !damager.getGameMode().equals(GameMode.CREATIVE))  {
								if(damager.getItemInHand().getType().toString().contains("SWORD")) {
									if(Util.pctChance(Util.config("crazycombat","broken").getInt("chance"),Util.config("crazycombat","broken").getInt("chancemod"))) {
										broken(damager);
									}
								}
							}
						} else if(event.getCause().equals(DamageCause.PROJECTILE)) {
							// BOW SNAPS FINGERS
							if(Util.config("crazycombat","bowsnaps").getBoolean("active") && !damager.getGameMode().equals(GameMode.CREATIVE))  {
								if(Util.pctChance(Util.config("crazycombat","bowsnaps").getInt("chance"),Util.config("crazycombat","bowsnaps").getInt("chancemod"))) {
									bowsnaps(event,damager);
								}
							}
						}
						
					} 
				}	
			} else if(e.getEventName().equalsIgnoreCase("PlayerInteractEvent")) {
				PlayerInteractEvent event = (PlayerInteractEvent) e;
				Player player = (Player) event.getPlayer();
				if(!Util.config("crazycombat",null).getList("skipworld").contains(player.getWorld().getName()) && !player.getGameMode().equals(GameMode.CREATIVE)) {
					// WOODSWORD SPLINTERS
					if(Util.config("crazycombat","splinter").getBoolean("active"))  {
						if(event.getAction().equals(Action.LEFT_CLICK_AIR) && player.getItemInHand().getType().equals(Material.WOOD_SWORD)) {
							if(Util.pctChance(Util.config("crazycombat","splinter").getInt("chance"),Util.config("crazycombat","splinter").getInt("chancemod"))) {
								splinter(player);
							}
						}
					}
				}
			} 
		}
	}
	
	// SPARKLE SET FIRE TO BOTH PLAYERS
	void sparkle(Player damager, Player damaged) {
		damaged.setFireTicks(Util.sec2tic(Util.config("crazycombat","sparkle").getInt("duration")));
		if(Util.config("crazycombat","sparkle").getBoolean("message")) damaged.sendMessage(ChatColor.GOLD + Util.chatColorText(Util.language.getString("lan_36")));
		damager.setFireTicks(Util.sec2tic(Util.config("crazycombat","sparkle").getInt("duration")));
		if(Util.config("crazycombat","sparkle").getBoolean("message")) damager.sendMessage(ChatColor.GOLD + Util.chatColorText(Util.language.getString("lan_36")));
	}
	
	// BACKFIRE TO DAMAGER
	void backfire(Player damager) {
		damager.damage(Util.config("crazycombat","backfire").getInt("damage"));
		if(Util.config("crazycombat","backfire").getBoolean("message")) damager.sendMessage(ChatColor.GOLD + Util.chatColorText(Util.language.getString("lan_37")));
	}
	
	// WEAPON BREAKS
	void broken(Player damager) {
		damager.getItemInHand().setDurability((short) 0);
		if(Util.config("crazycombat","broken").getBoolean("message")) damager.sendMessage(ChatColor.GOLD + Util.chatColorText(Util.language.getString("lan_39")));
	}

	// KILLRUSH
	void killrush(Player damager) {
		damager.setHealth(20);
		damager.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE,Util.config("crazycombat","killrush").getInt("modifier"),Util.sec2tic(Util.config("crazycombat","killrush").getInt("duration"))));
		if(Util.config("crazycombat","killrush").getBoolean("message")) damager.sendMessage(ChatColor.GOLD + Util.chatColorText(Util.language.getString("lan_40")));
	}
	
	// BOW SNAPS FINGERS
	void bowsnaps(EntityDamageByEntityEvent event, Player damager) {
		damager.damage(Util.config("crazycombat","bowsnaps").getInt("damage"));
		event.setCancelled(true);
		if(Util.config("crazycombat","bowsnaps").getBoolean("message")) damager.sendMessage(ChatColor.GOLD + Util.chatColorText(Util.language.getString("lan_41")));
	}
	
	
	// WOODSWORD SPLINTERS
	void splinter(Player damager) {
		damager.damage(Util.config("crazycombat","splinter").getInt("damage"));
		if(Util.config("crazycombat","splinter").getBoolean("message")) damager.sendMessage(ChatColor.GOLD + Util.chatColorText(Util.language.getString("lan_38")));
	}
}