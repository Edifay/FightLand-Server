package fight_land_server.main;

import networkAPI.Communication;

public class Main {

	public static Communication server;

	public static void main(String[] args) {

		server = new Communication();
		server.setCommunicationPostion(Communication.SERVER);
		server.setConnect(true);
		server.setConnectionType(Communication.TCP_AND_UDP);
		server.setPort(7630);
		server.create();
		
		NewConnexionManager.connexionGetter();
		
	}
}