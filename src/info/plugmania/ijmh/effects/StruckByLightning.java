package info.plugmania.ijmh.effects;

import info.plugmania.ijmh.Util;
import info.plugmania.ijmh.ijmh;

import java.util.Date;
import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerMoveEvent;

public class StruckByLightning {

	ijmh plugin;
	
	public long timer = 0;
	
	public StruckByLightning(ijmh instance){
		plugin = instance;
	}
	
	public void command(CommandSender sender, String[] args) {
		
		if(args.length==1) {
			HashMap<Integer, HashMap<String, String>> c = new HashMap<Integer, HashMap<String, String>>();
			c.put(0, plugin.util.cRow("skipworld", null, "list", null, null));
			c.put(1, plugin.util.cRow("skipbiome", null, "list", null, null));
			c.put(2, plugin.util.cRow("message", null, "boolean", "true", "true/false"));
			c.put(3, plugin.util.cRow("chance", null, "integer", "5", "1-100"));
			c.put(4, plugin.util.cRow("chancemod", null, "integer", "10", "1-?"));
			c.put(5, plugin.util.cRow("damage", null, "integer", "10", "1-?"));
			c.put(6, plugin.util.cRow("cooldown", null, "integer", "10", "1-? seconds"));
			plugin.util.cSend(c, args, sender);
		}	
	}
	
	public void main(Event e) {
		
		if(Util.config("lightning",null).getBoolean("active")){
			
			if(e.getEventName().equalsIgnoreCase("PlayerMoveEvent")) {
				PlayerMoveEvent event = (PlayerMoveEvent) e;
				Player player = event.getPlayer();
				Location to = event.getTo();
				Date curDate = new Date();
				long curTime = curDate.getTime();
				
				if(player.getWorld().hasStorm()){
					if(!player.hasPermission("ijmh.immunity.lightning")) {
						if(!player.getGameMode().equals(GameMode.CREATIVE) && !Util.config("fall",null).getList("skip_world").contains(player.getWorld().getName())) {
							if(
									!Util.config("lightning",null).getList("skip_biome").contains(player.getLocation().getBlock().getBiome().name()) &&
									(plugin.mazeMania==null || (plugin.mazeMania!=null && !plugin.mazeMania.arena.playing.contains(player))) &&
									curTime>timer
									) {
								int i = 0;
								boolean isHit = false;
								boolean doBreak = false;
								while(i++<=10 && !doBreak && !isHit){
									Material testBlock = player.getWorld().getBlockAt(to.getBlockX(), to.getBlockY()+i, to.getBlockZ()).getType();
									if(testBlock.equals(Material.LEAVES)) {
										isHit = true;
									} else if(!testBlock.equals(Material.AIR)) {
										doBreak = true;
										isHit = false;
									}
								}
							
								if(isHit==true && Util.pctChance(Util.config("lightning",null).getInt("chance"),Util.config("lightning",null).getInt("chancemod"))) {
									timer = curTime + (Util.config("lightning",null).getInt("cooldown") * 1000);
									player.getLocation().getWorld().strikeLightningEffect(player.getLocation());
									player.damage(Util.config("lightning",null).getInt("damage"));
									if(Util.config("lightning",null).getBoolean("message")) player.sendMessage(ChatColor.GOLD + Util.language.getString("lan_05"));
								}
							}
						}
					}	
				}
			}
		}
	}
}
