package fight_land_server.menuRequest;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

import fight_land_server.AskID;
import fight_land_server.Player;
import fight_land_server.lobby.Lobby;
import networkAPI.Communication;
import networkAPI.Packet;

public class MenuRequest implements Runnable {

	private Communication com;

	private Player player;

	public MenuRequest(Communication com) {
		this.com = com;
	}

	@Override
	public void run() {
		this.player = new Player(this.com, AskID.getAnID());
		this.manageTCP();
	}

	public void manageTCP() {

		Packet pack;
		while (this.com.getConnectionTCP().getS().isConnected() && this.player != null) {
			pack = this.com.nextPacket(Communication.TCP);

			if (pack == null)
				break;
			switch (pack.getPacketNumber()) {
			case 0: {// REQUEST FOR PING
				this.com.writeNextPacket(null, Communication.TCP, pack);
				break;
			}
			case 1: {// get Ping Client
				getPingClient(pack);
				break;
			}
			case 2: {// get Client Name
				this.player.setName(this.getName(pack));
				System.out.println(this.com.getConnectionTCP().getS() + " set the name: " + this.player.getName());
				System.out.println("------------------------------------------");
				break;
			}
			case 3: {// client join lobby
				System.out.println(this.player.getName() + " join Lobby !");
				System.out.println("------------------------------------------");
				Lobby.getActualLobby().addPlayer(this.player);
				this.player = null;
				break;
			}
			default:
				throw new IllegalArgumentException("Unexpected value: " + pack.getPacketNumber());
			}
		}
		if (this.player != null) {
			System.out.println("Disconected : Ip : " + this.com.getConnectionTCP().getS().getInetAddress() + ", port : "
					+ this.com.getConnectionTCP().getS().getPort());
			System.out.println("------------------------------------------");
		}
	}

	private void getPingClient(Packet pack) {
		try {
			ByteArrayInputStream bais = new ByteArrayInputStream(pack.getData().get(0));
			ObjectInputStream in = new ObjectInputStream(bais);
//					System.out.println(in.readLong());
			in.close();
			bais.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String getName(Packet pack) {
		ByteArrayInputStream in = new ByteArrayInputStream(pack.getData().get(0));
		ObjectInputStream inObj;
		try {
			inObj = new ObjectInputStream(in);

			String name = inObj.readUTF();

			in.close();
			inObj.close();

			return name;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "error";
	}
}