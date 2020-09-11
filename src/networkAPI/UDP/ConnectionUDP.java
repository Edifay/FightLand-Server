/**
 * Created_by @author : Arnaud ALET
 * Version : v.1
 * Creation_date : 08/08/2020
 */
package networkAPI.UDP;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import networkAPI.ConnectionType;
import networkAPI.Packet;
import networkAPI.TCP.ConnectionTCP;

public class ConnectionUDP implements ConnectionType {

	private DatagramSocket dataS;
	private DatagramPacket dataP;
	
	private Lock locker = new ReentrantLock();

	private int selfPort;
	private InetAddress ipSender;

	private int serverPort;

	private long communicationPosition;

	private ArrayList<DatagramPacket> clientFirstPacket;

	public ConnectionUDP(long communicationPosition, int selfPort) throws Exception {
		this.clientFirstPacket = new ArrayList<DatagramPacket>();
		this.communicationPosition = communicationPosition;
		this.selfPort = selfPort;
		try {
			this.dataS = new DatagramSocket(selfPort);
		} catch (SocketException e) {
			throw new Exception("ERROR in creation DatagramSocket !");
		}
	}

	public ConnectionUDP(long communicationPosition, InetAddress serverIp, int serverPort) throws Exception {
		this.communicationPosition = communicationPosition;
		try {
			this.dataS = new DatagramSocket();
		} catch (SocketException e) {
			throw new Exception("ERROR in creation DatagramSocket !");
		}
		this.ipSender = serverIp;
		this.serverPort = serverPort;
	}

	@Override
	public synchronized String ConnectionConnect() throws Exception {
		if (this.dataS.isConnected()) {
			throw new Exception("Can't Open Datagram is already open");
		} else {
			try {
				this.dataS = new DatagramSocket(this.selfPort);
			} catch (SocketException e) {
				e.printStackTrace();
			}
			return "DatagramSocket Open Succefuly";
		}
	}

	@Override
	public synchronized String ConnectionDisconnect() throws Exception {
		if (this.dataS.isConnected()) {
			this.dataS.close();
			return "DatagramSocket Closed";
		} else {
			throw new Exception("Can't close DatagramSocket : Already closed");
		}
	}
	
	public synchronized DatagramPacket read() {
		byte[] data = new byte[8196];
		this.dataP = new DatagramPacket(data, data.length);
		try {
			this.dataS.receive(this.dataP);
			return this.dataP;

		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public synchronized Packet ConnectionRead() {
		byte[] data = new byte[8196];
		this.dataP = new DatagramPacket(data, data.length);
		try {
			this.dataS.receive(this.dataP);
			if (this.clientFirstPacket != null) {
				Boolean find = false;
				for (int i = 0; i < this.clientFirstPacket.size(); i++) {
					if (this.clientFirstPacket.get(i).getAddress().equals(this.dataP.getAddress())
							&& this.clientFirstPacket.get(i).getPort() == this.dataP.getPort()) {
						find = true;
					}
				}
				if (!find) {
					this.clientFirstPacket.add(this.dataP);
				}
			}

			ByteArrayInputStream bis = new ByteArrayInputStream(this.dataP.getData());
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

	@Override
	public void ConnectionSend(Packet pack) {
		this.locker.lock();
		byte[] data;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			ObjectOutputStream out = new ObjectOutputStream(baos);
			out.writeObject(pack);
			out.flush();
			data = baos.toByteArray();
			if (data.length <= 8192) {
				if (this.ipSender != null) {
					this.dataP = new DatagramPacket(data, data.length, this.ipSender, this.serverPort);
					this.dataS.send(this.dataP);
				} else {
					throw new NullPointerException("IP is null");
				}
			} else {
				throw new Exception("DATA IS TOO BIG (data max : 8192)");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			this.locker.unlock();
		}
	}

	@Override
	public void ConnectionSend(DatagramPacket sender, Packet pack) {
		if (sender != null) {
			this.locker.lock();
			byte[] data;
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			try {
				ObjectOutputStream out = new ObjectOutputStream(baos);
				out.writeObject(pack);
				out.flush();
				data = baos.toByteArray();
				if (data.length <= 8192) {
					if (sender.getAddress() != null) {
						this.dataP = new DatagramPacket(data, data.length, sender.getAddress(), sender.getPort());
						this.dataS.send(this.dataP);
					} else {
						throw new NullPointerException("IP is null");
					}
				} else {
					throw new Exception("DATA IS TOO BIG (data max : 8192)");
				}
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				this.locker.unlock();
			}
		} else {
			this.ConnectionSend(pack);
		}
	}

	@Deprecated
	@Override
	public ConnectionTCP ConnectionServerGetClientTCP() {
		return null;
	}

	@Override
	public synchronized int ConnectionDestroy() {
		this.dataS.close();
		return 0;
	}

	@Override
	public Boolean isConnected() {
		return this.dataS.isConnected();
	}

	public int getPort() {
		return selfPort;
	}

	public void setPort(int port) {
		this.selfPort = port;
	}

	public InetAddress getIp() {
		return ipSender;
	}

	public void setIp(InetAddress ip) {
		this.ipSender = ip;
	}

	public long getCommunicationPosition() {
		return communicationPosition;
	}

	@Override
	public ArrayList<DatagramPacket> ConnectionServerGetClientsUDP() {
		return this.clientFirstPacket;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((clientFirstPacket == null) ? 0 : clientFirstPacket.hashCode());
		result = prime * result + (int) (communicationPosition ^ (communicationPosition >>> 32));
		result = prime * result + ((dataP == null) ? 0 : dataP.hashCode());
		result = prime * result + ((dataS == null) ? 0 : dataS.hashCode());
		result = prime * result + ((ipSender == null) ? 0 : ipSender.hashCode());
		result = prime * result + selfPort;
		result = prime * result + serverPort;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ConnectionUDP other = (ConnectionUDP) obj;
		if (clientFirstPacket == null) {
			if (other.clientFirstPacket != null)
				return false;
		} else if (!clientFirstPacket.equals(other.clientFirstPacket))
			return false;
		if (communicationPosition != other.communicationPosition)
			return false;
		if (dataP == null) {
			if (other.dataP != null)
				return false;
		} else if (!dataP.equals(other.dataP))
			return false;
		if (dataS == null) {
			if (other.dataS != null)
				return false;
		} else if (!dataS.equals(other.dataS))
			return false;
		if (ipSender == null) {
			if (other.ipSender != null)
				return false;
		} else if (!ipSender.equals(other.ipSender))
			return false;
		if (selfPort != other.selfPort)
			return false;
		if (serverPort != other.serverPort)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ConnectionUDP [dataS=" + dataS + ", dataP=" + dataP + ", selfPort=" + selfPort + ", ipSender="
				+ ipSender + ", serverPort=" + serverPort + ", communicationPosition=" + communicationPosition
				+ ", clientFirstPacket=" + clientFirstPacket + "]";
	}

}