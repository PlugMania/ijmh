package info.plugmania.ijmh;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class Store {
	
	public HashMap<Player, Integer> quicksand = new HashMap<Player, Integer>();
	public HashMap<Player, Block> drowning = new HashMap<Player, Block>();
	
	ijmh plugin;

	public Store(ijmh instance) {
		plugin = instance;
	}		
}

