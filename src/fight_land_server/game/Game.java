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

		// Add all textures of all Players
		ArrayList<Integer> nbAtLoad = new ArrayList<Integer>();
		for (int i = 0; i < this.players.size(); i++) {
			System.out.println("selected :" + this.players.get(i).getSelectedChamp());
			nbAtLoad.add(this.players.get(i).getSelectedChamp());
		}

		// this is map = 10
		nbAtLoad.add(10);

		// send this load preset
		for (int i = 0; i < this.players.size(); i++) {
			this.players.get(i).getGameRequestManager().sendAtLoad(nbAtLoad);
		}

		// send all info players for set Graphics and let clients load all

		for (int i = 0; i < players.size(); i++) {
			this.players.get(i).getGameRequestManager().sendPlayers(this.players);
		}
	}
}
