/**
 * Created_by @author : Arnaud ALET
 * Version : v.1
 * Creation_date : 08/08/2020
 */
package networkAPI;

import java.net.DatagramPacket;
import java.util.ArrayList;

import networkAPI.TCP.ConnectionTCP;

public interface ConnectionType {

	abstract String ConnectionConnect() throws Exception;

	abstract String ConnectionDisconnect() throws Exception;

	abstract void ConnectionSend(Packet pack) throws Exception;

	abstract void ConnectionSend(DatagramPacket sender, Packet pack) throws Exception;

	abstract Packet ConnectionRead() throws Exception;

	abstract ConnectionTCP ConnectionServerGetClientTCP() throws Exception;

	abstract ArrayList<DatagramPacket> ConnectionServerGetClientsUDP() throws Exception;

	abstract Boolean isConnected() throws Exception;

	abstract int ConnectionDestroy() throws Exception;
	
}
