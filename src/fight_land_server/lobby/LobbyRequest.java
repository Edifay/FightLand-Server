package fight_land_server.lobby;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import fight_land_server.Player;
import networkAPI.Communication;
import networkAPI.Packet;

public class LobbyRequest implements Runnable {

	private Player player;

	public LobbyRequest(Player player) {
		this.player = player;
	}

	@Override
	public void run() {
		manageTCPLobby();
	}

	public void manageTCPLobby() {

		Packet pack;
		Communication com = this.player.getCommunication();

		for (int i = 0; i < this.player.getLob().getPlayers().size(); i++) {
			if (this.player.getLob().getPlayers().get(i) != this.player) {
				// write Player name and player graphics
				Packet packetPlayer = new Packet(2);
				packetPlayer.add(getDataByte(this.player.getLob().getPlayers().get(i).getName()));
				packetPlayer.add(getDataByte(this.player.getLob().getPlayers().get(i).getSelectedChamp()));
				com.writeNextPacket(null, Communication.TCP, packetPlayer);

			}
		}

		while (this.player != null && this.player.getCommunication().getConnectionTCP().getS().isConnected()) {
			pack = com.nextPacket(Communication.TCP);
			if (pack == null)
				break;
			switch (pack.getPacketNumber()) {

			case 0: {// ping response
				com.writeNextPacket(null, Communication.TCP, new Packet(0));
				break;
			}
			case 2 : {// client ready to play
				Lobby.getActualLobby().setReady(this.player);
				break;
			}
			case 3 : {// client chose a champ
				Lobby.getActualLobby().setGraphicsPlayer(this.player, (int) readDataByteToObject(pack.getData().get(0)));
				break;
			}

			default: {
				System.out.println("wrong");
				break;
			}
			}
		}
		if (this.player != null) {
			this.player.getLob().removePlayer(this.player);
			System.out.println("Disconected : Ip : " + com.getConnectionTCP().getS().getInetAddress() + ", port : "
					+ com.getConnectionTCP().getS().getPort());
			System.out.println("------------------------------------------");
		}else {
//			System.out.println("Change Connection Manager !");
			// start the next RequestManager
		}
	}

	public static byte[] getDataByte(Object obj) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ObjectOutputStream outobj;
		try {
			outobj = new ObjectOutputStream(out);
			outobj.writeObject(obj);
			outobj.flush();
			outobj.close();
			return out.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public Object readDataByteToObject(byte[] data) {
		ByteArrayInputStream in = new ByteArrayInputStream(data);
		try {
			ObjectInputStream inObj = new ObjectInputStream(in);
			return inObj.readObject();
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void sendThisNewPlayer(Player player) {
		Packet packetPlayer = new Packet(2);
		packetPlayer.add(getDataByte(player.getName()));
		packetPlayer.add(getDataByte(player.getSelectedChamp()));
		this.player.getCommunication().writeNextPacket(null, Communication.TCP, packetPlayer);
	}
	public void removeThisPlayer(Player player) {
		Packet packetPlayer = new Packet(3);
		packetPlayer.add(getDataByte(player.getName()));
		this.player.getCommunication().writeNextPacket(null, Communication.TCP, packetPlayer);
	}
	public void setPlayerGraphics(Player player) {
		Packet pack = new Packet(4);
		pack.add(getDataByte(player.getName()));
		pack.add(getDataByte(player.getSelectedChamp()));
		this.player.getCommunication().writeNextPacket(null, Communication.TCP, pack);
	}
	public void startPlayerLobby() {
		Packet pack = new Packet(1);
		this.player.getCommunication().writeNextPacket(null, Communication.TCP, pack);
	}
	public void sendInterruptedLobby() {
		Packet pack = new Packet(5);
		this.player.getCommunication().writeNextPacket(null, Communication.TCP, pack);
	}
	public void stopThisRequestManager() {
		this.player = null;
	}
	public void sendStartGame() {
		Packet pack = new Packet(6);
		this.player.getCommunication().writeNextPacket(null, Communication.TCP, pack);
	}
}
