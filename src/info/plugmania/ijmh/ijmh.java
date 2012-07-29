package info.plugmania.ijmh;

import java.util.Arrays;

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

	}
	
	public void onEnable(){
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(new PlayerListener(this), this);
		
		this.getConfig().options().copyDefaults(true);
        this.saveConfig();
		
        util.checkVersion(false, null, null);
		if(this.getConfig().getBoolean("debug")) this.debug = true;
		if(this.debug) getLogger().info("Debug enabled.");
	}
	
	public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
		
		boolean err = false;
		
		try {
			if (args.length == 0) args = new String[] { "help" };
		    
			String[] effects = {"fire", "fall", "foodpoison", "lightning", "electro", "craftthumb", "cows", "happyminer"};
			
			if(sender.hasPermission("ijmh.admin")){
				if (args[0].equalsIgnoreCase("help")) {
					sender.sendMessage(ChatColor.AQUA + "- [ijhm] It Just Might Happen v" + this.getDescription().getVersion() + " ------------------"); 
					sender.sendMessage(ChatColor.AQUA + "Effects: fire, fall, foodpoison, lightning, electro, craftthumb, cows, happyminer"); 
					sender.sendMessage(ChatColor.GREEN + "/ijmh <effect>" + ChatColor.AQUA + " - HowTo change values for an effect");
					sender.sendMessage(ChatColor.GREEN + "/ijmh <effect> toggle" + ChatColor.AQUA + " - turn effect on/off");
					sender.sendMessage(ChatColor.GREEN + "/ijmh load" + ChatColor.AQUA + " - Load config.yml");
					sender.sendMessage(ChatColor.GREEN + "/ijmh update" + ChatColor.AQUA + " - Toggle the update messages on/off");
					sender.sendMessage(ChatColor.GREEN + "/ijmh version" + ChatColor.AQUA + " - See version and check for new updates"); 
				} 
				else if(args[0].equalsIgnoreCase("version")){
					util.checkVersion(true,null,sender);
				}
				else if(args[0].equalsIgnoreCase("update")){
					if(this.getConfig().getBoolean("update_message")) {
							this.getConfig().set("update_message", false);
							sender.sendMessage(ChatColor.AQUA + "[ijhm] update messages has been switched off");
							this.saveConfig();
					}
					else {
							this.getConfig().set("update_message", true);
							sender.sendMessage(ChatColor.AQUA + "[ijhm] update messages has been switched on");
							this.saveConfig();
					} 
				}
				else if(args[0].equalsIgnoreCase("load")) {
					this.reloadConfig();
					sender.sendMessage(ChatColor.AQUA + "[ijhm] Configuration loaded");
				}
				else if(Arrays.asList(effects).contains(args[0].toLowerCase()) && args.length==1){
					String state;
					if(util.config(args[0], null).getBoolean("active")) state = ChatColor.GREEN + "enabled";
					else state = ChatColor.RED + "disabled";
					
					sender.sendMessage(ChatColor.AQUA + "- HOWTO change: " + args[0] + " effect " + state + ChatColor.AQUA + " ----------------");
					sender.sendMessage(ChatColor.GREEN + "/ijmh " + args[0] + " <name> <value> or <section> <name> <value>");
					sender.sendMessage(ChatColor.GOLD + "section" + ChatColor.AQUA + " | " + ChatColor.GOLD + "name" + ChatColor.AQUA + " (default): current value");
					sender.sendMessage(ChatColor.AQUA + "| toggle");
					
					if(args[0].equalsIgnoreCase("fire")) {
						sender.sendMessage(ChatColor.AQUA + "| message (true): " + ChatColor.GOLD + util.config("fire",null).getBoolean("message"));
						sender.sendMessage(ChatColor.AQUA + "| chance (10): " + ChatColor.GOLD + util.config("fire",null).getInt("chance"));
						sender.sendMessage(ChatColor.AQUA + "| chancemod (1): " + ChatColor.GOLD + util.config("fire",null).getInt("chancemod"));
						sender.sendMessage(ChatColor.AQUA + "| duration (300): " + ChatColor.GOLD + util.config("fire",null).getInt("duration"));
					}
					else if(args[0].equalsIgnoreCase("fall")) {
						sender.sendMessage(ChatColor.AQUA + "| message (true): " + ChatColor.GOLD + util.config("fall",null).getBoolean("message"));
						sender.sendMessage(ChatColor.AQUA + "| duration (5): " + ChatColor.GOLD + util.config("fall",null).getInt("duration"));		
						sender.sendMessage(ChatColor.GREEN + "The duration for "  + args[0] + " is for the shortest effect, rest will sync to this value.");
					}
					else if(args[0].equalsIgnoreCase("foodpoison")) {
						sender.sendMessage(ChatColor.AQUA + "| message (true): " + ChatColor.GOLD + util.config("foodpoison",null).getBoolean("message"));
						sender.sendMessage(ChatColor.AQUA + "| chance (10): " + ChatColor.GOLD + util.config("foodpoison",null).getInt("chance"));
						sender.sendMessage(ChatColor.AQUA + "| chancemod (1): " + ChatColor.GOLD + util.config("foodpoison",null).getInt("chancemod"));
						sender.sendMessage(ChatColor.AQUA + "| multiplier (1): " + ChatColor.GOLD + util.config("foodpoison",null).getInt("multiplier"));
						sender.sendMessage(ChatColor.AQUA + "| duration (60): " + ChatColor.GOLD + util.config("foodpoison",null).getInt("duration"));
					} 
					else if(args[0].equalsIgnoreCase("lightning")) {
						sender.sendMessage(ChatColor.AQUA + "| message (true): " + ChatColor.GOLD + util.config("lightning",null).getBoolean("message"));
						sender.sendMessage(ChatColor.AQUA + "| chance (5): " + ChatColor.GOLD + util.config("lightning",null).getInt("chance"));
						sender.sendMessage(ChatColor.AQUA + "| chancemod (10): " + ChatColor.GOLD + util.config("lightning",null).getInt("chancemod"));
						sender.sendMessage(ChatColor.AQUA + "| damage (10): " + ChatColor.GOLD + util.config("lightning",null).getInt("damage"));
						sender.sendMessage(ChatColor.AQUA + "| cooldown (10): " + ChatColor.GOLD + util.config("lightning",null).getInt("cooldown"));
					} 
					else if(args[0].equalsIgnoreCase("electro")) {
						sender.sendMessage(ChatColor.AQUA + "high | message (true): " + ChatColor.GOLD + util.config("electro","high").getBoolean("message"));
						sender.sendMessage(ChatColor.AQUA + "high | chance (5): " + ChatColor.GOLD + util.config("electro","high").getInt("chance"));
						sender.sendMessage(ChatColor.AQUA + "high | chancemod (1): " + ChatColor.GOLD + util.config("electro","high").getInt("chancemod"));
						sender.sendMessage(ChatColor.AQUA + "high | damage (8): " + ChatColor.GOLD + util.config("electro","high").getInt("damage"));
						sender.sendMessage(ChatColor.AQUA + "low | message (true): " + ChatColor.GOLD + util.config("electro","low").getBoolean("message"));
						sender.sendMessage(ChatColor.AQUA + "low | damage (2): " + ChatColor.GOLD + util.config("electro","low").getInt("damage"));						
					} 
					else if(args[0].equalsIgnoreCase("craftthumb")) {
						sender.sendMessage(ChatColor.AQUA + "| message (true): " + ChatColor.GOLD + util.config("craftthumb",null).getBoolean("message"));
						sender.sendMessage(ChatColor.AQUA + "| chance (10): " + ChatColor.GOLD + util.config("craftthumb",null).getInt("chance"));
						sender.sendMessage(ChatColor.AQUA + "| chancemod (1): " + ChatColor.GOLD + util.config("craftthumb",null).getInt("chancemod"));
						sender.sendMessage(ChatColor.AQUA + "| damage (2): " + ChatColor.GOLD + util.config("craftthumb",null).getInt("damage"));
					} 
					else if(args[0].equalsIgnoreCase("cows")) {
						sender.sendMessage(ChatColor.AQUA + "| message (true): " + ChatColor.GOLD + util.config("cows",null).getBoolean("message"));
						sender.sendMessage(ChatColor.AQUA + "kick | message (true): " + ChatColor.GOLD + util.config("cows","kick").getBoolean("message"));
						sender.sendMessage(ChatColor.AQUA + "kick | damage (4): " + ChatColor.GOLD + util.config("cows","kick").getInt("damage"));
						sender.sendMessage(ChatColor.AQUA + "kick | duration (5): " + ChatColor.GOLD + util.config("cows","kick").getInt("duration"));
						sender.sendMessage(ChatColor.AQUA + "kick | backwards (2): " + ChatColor.GOLD + util.config("cows","kick").getInt("backwards"));
						sender.sendMessage(ChatColor.AQUA + "kick | upwards (1): " + ChatColor.GOLD + util.config("cows","kick").getInt("upwards"));
					} 
					else if(args[0].equalsIgnoreCase("happyminer")) {
						sender.sendMessage(ChatColor.AQUA + "tired | message (true): " + ChatColor.GOLD + util.config("happyminer","tired").getBoolean("message"));
						sender.sendMessage(ChatColor.AQUA + "tired | multiplier (1): " + ChatColor.GOLD + util.config("happyminer","tired").getInt("multiplier"));
						sender.sendMessage(ChatColor.AQUA + "tired | chance (5): " + ChatColor.GOLD + util.config("happyminer","tired").getInt("chance"));
						sender.sendMessage(ChatColor.AQUA + "tired | chancemod (1): " + ChatColor.GOLD + util.config("happyminer","tired").getInt("chancemod"));
						sender.sendMessage(ChatColor.AQUA + "tired | duration (10): " + ChatColor.GOLD + util.config("happyminer","tired").getInt("duration"));
						sender.sendMessage(ChatColor.AQUA + "hunger | multiplier (1): " + ChatColor.GOLD + util.config("happyminer","hunger").getInt("multiplier"));
						sender.sendMessage(ChatColor.AQUA + "hunger | duration (10): " + ChatColor.GOLD + util.config("happyminer","hunger").getInt("duration"));
						sender.sendMessage(ChatColor.AQUA + "energized | message (true): " + ChatColor.GOLD + util.config("happyminer","energized").getBoolean("message"));
						sender.sendMessage(ChatColor.AQUA + "energized | chance (5): " + ChatColor.GOLD + util.config("happyminer","energized").getInt("chance"));
						sender.sendMessage(ChatColor.AQUA + "energized | chancemod (1): " + ChatColor.GOLD + util.config("happyminer","energized").getInt("chancemod"));
						sender.sendMessage(ChatColor.AQUA + "energized | multiplier (1): " + ChatColor.GOLD + util.config("happyminer","energized").getInt("multiplier"));
						sender.sendMessage(ChatColor.AQUA + "energized | duration (10): " + ChatColor.GOLD + util.config("happyminer","energized").getInt("duration"));   
					}
					sender.sendMessage(ChatColor.GOLD + "message" + ChatColor.AQUA + " (true/false), " + ChatColor.GOLD + "chance" + ChatColor.AQUA + " (1-100), " + ChatColor.GOLD + "duration" + ChatColor.AQUA + " (seconds),");
					sender.sendMessage(ChatColor.GOLD + "damage" + ChatColor.AQUA + " (1=½hearth), " + ChatColor.GOLD + "multiplier" + ChatColor.AQUA + " (1-5), " + ChatColor.GOLD + "cooldown" + ChatColor.AQUA + " (seconds)");
				}
				else if(Arrays.asList(effects).contains(args[0].toLowerCase()) && args.length==2){
					if(util.config(args[0],null).isBoolean("active")) {
						if(this.getConfig().getConfigurationSection(args[0]).getBoolean("active")) {
							this.getConfig().getConfigurationSection(args[0]).set("active", false);
							sender.sendMessage(ChatColor.AQUA + "[ijhm] " + args[0] + " has been switched off");
							this.saveConfig();
						}
						else {
							this.getConfig().getConfigurationSection(args[0]).set("active", true);
							sender.sendMessage(ChatColor.AQUA + "[ijhm] " + args[0] + " has been switched on");
							this.saveConfig();
						}
					} 
					else {
						err = true;
					}
				}
				else if(Arrays.asList(effects).contains(args[0].toLowerCase()) && args.length==3){
					if(util.config(args[0],null).isSet(args[1])) {
						
						if(args[1].equalsIgnoreCase("message")) {
							boolean valueB = Boolean.parseBoolean(args[2]);
							util.config(args[0],null).set(args[1], valueB);
						}
						else {
							int valueI = Integer.parseInt(args[2]);
							util.config(args[0],null).set(args[1], valueI);
						}
						
						sender.sendMessage(ChatColor.AQUA + "[ijhm] " + args[1] + " was changed to " + args[2]);
						this.saveConfig();
					}
					else err = true;
				}
				else if(Arrays.asList(effects).contains(args[0].toLowerCase()) && args.length==4){
					if(util.config(args[0],args[1]).isSet(args[2])) {

						if(args[2].equalsIgnoreCase("message")) {
							boolean valueB = Boolean.parseBoolean(args[3]);
							util.config(args[0],args[1]).set(args[2], valueB);
						}
						else {
							int valueI = Integer.parseInt(args[3]);
							util.config(args[0],args[1]).set(args[2], valueI);
						}
						
						sender.sendMessage(ChatColor.AQUA + "[ijhm] " + args[2] + " was changed to " + args[3]);
						this.saveConfig();	
					}
					else err = true;					
				}
				if(err) sender.sendMessage(ChatColor.RED + "[ijhm] Either you had an error in your command or your config.yml is broken.");
			}
			else {
				sender.sendMessage(ChatColor.RED + "[ijhm] You don't have the permissions to use this command!");
			}
		
		} catch (Exception e) {
			sender.sendMessage(ChatColor.RED + "An error occured.");
			if(this.debug) sender.sendMessage(ChatColor.RED + "" + e);
		}
		
		return true;
	}
	
}
