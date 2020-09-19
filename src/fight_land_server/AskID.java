package fight_land_server;

public class AskID {
	
	private static long actualID = 1000;
	
	public static long getAnID() {
		actualID++;
		return actualID;
	}

}
