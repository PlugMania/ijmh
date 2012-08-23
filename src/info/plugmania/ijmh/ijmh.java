package info.plugmania.ijmh;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import info.plugmania.ijmh.Util;
import info.plugmania.ijmh.listeners.PlayerListener;
import info.plugmania.ijmh.effects.PlayerEffects;

import info.plugmania.mazemania.MazeMania;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

public class ijmh extends JavaPlugin {
	
	public final Util util;
	public final PlayerEffects playerEffects;
	public boolean debug;
	
	public MazeMania mazeMania;
	public WorldGuardPlugin wg;
	
	public ijmh() {
		this.util = new Util(this);
		this.playerEffects = new PlayerEffects(this);
	}
	
	public void onDisable (){

	}
	
	public void onEnable(){
		try {
		    Metrics metrics = new Metrics(this);
		    metrics.start();
		} catch (IOException e) {
		}
		
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(new PlayerListener(this), this);
		
		this.getConfig().options().copyDefaults(true);
        this.saveConfig();
		
        if(this.getConfig().getBoolean("update_message")) util.checkVersion(false, null, null);
		if(this.getConfig().getBoolean("debug")) this.debug = true;
		if(this.debug) getLogger().info("Debug enabled.");
		
		Plugin p = this.getServer().getPluginManager().getPlugin("MazeMania");
		if(p != null){
		  mazeMania = (MazeMania) p;
		  getLogger().info("MazeMania hooked");
		}
		p = getServer().getPluginManager().getPlugin("WorldGuard");
	    if (p != null || (p instanceof WorldGuardPlugin)) {
	        wg = (WorldGuardPlugin) p;
	        getLogger().info("WorldGuard hooked");
	    }
	}
	
	public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
		
		boolean err = false;
		
		try {
			if (args.length == 0) args = new String[] { "help" };
		    
			List effects = new LinkedList(); 
			effects.add("fire"); 
			effects.add("fall"); 
			effects.add("foodpoison");
			effects.add("lightning"); 
			effects.add("electro");
			effects.add("craftthumb");
			effects.add("cows");
			effects.add("happyminer");
			effects.add("roses");
			effects.add("brew");
			effects.add("squid");
			effects.add("tar");
			effects.add("bow");
			effects.add("rail");
			effects.add("fishing");
			
			if(sender.hasPermission("ijmh.admin")){
				if (args[0].equalsIgnoreCase("help")) {
					sender.sendMessage(ChatColor.AQUA + "- [ijhm] It Just Might Happen v" + this.getDescription().getVersion() + " ------------------"); 
					sender.sendMessage(ChatColor.AQUA + "Effects: " + ChatColor.GOLD + effects); 
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
				else if(effects.contains(args[0].toLowerCase()) && args.length==1){
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
						sender.sendMessage(ChatColor.AQUA + "| duration (5): " + ChatColor.GOLD + util.config("foodpoison",null).getInt("duration"));
					} 
					else if(args[0].equalsIgnoreCase("lightning")) {
						sender.sendMessage(ChatColor.AQUA + "| message (true): " + ChatColor.GOLD + util.config("lightning",null).getBoolean("message"));
						sender.sendMessage(ChatColor.AQUA + "| skip: " + ChatColor.GOLD + util.config("lightning",null).getList("skip_biome"));
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
					else if(args[0].equalsIgnoreCase("roses")) {
						sender.sendMessage(ChatColor.AQUA + "| message (true): " + ChatColor.GOLD + util.config("roses",null).getBoolean("message"));
						sender.sendMessage(ChatColor.AQUA + "| damage (1): " + ChatColor.GOLD + util.config("roses",null).getInt("damage"));
						sender.sendMessage(ChatColor.AQUA + "| multiplier (3): " + ChatColor.GOLD + util.config("roses",null).getInt("multiplier"));
						sender.sendMessage(ChatColor.AQUA + "| duration (2): " + ChatColor.GOLD + util.config("roses",null).getInt("duration"));
					}
					else if(args[0].equalsIgnoreCase("brew")) {
						sender.sendMessage(ChatColor.AQUA + "| signs (false): " + ChatColor.GOLD + util.config("brew",null).getBoolean("signs"));
						sender.sendMessage(ChatColor.AQUA + "| chance (1): " + ChatColor.GOLD + util.config("brew",null).getInt("chance"));
						sender.sendMessage(ChatColor.AQUA + "| chancemod (10): " + ChatColor.GOLD + util.config("brew",null).getInt("chancemod"));
						sender.sendMessage(ChatColor.AQUA + "| multiplier (1): " + ChatColor.GOLD + util.config("brew",null).getInt("multiplier"));
					}
					else if(args[0].equalsIgnoreCase("squid")) {
						sender.sendMessage(ChatColor.AQUA + "| message (true): " + ChatColor.GOLD + util.config("squid",null).getBoolean("message"));
						sender.sendMessage(ChatColor.AQUA + "| chance (25): " + ChatColor.GOLD + util.config("squid",null).getInt("chance"));
						sender.sendMessage(ChatColor.AQUA + "| chancemod (1): " + ChatColor.GOLD + util.config("squid",null).getInt("chancemod"));
						sender.sendMessage(ChatColor.AQUA + "| multiplier (1): " + ChatColor.GOLD + util.config("squid",null).getInt("multiplier"));
						sender.sendMessage(ChatColor.AQUA + "| duration (5): " + ChatColor.GOLD + util.config("squid",null).getInt("duraton"));
					}
					else if(args[0].equalsIgnoreCase("tar")) {
						sender.sendMessage(ChatColor.AQUA + "| message (true): " + ChatColor.GOLD + util.config("tar",null).getBoolean("message"));
						sender.sendMessage(ChatColor.AQUA + "| multiplier (5): " + ChatColor.GOLD + util.config("tar",null).getInt("multiplier"));
						sender.sendMessage(ChatColor.AQUA + "| duration (1): " + ChatColor.GOLD + util.config("tar",null).getInt("duration"));
					}
					else if(args[0].equalsIgnoreCase("bow")) {
						sender.sendMessage(ChatColor.AQUA + "| message (true): " + ChatColor.GOLD + util.config("bow",null).getBoolean("message"));
						sender.sendMessage(ChatColor.AQUA + "| chance (10): " + ChatColor.GOLD + util.config("bow",null).getInt("chance"));
						sender.sendMessage(ChatColor.AQUA + "| chancemod (1): " + ChatColor.GOLD + util.config("bow",null).getInt("chancemod"));
						sender.sendMessage(ChatColor.AQUA + "| damage (4): " + ChatColor.GOLD + util.config("bow",null).getInt("damage"));
					}
					else if(args[0].equalsIgnoreCase("rail")) {
						sender.sendMessage(ChatColor.AQUA + "| message (true): " + ChatColor.GOLD + util.config("rail",null).getBoolean("message"));
						sender.sendMessage(ChatColor.AQUA + "| chance (1): " + ChatColor.GOLD + util.config("rail",null).getInt("chance"));
						sender.sendMessage(ChatColor.AQUA + "| chancemod (1): " + ChatColor.GOLD + util.config("rail",null).getInt("chancemod"));
						sender.sendMessage(ChatColor.AQUA + "| distance (1): " + ChatColor.GOLD + util.config("rail",null).getInt("distance"));
						sender.sendMessage(ChatColor.AQUA + "| angle (1): " + ChatColor.GOLD + util.config("rail",null).getInt("angle"));
					}
					else if(args[0].equalsIgnoreCase("fishing")) {
						String state1;
						String state2;
						
						if(util.config("fishing", "lucky").getBoolean("active")) state1 = ChatColor.GREEN + "enabled";
						else state1 = ChatColor.RED + "disabled";
						if(util.config("fishing", "spawn").getBoolean("active")) state2 = ChatColor.GREEN + "enabled";
						else state2 = ChatColor.RED + "disabled";
						
						sender.sendMessage(ChatColor.AQUA + "lucky | toggle: " + state1);
						sender.sendMessage(ChatColor.AQUA + "lucky | message (true): " + ChatColor.GOLD + util.config("fishing","lucky").getBoolean("message"));
						sender.sendMessage(ChatColor.AQUA + "lucky | chance (5): " + ChatColor.GOLD + util.config("fishing","lucky").getInt("chance"));
						sender.sendMessage(ChatColor.AQUA + "lucky | chancemod (1): " + ChatColor.GOLD + util.config("fishing","lucky").getInt("chancemod"));
						sender.sendMessage(ChatColor.AQUA + "lucky | items: " + ChatColor.GOLD + util.config("fishing","lucky").getList("items"));
						sender.sendMessage(ChatColor.AQUA + "spawn | toggle: " + state2);
						sender.sendMessage(ChatColor.AQUA + "spawn | message (true): " + ChatColor.GOLD + util.config("fishing","spawn").getBoolean("message"));
						sender.sendMessage(ChatColor.AQUA + "spawn | chance (1): " + ChatColor.GOLD + util.config("fishing","spawn").getInt("chance"));
						sender.sendMessage(ChatColor.AQUA + "spawn | chancemod (1): " + ChatColor.GOLD + util.config("fishing","spawn").getInt("chancemod"));
						sender.sendMessage(ChatColor.AQUA + "spawn | mobs: " + ChatColor.GOLD + util.config("fishing","spawn").getList("mobs"));
					} 
					
					sender.sendMessage(ChatColor.GOLD + "message" + ChatColor.AQUA + " (true/false), " + ChatColor.GOLD + "chance" + ChatColor.AQUA + " (1-100), " + ChatColor.GOLD + "duration" + ChatColor.AQUA + " (seconds),");
					sender.sendMessage(ChatColor.GOLD + "damage" + ChatColor.AQUA + " (1=�hearth), " + ChatColor.GOLD + "multiplier" + ChatColor.AQUA + " (1-5), " + ChatColor.GOLD + "cooldown" + ChatColor.AQUA + " (seconds)");
				}
				else if(effects.contains(args[0].toLowerCase()) && args[1].equalsIgnoreCase("toggle")){
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
				else if(effects.contains(args[0].toLowerCase()) && args[2].equalsIgnoreCase("toggle")){
					if(util.config(args[0],args[1]).isBoolean("active")) {
						if(this.getConfig().getConfigurationSection(args[0]).getConfigurationSection(args[1]).getBoolean("active")) {
							this.getConfig().getConfigurationSection(args[0]).getConfigurationSection(args[1]).set("active", false);
							sender.sendMessage(ChatColor.AQUA + "[ijhm] " + args[1] + " has been switched off");
							this.saveConfig();
						}
						else {
							this.getConfig().getConfigurationSection(args[0]).getConfigurationSection(args[1]).set("active", true);
							sender.sendMessage(ChatColor.AQUA + "[ijhm] " + args[1] + " has been switched on");
							this.saveConfig();
						}
					} 
					else {
						err = true;
					}
				} 
				else if(effects.contains(args[0].toLowerCase()) && args.length==3){
					if(util.config(args[0],null).isSet(args[1]) || args[1].equalsIgnoreCase("skip")) {
						
						if(args[1].equalsIgnoreCase("message")) {
							boolean valueB = Boolean.parseBoolean(args[2]);
							util.config(args[0],null).set(args[1], valueB);
						}
						else if(args[1].equalsIgnoreCase("skip")) {
							if(Util.isBiome(args[2])) {
								if(Util.config("lightning",null).getList("skip_biome").contains(args[2].toUpperCase())) {
									Util.config("lightning",null).getList("skip_biome").remove(args[2].toUpperCase());
									sender.sendMessage(ChatColor.AQUA + "[ijhm] Biome:" + args[2] + " was removed from the list");
								} else {
									List skip_biome = Util.config("lightning",null).getList("skip_biome");
									skip_biome.add(args[2].toUpperCase());
									sender.sendMessage(ChatColor.AQUA + "[ijhm] Biome:" + args[2] + " was added to the list");
								}
							} else {
								sender.sendMessage(ChatColor.RED + "[ijhm] Biome not recognized!");
							}
						}
						else {
							int valueI = Integer.parseInt(args[2]);
							util.config(args[0],null).set(args[1], valueI);
						}
						
						if(!args[1].equalsIgnoreCase("skip")) sender.sendMessage(ChatColor.AQUA + "[ijhm] " + args[1] + " was changed to " + args[2]);
						this.saveConfig();
					}
					else err = true;
				}
				else if(effects.contains(args[0].toLowerCase()) && args.length==4){
					if(util.config(args[0],args[1]).isSet(args[2])) {

						if(args[2].equalsIgnoreCase("message")) {
							boolean valueB = Boolean.parseBoolean(args[3]);
							util.config(args[0],args[1]).set(args[2], valueB);
						}
						else if(args[0].equalsIgnoreCase("fishing") && args[2].equalsIgnoreCase("items")) {
							if(util.isItem(args[3])!=null) {
								if(Util.config("fishing","lucky").getList("items").contains(util.isItem(args[3]).toString())) {
									Util.config("fishing","lucky").getList("items").remove(util.isItem(args[3]).toString());
									sender.sendMessage(ChatColor.AQUA + "[ijhm] " + util.isItem(args[3]) + " was removed from the list");
								} else {
									List items = Util.config("fishing","lucky").getList("items");
									items.add(util.isItem(args[3]).toString());
									sender.sendMessage(ChatColor.AQUA + "[ijhm] " + util.isItem(args[3]) + " was added to the list");
								}
							} else {
								sender.sendMessage(ChatColor.RED + "[ijhm] Item (" + args[3] + ") not recognized!");
							}
						}
						else if(args[0].equalsIgnoreCase("fishing") && args[2].equalsIgnoreCase("mobs")) {
							if(util.isEntity(args[3])!=null) {
								if(Util.config("fishing","spawn").getList("mobs").contains(util.isEntity(args[3]).toString())) {
									Util.config("fishing","spawn").getList("mobs").remove(util.isEntity(args[3]).toString());
									sender.sendMessage(ChatColor.AQUA + "[ijhm] " + util.isEntity(args[3]) + " was removed from the list");
								} else {
									List mobs = Util.config("fishing","spawn").getList("mobs");
									mobs.add(util.isEntity(args[3]).toString());
									sender.sendMessage(ChatColor.AQUA + "[ijhm] " + util.isEntity(args[3]) + " was added to the list");
								}
							} else {
								sender.sendMessage(ChatColor.RED + "[ijhm] Mob (" + args[3] + ") not recognized!");
							}
						}
						else {
							int valueI = Integer.parseInt(args[3]);
							util.config(args[0],args[1]).set(args[2], valueI);
						}
						
						if(!args[2].equalsIgnoreCase("items") && !args[2].equalsIgnoreCase("mobs")) sender.sendMessage(ChatColor.AQUA + "[ijhm] " + args[2] + " was changed to " + args[3]);
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
