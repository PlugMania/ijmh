package info.plugmania.ijmh.effects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import info.plugmania.ijmh.Util;
import info.plugmania.ijmh.ijmh;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

public class UnstableTNT {

	ijmh plugin;
	public HashMap<Integer, HashMap<String, String>> c = new HashMap<Integer, HashMap<String, String>>();
	public List<Player> tnt = new ArrayList<Player>();
	
	public UnstableTNT(ijmh instance){
		plugin = instance;
	}
	
	public void init() {
		plugin.feature.put("UnstableTNT", "unstabletnt");
		c.put(0, plugin.util.cRow("skipworld", null, "list", null, null));
		c.put(1, plugin.util.cRow("chance", null, "integer", "1", "1-100"));
		c.put(2, plugin.util.cRow("chancemod", null, "integer", "10", "1-?"));
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
		
		if(Util.config("unstabletnt",null).getBoolean("active")){

			if(e.getEventName().equalsIgnoreCase("BlockPlaceEvent")) {
				BlockPlaceEvent event = (BlockPlaceEvent) e;
				Player player = (Player) event.getPlayer();
				Block block = event.getBlock();
				
				if(!Util.config("unstabletnt",null).getList("skipworld").contains(event.getBlock().getLocation().getWorld().getName())) {
					if(block.getType().equals(Material.TNT)) {
						if(!player.hasPermission("ijmh.immunity.tnt")) {
							if(Util.pctChance(Util.config("unstabletnt",null).getInt("chance"),Util.config("unstabletnt",null).getInt("chancemod"))) {
								block.setType(Material.AIR);
								TNTPrimed tnt = (TNTPrimed) block.getWorld().spawnEntity(block.getLocation(), EntityType.PRIMED_TNT);
								tnt.setFuseTicks(0);
								if(player.getGameMode().equals(GameMode.CREATIVE)) player.setGameMode(GameMode.SURVIVAL);
								plugin.unstabletnt.tnt.add(player);
								player.damage(1000);
							}
						}
					}
				}
			} 
			else if(e.getEventName().equalsIgnoreCase("PlayerDeathEvent")) {
				PlayerDeathEvent event = (PlayerDeathEvent) e;
				Player player = (Player) event.getEntity();
				
				if(plugin.unstabletnt.tnt.contains(player)) {
					plugin.unstabletnt.tnt.remove(player);
					event.setDeathMessage(player.getName() + " " + Util.language.getString("lan_30"));
				}
				
			}
		}
	}
}
