package info.plugmania.ijmh.effects;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

import info.plugmania.ijmh.Util;
import info.plugmania.ijmh.ijmh;

public class PlayerEffects {
	
	ijmh plugin;
	public static String[] effects = new String[3];

	public PlayerEffects(ijmh instance){
		plugin = instance;
	
		// CATCH FIRE
		effects[1] = "You caught fire, hurry and use a bucket of water to put it out!";	
		// PUT OUT FIRE
		effects[2] = "You really need to be careful next time.";
	}
	
	public static void addEffectOnItemUse(int itemId, PlayerInteractEvent event){
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
}
