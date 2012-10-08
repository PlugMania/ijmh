package info.plugmania.ijmh.effects;

import info.plugmania.ijmh.Util;
import info.plugmania.ijmh.ijmh;

import org.bukkit.ChatColor;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class BowBreaker {

	ijmh plugin;
	
	public BowBreaker(ijmh instance){
		plugin = instance;
	}
	
	public void main(Event e) {
		
		if(Util.config("bow",null).getBoolean("active")){
			
			if(e.getEventName().equalsIgnoreCase("EntityDamageByEntityEvent")) {
				EntityDamageByEntityEvent event = (EntityDamageByEntityEvent) e;
				Player damager = null;
				
				if(event.getDamager() instanceof Arrow && event.getEntity() instanceof Player) {
					Arrow arrow = (Arrow) event.getDamager();
					if(arrow.getShooter() instanceof Player) {
						damager = (Player) arrow.getShooter();
						if(!damager.hasPermission("ijmh.immunity.bow")) {
							if(!Util.config("bow",null).getList("skip_world").contains(damager.getWorld().getName())) {
								if(Util.pctChance(Util.config("bow",null).getInt("chance"),Util.config("bow",null).getInt("chancemod"))) {
									ItemStack itemHand = (ItemStack) damager.getItemInHand();
									Inventory inv = damager.getInventory();
									inv.remove(itemHand);
									damager.damage(Util.config("bow",null).getInt("damage"));
									if(Util.config("bow",null).getBoolean("message")) damager.sendMessage(ChatColor.GOLD + Util.language.getString("lan_17"));
								}
							}
						}
					}
				}	
			}
		}
	}
}
