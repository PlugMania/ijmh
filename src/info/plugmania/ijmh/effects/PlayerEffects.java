package info.plugmania.ijmh.effects;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import info.plugmania.ijmh.Util;
import info.plugmania.ijmh.ijmh;

public class PlayerEffects {
	
	ijmh plugin;
	public static String[] effects = new String[3];

	public PlayerEffects(ijmh instance){
		plugin = instance;
	
		// CATCH FIRE
		effects[1] = ChatColor.GOLD + "You caught fire, hurry and use a bucket of water to put it out!";	
		// PUT OUT FIRE
		effects[2] = ChatColor.AQUA + "You really need to be careful next time.";
		// FOOD POISONING
		effects[3] = ChatColor.GREEN + "Your belly starts to rumble, that food must have been bad!";
	}
	
	public static void addEffectInteract(int itemId, PlayerInteractEvent event){
		Player player = event.getPlayer();
		int effect = 0;
		// CATCH FIRE
		if(itemId==259 && Util.pctChance(10)) {
			player.setFireTicks(Util.sec2tic(300));
			effect = 1;
		}
		// PUT OUT FIRE
		else if(itemId==326 && player.getFireTicks()>0) {
			player.setFireTicks(0);
			event.setCancelled(true);
			effect = 2;
		}
		if(effect>0) player.sendMessage(effects[effect]);
	}

	public static void addEffectRegainHealth(String reason, EntityRegainHealthEvent event){
		Player player = (Player) event.getEntity();
		int effect = 0;
		// CATCH FIRE
		if(reason=="EATING" && Util.pctChance(10)) {
			player.addPotionEffect(new PotionEffect(PotionEffectType.POISON, Util.sec2tic(15), 1));
			effect = 3;
		}
		if(effect>0) player.sendMessage(effects[effect]);
	}
	
}
