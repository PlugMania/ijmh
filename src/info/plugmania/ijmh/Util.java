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
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

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
	
	public static HashMap<Material, Integer> protection = new HashMap<Material, Integer>();
	
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
	public void cSend(HashMap<Integer,HashMap<String,String>> c, String[] args, CommandSender sender) {
		
		String details = "";
		
		for (Iterator<Integer> i = c.keySet().iterator(); i.hasNext();) {
			Integer key = i.next();
			String message = "" + ChatColor.AQUA;
			if(c.get(key).get("sub") != null) message += c.get(key).get("sub") + " "; 
			if(c.get(key).get("name").contains("text")) {
				message += c.get(key).get("defaultvalue");
			}
			else if(c.get(key).get("name").contains("toggle")) {
				String state = ChatColor.RED + "disabled";
				if(Util.config(args[0], c.get(key).get("sub")).getBoolean("active")) state = ChatColor.GREEN + "enabled";
				message += "| " + c.get(key).get("name") + ": " + state;
			}
			else {
				message += "| " + c.get(key).get("name");
				if(c.get(key).get("defaultvalue") != null) message += " (" + c.get(key).get("defaultvalue") + ")";
				message += ": ";
				if(c.get(key).get("type").contains("list")) {
					if(Util.config(args[0],c.get(key).get("sub")).getList(c.get(key).get("name"))==null) message += "[]";
					else message += Util.config(args[0],c.get(key).get("sub")).getList(c.get(key).get("name"));
				}
				else if(c.get(key).get("type").contains("boolean")) message += Util.config(args[0],c.get(key).get("sub")).getBoolean(c.get(key).get("name"));
				else if(c.get(key).get("type").contains("integer")) message += Util.config(args[0],c.get(key).get("sub")).getInt(c.get(key).get("name"));
			}
			sender.sendMessage(message);
			if(c.get(key).get("range") != null) details += ChatColor.GOLD + c.get(key).get("name") + ChatColor.AQUA + " (" + c.get(key).get("range") + ") ";
		}
		sender.sendMessage(details);
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

	public Material isItem(String item){
		
		Material material = null;

		if(Material.matchMaterial(item) != null) material = Material.matchMaterial(item);
		
		return material;
	}

	public void armorval2type() {
		// LEATHER
		protection.put(Material.LEATHER_HELMET, 1);
		protection.put(Material.LEATHER_BOOTS, 1);
		protection.put(Material.LEATHER_CHESTPLATE, 3);
		protection.put(Material.LEATHER_LEGGINGS, 2);
		// GOLD
		protection.put(Material.GOLD_HELMET, 2);
		protection.put(Material.GOLD_BOOTS, 1);
		protection.put(Material.GOLD_CHESTPLATE, 5);
		protection.put(Material.GOLD_LEGGINGS, 3);
		// CHAINMAIL
		protection.put(Material.CHAINMAIL_HELMET, 2);
		protection.put(Material.CHAINMAIL_BOOTS, 1);
		protection.put(Material.CHAINMAIL_CHESTPLATE, 5);
		protection.put(Material.CHAINMAIL_LEGGINGS, 4);
		// IRON
		protection.put(Material.IRON_HELMET, 2);
		protection.put(Material.IRON_BOOTS, 2);
		protection.put(Material.IRON_CHESTPLATE, 6);
		protection.put(Material.IRON_LEGGINGS, 5);
		// DIAMOND
		protection.put(Material.DIAMOND_HELMET, 3);
		protection.put(Material.DIAMOND_BOOTS, 3);
		protection.put(Material.DIAMOND_CHESTPLATE, 8);
		protection.put(Material.DIAMOND_LEGGINGS, 6);	
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
		return curProt;
	}
	
	public EntityType isEntity(String entity){
		
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

	// YAML ------------------------------------------------------------------------------
	
    void firstRun() throws Exception {
        if(!languageFile.exists()){                        
        	languageFile.getParentFile().mkdirs();         
            copy(plugin.getResource("language.yml"), languageFile);
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
        	Util.toLog("Loading Languagefile",true);
        	language.load(languageFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void saveYamls() {
    	
        /* try {
        	language.save(languageFile);
        } catch (IOException e) {
            e.printStackTrace();
        } */
    }
	
    public void checkYamls() {

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
	    	key = "lan_01";	filecontents += "\n" + key + ": "; if(Util.language.getString(key)!=null) filecontents += Util.language.getString(key); else filecontents += "You caught fire, use a bucket of water or find water to put it out!" + Util.language.getString("lan_01");
	    	key = "lan_02";	filecontents += "\n" + key + ": "; if(Util.language.getString(key)!=null) filecontents += Util.language.getString(key); else filecontents += "You really need to be careful next time."; 
							filecontents += "\n\n# FOODPOISONING";
	    	key = "lan_03";	filecontents += "\n" + key + ": "; if(Util.language.getString(key)!=null) filecontents += Util.language.getString(key); else filecontents += "Your belly starts to rumble, that food must have been bad!? Milk Milk!!";
	    	key = "lan_04";	filecontents += "\n" + key + ": "; if(Util.language.getString(key)!=null) filecontents += Util.language.getString(key); else filecontents += "You feel better! You where lucky this time.";
	    					filecontents += "\n\n# STRUCK BY LIGHTNING";
	    	key = "lan_05";	filecontents += "\n" + key + ": "; if(Util.language.getString(key)!=null) filecontents += Util.language.getString(key); else filecontents += "Struck by lightning, didn''t your mom teach you not to hide under trees during a storm!?";
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
	    	key = "lan_20";	filecontents += "\n" + key + ": "; if(Util.language.getString(key)!=null) filecontents += Util.language.getString(key); else filecontents += "What! Something more on the hook, it''s a";
	    					filecontents += "\n\n# QUICK SAND";
	    	key = "lan_21";	filecontents += "\n" + key + ": "; if(Util.language.getString(key)!=null) filecontents += Util.language.getString(key); else filecontents += "Your feet suddenly dissapears in the sand... JUMP !";
	    	key = "lan_22";	filecontents += "\n" + key + ": "; if(Util.language.getString(key)!=null) filecontents += Util.language.getString(key); else filecontents += "Pew, that was close.";
	    	key = "lan_23";	filecontents += "\n" + key + ": "; if(Util.language.getString(key)!=null) filecontents += Util.language.getString(key); else filecontents += "was eaten by quicksand";
			filecontents += "\n\n# ROW YOUR BOAT";
			key = "lan_24";	filecontents += "\n" + key + ": "; if(Util.language.getString(key)!=null) filecontents += Util.language.getString(key); else filecontents += "The boat.. what.. you seem stuck! Break loose from the piece of the boat below you, break it and survive!";
			key = "lan_25";	filecontents += "\n" + key + ": "; if(Util.language.getString(key)!=null) filecontents += Util.language.getString(key); else filecontents += "You're free, hurry to the surface!";
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
			
	    	try{
	    		  // Create file 
	    		  FileWriter fstream = new FileWriter(languageFile);
	    		  BufferedWriter out = new BufferedWriter(fstream);
	    		  out.write(filecontents);
	    		  //Close the output stream
	    		  out.close();
	    		  
	    		  language.load(languageFile);
	    		  
	    		}catch (Exception e){//Catch exception if any
	    		  Util.toLog("Failed to update language.yml! Please delete it and restart server.",false);
	    		}
    	
    	}

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