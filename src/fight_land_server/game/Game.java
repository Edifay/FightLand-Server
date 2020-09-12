package fight_land_server.game;

import java.util.ArrayList;

import fight_land_server.Player;

public class Game {
	
	public Game(ArrayList<Player> players) {
		for(int i = 0; i < players.size(); i++) {
			new Thread(new GameRequestManager(players.get(i))).start();
		}
	}
}
