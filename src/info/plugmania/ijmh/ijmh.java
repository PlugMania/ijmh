package info.plugmania.ijmh;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;

import info.plugmania.ijmh.Util;

public class ijmh extends JavaPlugin {
	public final Util util;
	
	public ijmh() {
		this.util = new Util(this);
	}
	
	public void onDisable (){
		getLogger().info("ItJustMightHappen is now disabled.");
	}
	
	public void onEnable(){
		getServer().getPluginManager().registerEvents(new PluginListener(), this);
		getLogger().info("ItJustMightHappen is enabled.");
	}
	
	public class PluginListener implements Listener {
		@EventHandler
	    public void onPlayerInteract(PlayerInteractEvent event) {
			if(event.hasItem()) {
				Player player = event.getPlayer();
				int typeId = event.getItem().getTypeId();
				if(player.getGameMode().getValue()==0) {					
					if(typeId==259 && util.pctChance(10)) {
						player.setFireTicks(util.sec2tic(300));
						player.sendMessage(ChatColor.GOLD + "You caught fire, hurry and use a bucket of water to put it out!");
					} else if(typeId==326 && player.getFireTicks()>0) { 
						player.setFireTicks(0);
						event.setCancelled(true);
						player.sendMessage(ChatColor.GOLD + "You really need to be careful next time.");
					}
				}
			}
	    }	
	}
}
