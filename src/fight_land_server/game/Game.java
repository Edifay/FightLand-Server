package fight_land_server.game;

import java.net.DatagramPacket;
import java.util.ArrayList;

import fight_land_server.Player;
import fight_land_server.menuRequest.UDPRequest;

public class Game {

	private ArrayList<Player> players;

	public Game(ArrayList<Player> players) {
		this.players = players;
		for (int i = 0; i < this.players.size(); i++) {
			this.players.get(i).setGameRequestManager(new GameRequestManager(this.players.get(i)));
			new Thread(this.players.get(i).getGameRequestManager()).start();
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

		for (int i = 0; i < this.players.size(); i++) {
			this.players.get(i).getGameRequestManager().sendPlayers(this.players);
		}

		// set at players this game
		for (int i = 0; i < this.players.size(); i++) {
			this.players.get(i).setGame(this);
		}

		// Add listening UDP for players
		UDPRequest.areInGame.addAll(players);
	}

	public synchronized void setNewPostionPlayer(Player player, int x, int y, int animationState, DatagramPacket dataPacket) {
		System.out.println("Get player : "+player.getName());
		// set the packet sender
		if (player.getPacketForSend() == null) {
			player.setPacketForSend(dataPacket);
		}
		// send with UDP Location at all the game
		for (int i = 0; i < this.players.size(); i++) {
			if (this.players.get(i).getPacketForSend() != null /*&& this.players.get(i).getID() != player.getID()*/) {
				UDPRequest.sendLocationPlayer(this.players.get(i).getPacketForSend(), player.getID(), x, y, animationState);
			}
		}
	}
}
