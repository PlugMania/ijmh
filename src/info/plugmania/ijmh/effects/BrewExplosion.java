package info.plugmania.ijmh.effects;

import java.util.HashMap;

import org.bukkit.ChatColor;
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
	
	public BrewExplosion(ijmh instance){
		plugin = instance;
	}

	public void command(CommandSender sender, String[] args) {

			if(args.length==1) {
				HashMap<Integer, HashMap<String, String>> c = new HashMap<Integer, HashMap<String, String>>();
				c.put(0, plugin.util.cRow("skipworld", null, "list", null, null));
				c.put(1, plugin.util.cRow("signs", null, "boolean", "false", "true/false"));
				c.put(2, plugin.util.cRow("chance", null, "integer", "10", "1-100"));
				c.put(3, plugin.util.cRow("chancemod", null, "integer", "1", "1-?"));
				c.put(4, plugin.util.cRow("multiplier", null, "integer", "1", "1-5"));
				plugin.util.cSend(c, args, sender);
			}
	}
	
	public void main(Event e) {
		
		if(Util.config("brew",null).getBoolean("active")) {
			if(e.getEventName().equalsIgnoreCase("BrewEvent")) {
				BrewEvent event = (BrewEvent) e;		
			
				if(!Util.config("brew",null).getList("skip_world").contains(event.getBlock().getLocation().getWorld().getName())) {
					if(Util.pctChance(Util.config("brew",null).getInt("chance"),Util.config("brew",null).getInt("chancemod"))) {
						Block b = event.getBlock();
						b.getWorld().createExplosion(b.getLocation(), Util.config("brew",null).getInt("multiplier"));
				
						if(Util.config("brew",null).getBoolean("signs")) {
							if(b.getRelative(BlockFace.DOWN).getType().equals(Material.AIR)) b.getRelative(BlockFace.DOWN).setType(Material.GRASS);
		
							b.setTypeId(Material.SIGN_POST.getId());
		
							Sign sign = (Sign) b.getState();
							sign.setLine(0, "===============");
							sign.setLine(1, "BOOM");
							sign.setLine(2, "!");
							sign.setLine(3, "===============");
							sign.update();
						}
					}
				}
			}
		}
	}
}
