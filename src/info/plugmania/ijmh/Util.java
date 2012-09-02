package info.plugmania.ijmh;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.plugin.Plugin;

import com.sk89q.worldedit.Vector;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;

import static com.sk89q.worldguard.bukkit.BukkitUtil.*;

public class Util{
	
	static ijmh plugin;
	public static File languageFile;
	public static FileConfiguration language;
	
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
	
	static public ConfigurationSection config(String s1, String s2){
		
		ConfigurationSection config = plugin.getConfig().getConfigurationSection(s1);
		if(s2!=null) config = config.getConfigurationSection(s2);
		
		return config;
	}
	
	static public void toLog(String s, boolean isDebug){
		
		if((isDebug && plugin.debug) || (!isDebug)) plugin.getLogger().info(s);
	}	
	
	static public boolean isBiome(String biome){
		boolean isBiome = false;
		
		List BiomeList = new LinkedList(); 
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

    public void loadYamls() {
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
    	if(!Util.language.getString("file-version").equals(plugin.getDescription().getVersion())) {
    		
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
	    	
	    	try{
	    		  // Create file 
	    		  FileWriter fstream = new FileWriter(languageFile);
	    		  BufferedWriter out = new BufferedWriter(fstream);
	    		  out.write(filecontents);
	    		  //Close the output stream
	    		  out.close();
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
		LocalPlayer localPlayer = wg.wrapPlayer(p);
		 
		RegionManager regionManager = wg.getRegionManager(p.getWorld());
		ApplicableRegionSet set = regionManager.getApplicableRegions(pt);

		if(flag.equals(DefaultFlag.INVINCIBILITY)) {
			if(set.allows(flag)) return true;
		}
		
		return false;
	}
	
}