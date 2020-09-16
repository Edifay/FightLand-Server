package fight_land_server.game;

import java.util.ArrayList;

import fight_land_server.Player;

public class Game {

	private ArrayList<Player> players;

	public Game(ArrayList<Player> players) {
		this.players = players;
		for (int i = 0; i < players.size(); i++) {
			players.get(i).setGameRequestManager(new GameRequestManager(players.get(i)));
			new Thread(players.get(i).getGameRequestManager()).start();
		}
		ArrayList<Integer> nbAtLoad = new ArrayList<Integer>();
		for (int i = 0; i < this.players.size(); i++) {
			System.out.println("selected :" + this.players.get(i).getSelectedChamp());
			nbAtLoad.add(this.players.get(i).getSelectedChamp());
		}
		nbAtLoad.add(10);
		for (int i = 0; i < this.players.size(); i++) {
			this.players.get(i).getGameRequestManager().sendAtLoad(nbAtLoad);
		}
	}
}
