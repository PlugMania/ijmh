package info.plugmania.ijmh;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class Store {
	//public List<Player> player = new ArrayList<Player>();
	//public List<Integer> jumps = new ArrayList<Integer>();
	
	public HashMap<Player, Integer> quicksand = new HashMap<Player, Integer>();
	
	ijmh plugin;

	public Store(ijmh instance) {
		plugin = instance;
	}		
}

