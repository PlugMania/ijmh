package info.plugmania.ijmh;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class Util{
	static ijmh plugin;
	
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
	
	public static float getLookAtYaw(Vector motion, boolean isPlayer) {
        double dx = motion.getX();
        double dz = motion.getZ();
        double yaw = 0;
        // Set yaw
        if (dx != 0) {
            // Set yaw start value based on dx
            if (dx < 0) {
                yaw = 1.5 * Math.PI;
            } else {
                yaw = 0.5 * Math.PI;
            }
            yaw -= Math.atan(dz / dx);
        } else if (dz < 0) {
            yaw = Math.PI;
        }
        int extract = 0;
        if(isPlayer==false) extract = 90;  
        return (float) (-yaw * 180 / Math.PI - extract);
    }
	
	public static int sec2tic(int seconds){
		int ticks = seconds * 20;
		return ticks;
	}
	
	public static boolean pctChance(double d){
		boolean result = false;
		int rNum = (int) (100*Math.random()); 
		if(rNum<=d) result = true;
		if(plugin.debug) plugin.getLogger().info(rNum + " <= " + d + " State:" + result);
		return result;
	}
}