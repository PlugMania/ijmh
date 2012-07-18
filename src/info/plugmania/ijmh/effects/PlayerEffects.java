package info.plugmania.ijmh.effects;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import info.plugmania.ijmh.Util;
import info.plugmania.ijmh.ijmh;

public class PlayerEffects {
	
	ijmh plugin;

	public PlayerEffects(ijmh instance){
		plugin = instance;
	}
	
	void addEffectOnItemUse(int effect){
		if(effect==0) player.setFireTicks(Util.sec2tic(300));
	}
	
	int[][] effects = new int[2][2];
	
	effects[0][0] = "You caught fire, hurry and use a bucket of water to put it out!";
	effects[0][1] =	0;		
	effects[1][0] = "You really need to be careful next time.";
	effects[1][1] =	1;
	
	int[][] items = new int[1][2];
	
	items[259][0] = 0;
	items[259][1] = 0;
			
			/*
			if(typeId==259 && util.pctChance(10)) {
				player.setFireTicks(Util.sec2tic(300));
				player.sendMessage(ChatColor.GOLD + "You caught fire, hurry and use a bucket of water to put it out!");
			} else if(typeId==326 && player.getFireTicks()>0) { 
				player.setFireTicks(0);
				event.setCancelled(true);
				player.sendMessage(ChatColor.GOLD + "You really need to be careful next time.");
			}
			*/
}
