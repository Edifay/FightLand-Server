package fight_land_server.game;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import fight_land_server.Player;
import networkAPI.Communication;
import networkAPI.Packet;

public class GameRequestManager implements Runnable {

	private Player player;

	public GameRequestManager(Player player) {
		this.player = player;
	}

	@Override
	public void run() {
		this.manageTCPGame();
	}

	public void manageTCPGame() {

		Packet pack;
		Communication com = this.player.getCommunication();

		while (this.player != null && this.player.getCommunication().getConnectionTCP().getS().isConnected()) {
			pack = com.nextPacket(Communication.TCP);
			if (pack == null)
				break;
			switch (pack.getPacketNumber()) {

			case 0: // ping response
				com.writeNextPacket(null, Communication.TCP, new Packet(0));
				break;
				
			default:
				System.out.println("ERROR Unknow Response !");
				break;
			}
		}
		if (this.player != null) {
			this.player.getLob().removePlayer(this.player);
			System.out.println("Disconected : Ip : " + com.getConnectionTCP().getS().getInetAddress() + ", port : "
					+ com.getConnectionTCP().getS().getPort());
			System.out.println("------------------------------------------");
		} else {
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

	public void sendAtLoad(ArrayList<Integer> numberTextureLoad) {
		Packet pack = new Packet(1);
		pack.add(getDataByte(numberTextureLoad));
		this.player.getCommunication().writeNextPacket(null, Communication.TCP, pack);
	}

	public void sendPlayers(ArrayList<Player> players) {
		Packet pack = new Packet(2);
		for (int i = 0; i < players.size(); i++) {
			ArrayList<Object> playersInfo = new ArrayList<Object>();
			playersInfo.add((Long)players.get(i).getID());
			playersInfo.add(players.get(i) == this.player ? (Boolean)true : (Boolean)false);
			playersInfo.add((Integer)players.get(i).getSelectedChamp());
			playersInfo.add((String)players.get(i).getName());
			pack.add(getDataByte(playersInfo));
		}
		this.player.getCommunication().writeNextPacket(null, Communication.TCP, pack);
	}
}
