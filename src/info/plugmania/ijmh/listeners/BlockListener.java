package info.plugmania.ijmh.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import info.plugmania.ijmh.ijmh;

public class BlockListener implements Listener {

	ijmh plugin;

	public BlockListener(ijmh instance) {
		plugin = instance;
	}
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {		
		plugin.unstabletnt.main(event); // UNSTABLE TNT
		plugin.buggyblock.main(event); // BUGGY BLOCK
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		plugin.rowyourboat.main(event); // ROW YOUR BOAT
		plugin.quicksand.main(event); // QUICKSAND
		plugin.thehappyminer.main(event); // THE HAPPY MINER
		plugin.unstabletnt.main(event); // UNSTABLE TNT
	}
}
