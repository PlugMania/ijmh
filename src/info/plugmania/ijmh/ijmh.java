package info.plugmania.ijmh;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffectType;

import info.plugmania.ijmh.Util;
import info.plugmania.ijmh.listeners.BlockListener;
import info.plugmania.ijmh.listeners.CraftListener;
import info.plugmania.ijmh.listeners.EntityListener;
import info.plugmania.ijmh.listeners.InventoryListener;
import info.plugmania.ijmh.listeners.PlayerListener;
import info.plugmania.ijmh.effects.BowBreaker;
import info.plugmania.ijmh.effects.BrewExplosion;
import info.plugmania.ijmh.effects.BuggyBlock;
import info.plugmania.ijmh.effects.BumpInTheRail;
import info.plugmania.ijmh.effects.Concussion;
import info.plugmania.ijmh.effects.CowsDoKick;
import info.plugmania.ijmh.effects.CraftThumb;
import info.plugmania.ijmh.effects.DizzyInTheDesert;
import info.plugmania.ijmh.effects.Electrocution;
import info.plugmania.ijmh.effects.FishermanOnHook;
import info.plugmania.ijmh.effects.Foodpoisoning;
import info.plugmania.ijmh.effects.HeavyDuty;
import info.plugmania.ijmh.effects.OnFire;
import info.plugmania.ijmh.effects.Quicksand;
import info.plugmania.ijmh.effects.RosesHaveThorns;
import info.plugmania.ijmh.effects.RowYourBoat;
import info.plugmania.ijmh.effects.SneakyPickup;
import info.plugmania.ijmh.effects.SquidDefense;
import info.plugmania.ijmh.effects.StickyTar;
import info.plugmania.ijmh.effects.StruckByLightning;
import info.plugmania.ijmh.effects.TheHappyMiner;
import info.plugmania.ijmh.effects.UnstableTNT;
import info.plugmania.ijmh.effects.UntamedRide;
import info.plugmania.ijmh.effects.WorldDrop;
import info.plugmania.ijmh.effects.ZombieNation;

import info.plugmania.mazemania.MazeMania;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

public class ijmh extends JavaPlugin {
	
	public Scheduler scheduler;
	public final Util util;
	public boolean debug;
	public List<String> disabled = new LinkedList<String>();
	public HashMap<String, String> feature = new HashMap<String, String>();
	public HashMap<String, String> subfeature = new HashMap<String, String>();
	public HashMap<String, String> cmdRef = new HashMap<String, String>();
	
	// EFFECTS
	public CraftThumb craftthumb;
	public BrewExplosion brewexplosion;
	public HeavyDuty heavyduty;
	public BumpInTheRail bumpintherail;
	public UnstableTNT unstabletnt;
	public BuggyBlock buggyblock;
	public TheHappyMiner thehappyminer;
	public Quicksand quicksand;
	public ZombieNation zombienation;
	public BowBreaker bowbreaker;
	public SquidDefense squiddefense;
	public SneakyPickup sneakypickup;
	public FishermanOnHook fishermanonhook;
	public Foodpoisoning foodpoisoning;
	public OnFire onfire;
	public Electrocution electrocution;
	public StickyTar stickytar;
	public RosesHaveThorns roseshavethorns;
	public StruckByLightning struckbylightning;
	public Concussion concussion;
	public CowsDoKick cowsdokick;
	public RowYourBoat rowyourboat;
	public UntamedRide untamedride;
	public DizzyInTheDesert dizzyinthedesert;
	public WorldDrop worlddrop;
	
	// PLUGIN SUPPORT
	public MazeMania mazeMania;
	public WorldGuardPlugin wg;
	
	public ijmh() {
		this.util = new Util(this);
		this.scheduler = new Scheduler(this);
		
		// EFFECTS
		this.craftthumb = new CraftThumb(this);
		this.brewexplosion = new BrewExplosion(this);
		this.heavyduty = new HeavyDuty(this);
		this.bumpintherail = new BumpInTheRail(this);
		this.unstabletnt = new UnstableTNT(this);
		this.buggyblock = new BuggyBlock(this);
		this.thehappyminer = new TheHappyMiner(this);
		this.quicksand = new Quicksand(this);
		this.zombienation = new ZombieNation(this);
		this.bowbreaker = new BowBreaker(this);
		this.squiddefense = new SquidDefense(this);
		this.sneakypickup = new SneakyPickup(this);
		this.fishermanonhook = new FishermanOnHook(this);
		this.foodpoisoning = new Foodpoisoning(this);
		this.onfire = new OnFire(this);
		this.electrocution = new Electrocution(this);
		this.stickytar = new StickyTar(this);
		this.roseshavethorns = new RosesHaveThorns(this);
		this.struckbylightning = new StruckByLightning(this);
		this.concussion = new Concussion(this);
		this.cowsdokick = new CowsDoKick(this);
		this.rowyourboat = new RowYourBoat(this);
		this.untamedride = new UntamedRide(this);
		this.dizzyinthedesert = new DizzyInTheDesert(this);
		this.worlddrop = new WorldDrop(this);
	}
	
	public void onDisable(){
		this.quicksand.quicksand.clear();
		for(Player player : this.rowyourboat.drowning.keySet()){
			this.rowyourboat.drowning.get(player).breakNaturally();
			player.removePotionEffect(PotionEffectType.BLINDNESS);
		}
		for(Player player : this.dizzyinthedesert.desert){
			player.removePotionEffect(PotionEffectType.SLOW);
			player.removePotionEffect(PotionEffectType.CONFUSION);
		}		
		this.rowyourboat.drowning.clear();
	}
	
	public void onEnable(){
		try {
		    Metrics metrics = new Metrics(this);
		    metrics.start();
		} catch (IOException e) {
		}
		
		this.bowbreaker.init();
		this.brewexplosion.init();
		this.buggyblock.init();
		this.bumpintherail.init();
		this.concussion.init();
		this.cowsdokick.init();
		this.craftthumb.init();
		this.dizzyinthedesert.init();
		this.electrocution.init();
		this.fishermanonhook.init();
		this.foodpoisoning.init();
		this.heavyduty.init();
		this.onfire.init();
		this.quicksand.init();
		this.roseshavethorns.init();
		this.rowyourboat.init();
		this.sneakypickup.init();
		this.squiddefense.init();
		this.stickytar.init();
		this.struckbylightning.init();
		this.thehappyminer.init();
		this.unstabletnt.init();
		this.untamedride.init();
		this.worlddrop.init();
		this.zombienation.init();
		
		// COMMANDS
		this.cmdRef.put("a", 	"angle");
		this.cmdRef.put("abs", 	"abovesealvl");
		this.cmdRef.put("am", 	"amount");
		this.cmdRef.put("ba", 	"backwards");
		this.cmdRef.put("bl", 	"blocks");
		this.cmdRef.put("c", 	"chance");
		this.cmdRef.put("cd", 	"cooldown");
		this.cmdRef.put("cm", 	"chancemod");
		this.cmdRef.put("d", 	"damage");
		this.cmdRef.put("di", 	"distance");
		this.cmdRef.put("du", 	"duration");
		this.cmdRef.put("fs", 	"flyspeed");
		this.cmdRef.put("i", 	"items");
		this.cmdRef.put("ju", 	"jumps");
		this.cmdRef.put("ml", 	"maxlocs");
		this.cmdRef.put("m", 	"message");
		this.cmdRef.put("mo", 	"modifier");
		this.cmdRef.put("mob", 	"mobs");
		this.cmdRef.put("mu", 	"multiplier");
		this.cmdRef.put("r", 	"radius");
		this.cmdRef.put("sig", 	"signs");
		this.cmdRef.put("skb", 	"skipbiome");
		this.cmdRef.put("skw", 	"skipworld");
		this.cmdRef.put("up", 	"upwards");
		this.cmdRef.put("wd", 	"whendesert");
		this.cmdRef.put("ws", 	"walkspeed");
		this.cmdRef.put("wz", 	"whenzombie");
		
		Util.languageFile = new File(getDataFolder(), "language.yml");
		Util.configFile = new File(getDataFolder(), "config.yml");
		try {
            util.firstRun();
        } catch (Exception e) {
            e.printStackTrace();
        }
		Util.language = new YamlConfiguration();
		Util.config = new YamlConfiguration();
		Util.loadYamls();
		
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(new PlayerListener(this), this);
		pm.registerEvents(new BlockListener(this), this);
		pm.registerEvents(new EntityListener(this), this);
		pm.registerEvents(new InventoryListener(this), this);
		pm.registerEvents(new CraftListener(this), this);
		
        if(this.getConfig().getBoolean("update_message")) util.checkVersion(false, null, null);
		if(this.getConfig().getBoolean("debug")) this.debug = true;
		Util.toLog("Debug enabled", true);
		
		util.checkYamls();
		
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
	    
	    this.worlddrop.main();
	    
	    // ARMORVALUES TO HASHMAP
	    util.armorval2type();
	}
	
	public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
		
		boolean err = false;
		
		try {
			if (args.length == 0) args = new String[] { "help" };
		    
			List<String> effects = new LinkedList<String>(); 
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
			effects.add("quicksand");
			effects.add("boat");
			effects.add("desert");
			effects.add("buggyblock");
			effects.add("tnt");
			effects.add("ride");
			effects.add("worlddrop");
			effects.add("sneaky");
			effects.add("zombie");
			effects.add("heavy");
			
			this.disabled.add("ride");
			
			if(sender.hasPermission("ijmh.admin")){
				String features = "";

				for (Entry<String, String> e : this.feature.entrySet()) {
				    String eKey = e.getKey();
				    String eValue = e.getValue();
				    
					ConfigurationSection config = this.getConfig().getConfigurationSection(eValue);
					if(config.getBoolean("active")) features += ChatColor.GREEN;
					else features += ChatColor.RED;
					features += eValue + ChatColor.WHITE + ", ";
				}
				if (args[0].equalsIgnoreCase("help")) {
					sender.sendMessage(ChatColor.AQUA + "- [ijhm] It Just Might Happen v" + this.getDescription().getVersion() + " ------------------"); 
					sender.sendMessage(ChatColor.AQUA + "Features: " + ChatColor.GOLD + features.substring(0, features.length() - 2)); 
					sender.sendMessage(ChatColor.GREEN + "/ijmh <feature or part of featurename>" + ChatColor.AQUA + " - HowTo change values for a feature");
					sender.sendMessage(ChatColor.GREEN + "/ijmh <feature or part of featurename> toggle" + ChatColor.AQUA + " - turn feature on/off");
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
							Util.saveYamls();
					}
					else {
							this.getConfig().set("update_message", true);
							sender.sendMessage(ChatColor.AQUA + "[ijhm] update messages has been switched on");
							Util.saveYamls();
					} 
				}
				else if(args[0].equalsIgnoreCase("load")) {
					Util.loadYamls();
					this.reloadConfig();
					sender.sendMessage(ChatColor.AQUA + "[ijhm] Configuration loaded");
				}
				else if(this.disabled.contains(args[0])) {
					sender.sendMessage(ChatColor.RED + "[ijhm] This feature is disabled for now");
				}
				else {
					if("bowbreaker".contains(args[0].toLowerCase())) 				{	args[0] = "bowbreaker"; 		this.bowbreaker.command(sender, args); }
					else if("onfire".contains(args[0].toLowerCase())) 				{	args[0] = "onfire"; 			this.onfire.command(sender, args); }
					else if("concussion".contains(args[0].toLowerCase())) 			{	args[0] = "concussion"; 		this.concussion.command(sender, args); }
					else if("foodpoisoning".contains(args[0].toLowerCase()))		{	args[0] = "foodpoisoning"; 		this.foodpoisoning.command(sender, args); }
					else if("struckbylightning".contains(args[0].toLowerCase())) 	{	args[0] = "struckbylightning"; 	this.struckbylightning.command(sender, args); } 
					else if("electrocution".contains(args[0].toLowerCase())) 		{	args[0] = "electrocution"; 		this.electrocution.command(sender, args); }					
					else if("craftthumb".contains(args[0].toLowerCase())) 			{	args[0] = "craftthumb"; 		this.craftthumb.command(sender, args); }
					else if("cowsdokick".contains(args[0].toLowerCase())) 			{	args[0] = "cowsdokick"; 		this.cowsdokick.command(sender, args); }
					else if("thehappyminer".contains(args[0].toLowerCase())) 		{	args[0] = "thehappyminer"; 		this.thehappyminer.command(sender, args); }
					else if("roseshavethorns".contains(args[0].toLowerCase())) 		{	args[0] = "roseshavethorns"; 	this.roseshavethorns.command(sender, args); }
					else if("brewexplosion".contains(args[0].toLowerCase())) 		{	args[0] = "brewexplosion"; 		this.brewexplosion.command(sender, args); }
					else if("squiddefense".contains(args[0].toLowerCase())) 		{	args[0] = "squiddefense"; 		this.squiddefense.command(sender, args); }
					else if("stickytar".contains(args[0].toLowerCase()))			{	args[0] = "stickytar"; 			this.stickytar.command(sender, args); }
					else if("bumpintherail".contains(args[0].toLowerCase())) 		{	args[0] = "bumpintherail"; 		this.bumpintherail.command(sender, args); }
					else if("quicksand".contains(args[0].toLowerCase())) 			{	args[0] = "quicksand"; 			this.quicksand.command(sender, args); }
					else if("dizzyinthedesert".contains(args[0].toLowerCase())) 	{	args[0] = "dizzyinthedesert"; 	this.dizzyinthedesert.command(sender, args); }
					else if("rowyourboat".contains(args[0].toLowerCase()))			{	args[0] = "rowyourboat"; 		this.rowyourboat.command(sender, args); }
					else if("buggyblock".contains(args[0].toLowerCase()))			{	args[0] = "buggyblock"; 		this.buggyblock.command(sender, args); }
					else if("unstabletnt".contains(args[0].toLowerCase())) 			{	args[0] = "unstabletnt"; 		this.unstabletnt.command(sender, args); }
					else if("untamedride".contains(args[0].toLowerCase())) 			{	args[0] = "untamedride"; 		this.untamedride.command(sender, args); }
					else if("worlddrop".contains(args[0].toLowerCase()))			{	args[0] = "worlddrop"; 			this.worlddrop.command(sender, args); }
					else if("sneakypickup".contains(args[0].toLowerCase())) 		{	args[0] = "sneakypickup"; 		this.sneakypickup.command(sender, args); }
					else if("zombienation".contains(args[0].toLowerCase())) 		{	args[0] = "zombienation"; 		this.zombienation.command(sender, args); }
					else if("heavyduty".contains(args[0].toLowerCase())) 			{	args[0] = "heavyduty"; 			this.heavyduty.command(sender, args); }			
					else if("fishermanonhook".contains(args[0].toLowerCase())) 		{	args[0] = "fishermanonhook"; 	this.fishermanonhook.command(sender, args); }
					else sender.sendMessage(ChatColor.RED + "No feature was recognized with that namesearch!");
				}
/*				
						else if(args[1].equalsIgnoreCase("entitytype")) {
							if(EntityType.fromName(args[2])!=null) {
								if(Util.config(args[0],null).getList("entitytype").contains(args[2].toUpperCase())) {
									Util.config(args[0],null).getList("entitytype").remove(args[2].toUpperCase());
									sender.sendMessage(ChatColor.AQUA + "[ijhm] Entitytype:" + args[2].toUpperCase() + " was removed from the list");
								} else {
									List entitytype = Util.config(args[0],null).getList("entitytype");
									entitytype.add(args[2].toUpperCase());
									sender.sendMessage(ChatColor.AQUA + "[ijhm] Entitytype:" + args[2].toUpperCase() + " was added to the list");
								}
							} else {
								sender.sendMessage(ChatColor.RED + "[ijhm] Entitytype not recognized!");
							}
						}
*/						
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
