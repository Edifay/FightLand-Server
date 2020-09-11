/**
 * Created_by @author : Arnaud ALET
 * Version : v.1
 * Creation_date : 08/08/2020
 */
package networkAPI;

import java.io.Serializable;
import java.util.ArrayList;

public class Packet implements Serializable {

	private static final long serialVersionUID = -8265088070820851410L;

	private int packetNumber;
	private ArrayList<byte[]> dataList;

	public Packet(int packetNumber) {
		this.dataList = new ArrayList<byte[]>();
		this.packetNumber = packetNumber;
	}

	public void add(byte[] data) {
		this.dataList.add(data);
	}

	public int getPacketNumber() {
		return this.packetNumber;
	}

	public void addAll(ArrayList<byte[]> data) {
		this.dataList.addAll(data);
	}

	public ArrayList<byte[]> getData() {
		return this.dataList;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((dataList == null) ? 0 : dataList.hashCode());
		result = prime * result + packetNumber;
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
		Packet other = (Packet) obj;
		if (dataList == null) {
			if (other.dataList != null)
				return false;
		} else if (!dataList.equals(other.dataList))
			return false;
		if (packetNumber != other.packetNumber)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Packet [packetNumber=" + packetNumber + ", dataList=" + dataList + "]";
	}

}