package info.plugmania.ijmh.effects;

import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Event;
import org.bukkit.event.inventory.BrewEvent;

import info.plugmania.ijmh.Util;
import info.plugmania.ijmh.ijmh;

public class BrewExplosion {

	ijmh plugin;
	public HashMap<Integer, HashMap<String, String>> c = new HashMap<Integer, HashMap<String, String>>();
	public Block b;
	
	public BrewExplosion(ijmh instance){
		plugin = instance;
	}

	public void init() {
		plugin.feature.put("BrewExplosion", "brewexplosion");
		c.put(0, plugin.util.cRow("skipworld", null, "list", null, null));
		c.put(1, plugin.util.cRow("signs", null, "boolean", "true", "true/false/*"));
		c.put(2, plugin.util.cRow("chance", null, "integer", "10", "1-100"));
		c.put(3, plugin.util.cRow("chancemod", null, "integer", "1", "1-?"));
		c.put(4, plugin.util.cRow("multiplier", null, "integer", "1", "1-5"));
	}	
	
	public boolean command(CommandSender sender, String[] args) {
		if(args.length==1) {
			plugin.util.cSend(c, args, sender);
		} else {
			Util.cmdExecute(sender, args);
		} 
		return true;
	}
	
	public void main(Event e) {
		
		if(Util.config("brewexplosion",null).getBoolean("active")) {
			if(e.getEventName().equalsIgnoreCase("BrewEvent")) {
				BrewEvent event = (BrewEvent) e;		
			
				if(!Util.config("brewexplosion",null).getList("skipworld").contains(event.getBlock().getLocation().getWorld().getName())) {
					if(Util.pctChance(Util.config("brewexplosion",null).getInt("chance"),Util.config("brewexplosion",null).getInt("chancemod"))) {
						b = event.getBlock();
						b.getWorld().createExplosion(b.getLocation(), Util.config("brewexplosion",null).getInt("multiplier"));
				
						if(Util.config("brewexplosion",null).getBoolean("signs")) {
							if(b.getRelative(BlockFace.DOWN).getType().equals(Material.AIR)) b.getRelative(BlockFace.DOWN).setType(Material.GRASS);  
   
							plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
							    @Override 
							    public void run() {
									b.setType(Material.SIGN_POST);
										  
									Sign sign = (Sign) b.getState();
									sign.setLine(0, "[ijmh]");
									sign.setLine(2, "§4BOOM");
									sign.setLine(3, "§4!");
									sign.update();
							    }
							}, 10L);
							
						}
					}
				}	
			} 
		}
	}
}
