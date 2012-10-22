package info.plugmania.ijmh;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
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

import com.sk89q.worldedit.blocks.BlockType;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

public class ijmh extends JavaPlugin {
	
	public Scheduler scheduler;
	public final Util util;
	public boolean debug;
	public List<String> disabled = new LinkedList<String>();
	
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
		util.saveYamls();
	}
	
	public void onEnable(){
		try {
		    Metrics metrics = new Metrics(this);
		    metrics.start();
		} catch (IOException e) {
		}
		
		Util.languageFile = new File(getDataFolder(), "language.yml");
		try {
            util.firstRun();
        } catch (Exception e) {
            e.printStackTrace();
        }
		Util.language = new YamlConfiguration();
		Util.loadYamls();
		
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(new PlayerListener(this), this);
		pm.registerEvents(new BlockListener(this), this);
		pm.registerEvents(new EntityListener(this), this);
		pm.registerEvents(new InventoryListener(this), this);
		pm.registerEvents(new CraftListener(this), this);
		
		this.getConfig().options().copyDefaults(true);
        this.saveConfig();
		
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
				else if(this.disabled.contains(args[0])) {
					sender.sendMessage(ChatColor.RED + "[ijhm] This feature is disabled for now");
				}
				else if(effects.contains(args[0].toLowerCase()) && args.length==1){
					
					String state;
					if(Util.config(args[0], null).getBoolean("active")) state = ChatColor.GREEN + "enabled";
					else state = ChatColor.RED + "disabled";
					
					sender.sendMessage(ChatColor.AQUA + "- HOWTO change: " + args[0] + " effect " + state + ChatColor.AQUA + " ----------------");
					sender.sendMessage(ChatColor.GREEN + "/ijmh " + args[0] + " <name> <value> or <section> <name> <value>");
					sender.sendMessage(ChatColor.GOLD + "section" + ChatColor.AQUA + " | " + ChatColor.GOLD + "name" + ChatColor.AQUA + " (default): current value");
					sender.sendMessage(ChatColor.AQUA + "| toggle");
					
					if(args[0].equalsIgnoreCase("fire")) 			this.onfire.command(sender, args);
					else if(args[0].equalsIgnoreCase("fall")) 		this.concussion.command(sender, args);
					else if(args[0].equalsIgnoreCase("foodpoison")) this.foodpoisoning.command(sender, args);
					else if(args[0].equalsIgnoreCase("lightning")) 	this.struckbylightning.command(sender, args); 
					else if(args[0].equalsIgnoreCase("electro")) 	this.electrocution.command(sender, args);					
					else if(args[0].equalsIgnoreCase("craftthumb")) this.craftthumb.command(sender, args);
					else if(args[0].equalsIgnoreCase("cows")) 		this.cowsdokick.command(sender, args);
					else if(args[0].equalsIgnoreCase("happyminer")) this.thehappyminer.command(sender, args);
					else if(args[0].equalsIgnoreCase("roses")) 		this.roseshavethorns.command(sender, args);
					else if(args[0].equalsIgnoreCase("brew")) 		this.brewexplosion.command(sender, args);
					else if(args[0].equalsIgnoreCase("squid")) 		this.squiddefense.command(sender, args);
					else if(args[0].equalsIgnoreCase("tar")) 		this.stickytar.command(sender, args);
					else if(args[0].equalsIgnoreCase("bow")) 		this.bowbreaker.command(sender, args);
					else if(args[0].equalsIgnoreCase("rail")) 		this.bumpintherail.command(sender, args);
					else if(args[0].equalsIgnoreCase("quicksand")) 	this.quicksand.command(sender, args);
					else if(args[0].equalsIgnoreCase("desert")) 	this.dizzyinthedesert.command(sender, args);
					else if(args[0].equalsIgnoreCase("boat")) 		this.rowyourboat.command(sender, args);
					else if(args[0].equalsIgnoreCase("buggyblock")) this.buggyblock.command(sender, args);
					else if(args[0].equalsIgnoreCase("tnt")) 		this.unstabletnt.command(sender, args);
					else if(args[0].equalsIgnoreCase("ride")) 		this.untamedride.command(sender, args);
					else if(args[0].equalsIgnoreCase("worlddrop")) 	this.worlddrop.command(sender, args);
					else if(args[0].equalsIgnoreCase("sneaky")) 	this.sneakypickup.command(sender, args);
					else if(args[0].equalsIgnoreCase("zombie")) 	this.zombienation.command(sender, args);
					else if(args[0].equalsIgnoreCase("heavy")) 		this.heavyduty.command(sender, args);					
					else if(args[0].equalsIgnoreCase("fishing")) 	this.fishermanonhook.command(sender, args);
				}
				else if(effects.contains(args[0].toLowerCase()) && args[1].equalsIgnoreCase("toggle")){
					if(Util.config(args[0],null).isBoolean("active")) {
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
					if(Util.config(args[0],args[1]).isBoolean("active")) {
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
					if(Util.config(args[0],null).isSet(args[1]) || args[1].equalsIgnoreCase("skipbiome") || args[1].equalsIgnoreCase("skipworld")) {
						if(args[1].equalsIgnoreCase("message") || args[1].equalsIgnoreCase("signs") || args[1].equalsIgnoreCase("whenzombie") || args[1].equalsIgnoreCase("whendesert")) {
							boolean valueB = Boolean.parseBoolean(args[2]);
							Util.config(args[0],null).set(args[1], valueB);
						}
						else if(args[1].equalsIgnoreCase("skipbiome")) {
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
						else if(args[1].equalsIgnoreCase("skipworld")) {
							World world = this.getServer().getWorld(args[2].toString());
							if(this.getServer().getWorlds().contains(world)) {
								if(Util.config(args[0],null).getList("skip_world").contains(args[2])) {
									Util.config(args[0],null).getList("skip_world").remove(args[2]);
									sender.sendMessage(ChatColor.AQUA + "[ijhm] World:" + args[2] + " was removed from the list");
								} else {
									List skip_world = Util.config(args[0],null).getList("skip_world");
									skip_world.add(args[2]);
									sender.sendMessage(ChatColor.AQUA + "[ijhm] World:" + args[2] + " was added to the list");
								}
							} else {
								sender.sendMessage(ChatColor.RED + "[ijhm] World not recognized!");
							}
						}
						else if(args[1].equalsIgnoreCase("blocks")) {
							if(Material.matchMaterial(args[2])!=null) {
								if(Util.config(args[0],null).getList("blocks").contains(args[2].toUpperCase())) {
									Util.config(args[0],null).getList("blocks").remove(args[2].toUpperCase());
									sender.sendMessage(ChatColor.AQUA + "[ijhm] Blocktype:" + args[2].toUpperCase() + " was removed from the list");
								} else {
									List blocks = Util.config(args[0],null).getList("blocks");
									blocks.add(args[2].toUpperCase());
									sender.sendMessage(ChatColor.AQUA + "[ijhm] Blocktype:" + args[2].toUpperCase() + " was added to the list");
								}
							} else {
								sender.sendMessage(ChatColor.RED + "[ijhm] Blocktype not recognized!");
							}
						}
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
						else if(args[1].equalsIgnoreCase("items")) {
							if(util.isItem(args[2])!=null) {
								if(Util.config(args[0],null).getList("items").contains(util.isItem(args[2]).toString())) {
									Util.config(args[0],null).getList("items").remove(util.isItem(args[2]).toString());
									sender.sendMessage(ChatColor.AQUA + "[ijhm] " + util.isItem(args[2]) + " was removed from the list");
								} else {
									List items = Util.config(args[0],null).getList("items");
									items.add(util.isItem(args[2]).toString());
									sender.sendMessage(ChatColor.AQUA + "[ijhm] " + util.isItem(args[2]) + " was added to the list");
								}
							} else {
								sender.sendMessage(ChatColor.RED + "[ijhm] Item (" + args[2] + ") not recognized!");
							}
						}
						else if(args[0].equalsIgnoreCase("heavy") && args[1].equalsIgnoreCase("modifier")) {
							double valueD = 1.0;
							if(Double.parseDouble(args[2])<=1 && Double.parseDouble(args[2])>0) {
								valueD = Double.parseDouble(args[2]);
							} else {
								sender.sendMessage(ChatColor.RED + "[ijhm] modifier larger than 0 and lesser or equal to 1.0! Value set to default,");
							}
							Util.config(args[0],null).set(args[1], valueD);
						}
						else if(args[1].equalsIgnoreCase("walkspeed") || args[1].equalsIgnoreCase("flyspeed")) {
							double valueD = Double.parseDouble(args[2]);
							Util.config(args[0],null).set(args[1], valueD);
						}
						else {
							int valueI = Integer.parseInt(args[2]);
							Util.config(args[0],null).set(args[1], valueI);
						}
						
						if(!args[1].equalsIgnoreCase("skipbiome") && !args[1].equalsIgnoreCase("skipworld") && !args[1].equalsIgnoreCase("blocks") && !args[1].equalsIgnoreCase("items") && !args[1].equalsIgnoreCase("entitytype")) sender.sendMessage(ChatColor.AQUA + "[ijhm] " + args[1] + " was changed to " + args[2]);
						this.saveConfig();
					}
					else err = true;
				}
				else if(effects.contains(args[0].toLowerCase()) && args.length==4){
					if(Util.config(args[0],args[1]).isSet(args[2])) {

						if(args[2].equalsIgnoreCase("message")) {
							boolean valueB = Boolean.parseBoolean(args[3]);
							Util.config(args[0],args[1]).set(args[2], valueB);
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
							Util.config(args[0],args[1]).set(args[2], valueI);
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
