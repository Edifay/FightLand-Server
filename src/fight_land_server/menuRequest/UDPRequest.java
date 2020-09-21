package fight_land_server.menuRequest;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.util.ArrayList;

import fight_land_server.Player;
import fight_land_server.main.Main;
import networkAPI.Packet;
import networkAPI.UDP.ConnectionUDP;

public class UDPRequest {

	public static ArrayList<Player> areInGame = new ArrayList<Player>();

	private static DatagramPacket dataPacket;

	public static ConnectionUDP con;

	public static void startManageUdp() {
		con = Main.server.getConnectionUDP();
		Packet pack;
		while (true) {
			dataPacket = con.read();
			pack = transform(dataPacket);

			switch (pack.getPacketNumber()) {
			case 0:
				con.ConnectionSend(dataPacket, pack);
				break;
			case 1:
				long ID = (long) readDataByteToObject(pack.getData().get(0));
				int x = (int) readDataByteToObject(pack.getData().get(1));
				int y = (int) readDataByteToObject(pack.getData().get(2));
				int animationState = (int) readDataByteToObject(pack.getData().get(3));
				for (int i = 0; i < areInGame.size(); i++) {
					if (areInGame.get(i).getID() == ID) {
						areInGame.get(i).getGame().setNewPostionPlayer(areInGame.get(i), x, y, animationState,
								dataPacket);
						System.out.println("Set Position For player : " + ID + " x: " + x + " y: " + y
								+ " animationState: " + animationState);
						break;
					}
				}
				break;
			default:
				System.out.println("ERROR Unknow response !");
			}

		}
	}

	public static Packet transform(DatagramPacket packetAtTransform) {
		ByteArrayInputStream bis = new ByteArrayInputStream(packetAtTransform.getData());
		try {
			ObjectInput in = new ObjectInputStream(bis);
			try {
				return (Packet) in.readObject();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
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

	public static Object readDataByteToObject(byte[] data) {
		ByteArrayInputStream in = new ByteArrayInputStream(data);
		try {
			ObjectInputStream inObj = new ObjectInputStream(in);
			return inObj.readObject();
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void sendLocationPlayer(DatagramPacket forSend, long ID, int x, int y, int animationState) {
		Packet pack = new Packet(1);
		pack.add(getDataByte(ID));
		pack.add(getDataByte(x));
		pack.add(getDataByte(y));
		pack.add(getDataByte(animationState));
		con.ConnectionSend(forSend, pack);
	}
}