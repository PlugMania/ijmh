package info.plugmania.ijmh;

import java.util.LinkedList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Scheduler {
	
	ijmh plugin;

	public Scheduler(ijmh instance) {
		plugin = instance;
	}
	
	public Player player;
	public int falldelay = 0;
	public List items;
	
	public void worldevents() {
		
		for (final World world : plugin.getServer().getWorlds()) {
			// WORLDDROP
			if(Util.config("worlddrop",null).getBoolean("active") && !Util.config("worlddrop",null).getList("skip_world").contains(world.getName())) {
				items = Util.config("worlddrop",null).getList("items");
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
												Material material = Material.matchMaterial(Util.shuffle(plugin.scheduler.items).toString());
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
