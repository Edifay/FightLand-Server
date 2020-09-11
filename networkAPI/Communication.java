/**
 * Created_by @author : Arnaud ALET
 * Version : v.1
 * Creation_date : 08/08/2020
 */
package networkAPI;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

import networkAPI.TCP.ConnectionTCP;
import networkAPI.UDP.ConnectionUDP;

public class Communication {

	/**
	 * Parameters Variable
	 */
	public static long CLIENT = 000000000000000000100l;
	public static long SERVER = 000000000000000001000l;

	/**
	 * Parameters Variable
	 */
	public static long UDP = 000000000000000000001l;
	public static long TCP = 000000000000000000010l;
	public static long TCP_AND_UDP = 000000000000000000011l;

	private InetAddress ip;
	private int port;
	private Boolean connect;

	/**
	 * Choose the position of your Communication : - CLIENT = 000000000000000000100l
	 * - SERVER = 000000000000000001000l
	 */
	private long communicationPosition;

	/**
	 * Choose the type of protocol for the Connection : - UDP =
	 * 000000000000000000001l - TCP = 000000000000000000010l - TCP_AND_UDP =
	 * 000000000000000000011l
	 */
	private long connectionType;

	/**
	 * An communication can only have 2 connection : 1 TCP and 1 UDP
	 */
	private ConnectionTCP connectionTCP;
	private ConnectionUDP connectionUDP;
	
	

	public ConnectionTCP getConnectionTCP() {
		return connectionTCP;
	}

	public void setConnectionTCP(ConnectionTCP connectionTCP) {
		this.connectionTCP = connectionTCP;
	}

	public ConnectionUDP getConnectionUDP() {
		return connectionUDP;
	}

	public void setConnectionUDP(ConnectionUDP connectionUDP) {
		this.connectionUDP = connectionUDP;
	}

	private ArrayList<Communication> clients;

	/**
	 * Constructor for clients from server TCP
	 */
	private Communication(ConnectionTCP co) {
		this.connectionTCP = co;
	}

	/**
	 * Basical constructor
	 */
	public Communication() {
		this.clients = new ArrayList<Communication>();
		this.communicationPosition = CLIENT;
		this.connectionType = TCP;
		try {
			this.ip = InetAddress.getLocalHost();
		} catch (UnknownHostException e) {
		}
		this.port = 0;
		this.connect = false;
	}

	/**
	 * Force the creation of connection.
	 * 
	 * @param connectionType = Communication.TCP/UDP/TCP_AND_UDP
	 * @param communicationPosition = Communication.CLIENT/SERVER
	 */
	public void forceCreate(long connectionType, long communicationPosition) {
		if (communicationPosition == CLIENT) {
			if (connectionType == TCP) {
				clientTCPcreator();
			} else if (connectionType == UDP) {
				clientUDPcreator();
			} else if (connectionType == TCP_AND_UDP) {
				clientUDPcreator();
				clientTCPcreator();
			}
		} else if (communicationPosition == SERVER) {
			if (connectionType == TCP) {
				serverTCPcreator();
			} else if (connectionType == UDP) {
				serverUDPcreator();
			} else if (connectionType == TCP_AND_UDP) {
				serverTCPcreator();
				serverUDPcreator();
			}
		}
	}

	/**
	 * Create the connection requiere for this communication. Need to be execute
	 * after give parameters for use connection.
	 */
	public void create() {
		if (this.communicationPosition == CLIENT) {
			if (this.connectionType == TCP) {
				clientTCPcreator();
			} else if (this.connectionType == UDP) {
				clientUDPcreator();
			} else if (this.connectionType == TCP_AND_UDP) {
				clientUDPcreator();
				clientTCPcreator();
			}
		} else if (this.communicationPosition == SERVER) {
			if (this.connectionType == TCP) {
				serverTCPcreator();
			} else if (this.connectionType == UDP) {
				serverUDPcreator();
			} else if (this.connectionType == TCP_AND_UDP) {
				serverTCPcreator();
				serverUDPcreator();
			}
		}
	}

	/**
	 * Creation of UDP server. On port : this.port.
	 */
	private void serverUDPcreator() {
		try {
			this.connectionUDP = new ConnectionUDP(Communication.SERVER, this.port);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Creation of TCP server. On port : this.port. If this.connect is true looking
	 * for new client and add to list of clients on this server this.clients.
	 */
	private void serverTCPcreator() {
		this.connectionTCP = new ConnectionTCP(this.port, this.connect);// creation du server TCP
		if (this.connect) {
			new Thread(() -> {
				ConnectionTCP co;
				while (this.connect) {
					try {
						co = this.connectionTCP.ConnectionServerGetClientTCP();
						if (co != null) {
							if (co != null) {
								Communication add = new Communication(co);
								add.setCommunicationPostion(CLIENT);
								add.setConnect(false);
								add.setConnectionType(TCP);
								add.setPort(this.getPort());
								this.clients.add(add);
							}
						}
					} catch (Exception e1) {
						e1.printStackTrace();
					}
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}).start();
		}
	}

	/**
	 * Creation of UDP client. On InetAddress : this.ip; On port : this.port.
	 */
	private void clientUDPcreator() {
		System.out.println("Connection UDP ready.");
		try {
			this.connectionUDP = new ConnectionUDP(Communication.CLIENT, this.ip, this.port);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Creation of TCP client. On InetAddress : this.ip; On port : this.port. If
	 * this.connect is true the connection will wait for connect.
	 */
	private void clientTCPcreator() {// client TCP
		System.out.println("Connection TCP ready.");
		this.connectionTCP = new ConnectionTCP(this.ip, this.port, this.connect);// Creation du socket client
																					// tcp
	}

	public InetAddress getIp() {
		return ip;
	}

	public void setIp(InetAddress ip) {
		this.ip = ip;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public Boolean getConnect() {
		return connect;
	}

	public void setConnect(Boolean connect) {
		this.connect = connect;
	}

	public long getConnectionType() {
		return connectionType;
	}

	public void setConnectionType(long connectionType) {
		this.connectionType = connectionType;
	}

	/**
	 * Send a packet to the other part
	 * 
	 * TCP server are not enable for this.
	 * 
	 * @param sender:
	 * 	-TCP (client): null
	 * 	-UDP (server): need a packet get from the receiver : get all first packet receive from clients : (ArrayList type DatagramPacket) this.getUDPClient()
	 *  -UDP (client): null
	 *  
	 * @param connectionType:
	 *  -TCP (client): Communication.TCP
	 *  -UDP (client/server): Communication.UDP
	 *  
	 * @param pack:
	 * 	- all : your Packet
	 */
	public void writeNextPacket(DatagramPacket sender, long connectionType, Packet pack) {
		if (connectionType == TCP) {
			try {
				this.connectionTCP.ConnectionSend(pack);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (connectionType == UDP) {
			try {
				this.connectionUDP.ConnectionSend(sender, pack);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else {
			throw new IllegalArgumentException("Wrong connection Type");
		}
	}

	public long getCommunicationPostion() {
		return communicationPosition;
	}

	public void setCommunicationPostion(long communicationPostion) {
		this.communicationPosition = communicationPostion;
	}

	/**
	 * Wait for the next Packet can be read.
	 * 
	 * @param connectionType :
	 * 	-TCP : Communication.TCP
	 * 	-UDP : Communication.UDP
	 * 
	 * @return Packet with data
	 */
	public Packet nextPacket(long connectionType) {
		Packet pack = null;
		if (connectionType == TCP) {
			while (pack == null) {
				try {
					pack = this.connectionTCP.ConnectionRead();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

		} else if (connectionType == UDP) {
			try {
				pack = this.connectionUDP.ConnectionRead();
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			throw new IllegalArgumentException(
					"Argument Possible : " + Communication.SERVER + "/" + Communication.CLIENT);
		}
		return pack;
	}

	/**
	 * @return if TCP server has client.
	 */
	public Boolean hasClient() {
		return (this.clients.size() != 0);
	}

	/**
	 * @return the first client of the list and remove him from the list.
	 */
	public Communication getNextClient() {
		Communication stock = this.clients.get(0);
		this.clients.remove(0);
		return stock;
	}

	/**
	 * @return the list of all first packet that the server UDP get.
	 */
	public ArrayList<DatagramPacket> getUDPClient() {
		try {
			return this.connectionUDP.ConnectionServerGetClientsUDP();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Close all server and Connection from this Communication Close all clients are
	 * in this.clients
	 * 
	 * 
	 */
	public void destroy() {
		if (this.clients != null) {
			for (int i = 0; i < this.clients.size(); i++) {
				this.clients.get(i).destroy();
			}
		}

		this.connect = false;
		if (this.connectionType == TCP) {
			try {
				this.connectionTCP.ConnectionDestroy();
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (this.connectionType == UDP) {
			try {
				this.connectionUDP.ConnectionDestroy();
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (this.connectionType == TCP_AND_UDP) {
			try {
				this.connectionTCP.ConnectionDestroy();
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				this.connectionUDP.ConnectionDestroy();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((clients == null) ? 0 : clients.hashCode());
		result = prime * result + (int) (communicationPosition ^ (communicationPosition >>> 32));
		result = prime * result + ((connect == null) ? 0 : connect.hashCode());
		result = prime * result + ((connectionTCP == null) ? 0 : connectionTCP.hashCode());
		result = prime * result + (int) (connectionType ^ (connectionType >>> 32));
		result = prime * result + ((connectionUDP == null) ? 0 : connectionUDP.hashCode());
		result = prime * result + ((ip == null) ? 0 : ip.hashCode());
		result = prime * result + port;
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
		Communication other = (Communication) obj;
		if (clients == null) {
			if (other.clients != null)
				return false;
		} else if (!clients.equals(other.clients))
			return false;
		if (communicationPosition != other.communicationPosition)
			return false;
		if (connect == null) {
			if (other.connect != null)
				return false;
		} else if (!connect.equals(other.connect))
			return false;
		if (connectionTCP == null) {
			if (other.connectionTCP != null)
				return false;
		} else if (!connectionTCP.equals(other.connectionTCP))
			return false;
		if (connectionType != other.connectionType)
			return false;
		if (connectionUDP == null) {
			if (other.connectionUDP != null)
				return false;
		} else if (!connectionUDP.equals(other.connectionUDP))
			return false;
		if (ip == null) {
			if (other.ip != null)
				return false;
		} else if (!ip.equals(other.ip))
			return false;
		if (port != other.port)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Communication [ip=" + ip + ", port=" + port + ", connect=" + connect + ", communicationPostion="
				+ communicationPosition + ", connectionType=" + connectionType + ", connectionTCP=" + connectionTCP
				+ ", connectionUDP=" + connectionUDP + ", clients=" + clients + "]";
	}
}