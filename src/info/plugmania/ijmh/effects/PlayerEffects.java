package info.plugmania.ijmh.effects;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

import info.plugmania.ijmh.Util;
import info.plugmania.ijmh.ijmh;

public class PlayerEffects {
	
	ijmh plugin;

	public PlayerEffects(ijmh instance){
		plugin = instance;
	}

	int[][] effects;
	effects = new int[2][2];
	
	// CATCH FIRE
	effects[0][0] = "You caught fire, hurry and use a bucket of water to put it out!";
	effects[0][1] =	0;		
	// PUT OUT FIRE
	effects[1][0] = "You really need to be careful next time.";
	effects[1][1] =	1;
	
	void addEffectOnItemUse(int effect, PlayerInteractEvent event){
		Player player = event.getPlayer();
		if(effect==0 && Util.pctChance(10)) {
			player.setFireTicks(Util.sec2tic(300));
		}
		else if(effect==1 && player.getFireTicks()>0) {
			player.setFireTicks(0);
			event.setCancelled(true);
		}
	}
}
