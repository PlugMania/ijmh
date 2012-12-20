package info.plugmania.ijmh.effects;

import java.util.HashMap;

import info.plugmania.ijmh.Util;
import info.plugmania.ijmh.ijmh;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class BowBreaker {

	ijmh plugin;
	public HashMap<Integer, HashMap<String, String>> c = new HashMap<Integer, HashMap<String, String>>();
	
	public BowBreaker(ijmh instance){
		plugin = instance;
	}
	
	public void init() {
		plugin.feature.put("BowBreaker", "bowbreaker");
		c.put(0, plugin.util.cRow("skipworld", 	null, 	"list", 	null, 	null));
		c.put(1, plugin.util.cRow("message", 	null, 	"boolean", 	"true", "true/false/*"));
		c.put(2, plugin.util.cRow("chance", 	null, 	"integer", 	"10", 	"1-100"));
		c.put(3, plugin.util.cRow("chancemod", 	null, 	"integer",	"1", 	"1-?"));
		c.put(4, plugin.util.cRow("damage", 	null, 	"integer", 	"4", 	"1=½hearth"));
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
		
		if(Util.config("bowbreaker",null).getBoolean("active")){
			
			if(e.getEventName().equalsIgnoreCase("EntityDamageByEntityEvent")) {
				EntityDamageByEntityEvent event = (EntityDamageByEntityEvent) e;
				Player damager = null;
				
				if(event.getDamager() instanceof Arrow && event.getEntity() instanceof Player) {
					Arrow arrow = (Arrow) event.getDamager();
					if(arrow.getShooter() instanceof Player) {
						damager = (Player) arrow.getShooter();
						if(!damager.hasPermission("ijmh.immunity.bow")) {
							if(!Util.config("bowbreaker",null).getList("skipworld").contains(damager.getWorld().getName())) {
								if(Util.pctChance(Util.config("bowbreaker",null).getInt("chance"),Util.config("bowbreaker",null).getInt("chancemod"))) {
									ItemStack itemHand = (ItemStack) damager.getItemInHand();
									Inventory inv = damager.getInventory();
									inv.remove(itemHand);
									damager.damage(Util.config("bowbreaker",null).getInt("damage"));
									if(Util.config("bowbreaker",null).getBoolean("message")) damager.sendMessage(ChatColor.GOLD + Util.chatColorText(Util.language.getString("lan_17")));
								}
							}
						}
					}
				}	
			}
		}
	}
}
