package info.plugmania.ijmh;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import info.plugmania.ijmh.Util;
import info.plugmania.ijmh.listeners.PlayerListener;
import info.plugmania.ijmh.effects.PlayerEffects;

public class ijmh extends JavaPlugin {
	
	public final Util util;
	public final PlayerEffects playerEffects;
	public boolean debug;
	
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
		
		this.getConfig().options().copyDefaults(true);
        this.saveConfig();
		
		getLogger().info("ItJustMightHappen is enabled.");
		if(this.getConfig().getBoolean("debug")) this.debug = true;
		if(this.debug) getLogger().info("Debug enabled.");
	}
	
	public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
		
		try {
			if (args.length == 0) args = new String[] { "help" };
		    
			if(sender.hasPermission("ijmh.admin")){
				if (args[0].equalsIgnoreCase("help")) {
					sender.sendMessage(ChatColor.AQUA + "- [ijhm] It Just Might Happen v" + this.getDescription().getVersion() + " ------------------"); 
					sender.sendMessage(ChatColor.AQUA + "Effects: fire, fall, foodpoison, lightning, electro"); 
					sender.sendMessage(ChatColor.GREEN + "/ijmh toggle <effect>" + ChatColor.AQUA + " - turn effect on/off");
					sender.sendMessage(ChatColor.GREEN + "/ijmh load" + ChatColor.AQUA + " - Load config.yml"); 
					sender.sendMessage(ChatColor.GREEN + "/ijmh version" + ChatColor.AQUA + " - See version and check for new updates"); 
				} 
				else if(args[0].equalsIgnoreCase("version")){
					util.checkVersion(true,null,sender);
				}
				else if(args[0].equalsIgnoreCase("toggle") && args.length==2){
					if(this.getConfig().isConfigurationSection(args[1])) {
						if(this.getConfig().getConfigurationSection(args[1]).getBoolean("active")) {
							this.getConfig().getConfigurationSection(args[1]).set("active", false);
							sender.sendMessage(ChatColor.AQUA + "[ijhm] " + args[1] + " has been switched off");
							this.saveConfig();
						}
						else {
							this.getConfig().getConfigurationSection(args[1]).set("active", true);
							sender.sendMessage(ChatColor.AQUA + "[ijhm] " + args[1] + " has been switched on");
							this.saveConfig();
						}
					} 
					else {
						sender.sendMessage(ChatColor.RED + "[ijhm] Either you had an error in your command or your config.yml is broken.");
					}
				} 
				else if(args[0].equalsIgnoreCase("load")) {
					this.reloadConfig();
					sender.sendMessage(ChatColor.AQUA + "[ijhm] Configuration loaded");
				}
			}
			else {
				return false;
			}
		
		} catch (Exception e) {
			sender.sendMessage(ChatColor.RED + "An error occured.");
		}
		
		return true;
	}
	
}
