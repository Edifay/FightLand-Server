/**
 * Created_by @author : Arnaud ALET
 * Version : v.1
 * Creation_date : 08/08/2020
 */
package networkAPI.TCP;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import networkAPI.ConnectionType;
import networkAPI.Packet;

public class ConnectionTCP implements ConnectionType {

	private Socket s;
	private Thread tConnectionAttemp;
	private Boolean connectionAttemp;
	private ObjectOutputStream out;
	private ObjectInputStream in;
	
	private Lock locker = new ReentrantLock();

	
	public Socket getS() {
		return s;
	}

	public void setS(Socket s) {
		this.s = s;
	}

	private ServerSocket ss;
	private Boolean acceptConnexion;
	private Thread tWaitConnexion;
	private ConnectionTCP client;

	private int port;
	private InetAddress ip;

	private ConnectionTCP(Socket s) {
		this.s = s;
		this.port = s.getLocalPort();
		this.ip = s.getInetAddress();
	}

	public ConnectionTCP(InetAddress ip, int port, Boolean connectionAttemp) {
		this.ip = ip;
		this.port = port;
		this.connectionAttemp = connectionAttemp;
		this.s = new Socket();
		if (connectionAttemp) {
			this.tConnectionAttemp = new Thread(new ConnectionAttemps(this));
			this.tConnectionAttemp.run();
		}
	}

	public ConnectionTCP(int port, Boolean acceptConnexion) {
		this.port = port;
		this.acceptConnexion = acceptConnexion;
		try {
			this.ss = new ServerSocket(port);
			System.out.println("Server Created on port : " + port + " connexions "
					+ (acceptConnexion ? "accepted" : "refused") + ".");
		} catch (IOException e) {
			System.out.println("0 : Server Creation ERROR !");
			System.exit(0);
		}
		if (acceptConnexion) {
			this.tWaitConnexion = new Thread(new WaitForConnection(this));
			this.tWaitConnexion.start();
		}
	}

	public ConnectionTCP(int port, int maxConnexion, InetAddress ip, Boolean acceptConnexion) {
		this.port = port;
		this.acceptConnexion = acceptConnexion;
		this.ip = ip;
		try {
			this.ss = new ServerSocket(port, maxConnexion, ip);
			System.out.println("Server Created on ip :" + ip + " and on port : " + port + " connexions "
					+ (acceptConnexion ? "accepted" : "refused") + ".");
		} catch (IOException e) {
			System.out.println("1 : Server Creation ERROR !");
			System.exit(0);
		}
		if (this.acceptConnexion) {
			this.tWaitConnexion = new Thread(new WaitForConnection(this));
			this.tWaitConnexion.start();
		}
	}

	@Override
	public synchronized String ConnectionConnect() {
		if (this.ss == null) {
			if (this.s.isClosed() && !this.tConnectionAttemp.isAlive()) {
				this.connectionAttemp = true;
				this.tConnectionAttemp = new Thread(new ConnectionAttemps(this));
				this.tConnectionAttemp.start();
			} else
				return "The Socket is already connect.";
		} else {
			if (this.ss.isClosed()) {

				this.acceptConnexion = true;
				this.tWaitConnexion = new Thread(new WaitForConnection(this));
				this.tWaitConnexion.start();
			} else
				return "The ServerSocket is already Connected.";
		}
		return null;
	}

	@Override
	public synchronized String ConnectionDisconnect() throws Exception {
		if (this.ss == null) {
			if (this.s != null) {
				if (!this.s.isClosed()) {
					try {
						this.connectionAttemp = false;
						this.s.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				} else {
					throw new Exception("The Socket is already closed.");
				}
			} else {
				throw new Exception("The Socket is already closed.");
			}
		} else {
			if (!this.ss.isClosed()) {
				this.acceptConnexion = false;
				if (this.tWaitConnexion.isAlive()) {
					this.tWaitConnexion.interrupt();
				}
				try {
					this.ss.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				throw new Exception("The ServerSocket is already closed.");
			}
		}
		return null;
	}

	@Override
	public void ConnectionSend(Packet pack) throws Exception {
		if (this.s.isConnected()) {
			this.locker.lock();
			if (this.out == null) {
				try {
					this.out = new ObjectOutputStream(this.s.getOutputStream());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			try {
				this.out.writeObject(pack);
				this.out.flush();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				this.locker.unlock();
			}
		} else {
			throw new Exception("Can't send Packet Socket is not Connected.");
		}
	}

	@Override
	public synchronized Packet ConnectionRead() {
		this.s.toString();
		if (this.s.isConnected()) {
			if (this.in == null) {
				try {
					this.in = new ObjectInputStream(this.s.getInputStream());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			try {
				return (Packet) this.in.readObject();
			} catch (ClassNotFoundException | IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	@Override
	public synchronized ConnectionTCP ConnectionServerGetClientTCP() {
		if (!this.ss.isClosed()) {
			ConnectionTCP stockageClient = this.client;
			this.client = null;
			return stockageClient;
		} else {
			return null;
		}
	}

	private class WaitForConnection implements Runnable {

		private ConnectionTCP co;

		public WaitForConnection(ConnectionTCP co) {
			this.co = co;
		}

		@Override
		public void run() {
			while (this.co.acceptConnexion) {
				try {
					this.co.client = new ConnectionTCP(this.co.ss.accept());
					System.out.println(
							"New Client connected on ip : " + this.co.client.ip + " port : " + this.co.client.port);
					Thread.sleep(10);
				} catch (IOException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public class ConnectionAttemps implements Runnable {

		private ConnectionTCP co;

		public ConnectionAttemps(ConnectionTCP co) {
			this.co = co;
		}

		@Override
		public void run() {
			while (this.co.connectionAttemp) {
				try {
					Thread.sleep(2000);
					this.co.s = new Socket(this.co.ip, this.co.port);
					break;
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (IOException e) {
				}
			}
		}
	}

	@Override
	public synchronized int ConnectionDestroy() {
		try {
			this.ConnectionDisconnect();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		if (this.s != null) {
			if (this.s.isConnected()) {
				try {
					this.s.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		if (this.ss != null) {
			if (!this.ss.isClosed()) {
				try {
					this.ss.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		try {
			if (this.out != null)
				this.out.close();
			if (this.in != null)
				this.in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return 0;
	}

	@Override
	public synchronized Boolean isConnected() throws Exception {
		if (this.s != null) {
			return this.s.isConnected();
		} else {
			throw new Exception("Socket is null.");
		}
	}

	@Deprecated
	@Override
	public ArrayList<DatagramPacket> ConnectionServerGetClientsUDP() {
		return null;
	}

	@Override
	public void ConnectionSend(DatagramPacket useless, Packet pack) {
		try {
			this.ConnectionSend(pack);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((acceptConnexion == null) ? 0 : acceptConnexion.hashCode());
		result = prime * result + ((client == null) ? 0 : client.hashCode());
		result = prime * result + ((connectionAttemp == null) ? 0 : connectionAttemp.hashCode());
		result = prime * result + ((in == null) ? 0 : in.hashCode());
		result = prime * result + ((ip == null) ? 0 : ip.hashCode());
		result = prime * result + ((out == null) ? 0 : out.hashCode());
		result = prime * result + port;
		result = prime * result + ((s == null) ? 0 : s.hashCode());
		result = prime * result + ((ss == null) ? 0 : ss.hashCode());
		result = prime * result + ((tConnectionAttemp == null) ? 0 : tConnectionAttemp.hashCode());
		result = prime * result + ((tWaitConnexion == null) ? 0 : tWaitConnexion.hashCode());
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
		ConnectionTCP other = (ConnectionTCP) obj;
		if (acceptConnexion == null) {
			if (other.acceptConnexion != null)
				return false;
		} else if (!acceptConnexion.equals(other.acceptConnexion))
			return false;
		if (client == null) {
			if (other.client != null)
				return false;
		} else if (!client.equals(other.client))
			return false;
		if (connectionAttemp == null) {
			if (other.connectionAttemp != null)
				return false;
		} else if (!connectionAttemp.equals(other.connectionAttemp))
			return false;
		if (in == null) {
			if (other.in != null)
				return false;
		} else if (!in.equals(other.in))
			return false;
		if (ip == null) {
			if (other.ip != null)
				return false;
		} else if (!ip.equals(other.ip))
			return false;
		if (out == null) {
			if (other.out != null)
				return false;
		} else if (!out.equals(other.out))
			return false;
		if (port != other.port)
			return false;
		if (s == null) {
			if (other.s != null)
				return false;
		} else if (!s.equals(other.s))
			return false;
		if (ss == null) {
			if (other.ss != null)
				return false;
		} else if (!ss.equals(other.ss))
			return false;
		if (tConnectionAttemp == null) {
			if (other.tConnectionAttemp != null)
				return false;
		} else if (!tConnectionAttemp.equals(other.tConnectionAttemp))
			return false;
		if (tWaitConnexion == null) {
			if (other.tWaitConnexion != null)
				return false;
		} else if (!tWaitConnexion.equals(other.tWaitConnexion))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ConnectionTCP [s=" + s + ", tConnectionAttemp=" + tConnectionAttemp + ", connectionAttemp="
				+ connectionAttemp + ", out=" + out + ", in=" + in + ", ss=" + ss + ", acceptConnexion="
				+ acceptConnexion + ", tWaitConnexion=" + tWaitConnexion + ", client=" + client + ", port=" + port
				+ ", ip=" + ip + "]";
	}

}