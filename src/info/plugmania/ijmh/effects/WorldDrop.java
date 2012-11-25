package info.plugmania.ijmh.effects;

import java.util.HashMap;
import java.util.List;

import info.plugmania.ijmh.Util;
import info.plugmania.ijmh.ijmh;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class WorldDrop {

	ijmh plugin;
	public HashMap<Integer, HashMap<String, String>> c = new HashMap<Integer, HashMap<String, String>>();
	public Player player;
	public int falldelay = 0;
	public List<?> items;
	
	public WorldDrop(ijmh instance){
		plugin = instance;
	}
	
	public void init() {
		plugin.feature.put("WorldDrop", "worlddrop");
		c.put(0, plugin.util.cRow("skipworld", null, "list", null, null));
		c.put(1, plugin.util.cRow("chance", null, "integer", "10", "1-100"));
		c.put(2, plugin.util.cRow("chancemod", null, "integer", "1", "1-?"));
		c.put(3, plugin.util.cRow("radius", null, "integer", "30", "1-?"));
		c.put(4, plugin.util.cRow("abovesealvl", null, "integer", "20", "1-?"));
		c.put(5, plugin.util.cRow("maxlocs", null, "integer", "20", "1-?"));
		c.put(6, plugin.util.cRow("cooldown", null, "integer", "900", "1-? seconds"));
		c.put(7, plugin.util.cRow("amount", null, "integer", "10", "1-?"));
		c.put(8, plugin.util.cRow("items", null, "list", null, null));
	}	
	
	public boolean command(CommandSender sender, String[] args) {
		if(args.length==1) {
			plugin.util.cSend(c, args, sender);
		} else {
			Util.cmdExecute(sender, args);
		} 
		return true;
	}
	
	public void main() {
		
		if(Util.config("worlddrop",null).getBoolean("active")) {
			for (final World world : plugin.getServer().getWorlds()) {
				if(!Util.config("worlddrop",null).getList("skipworld").contains(world.getName())) {
					items = (List<?>) Util.config("worlddrop",null).getList("items");
					if(items.size()>0) {
						plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
							@Override
							public void run() {
								int droplocs = 0;
								for (Player p : plugin.getServer().getWorld(world.getName()).getPlayers()) {
									player = p;
									if(droplocs <= Util.config("worlddrop",null).getInt("maxlocs") && Util.pctChance(Util.config("worlddrop",null).getInt("chance"),Util.config("worlddrop",null).getInt("chancemod"))) {
										for(int drop=1;drop<=Util.config("worlddrop",null).getInt("amount");drop++) {
											plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() { 
												@Override
												public void run() {
													Material material = Material.matchMaterial(Util.shuffle(plugin.worlddrop.items).toString());
													if(material!=null) {
														double r = Util.config("worlddrop",null).getInt("radius");
														double x = player.getLocation().getX();
														double z = player.getLocation().getZ();
														double angle = Math.random() * 2 * Math.PI;
														r *= Math.random();
														x += r * Math.sin(angle);
														z += r * Math.cos(angle);
														double y = plugin.getServer().getWorld(world.getName()).getSeaLevel() + Util.config("worlddrop",null).getInt("abovesealvl");
														Location loc = new Location(world, x,y,z);
														
														plugin.getServer().getWorld(world.getName()).dropItem(loc, new ItemStack(material)); 
													}
												}
											},falldelay);
											falldelay += 2;
										}
										falldelay = 0;
										droplocs++;
									}
								}
							}
						}, Util.sec2tic(Util.config("worlddrop",null).getInt("cooldown")), Util.sec2tic(Util.config("worlddrop",null).getInt("cooldown")));
					}
				}
			}
		}
	}
}
