package fight_land_server;

import java.net.DatagramPacket;

import fight_land_server.game.Game;
import fight_land_server.game.GameRequestManager;
import fight_land_server.lobby.Lobby;
import fight_land_server.lobby.LobbyRequest;
import networkAPI.Communication;

public class Player {
	
	private String name;
	private Communication com;
	private Thread managerThread;
	private LobbyRequest requestManager;
	private GameRequestManager gameRequestManager;
	private Lobby lob;
	private int selectedChamp;
	private Boolean readyToStart;
	private long ID;
	private Game game;
	private DatagramPacket packetForSend;
	
	public Player(Communication com, long ID) {
		this.com = com;
		this.selectedChamp = -1;
		this.readyToStart = false;
		this.ID = ID;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Communication getCommunication() {
		return this.com;
	}

	public void setCommunication(Communication com) {
		this.com = com;
	}

	public Thread getManagerThread() {
		return this.managerThread;
	}

	public void setManagerThread(Thread managerThread) {
		this.managerThread = managerThread;
	}

	public Lobby getLob() {
		return this.lob;
	}

	public void setLob(Lobby lob) {
		this.lob = lob;
	}

	public int getSelectedChamp() {
		return this.selectedChamp;
	}

	public void setSelectedChamp(int selectedChamp) {
		this.selectedChamp = selectedChamp;
	}

	public LobbyRequest getRequestManager() {
		return this.requestManager;
	}

	public void setRequestManager(LobbyRequest requestManager) {
		this.requestManager = requestManager;
	}

	public Boolean getReadyToStart() {
		return this.readyToStart;
	}

	public void setReadyToStart(Boolean readyToStart) {
		this.readyToStart = readyToStart;
	}

	public GameRequestManager getGameRequestManager() {
		return this.gameRequestManager;
	}

	public void setGameRequestManager(GameRequestManager gameRequestManager) {
		this.gameRequestManager = gameRequestManager;
	}

	public long getID() {
		return this.ID;
	}

	public void setID(long iD) {
		this.ID = iD;
	}

	public Game getGame() {
		return this.game;
	}

	public void setGame(Game game) {
		this.game = game;
	}

	public DatagramPacket getPacketForSend() {
		return this.packetForSend;
	}

	public void setPacketForSend(DatagramPacket packetForSend) {
		this.packetForSend = packetForSend;
	}

}
