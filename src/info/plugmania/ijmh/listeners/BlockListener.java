package info.plugmania.ijmh.listeners;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.potion.PotionEffectType;

import info.plugmania.ijmh.Util;
import info.plugmania.ijmh.ijmh;

public class BlockListener implements Listener {

	ijmh plugin;

	public BlockListener(ijmh instance) {
		plugin = instance;
	}
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		plugin.playerEffects.addEffectBlockPlace(event);
		
		
		plugin.unstabletnt.main(event); // UNSTABLE TNT
		plugin.buggyblock.main(event); // BUGGY BLOCK
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		Player player = event.getPlayer();
		
		if(plugin.store.drowning.containsKey(player) && event.getBlock().getType().equals(Material.WOOD)) {
			plugin.store.drowning.remove(player);
			player.removePotionEffect(PotionEffectType.BLINDNESS);
			if(Util.config("boat",null).getBoolean("message")) player.sendMessage(ChatColor.GOLD + Util.language.getString("lan_25"));
		}
		
		if(player.getGameMode().equals(GameMode.SURVIVAL)) {
			plugin.playerEffects.addEffectBlockBreak(event);			
		}
		
		plugin.unstabletnt.main(event); // UNSTABLE TNT
	}
	
}
