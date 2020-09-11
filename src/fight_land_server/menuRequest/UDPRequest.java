package fight_land_server.menuRequest;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;

import fight_land_server.main.Main;
import networkAPI.Packet;
import networkAPI.UDP.ConnectionUDP;

public class UDPRequest {

	public static void startManageUdp() {
		ConnectionUDP con = Main.server.getConnectionUDP();
		DatagramPacket dataPacket;
		Packet pack;
		while (true) {
			dataPacket = con.read();
			pack = transform(dataPacket);
			
			switch (pack.getPacketNumber()) {
			case 0: {
				con.ConnectionSend(dataPacket, pack);
				break;
			}
			default:
				throw new IllegalArgumentException("Unexpected value: " + pack.getPacketNumber());
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
}