package info.plugmania.ijmh;

public class Util{
	static ijmh plugin;
	
	public Util(ijmh instance) {
		plugin = instance;
	}
	
	public static int sec2tic(int seconds){
		int ticks = seconds * 20;
		return ticks;
	}
	
	public static boolean pctChance(int pct){
		boolean result = false;
		int rNum = (int) (100*Math.random());
		if(rNum<=pct) result = true;
		return result;
	}
}