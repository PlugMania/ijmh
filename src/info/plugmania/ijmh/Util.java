package info.plugmania.ijmh;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.TreeMap;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.json.simple.ItemList;

import com.sk89q.worldedit.Vector;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;

import static com.sk89q.worldguard.bukkit.BukkitUtil.*;

@SuppressWarnings("unused")
public class Util{
	
	static ijmh plugin;
	public static File languageFile;
	public static FileConfiguration language;
	public static File configFile;
	public static FileConfiguration config;
	
	public static HashMap<Material, Integer> protection = new HashMap<Material, Integer>();
	public static HashMap<Material, Integer> protectionDef = new HashMap<Material, Integer>();
	
	public Util(ijmh instance) {
		plugin = instance;
	}
	
	public void checkVersion(boolean isCmd, Player player,CommandSender sender){
		
		try {	
			URL url = new URL("http://dev.bukkit.org/server-mods/" + plugin.getDescription().getName() + "/files.rss");
			BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
						
			String str;
			Boolean endCheck = false;
			while (((str = in.readLine()) != null) && !endCheck) {				
				String pattern = "(\\d)\\.(\\d)\\.(\\d)";
				Pattern r = Pattern.compile(pattern);
				Matcher m = r.matcher(str);
				
				if(m.find()) {
					String chkVersion = m.group(0).replace(".", "");
					int chkVal = Integer.parseInt(chkVersion);
					String curVersion = (plugin.getDescription().getVersion()).replace(".", "");
					int curVal = Integer.parseInt(curVersion);
					if(curVal<chkVal){
						String msg = ChatColor.AQUA + "[ijmh] New version " + ChatColor.GOLD + "v" + m.group(0) + ChatColor.AQUA + " is out! You are running " + ChatColor.GRAY + "v" + plugin.getDescription().getVersion() + ChatColor.AQUA + ". Get the new version is at " + ChatColor.GOLD + "http://dev.bukkit.org/server-mods/" + plugin.getDescription().getName();
						if(player!=null) player.sendMessage(msg);
						else if(sender!=null) sender.sendMessage(msg);
						else plugin.getLogger().info("New version v" + m.group(0) + " is out! You are running v" + plugin.getDescription().getVersion() + ". Get the new version is at http://dev.bukkit.org/server-mods/" + plugin.getDescription().getName());
					} else if(isCmd){
						sender.sendMessage(ChatColor.AQUA + "[ijmh] Newest version " + ChatColor.GOLD + plugin.getDescription().getVersion() + ChatColor.AQUA + " already running.");	
					}
					endCheck = true;
				}
			}
			in.close();
		 
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	// SHORTCUT TO CONFIG SECTIONS
	static public ConfigurationSection config(String s1, String s2){
		
		ConfigurationSection config = plugin.getConfig().getConfigurationSection(s1);
		if(s2!=null) config = config.getConfigurationSection(s2);
		
		return config; 
	}
	
	// CREATE CONFIG MESSAGE ROW
	public HashMap<String, String> cRow(String name, String sub, String type, String defaultvalue, String range) {
		
		HashMap<String, String> v = new HashMap<String, String>();
		v.put("name", name);
		v.put("sub", sub);
		v.put("type", type);
		v.put("defaultvalue", defaultvalue);
		v.put("range", range);
		
		return v;
	}
	
	// HASHMAP BREAKDOWN AND SEND TO COMMANDSENDER
	public void cSend(HashMap<Integer, HashMap<String, String>> c, String[] args, CommandSender sender) {
		
		String details = "";
		String state = "";
		if(Util.config(args[0], null).getBoolean("active")) state = ChatColor.GREEN + "enabled";
		else state = ChatColor.RED + "disabled";
		
		sender.sendMessage(ChatColor.AQUA + "- HOWTO change: " + args[0] + " effect " + state + ChatColor.AQUA + " ----------------");
		sender.sendMessage(ChatColor.GREEN + "/ijmh " + args[0] + " <option1>:<value> <section>@<option2>:<value> ...");
		sender.sendMessage(ChatColor.GOLD + "command" + ChatColor.AQUA + " | " + ChatColor.GOLD + "section" + ChatColor.AQUA + " | " + ChatColor.GOLD + "name" + ChatColor.AQUA + " (default): current value");
		sender.sendMessage(ChatColor.AQUA + "| toggle");
		if(args[0].equalsIgnoreCase("heavyduty")) {
			String resetStatus;
			if(Util.config(args[0], null).getBoolean("reset")) resetStatus = ChatColor.GREEN + "is on and speeds will be reset upon login";
			else resetStatus = ChatColor.RED + "is off";
			sender.sendMessage(ChatColor.AQUA + "| reset " + resetStatus);
		}
		
		for (Iterator<Integer> i = c.keySet().iterator(); i.hasNext();) {
			Integer key = i.next();
			String message = "" + ChatColor.AQUA;
			for (Entry<String, String> e : plugin.subfeature.entrySet()) {
			    String eKey = e.getKey();
			    String eValue = e.getValue();
			    
			    if(c.get(key).get("sub") == eValue) {
			    	message += eKey + "@";
			    }
			}
			for (Entry<String, String> e : plugin.cmdRef.entrySet()) {
			    String eKey = e.getKey();
			    String eValue = e.getValue();
			    
			    if(c.get(key).get("name") == eValue) {
			    	message += eKey;
				    if(eKey.length()==1) message += "  ";
				    else if(eKey.length()==2) message += " ";
			    }
			}
			if(c.get(key).get("sub") != null) message += " | " + c.get(key).get("sub"); 
			if(c.get(key).get("name").contains("text")) {
				message += c.get(key).get("defaultvalue");
			}
			else if(c.get(key).get("name").contains("toggle")) {
				state = ChatColor.RED + "disabled";
				if(Util.config(args[0], c.get(key).get("sub")).getBoolean("active")) state = ChatColor.GREEN + "enabled";
				message += " | " + c.get(key).get("name") + ": " + state;
			}
			else {
				message += " | " + c.get(key).get("name");
				if(c.get(key).get("defaultvalue") != null) message += " (" + c.get(key).get("defaultvalue") + ")";
				message += ": ";
				if(c.get(key).get("type").contains("list")) {
					if(Util.config(args[0],c.get(key).get("sub")).getList(c.get(key).get("name"))==null) message += "[]";
					else message += Util.config(args[0],c.get(key).get("sub")).getList(c.get(key).get("name"));
				}
				else if(c.get(key).get("type").contains("boolean")) message += Util.config(args[0],c.get(key).get("sub")).getBoolean(c.get(key).get("name"));
				else if(c.get(key).get("type").contains("integer")) message += Util.config(args[0],c.get(key).get("sub")).getInt(c.get(key).get("name"));
				else if(c.get(key).get("type").contains("double")) message += Util.config(args[0],c.get(key).get("sub")).getDouble(c.get(key).get("name"));
			}
			sender.sendMessage(message);
			if(c.get(key).get("range") != null) details += ChatColor.GOLD + c.get(key).get("name") + ChatColor.AQUA + " (" + c.get(key).get("range") + ") ";
		}
		sender.sendMessage(details);
	}
	
	// HASHMAP BREAKDOWN, VERIFY CONFIG.YML VALUES
	public String VerifyConfig(HashMap<Integer, HashMap<String, String>> c, String s1) {
		
		String filecontents = "";
		String lastsub = "";
		
		for (Iterator<Integer> i = c.keySet().iterator(); i.hasNext();) {
			Integer key = i.next();
			if(!c.get(key).get("name").contains("text")){
				filecontents += "\n";
				if(c.get(key).get("sub") != null) {
					if(c.get(key).get("sub")!=lastsub) filecontents += "  " + c.get(key).get("sub") + ":\n";
					filecontents += "  ";
					lastsub = c.get(key).get("sub"); 
				}
				filecontents += "  " + c.get(key).get("name") + ": ";
				if(c.get(key).get("type").contains("list")) {
					String name = c.get(key).get("name");
					
					// convert from old file
					if(name=="skipworld" && !Util.config.isString("plugin-version")) name = "skip_world";
					else if(name=="skipbiome" && !Util.config.isString("plugin-version")) name = "skip_biome";
					// ----------------------------------------

					if(s1.contains("heavyduty") && name.contains("armor") && (!Util.config(s1,c.get(key).get("sub")).isSet(name) || (Util.config(s1,c.get(key).get("sub")).isSet(name) && Util.config(s1,c.get(key).get("sub")).getList("armor").size()==0))) {
						
						Map<Material,Integer> armorlist = new TreeMap<Material,Integer>(plugin.util.protectionDef);
					    
						for (Iterator<Material> j = armorlist.keySet().iterator(); j.hasNext();) {
							Material armor = j.next();
							
					        Util.toLog(armor + ":" + plugin.util.protectionDef.get(armor), true);
							
							filecontents += "\n";
							filecontents += "  - " + armor + "," + plugin.util.protectionDef.get(armor);
						}
					} else if(Util.config(s1,c.get(key).get("sub")).isSet(name)) {
						List<?> list = (List<?>) Util.config(s1,c.get(key).get("sub")).getList(name);
						if(list.isEmpty()) filecontents += "[]";
						else {
							Iterator<?> iterator = list.iterator();
							while (iterator.hasNext()) {
								filecontents += "\n";
								if(c.get(key).get("sub") != null) filecontents += "  ";
								filecontents += "  - " + iterator.next();
							}
						}
					} else filecontents += "[]";
				}
				else if(c.get(key).get("type").contains("boolean") || c.get(key).get("type").contains("toggle")) {
					if(!Util.config.isConfigurationSection(s1) || !Util.config(s1,c.get(key).get("sub")).isBoolean(c.get(key).get("name"))) filecontents += c.get(key).get("defaultvalue");
					else filecontents += Util.config(s1,c.get(key).get("sub")).getBoolean(c.get(key).get("name"));
				}
				else if(c.get(key).get("type").contains("integer")) {
					if(!Util.config.isConfigurationSection(s1) || !Util.config(s1,c.get(key).get("sub")).isInt(c.get(key).get("name"))) filecontents += c.get(key).get("defaultvalue");
					else filecontents += Util.config(s1,c.get(key).get("sub")).getInt(c.get(key).get("name"));
				}
				else if(c.get(key).get("type").contains("double")) {
					if(!Util.config.isConfigurationSection(s1) || !Util.config(s1,c.get(key).get("sub")).isDouble(c.get(key).get("name"))) filecontents += c.get(key).get("defaultvalue");
					else filecontents += Util.config(s1,c.get(key).get("sub")).getDouble(c.get(key).get("name"));
				}
			}
		}
		return filecontents;
	}
	class ValueComparator implements Comparator<String> {

	    Map<Material, Integer> base;
	    public ValueComparator(Map<Material, Integer> base) {
	        this.base = base;
	    }

	    // Note: this comparator imposes orderings that are inconsistent with equals.    
	    public int compare(String a, String b) {
	        if (base.get(a) >= base.get(b)) {
	            return -1;
	        } else {
	            return 1;
	        } // returning 0 would merge keys
	    }
	}
	
	public static boolean cmdExecute(CommandSender sender, String[] args) {
		
		String[] cmdSplit;
		String UsrCmd;
		String subkey = null;
		String subfeature = null;
		String onoff = "off";
		int armorUpdate = 0;
		
		if(args[1].equalsIgnoreCase("toggle")) {
			Util.config(args[0],null).set("active", (Boolean) !Util.config(args[0],null).getBoolean("active"));
			if(Util.config(args[0],null).getBoolean("active")==true) onoff = "on";
			sender.sendMessage(ChatColor.AQUA + "[ijhm] " + args[0] + " has been switched " + onoff);
		} else if(args[0].equalsIgnoreCase("heavyduty") && args[1].equalsIgnoreCase("reset")) {
				Util.config(args[0],null).set("reset", (Boolean) !Util.config(args[0],null).getBoolean("reset"));
				if(Util.config(args[0],null).getBoolean("reset")==true) onoff = "on. Onlineplayers have been reset. Upon login players will be reset as long as feature is disabled.";
				for(Player p : plugin.getServer().getOnlinePlayers()) {
					p.getPlayer().setWalkSpeed((float) 0.1);
					p.getPlayer().setFlySpeed((float) 0.2);
				}
				sender.sendMessage(ChatColor.AQUA + "[ijhm] reset has been switched " + onoff);
		} else {
			for(String cmd : args) {
				if(args[0]!=cmd) {
					UsrCmd = cmd;
					if(cmd.contains(":")) {
						cmdSplit = cmd.split(":");
						if(cmdSplit[0].contains("@")) {
							subkey = cmdSplit[0].split("@")[0];
							cmdSplit[0] = cmdSplit[0].split("@")[1];
							subfeature = plugin.subfeature.get(subkey);
						}
						if(plugin.cmdRef.containsKey(cmdSplit[0].toLowerCase()) && (subkey==null || Util.config(args[0], subfeature).isSet(plugin.cmdRef.get(cmdSplit[0].toLowerCase())))) {
							if(cmdSplit[0].equalsIgnoreCase("skw")) {
								World world = plugin.getServer().getWorld(cmdSplit[1].toString());
								if(plugin.getServer().getWorlds().contains(world)) {
									if(Util.config(args[0],subfeature).getList("skipworld").contains(cmdSplit[1])) {
										Util.config(args[0],subfeature).getList("skipworld").remove(cmdSplit[1]);
										sender.sendMessage(ChatColor.AQUA + "[ijhm] World:" + cmdSplit[1] + " was removed from the list");
									} else {
										@SuppressWarnings("unchecked")
										List<String> skipworld = (List<String>) Util.config(args[0],subfeature).getList("skipworld");
										skipworld.add(cmdSplit[1]);
										sender.sendMessage(ChatColor.AQUA + "[ijhm] World: " + cmdSplit[1] + " was added to the list");
									}
								} else {
									sender.sendMessage(ChatColor.RED + "[ijhm] World: " + cmdSplit[1] + " was not recognized!");
								}
							} else if(cmdSplit[0].equalsIgnoreCase("skb")) {
								if(Util.isBiome(cmdSplit[1])) {
									if(Util.config(args[0],subfeature).getList("skipbiome").contains(cmdSplit[1].toUpperCase())) {
										Util.config(args[0],subfeature).getList("skipbiome").remove(cmdSplit[1].toUpperCase());
										sender.sendMessage(ChatColor.AQUA + "[ijhm] Biome: " + cmdSplit[1] + " was removed from the list");
									} else {
										@SuppressWarnings("unchecked")
										List<String> skip_biome = (List<String>) Util.config(args[0],subfeature).getList("skipbiome");
										skip_biome.add(cmdSplit[1].toUpperCase());
										sender.sendMessage(ChatColor.AQUA + "[ijhm] Biome: " + cmdSplit[1] + " was added to the list");
									}
								} else {
									sender.sendMessage(ChatColor.RED + "[ijhm] Biome not recognized!");
								}
							} else if(cmdSplit[0].equalsIgnoreCase("bl")) {
								if(Material.matchMaterial(cmdSplit[1])!=null) {
									if(Util.config(args[0],subfeature).getList("blocks").contains(cmdSplit[1].toUpperCase())) {
										Util.config(args[0],subfeature).getList("blocks").remove(cmdSplit[1].toUpperCase());
										sender.sendMessage(ChatColor.AQUA + "[ijhm] Blocktype: " + cmdSplit[1].toUpperCase() + " was removed from the list");
									} else {
										@SuppressWarnings("unchecked")
										List<Material> blocks = (List<Material>) Util.config(args[0],subfeature).getList("blocks");
										Material block = Material.matchMaterial(cmdSplit[1]);
										blocks.add(block);
										sender.sendMessage(ChatColor.AQUA + "[ijhm] Blocktype: " + block + " was added to the list");
									}
								} else {
									sender.sendMessage(ChatColor.RED + "[ijhm] Blocktype: " + cmdSplit[1].toUpperCase() + " was not recognized!");
								}
							} else if(cmdSplit[0].equalsIgnoreCase("i")) {
								if(Util.isItem(cmdSplit[1])!=null) {
									if(Util.config(args[0],subfeature).getList("items").contains(Util.isItem(cmdSplit[1]).toString())) {
										Util.config(args[0],subfeature).getList("items").remove(Util.isItem(cmdSplit[1]).toString());
										sender.sendMessage(ChatColor.AQUA + "[ijhm] " + Util.isItem(cmdSplit[1]) + " was removed from the list");
									} else {
										@SuppressWarnings("unchecked")
										List<Material> items = (List<Material>) Util.config(args[0],subfeature).getList("items");
										items.add(Util.isItem(cmdSplit[1]));
										sender.sendMessage(ChatColor.AQUA + "[ijhm] " + Util.isItem(cmdSplit[1]) + " was added to the list");
									}
								} else {
									sender.sendMessage(ChatColor.RED + "[ijhm] Item: " + cmdSplit[1] + " was not recognized!");
								}
							} else if(cmdSplit[0].equalsIgnoreCase("arm")) {
								if(Util.isItem(cmdSplit[1].split(",")[0])!=null) {
									int isItem = 0;
									
									List<String> items = (List<String>) Util.config(args[0],subfeature).getList("armor");
									List<String> ItemList = new ArrayList<String>(items);

									for(String armor : items) {

										if(cmdSplit[1].split(",")[0].equalsIgnoreCase(armor.split(",")[0])) {
											isItem = 1;
											
											if(cmdSplit[1].split(",").length==1) {
												ItemList.remove(armor.toString());
												protection.remove(Util.isItem(armor.split(",")[0]));
												sender.sendMessage(ChatColor.AQUA + "[ijhm] " + Util.isItem(cmdSplit[1].split(",")[0]) + " was removed from the list");
											} else {
												ItemList.remove(armor.toString());
												ItemList.add(Util.isItem(cmdSplit[1].split(",")[0])+","+cmdSplit[1].split(",")[1]);
												sender.sendMessage(ChatColor.AQUA + "[ijhm] " + Util.isItem(cmdSplit[1].split(",")[0]) + " was updated");
											}
										}
									}
									Collections.sort(ItemList);
									items.clear();
									items.addAll(ItemList);
									
									if(isItem==0){
										items.add(Util.isItem(cmdSplit[1].split(",")[0])+","+cmdSplit[1].split(",")[1]);
										sender.sendMessage(ChatColor.AQUA + "[ijhm] " + Util.isItem(cmdSplit[1].split(",")[0]) + " was added to the list");
									}
									armorUpdate = 1;
								} else {
									sender.sendMessage(ChatColor.RED + "[ijhm] Item: " + cmdSplit[1].split(",")[0] + " was not recognized!");
								}
							} else if(cmdSplit[0].equalsIgnoreCase("mob")) {
								if(Util.isEntity(cmdSplit[1])!=null) {
									if(Util.config(args[0],subfeature).getList("mobs").contains(Util.isEntity(cmdSplit[1]).toString())) {
										Util.config(args[0],subfeature).getList("mobs").remove(Util.isEntity(cmdSplit[1]).toString());
										sender.sendMessage(ChatColor.AQUA + "[ijhm] " + Util.isEntity(cmdSplit[1]) + " was removed from the list");
									} else {
										@SuppressWarnings("unchecked")
										List<EntityType> mobs = (List<EntityType>) Util.config(args[0],subfeature).getList("mobs");
										mobs.add(Util.isEntity(cmdSplit[1]));
										sender.sendMessage(ChatColor.AQUA + "[ijhm] " + Util.isEntity(cmdSplit[1]) + " was added to the list");
									}
								} else {
									sender.sendMessage(ChatColor.RED + "[ijhm] Mob (" + cmdSplit[1] + ") not recognized!");
								}
							} else if(cmdSplit[0].equalsIgnoreCase("m") || cmdSplit[0].equalsIgnoreCase("sig") || cmdSplit[0].equalsIgnoreCase("wd") || cmdSplit[0].equalsIgnoreCase("wz") || cmdSplit[0].equalsIgnoreCase("was")) {
								Boolean tf = null;
								if(cmdSplit[1].equalsIgnoreCase("t")) tf = true;
								else if(cmdSplit[1].equalsIgnoreCase("f")) tf = false;
								else tf = (Boolean) !Util.config(args[0],subfeature).getBoolean(plugin.cmdRef.get(cmdSplit[0].toLowerCase()));
								Util.config(args[0],subfeature).set(plugin.cmdRef.get(cmdSplit[0].toLowerCase()), tf);
								sender.sendMessage(ChatColor.AQUA + "[ijhm] " + plugin.cmdRef.get(cmdSplit[0].toLowerCase()) + " was set to " + tf);
							} else if(cmdSplit[0].equalsIgnoreCase("ws") || cmdSplit[0].equalsIgnoreCase("fs") || cmdSplit[0].equalsIgnoreCase("mo")) {
								Util.config(args[0],subfeature).set(plugin.cmdRef.get(cmdSplit[0].toLowerCase()), Double.parseDouble(cmdSplit[1]));
								sender.sendMessage(ChatColor.AQUA + "[ijhm] " + plugin.cmdRef.get(cmdSplit[0].toLowerCase()) + " was set to " + cmdSplit[1]);
							} else {
								Util.config(args[0],subfeature).set(plugin.cmdRef.get(cmdSplit[0].toLowerCase()), Integer.parseInt(cmdSplit[1]));
								sender.sendMessage(ChatColor.AQUA + "[ijhm] " + plugin.cmdRef.get(cmdSplit[0].toLowerCase()) + " was set to " + cmdSplit[1]);
							} 
						} else {
							sender.sendMessage(ChatColor.RED + "[ijhm] Option: " + UsrCmd + " is unknown to " + args[0]);
						}
					} else sender.sendMessage(ChatColor.RED + "[ijhm] Command: " + cmd + " is invalid, commands are <option>:<value>");
				}
			}
		}
		
		Util.saveYamls();
		if(armorUpdate>0) armorval2type();
		
		return true;
	}
	
	// EASY SAVE TO CONFIG
	public void SavetoConfig(String s1, String s2, String key, String value) {
			Util.config(s1,s2).set(key, value);
	}
	
	static public void toLog(String s, boolean isDebug){
		
		if((isDebug && plugin.debug) || (!isDebug)) plugin.getLogger().info(s);
	}	
	
	static public boolean isBiome(String biome){
		boolean isBiome = false;
		
		List<String> BiomeList = new LinkedList<String>(); 
		BiomeList.add("FOREST"); 
		BiomeList.add("DESERT"); 
		BiomeList.add("PLAINS");
		BiomeList.add("SWAMPLAND"); 
		BiomeList.add("JUNGLE");
		BiomeList.add("TUNDRA");
		BiomeList.add("TAIGA");
		BiomeList.add("EXTREME_HILLS");
		BiomeList.add("OCEAN");	
		BiomeList.add("MUSHROOM_ISLAND");
		
		if(BiomeList.contains(biome.toUpperCase())) isBiome = true;
		
		return isBiome;
	}

	public static Material isItem(String item){
		
		Material material = null;

		if(Material.matchMaterial(item) != null) material = Material.matchMaterial(item);
		
		return material;
	}
	
	static int armorFromConfig(Material armor) {
		
		int armorVal = 0;
		
		if(Util.config("heavyduty", null).isList("armor")) { 
			
			@SuppressWarnings("unchecked")
			List<String> armorList = (List<String>) Util.config("heavyduty", null).getList("armor");

			for(String armorListItem : armorList) {
				String[] armorItem = armorListItem.split(",");
				Material ArmorType = Material.matchMaterial(armorItem[0]);
				int ArmorWeight = Integer.parseInt(armorItem[1]);
				
				if(armor.equals(ArmorType)) {
					armorVal = ArmorWeight;
				}
			}
		}
		
		if(armorVal == 0 && protectionDef.containsKey((Material) armor)) {
			armorVal = protectionDef.get((Material) armor);
		}
		
		return armorVal;
	}
	
	public static void armorval2type() {
		
		// SKULL ITEMS
		protection.put(Material.SKULL_ITEM, armorFromConfig(Material.SKULL_ITEM));
		protection.put(Material.BONE, armorFromConfig(Material.BONE));
		// LEATHER
		protection.put(Material.LEATHER_HELMET, armorFromConfig(Material.LEATHER_HELMET));
		protection.put(Material.LEATHER_BOOTS, armorFromConfig(Material.LEATHER_BOOTS));
		protection.put(Material.LEATHER_CHESTPLATE, armorFromConfig(Material.LEATHER_CHESTPLATE));
		protection.put(Material.LEATHER_LEGGINGS, armorFromConfig(Material.LEATHER_LEGGINGS));
		// GOLD
		protection.put(Material.GOLD_HELMET, armorFromConfig(Material.GOLD_HELMET));
		protection.put(Material.GOLD_BOOTS, armorFromConfig(Material.GOLD_BOOTS));
		protection.put(Material.GOLD_CHESTPLATE, armorFromConfig(Material.GOLD_CHESTPLATE));
		protection.put(Material.GOLD_LEGGINGS, armorFromConfig(Material.GOLD_LEGGINGS));
		// CHAINMAIL
		protection.put(Material.CHAINMAIL_HELMET, armorFromConfig(Material.CHAINMAIL_HELMET));
		protection.put(Material.CHAINMAIL_BOOTS, armorFromConfig(Material.CHAINMAIL_BOOTS));
		protection.put(Material.CHAINMAIL_CHESTPLATE, armorFromConfig(Material.CHAINMAIL_CHESTPLATE));
		protection.put(Material.CHAINMAIL_LEGGINGS, armorFromConfig(Material.CHAINMAIL_LEGGINGS));
		// IRON
		protection.put(Material.IRON_HELMET, armorFromConfig(Material.IRON_HELMET));
		protection.put(Material.IRON_BOOTS, armorFromConfig(Material.IRON_BOOTS));
		protection.put(Material.IRON_CHESTPLATE, armorFromConfig(Material.IRON_CHESTPLATE));
		protection.put(Material.IRON_LEGGINGS, armorFromConfig(Material.IRON_LEGGINGS));
		// DIAMOND
		protection.put(Material.DIAMOND_HELMET, armorFromConfig(Material.DIAMOND_HELMET));
		protection.put(Material.DIAMOND_BOOTS, armorFromConfig(Material.DIAMOND_BOOTS));
		protection.put(Material.DIAMOND_CHESTPLATE, armorFromConfig(Material.DIAMOND_CHESTPLATE));
		protection.put(Material.DIAMOND_LEGGINGS, armorFromConfig(Material.DIAMOND_LEGGINGS));	
	}
	
	public static int getPlayerArmorValue(Player player) {
		int curProt = 0;
		if(player.getInventory().getHelmet()!=null) {
			ItemStack helmet = player.getInventory().getHelmet();
			curProt += Util.protection.get(helmet.getType());
		}
		if(player.getInventory().getChestplate()!=null) {
			ItemStack chest = player.getInventory().getChestplate();
			curProt += Util.protection.get(chest.getType());
		}
		if(player.getInventory().getLeggings()!=null) {
			ItemStack leggings =  player.getInventory().getLeggings();
			curProt += Util.protection.get(leggings.getType());
		}
		if(player.getInventory().getBoots()!=null) {
			ItemStack boots = player.getInventory().getBoots();
			curProt += Util.protection.get(boots.getType());
		}
		
		Util.toLog(player.getName() + " has " + curProt + " armorpoints", true);
		
		return (curProt/5);
	}
	
	public static EntityType isEntity(String entity){
		
		EntityType entityType = null;

		if(EntityType.fromName(entity) != null) entityType = EntityType.fromName(entity);
		
		return entityType;
	}
	
	public static int sec2tic(int seconds){
		int ticks = seconds * 20;
		return ticks;
	}
	
	public static boolean pctChance(int d, int m){
		boolean result = false;
		
		int rNum = (int) ((m*100)*Math.random()); 
		if(rNum<=d) result = true;
		if(plugin.debug) plugin.getLogger().info(rNum + " <= " + d + " State:" + result);
		return result;
	}
	
	public static Object shuffle(List<?> list) {
		int size = list.size();
		int item = new Random().nextInt(size); // In real life, the Random object should be rather more shared than this
		int i = 0;
		Util.toLog("Rand:" + item, true);
		for(Object obj : list) {
		    if (i == item) return obj;
		    i = i + 1;
		}
		return null;
	}
	
	public static String chatColorText(String str) {
		
		str = str.replaceAll("&0", "" + ChatColor.BLACK);
		str = str.replaceAll("&1", "" + ChatColor.DARK_BLUE);
		str = str.replaceAll("&2", "" + ChatColor.DARK_GREEN);
		str = str.replaceAll("&3", "" + ChatColor.DARK_AQUA);
		str = str.replaceAll("&4", "" + ChatColor.DARK_RED);
		str = str.replaceAll("&5", "" + ChatColor.DARK_PURPLE);
		str = str.replaceAll("&6", "" + ChatColor.GOLD);
		str = str.replaceAll("&7", "" + ChatColor.GRAY);
		str = str.replaceAll("&8", "" + ChatColor.DARK_GRAY);
		str = str.replaceAll("&9", "" + ChatColor.BLUE);
		str = str.replaceAll("&a", "" + ChatColor.GREEN);
		str = str.replaceAll("&b", "" + ChatColor.AQUA);
		str = str.replaceAll("&c", "" + ChatColor.RED);
		str = str.replaceAll("&d", "" + ChatColor.LIGHT_PURPLE);
		str = str.replaceAll("&e", "" + ChatColor.YELLOW);
		str = str.replaceAll("&f", "" + ChatColor.WHITE);
		str = str.replaceAll("&I", "" + ChatColor.ITALIC);
		str = str.replaceAll("&B", "" + ChatColor.BOLD);
		str = str.replaceAll("&M", "" + ChatColor.MAGIC);
		str = str.replaceAll("&S", "" + ChatColor.STRIKETHROUGH);
		str = str.replaceAll("&U", "" + ChatColor.UNDERLINE);
		
		return str;
	}

	// YAML ------------------------------------------------------------------------------
	
    void firstRun() throws Exception {
        if(!languageFile.exists()){                        
        	languageFile.getParentFile().mkdirs();         
            copy(plugin.getResource("language.yml"), languageFile);
        }
        if(!configFile.exists()){                        
        	configFile.getParentFile().mkdirs();         
            copy(plugin.getResource("config.yml"), configFile);
        }       
    }

    private void copy(InputStream in, File file) {
        try {
            OutputStream out = new FileOutputStream(file);
            byte[] buf = new byte[1024];
            int len;
            while((len=in.read(buf))>0){
                out.write(buf,0,len);
            }
            out.close();
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void loadYamls() {
        try {
        	Util.toLog("Loading Config- & Languagefile",true);
        	language.load(languageFile);
        	config.load(configFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void saveYamls() {
    	
    	Util.toLog("Verifying config.yml",true);
    	
    	String filecontents = configFileContentsVerify();
    	
    	try
    	{
    		  // Create file 
    		  FileWriter fstream = new FileWriter(configFile);
    		  BufferedWriter out = new BufferedWriter(fstream);
    		  out.write(filecontents);
    		  //Close the output stream
    		  out.close();
    		  
    		  config.load(configFile);
    		  plugin.reloadConfig();
    		  
    	}catch (Exception e){ //Catch exception if any
    		  Util.toLog("Failed to save config.yml! Please delete it and restart server.",false);
    	} 
    }
	
    public static void checkYamls() {

    	// language.yml
    	if(!Util.language.isString("file-version") || !Util.language.getString("file-version").equals(plugin.getDescription().getVersion())) {
    		
	    	Util.toLog("Updating language.yml",false);
	    	
	    	String filecontents = "";
	    	String key = "";
	    					filecontents  = "# -------------------------------------------------";
	    					filecontents += "\n# -----------| LANGUAGE FILE FOR ijmh |------------";
	    					filecontents += "\n# -------------------------------------------------";
	    					filecontents += "\nfile-version: " + plugin.getDescription().getVersion();
	    					filecontents += "\n# -------------------------------------------------";
	    					filecontents += "\n\n# ON FIRE";
	    	key = "lan_01";	filecontents += "\n" + key + ": "; if(Util.language.getString(key)!=null) filecontents += Util.language.getString(key); else filecontents += "You caught fire, use a bucket of water or find water to put it out!";
	    	key = "lan_02";	filecontents += "\n" + key + ": "; if(Util.language.getString(key)!=null) filecontents += Util.language.getString(key); else filecontents += "You really need to be careful next time."; 
							filecontents += "\n\n# FOODPOISONING";
	    	key = "lan_03";	filecontents += "\n" + key + ": "; if(Util.language.getString(key)!=null) filecontents += Util.language.getString(key); else filecontents += "Your belly starts to rumble, that food must have been bad!? Milk Milk!!";
	    	key = "lan_04";	filecontents += "\n" + key + ": "; if(Util.language.getString(key)!=null) filecontents += Util.language.getString(key); else filecontents += "You feel better! You where lucky this time.";
	    					filecontents += "\n\n# STRUCK BY LIGHTNING";
	    	key = "lan_05";	filecontents += "\n" + key + ": "; if(Util.language.getString(key)!=null) filecontents += Util.language.getString(key); else filecontents += "Struck by lightning, guess your mom did not teach you not to hide under trees during a storm!?";
	    					filecontents += "\n\n# CONCUSSION";
	    	key = "lan_06";	filecontents += "\n" + key + ": "; if(Util.language.getString(key)!=null) filecontents += Util.language.getString(key); else filecontents += "You might have hit the ground a bit too hard there ...";
	    					filecontents += "\n\n# ELECTROCUTION";
	    	key = "lan_07";	filecontents += "\n" + key + ": "; if(Util.language.getString(key)!=null) filecontents += Util.language.getString(key); else filecontents += "You got lucky, the redstone was only carrying low voltage!";
	    	key = "lan_08";	filecontents += "\n" + key + ": "; if(Util.language.getString(key)!=null) filecontents += Util.language.getString(key); else filecontents += "Aaaaaarggghhh, the redstone zapped you with HIGH VOLTAGE!";
	    					filecontents += "\n\n# CRAFTING THUMB";
	    	key = "lan_09";	filecontents += "\n" + key + ": "; if(Util.language.getString(key)!=null) filecontents += Util.language.getString(key); else filecontents += "Auch! You struck your thumb.";
	    					filecontents += "\n\n# COWS DO KICK";
	    	key = "lan_10";	filecontents += "\n" + key + ": "; if(Util.language.getString(key)!=null) filecontents += Util.language.getString(key); else filecontents += "Seriously ... Do you know any cows that can be milked like that?!";
	    	key = "lan_11";	filecontents += "\n" + key + ": "; if(Util.language.getString(key)!=null) filecontents += Util.language.getString(key); else filecontents += "That might leave a mark ...!";
	    					filecontents += "\n\n# THE HAPPY MINER";
	    	key = "lan_12";	filecontents += "\n" + key + ": "; if(Util.language.getString(key)!=null) filecontents += Util.language.getString(key); else filecontents += "This is fun! you feel energized!!";
	    	key = "lan_13";	filecontents += "\n" + key + ": "; if(Util.language.getString(key)!=null) filecontents += Util.language.getString(key); else filecontents += "So tired... must slow down ...";
	    					filecontents += "\n\n# ROSES HAVE THORNS";
	    	key = "lan_14";	filecontents += "\n" + key + ": "; if(Util.language.getString(key)!=null) filecontents += Util.language.getString(key); else filecontents += "Thorns... why... thorns...!";
	    					filecontents += "\n\n# SQUID DEFENSE";
	    	key = "lan_15";	filecontents += "\n" + key + ": "; if(Util.language.getString(key)!=null) filecontents += Util.language.getString(key); else filecontents += "The Squid tries to defend itself!";
	    					filecontents += "\n\n# STICKY TAR";
	    	key = "lan_16";	filecontents += "\n" + key + ": "; if(Util.language.getString(key)!=null) filecontents += Util.language.getString(key); else filecontents += "The ground under you suddenly feels terribly sticky ...";
	    					filecontents += "\n\n# BOW BREAKER";
	    	key = "lan_17";	filecontents += "\n" + key + ": "; if(Util.language.getString(key)!=null) filecontents += Util.language.getString(key); else filecontents += "Your bow suddenly broke, not your day it seems ...";
	    					filecontents += "\n\n# BUMP IN THE RAIL";
	    	key = "lan_18";	filecontents += "\n" + key + ": "; if(Util.language.getString(key)!=null) filecontents += Util.language.getString(key); else filecontents += "Your cart hits a bump, what the ... oh no !";
	    					filecontents += "\n\n# FISHERMAN ON HOOK";
	    	key = "lan_19";	filecontents += "\n" + key + ": "; if(Util.language.getString(key)!=null) filecontents += Util.language.getString(key); else filecontents += "Lucky you! You also caught";
	    	key = "lan_20";	filecontents += "\n" + key + ": "; if(Util.language.getString(key)!=null) filecontents += Util.language.getString(key); else filecontents += "What! Something more on the hook, its a";
	    					filecontents += "\n\n# QUICK SAND";
	    	key = "lan_21";	filecontents += "\n" + key + ": "; if(Util.language.getString(key)!=null) filecontents += Util.language.getString(key); else filecontents += "Your feet suddenly dissapears in the sand... JUMP !";
	    	key = "lan_22";	filecontents += "\n" + key + ": "; if(Util.language.getString(key)!=null) filecontents += Util.language.getString(key); else filecontents += "Pew, that was close.";
	    	key = "lan_23";	filecontents += "\n" + key + ": "; if(Util.language.getString(key)!=null) filecontents += Util.language.getString(key); else filecontents += "was eaten by quicksand";
			filecontents += "\n\n# ROW YOUR BOAT";
			key = "lan_24";	filecontents += "\n" + key + ": "; if(Util.language.getString(key)!=null) filecontents += Util.language.getString(key); else filecontents += "The boat.. what.. you seem stuck! Break loose from the piece of the boat below you, break it and survive!";
			key = "lan_25";	filecontents += "\n" + key + ": "; if(Util.language.getString(key)!=null) filecontents += Util.language.getString(key); else filecontents += "You are free, hurry to the surface!";
			filecontents += "\n\n# DIZZY IN THE DESERT";
			key = "lan_26";	filecontents += "\n" + key + ": "; if(Util.language.getString(key)!=null) filecontents += Util.language.getString(key); else filecontents += "Sooooo thirsty... too hot...";
			key = "lan_27";	filecontents += "\n" + key + ": "; if(Util.language.getString(key)!=null) filecontents += Util.language.getString(key); else filecontents += "Ahhhhh, time to move on!";
			filecontents += "\n\n# BUGGYBLOCK";
			key = "lan_28";	filecontents += "\n" + key + ": "; if(Util.language.getString(key)!=null) filecontents += Util.language.getString(key); else filecontents += "BuggyBlock placed";
			key = "lan_29";	filecontents += "\n" + key + ": "; if(Util.language.getString(key)!=null) filecontents += Util.language.getString(key); else filecontents += "The block beneath you breaks!";
			filecontents += "\n\n# UNSTABLE TNT";
			key = "lan_30";	filecontents += "\n" + key + ": "; if(Util.language.getString(key)!=null) filecontents += Util.language.getString(key); else filecontents += "was killed by unstable TNT!";
			filecontents += "\n\n# UNTAMED RIDE";
			key = "lan_31";	filecontents += "\n" + key + ": "; if(Util.language.getString(key)!=null) filecontents += Util.language.getString(key); else filecontents += "becomes wild, you need to control it, say";
			key = "lan_32";	filecontents += "\n" + key + ": "; if(Util.language.getString(key)!=null) filecontents += Util.language.getString(key); else filecontents += "brrrrr";
			key = "lan_33";	filecontents += "\n" + key + ": "; if(Util.language.getString(key)!=null) filecontents += Util.language.getString(key); else filecontents += "is under control again, you can relax !";
			key = "lan_34";	filecontents += "\n" + key + ": "; if(Util.language.getString(key)!=null) filecontents += Util.language.getString(key); else filecontents += "-whisperer is NOT in your near future !";
			filecontents += "\n\n# ZOMBIE NATION";
			key = "lan_35";	filecontents += "\n" + key + ": "; if(Util.language.getString(key)!=null) filecontents += Util.language.getString(key); else filecontents += "a zombie took his place in the world!";
			filecontents += "\n\n# CRAZY COMBAT";
			key = "lan_36";	filecontents += "\n" + key + ": "; if(Util.language.getString(key)!=null) filecontents += Util.language.getString(key); else filecontents += "A spark arised from your weapons and set you on fire!";
			key = "lan_37";	filecontents += "\n" + key + ": "; if(Util.language.getString(key)!=null) filecontents += Util.language.getString(key); else filecontents += "Your attack backfired!";
			key = "lan_38";	filecontents += "\n" + key + ": "; if(Util.language.getString(key)!=null) filecontents += Util.language.getString(key); else filecontents += "Auch! Your the wood in your hand just gave a splinter";
			key = "lan_39";	filecontents += "\n" + key + ": "; if(Util.language.getString(key)!=null) filecontents += Util.language.getString(key); else filecontents += "Oh dear! Your weapon broke ...";
			key = "lan_40";	filecontents += "\n" + key + ": "; if(Util.language.getString(key)!=null) filecontents += Util.language.getString(key); else filecontents += "You feel the rush from your kill!";
			key = "lan_41";	filecontents += "\n" + key + ": "; if(Util.language.getString(key)!=null) filecontents += Util.language.getString(key); else filecontents += "Your fingers get in the way, man that hurt!";
			key = "lan_42";	filecontents += "\n" + key + ": "; if(Util.language.getString(key)!=null) filecontents += Util.language.getString(key); else filecontents += "Only real weapons can hurt other players!";
			key = "lan_43";	filecontents += "\n" + key + ": "; if(Util.language.getString(key)!=null) filecontents += Util.language.getString(key); else filecontents += "You cannot believe your eyes, it is multiplying... oh noes!";
			key = "lan_44";	filecontents += "\n" + key + ": "; if(Util.language.getString(key)!=null) filecontents += Util.language.getString(key); else filecontents += "You cannot believe your eyes, it is multiplying... its unreal!";
 			
	    	try
	    	{
	    		  // Create file 
	    		  FileWriter fstream = new FileWriter(languageFile);
	    		  BufferedWriter out = new BufferedWriter(fstream);
	    		  out.write(filecontents);
	    		  //Close the output stream
	    		  out.close();
	    		  
	    		  language.load(languageFile);
	    		  
	    	} catch (Exception e){ //Catch exception if any
	    		  Util.toLog("Failed to update language.yml! Please delete it and restart server.",false);
	    	}
    	}
    	
    	// config.yml
    	String filecontents = "";
    	
    	if(!Util.config.isString("plugin-version")) {
    		filecontents  = "# -------------------------------------------------";
        	filecontents += "\n# ------------| CONFIG FILE FOR ijmh |-------------";
        	filecontents += "\n# -------------------------------------------------";
        	filecontents += "\nplugin-version: " + plugin.getDescription().getVersion();
        	filecontents += "\n\n# Get notified about updates upon login?";
        	filecontents += "\nupdate_message: " + config.getBoolean("update_message");
        	if(config.isBoolean("debug")) filecontents += "\n\ndebug: " + config.getBoolean("debug");
        	filecontents += "\n# -------------------------------------------------";
        	filecontents += configFileContentsGen ("Bow Breaker", "bowbreaker");
        	filecontents += plugin.util.VerifyConfig(plugin.bowbreaker.c, "bow");
        	filecontents += configFileContentsGen ("Brew Explosion", "brewexplosion");
        	filecontents += plugin.util.VerifyConfig(plugin.brewexplosion.c, "brew");
        	filecontents += configFileContentsGen ("Buggy Block", "buggyblock");
        	filecontents += plugin.util.VerifyConfig(plugin.buggyblock.c, "buggyblock");
        	filecontents += configFileContentsGen ("Bump In The Rail", "bumpintherail");
        	filecontents += plugin.util.VerifyConfig(plugin.bumpintherail.c, "rail");
        	filecontents += configFileContentsGen ("Concussion", "concussion");
        	filecontents += plugin.util.VerifyConfig(plugin.concussion.c, "fall");
        	filecontents += configFileContentsGen ("Crazy Combat", "crazycombat");
        	filecontents += plugin.util.VerifyConfig(plugin.crazycombat.c, "crazycombat");
        	filecontents += configFileContentsGen ("Crafting Thumb", "craftthumb");
        	filecontents += plugin.util.VerifyConfig(plugin.craftthumb.c, "craftthumb");
        	filecontents += configFileContentsGen ("Cows Do Kick", "cowsdokick");
        	filecontents += plugin.util.VerifyConfig(plugin.cowsdokick.c, "cows");
        	filecontents += configFileContentsGen ("Dizzy In The Desert", "dizzyinthedesert");
        	filecontents += plugin.util.VerifyConfig(plugin.dizzyinthedesert.c, "desert");
        	filecontents += configFileContentsGen ("Electrocution", "electrocution");
        	filecontents += plugin.util.VerifyConfig(plugin.electrocution.c, "electro");
        	filecontents += configFileContentsGen ("Fisherman On Hook", "fishermanonhook");
        	filecontents += plugin.util.VerifyConfig(plugin.fishermanonhook.c, "fishing");
        	filecontents += configFileContentsGen ("Foodpoisoning", "foodpoisoning");
        	filecontents += plugin.util.VerifyConfig(plugin.foodpoisoning.c, "foodpoison");
        	filecontents += configFileContentsGen ("Heavy Duty", "heavyduty");
        	filecontents += plugin.util.VerifyConfig(plugin.heavyduty.c, "heavy");
        	filecontents += configFileContentsGen ("Near Death", "neardeath");
        	filecontents += plugin.util.VerifyConfig(plugin.neardeath.c, "neardeath");
        	filecontents += configFileContentsGen ("On Fire", "onfire");
        	filecontents += plugin.util.VerifyConfig(plugin.onfire.c, "fire");
        	filecontents += configFileContentsGen ("Quicksand", "quicksand");
        	filecontents += plugin.util.VerifyConfig(plugin.quicksand.c, "quicksand");
        	filecontents += configFileContentsGen ("Roses Have Thorns", "roseshavethorns");
        	filecontents += plugin.util.VerifyConfig(plugin.roseshavethorns.c, "roses");
        	filecontents += configFileContentsGen ("Row Your Boat", "rowyourboat");
        	filecontents += plugin.util.VerifyConfig(plugin.rowyourboat.c, "boat");
        	filecontents += configFileContentsGen ("Sneaky Pickup", "sneakypickup");
        	filecontents += plugin.util.VerifyConfig(plugin.sneakypickup.c, "sneaky");
        	filecontents += configFileContentsGen ("Squid Defense", "squiddefense");
        	filecontents += plugin.util.VerifyConfig(plugin.squiddefense.c, "squid");
        	filecontents += configFileContentsGen ("Sticky Tar", "stickytar");
        	filecontents += plugin.util.VerifyConfig(plugin.stickytar.c, "tar");
        	filecontents += configFileContentsGen ("Struck By Lightning", "struckbylightning");
        	filecontents += plugin.util.VerifyConfig(plugin.struckbylightning.c, "lightning");
        	filecontents += configFileContentsGen ("The Happy Miner", "thehappyminer");
        	filecontents += plugin.util.VerifyConfig(plugin.thehappyminer.c, "happyminer");
        	filecontents += configFileContentsGen ("Unstable TNT", "unstabletnt");
        	filecontents += plugin.util.VerifyConfig(plugin.unstabletnt.c, "tnt");
        	filecontents += configFileContentsGen ("Untamed Ride", "untamedride");
        	filecontents += plugin.util.VerifyConfig(plugin.untamedride.c, "ride");
           	filecontents += configFileContentsGen ("World Drop", "worlddrop");
        	filecontents += plugin.util.VerifyConfig(plugin.worlddrop.c, "worlddrop");
        	filecontents += configFileContentsGen ("Zombie Nation", "zombienation");
        	filecontents += plugin.util.VerifyConfig(plugin.zombienation.c, "zombie");
        	
        	try
        	{
        		  // Create file 
        		  FileWriter fstream = new FileWriter(configFile);
        		  BufferedWriter out = new BufferedWriter(fstream);
        		  out.write(filecontents);
        		  //Close the output stream
        		  out.close();
        		  
        		  config.load(configFile);
        		  plugin.reloadConfig();
        		  
        	} catch (Exception e){ //Catch exception if any
        		  Util.toLog("Failed to convert and update config.yml! Please delete it and restart server.",false);
        	}
        	
    	} else {
        	Util.toLog("Verifying config.yml",true);
	    	
        	filecontents = configFileContentsVerify(); 
        	
        	try
        	{
        		  // Create file 
        		  FileWriter fstream = new FileWriter(configFile);
        		  BufferedWriter out = new BufferedWriter(fstream);
        		  out.write(filecontents);
        		  //Close the output stream
        		  out.close();
        		  
        		  config.load(configFile);
        		  
        	} catch (Exception e){ //Catch exception if any
        		  Util.toLog("Failed to update config.yml! Please delete it and restart server.",false);
        	}
    	}
  
	}
    
	static String configFileContentsGen (String s1, String s2) {
    	String filecontents = null;
    	
    	filecontents  = "\n\n# " + s1;
    	filecontents += "\n" + s2 + ": ";
    	filecontents += "\n  active: "; 
    	if(Util.config.isConfigurationSection(s2) && Util.config(s2, null).isBoolean("active")) filecontents += Util.config(s2, null).getBoolean("active"); 
    	else filecontents += "true";
    	if(s2.equals("heavyduty")) {
        	filecontents += "\n  reset: "; 
        	if(Util.config.isConfigurationSection(s2) && Util.config(s2, null).isBoolean("reset")) filecontents += Util.config(s2, null).getBoolean("reset"); 
        	else filecontents += "true";    		
    	}
	
    	return filecontents;
	}
	
	static String configFileContentsVerify() {
		
		String filecontents = "";
		filecontents  = "# -------------------------------------------------";
    	filecontents += "\n# ------------| CONFIG FILE FOR ijmh |-------------";
    	filecontents += "\n# -------------------------------------------------";
    	filecontents += "\nplugin-version: " + plugin.getDescription().getVersion();
    	filecontents += "\n\n# Get notified about updates upon login?";
    	filecontents += "\nupdate_message: " + config.getBoolean("update_message");
    	if(config.isBoolean("debug")) filecontents += "\n\ndebug: " + config.getBoolean("debug");
    	filecontents += "\n# -------------------------------------------------";
    	filecontents += configFileContentsGen ("Bow Breaker", "bowbreaker");
    	filecontents += plugin.util.VerifyConfig(plugin.bowbreaker.c, "bowbreaker");
    	filecontents += configFileContentsGen ("Brew Explosion", "brewexplosion");
    	filecontents += plugin.util.VerifyConfig(plugin.brewexplosion.c, "brewexplosion");
    	filecontents += configFileContentsGen ("Buggy Block", "buggyblock");
    	filecontents += plugin.util.VerifyConfig(plugin.buggyblock.c, "buggyblock");
    	filecontents += configFileContentsGen ("Bump In The Rail", "bumpintherail");
    	filecontents += plugin.util.VerifyConfig(plugin.bumpintherail.c, "bumpintherail");
    	filecontents += configFileContentsGen ("Concussion", "concussion");
    	filecontents += plugin.util.VerifyConfig(plugin.concussion.c, "concussion");
    	filecontents += configFileContentsGen ("Cows Do Kick", "cowsdokick");
    	filecontents += plugin.util.VerifyConfig(plugin.cowsdokick.c, "cows");
    	filecontents += configFileContentsGen ("Crafting Thumb", "craftthumb");
    	filecontents += plugin.util.VerifyConfig(plugin.craftthumb.c, "craftthumb");
    	filecontents += configFileContentsGen ("Crazy Combat", "crazycombat");
    	filecontents += plugin.util.VerifyConfig(plugin.crazycombat.c, "crazycombat");
    	filecontents += configFileContentsGen ("Dizzy In The Desert", "dizzyinthedesert");
    	filecontents += plugin.util.VerifyConfig(plugin.dizzyinthedesert.c, "dizzyinthedesert");
    	filecontents += configFileContentsGen ("Electrocution", "electrocution");
    	filecontents += plugin.util.VerifyConfig(plugin.electrocution.c, "electrocution");
    	filecontents += configFileContentsGen ("Fisherman On Hook", "fishermanonhook");
    	filecontents += plugin.util.VerifyConfig(plugin.fishermanonhook.c, "fishermanonhook");
    	filecontents += configFileContentsGen ("Foodpoisoning", "foodpoisoning");
    	filecontents += plugin.util.VerifyConfig(plugin.foodpoisoning.c, "foodpoisoning");
    	filecontents += configFileContentsGen ("Heavy Duty", "heavyduty");
    	filecontents += plugin.util.VerifyConfig(plugin.heavyduty.c, "heavyduty");
    	filecontents += configFileContentsGen ("Near Death", "neardeath");
    	filecontents += plugin.util.VerifyConfig(plugin.neardeath.c, "neardeath");
    	filecontents += configFileContentsGen ("On Fire", "onfire");
    	filecontents += plugin.util.VerifyConfig(plugin.onfire.c, "onfire");
    	filecontents += configFileContentsGen ("Quicksand", "quicksand");
    	filecontents += plugin.util.VerifyConfig(plugin.quicksand.c, "quicksand");
    	filecontents += configFileContentsGen ("Roses Have Thorns", "roseshavethorns");
    	filecontents += plugin.util.VerifyConfig(plugin.roseshavethorns.c, "roseshavethorns");
    	filecontents += configFileContentsGen ("Row Your Boat", "rowyourboat");
    	filecontents += plugin.util.VerifyConfig(plugin.rowyourboat.c, "rowyourboat");
    	filecontents += configFileContentsGen ("Sneaky Pickup", "sneakypickup");
    	filecontents += plugin.util.VerifyConfig(plugin.sneakypickup.c, "sneakypickup");
    	filecontents += configFileContentsGen ("Squid Defense", "squiddefense");
    	filecontents += plugin.util.VerifyConfig(plugin.squiddefense.c, "squiddefense");
    	filecontents += configFileContentsGen ("Sticky Tar", "stickytar");
    	filecontents += plugin.util.VerifyConfig(plugin.stickytar.c, "stickytar");
    	filecontents += configFileContentsGen ("Struck By Lightning", "struckbylightning");
    	filecontents += plugin.util.VerifyConfig(plugin.struckbylightning.c, "struckbylightning");
    	filecontents += configFileContentsGen ("The Happy Miner", "thehappyminer");
    	filecontents += plugin.util.VerifyConfig(plugin.thehappyminer.c, "thehappyminer");
    	filecontents += configFileContentsGen ("Unstable TNT", "unstabletnt");
    	filecontents += plugin.util.VerifyConfig(plugin.unstabletnt.c, "unstabletnt");
    	filecontents += configFileContentsGen ("Untamed Ride", "untamedride");
    	filecontents += plugin.util.VerifyConfig(plugin.untamedride.c, "untamedride");
       	filecontents += configFileContentsGen ("World Drop", "worlddrop");
    	filecontents += plugin.util.VerifyConfig(plugin.worlddrop.c, "worlddrop");
    	filecontents += configFileContentsGen ("Zombie Nation", "zombienation");
    	filecontents += plugin.util.VerifyConfig(plugin.zombienation.c, "zombienation");
		return filecontents;
	}
    
	// HOOKS ------------------------------------------------------------------------------
	
	public static boolean WorldGuard(StateFlag flag, Location loc, Player p) {

		if(plugin.wg==null) return false;

		WorldGuardPlugin wg = plugin.wg;
		Vector pt = toVector(loc); // This also takes a location
		//LocalPlayer localPlayer = wg.wrapPlayer(p);
		 
		RegionManager regionManager = wg.getRegionManager(p.getWorld());
		ApplicableRegionSet set = regionManager.getApplicableRegions(pt);

		if(flag.equals(DefaultFlag.INVINCIBILITY)) {
			if(set.allows(flag)) return true;
		}
		
		return false;
	}
	
}