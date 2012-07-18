package info.plugmania.ijmh;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import info.plugmania.ijmh.Util;
import info.plugmania.ijmh.listeners.PlayerListener;
import info.plugmania.ijmh.effects.PlayerEffects;

public class ijmh extends JavaPlugin {
	
	public final Util util;
	public final PlayerEffects playerEffects;
	
	public ijmh() {
		this.util = new Util(this);
		this.playerEffects = new PlayerEffects(this);
	}
	
	public void onDisable (){
		getLogger().info("ItJustMightHappen is now disabled.");
	}
	
	public void onEnable(){
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(new PlayerListener(this), this);
		getLogger().info("ItJustMightHappen is enabled.");
	}
	
}
