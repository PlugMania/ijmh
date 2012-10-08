package info.plugmania.ijmh.effects;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.event.Event;
import org.bukkit.event.inventory.BrewEvent;

import info.plugmania.ijmh.Util;
import info.plugmania.ijmh.ijmh;

public class BrewExplosion {

	ijmh plugin;
	
	public BrewExplosion(ijmh instance){
		plugin = instance;
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
