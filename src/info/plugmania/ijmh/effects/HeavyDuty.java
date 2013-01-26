package info.plugmania.ijmh.effects;

import java.util.HashMap;

import info.plugmania.ijmh.Util;
import info.plugmania.ijmh.ijmh;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class HeavyDuty {

	ijmh plugin;
	public HashMap<Integer, HashMap<String, String>> c = new HashMap<Integer, HashMap<String, String>>();
	
	public HeavyDuty(ijmh instance){
		plugin = instance;
	}

	public void init() {
		plugin.feature.put("HeavyDuty", "heavyduty");
		
		// SKULL ITEMS
		Util.protectionDef.put(Material.SKULL_ITEM, 0);
		Util.protectionDef.put(Material.BONE, 0);
		// LEATHER
		Util.protectionDef.put(Material.LEATHER_HELMET, 5);
		Util.protectionDef.put(Material.LEATHER_BOOTS, 5);
		Util.protectionDef.put(Material.LEATHER_CHESTPLATE, 15);
		Util.protectionDef.put(Material.LEATHER_LEGGINGS, 10);
		// GOLD
		Util.protectionDef.put(Material.GOLD_HELMET, 10);
		Util.protectionDef.put(Material.GOLD_BOOTS, 5);
		Util.protectionDef.put(Material.GOLD_CHESTPLATE, 25);
		Util.protectionDef.put(Material.GOLD_LEGGINGS, 15);
		// CHAINMAIL
		Util.protectionDef.put(Material.CHAINMAIL_HELMET, 10);
		Util.protectionDef.put(Material.CHAINMAIL_BOOTS, 5);
		Util.protectionDef.put(Material.CHAINMAIL_CHESTPLATE, 25);
		Util.protectionDef.put(Material.CHAINMAIL_LEGGINGS, 20);
		// IRON
		Util.protectionDef.put(Material.IRON_HELMET, 10);
		Util.protectionDef.put(Material.IRON_BOOTS, 10);
		Util.protectionDef.put(Material.IRON_CHESTPLATE, 30);
		Util.protectionDef.put(Material.IRON_LEGGINGS, 25);
		// DIAMOND
		Util.protectionDef.put(Material.DIAMOND_HELMET, 15);
		Util.protectionDef.put(Material.DIAMOND_BOOTS, 15);
		Util.protectionDef.put(Material.DIAMOND_CHESTPLATE, 40);
		Util.protectionDef.put(Material.DIAMOND_LEGGINGS, 30);
		
		c.put(0, plugin.util.cRow("skipworld", null, "list", null, null));
		c.put(1, plugin.util.cRow("walkspeed", null, "double", "0.2", null));
		c.put(2, plugin.util.cRow("flyspeed", null, "double", "0.1", null));
		c.put(3, plugin.util.cRow("modifier", null, "double", "0.4", "0<=1"));
		c.put(4, plugin.util.cRow("armor", null, "list", null, "Item,1-100"));
		c.put(5, plugin.util.cRow("text", null, null, ChatColor.GREEN + "* There are limits to speed, for both client and server performance:", null));
		c.put(6, plugin.util.cRow("text", null, null, ChatColor.GREEN + "** 1 is moving very fast /  0 is not moving at all", null));
		c.put(7, plugin.util.cRow("text", null, null, ChatColor.GREEN + "** negative values make you move backwards", null));
		c.put(8, plugin.util.cRow("text", null, null, ChatColor.GREEN + "** negative values make you move backwards", null));
		c.put(9, plugin.util.cRow("text", null, null, ChatColor.GREEN + "* modifier must be larger than 0 and lesser or eaqual to 1.0 where max armor means no movement.", null));
		c.put(10, plugin.util.cRow("text", null, null, ChatColor.RED + "* You can reset speeds by turning the feature off and turning on reset in this feature", null));
		c.put(10, plugin.util.cRow("text", null, null, ChatColor.RED + "* You can change each items weight by editing its value in armor option, an armorset summed by all items the give 100 will result in the maximum reduction you have set.", null));
		
	}	
	
	public boolean command(CommandSender sender, String[] args) {
		if(args.length==1) {
			plugin.util.cSend(c, args, sender);
		} else {
			Util.cmdExecute(sender, args);
			if(!args[1].equalsIgnoreCase("reset") || (Util.config("heavyduty", null).getBoolean("reset") && !Util.config("heavyduty", null).getBoolean("active"))) {
				for(Player player : plugin.getServer().getOnlinePlayers()) {
					if(Util.config("heavyduty", null).getBoolean("reset") && !Util.config("heavyduty", null).getBoolean("active")) {
						player.setWalkSpeed((float) 0.2);
						player.setFlySpeed((float) 0.1);
						Util.toLog("Walkspeed is " + player.getWalkSpeed() + ", Flyspeed is " + player.getFlySpeed(), true);
					} else {
						this.extend(player, player.getGameMode().name());
					}
				}
				sender.sendMessage(ChatColor.AQUA + "All online players movement have been updated. Offline players will be updated when joining server.");
			}
		} 
		return true;
	}
	
	public void main(Event e) {
		if(Util.config("heavyduty",null).getBoolean("active")){
			if(e.getEventName().equalsIgnoreCase("PlayerJoinEvent")) {
				PlayerJoinEvent event = (PlayerJoinEvent) e;
				Player player = (Player) event.getPlayer();
					
				this.extend(player, null);
			}
			else if(e.getEventName().equalsIgnoreCase("InventoryCloseEvent")) {
				InventoryCloseEvent event = (InventoryCloseEvent) e;
				Player player = (Player) event.getPlayer();
					
				this.extend(player, null);
			}
			else if(e.getEventName().equalsIgnoreCase("PlayerGameModeChangeEvent")) {
				PlayerGameModeChangeEvent event = (PlayerGameModeChangeEvent) e;
					Player player = (Player) event.getPlayer();
					this.extend(player, event.getNewGameMode().name());
			}
		} else if(Util.config("heavyduty",null).getBoolean("reset")==true) {
			if(e.getEventName().equalsIgnoreCase("PlayerJoinEvent")) {
				PlayerJoinEvent event = (PlayerJoinEvent) e;
				Player player = (Player) event.getPlayer();
				
				player.setWalkSpeed((float) 0.2);
				player.setFlySpeed((float) 0.1);
				Util.toLog("Speeds have been reset for " + player.getName(), true);
			}
		}
	}
	
	public void extend(Player player, String newgamemode) {
		
		double WalkSpeed =  Util.config("heavyduty",null).getDouble("walkspeed");
		double FlySpeed = Util.config("heavyduty",null).getDouble("flyspeed");
		
		if(newgamemode=="CREATIVE" || (player.getGameMode().equals(GameMode.CREATIVE) && newgamemode==null)) {
			player.setWalkSpeed((float) WalkSpeed);
			player.setFlySpeed((float) FlySpeed);
			Util.toLog("Walkspeed is " + player.getWalkSpeed() + ", Flyspeed is " + player.getFlySpeed(), true);
		} else { 	
			if(!Util.config("heavyduty",null).getList("skipworld").contains(player.getWorld().getName())) {
				if(!player.hasPermission("ijmh.immunity.heavy")) {
			
					double curProt = Util.getPlayerArmorValue(player)*Util.config("heavyduty",null).getDouble("modifier");

					if(curProt>0) {
						player.setWalkSpeed((float) (WalkSpeed-((WalkSpeed*curProt)/20)));					
						player.setFlySpeed((float) (FlySpeed-((FlySpeed*curProt)/20)));
						Util.toLog("Current Protection is: " + curProt + " Walkspeed is " + player.getWalkSpeed() + ", Flyspeed is " + player.getFlySpeed(), true);
					} 
					else {
						player.setWalkSpeed((float) WalkSpeed);
						player.setFlySpeed((float) FlySpeed);
						Util.toLog("Current Protection is: " + curProt + " Walkspeed is " + player.getWalkSpeed() + ", Flyspeed is " + player.getFlySpeed(), true);
					}
				}
			}
		}
	} 
}
