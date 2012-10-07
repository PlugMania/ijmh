package info.plugmania.ijmh.effects;

import info.plugmania.ijmh.Util;
import info.plugmania.ijmh.ijmh;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class HeavyDuty {

	ijmh plugin;
	
	public HeavyDuty(ijmh instance){
		plugin = instance;
	}

	public void main(Event e) {
		
		Util.toLog("EventName: " + e.getEventName(), true); // DEBUG
		
		if(Util.config("heavy",null).getBoolean("active")){
			if(e.getEventName().equalsIgnoreCase("PlayerJoinEvent")) {
				PlayerJoinEvent event = (PlayerJoinEvent) e;
				Player player = (Player) event.getPlayer();
					
				this.extend(player);
			}
			else if(e.getEventName().equalsIgnoreCase("InventoryCloseEvent")) {
				InventoryCloseEvent event = (InventoryCloseEvent) e;
				Player player = (Player) event.getPlayer();
					
				this.extend(player);
			}
			else if(e.getEventName().equalsIgnoreCase("PlayerGameModeChangeEvent")) {
				PlayerGameModeChangeEvent event = (PlayerGameModeChangeEvent) e;
					Player player = (Player) event.getPlayer();
						
					this.extend(player);
			}
		}
	}
	
	public void extend(Player player) {
		
		if(!player.getGameMode().equals(GameMode.CREATIVE)) {
			if(!Util.config("heavy",null).getList("skip_world").contains(player.getWorld().getName())) {
				if(!player.hasPermission("ijmh.immunity.heavy")) {
											
					double WalkSpeed =  Util.config("heavy",null).getDouble("walkspeed");
					double FlySpeed = Util.config("heavy",null).getDouble("flyspeed");
			
					double curProt = Util.getPlayerArmorValue(player)*Util.config("heavy",null).getDouble("modifier");
			
					if(curProt>0 && !player.getGameMode().equals(GameMode.CREATIVE)) {
						player.setWalkSpeed((float) (WalkSpeed-((WalkSpeed*curProt)/20)));					
						player.setFlySpeed((float) (FlySpeed-((FlySpeed*curProt)/20)));
						Util.toLog("Current Protection is: " + curProt + " Walkspeed is " + player.getWalkSpeed() + ", Flyspeed is " + player.getFlySpeed(), true);
					} 
					else {
						player.setWalkSpeed((float) WalkSpeed);
						player.setFlySpeed((float) FlySpeed);
					}
				}
			}
		}
	}
}
