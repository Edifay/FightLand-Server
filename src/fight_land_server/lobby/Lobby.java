package fight_land_server.lobby;

import java.util.ArrayList;

import fight_land_server.Player;
import fight_land_server.game.Game;

public class Lobby {

	private ArrayList<Player> players;

	private static Lobby actual_lobby;

	public Lobby() {
		this.players = new ArrayList<Player>();
	}

	public void addPlayer(Player player) {
		for (int i = 0; i < this.players.size(); i++) {
			this.players.get(i).getRequestManager().sendThisNewPlayer(player);
		}
		this.players.add(player);
		player.setLob(actual_lobby);
		player.setRequestManager(new LobbyRequest(player));
		new Thread(player.getRequestManager()).start();
	}

	public static Lobby getActualLobby() {
		if (actual_lobby == null) {
			actual_lobby = new Lobby();
		}
		return actual_lobby;
	}

	public void removePlayer(Player player) {
		this.players.remove(player);
		for (int i = 0; i < this.players.size(); i++) {
			this.players.get(i).getRequestManager().removeThisPlayer(player);
		}
	}

	public ArrayList<Player> getPlayers() {
		return this.players;
	}

	public int setReady(Player player) {
		player.setReadyToStart(true);
		System.out.println(player.getName() + " is Ready !");
		if (this.players.size() > 1) {
			for (int i = 0; i < this.players.size(); i++) {
				if (!this.players.get(i).getReadyToStart()) {
					return 0;// if not ready find
				}
			}
			System.out.println("start game");
			new Thread(() -> {
				this.startGame();
			}).start();
		}
		return -1;
	}

	public void setGraphicsPlayer(Player player, int graphics) {
		player.setSelectedChamp(graphics);
		for (int i = 0; i < this.players.size(); i++) {
			if (!player.equals(this.players.get(i))) {
				this.players.get(i).getRequestManager().setPlayerGraphics(player);
			}
		}
	}

	public void startGame() {
		actual_lobby = null;
		for (int i = 0; i < this.players.size(); i++) {
			this.players.get(i).getRequestManager().startPlayerLobby();
		}
		try {
			int time = 0;
			while (time != 5) {
				Thread.sleep(1000);
				time++;
				if (this.players.size() < 2) {
					Thread.currentThread().interrupt();
				}

			}
			for (int i = 0; i < this.players.size(); i++) {
				this.players.get(i).getRequestManager().sendStartGame();
			}
			for (int i = 0; i < this.players.size(); i++) {
				this.players.get(i).getRequestManager().stopThisRequestManager();
			}
			new Game(players);
		} catch (InterruptedException e) {
		}
		if (this.players.size() < 2) {
			this.players.get(0).getRequestManager().sendInterruptedLobby();
			this.players.get(0).setReadyToStart(false);
			getActualLobby().players.add(this.players.get(0));
			this.players.get(0).setLob(getActualLobby());
		}
	}
}
