package info.plugmania.ijmh;

import java.util.Random;

public class Util{
	static ijmh plugin;
	
	public Util(ijmh instance) {
		plugin = instance;
	}
	
	public static int sec2tic(int seconds){
		int ticks = seconds * 20;
		return ticks;
	}
	
	public boolean pctChance(int pct){
		boolean result = false;
		Random r = new Random();
		int rNum = r.nextInt(100);
		if(rNum<=pct) result = true;
		return result;
	}
}