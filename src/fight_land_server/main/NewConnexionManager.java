package fight_land_server.main;

import static fight_land_server.main.Main.server;

import fight_land_server.menuRequest.MenuRequest;
import fight_land_server.menuRequest.UDPRequest;
import networkAPI.Communication;

public class NewConnexionManager {

	public static void connexionGetter() {

		new Thread(()->{
			UDPRequest.startManageUdp();
		}).start();
		System.out.println("------------------------------------------");
		
		while (true) {
			try {

				if (server.hasClient()) {
					System.out.println("New Client :");
					Communication client = server.getNextClient();
					System.out.println("	- Ip : "+client.getConnectionTCP().getS().getInetAddress());
					System.out.println("	- Port : "+client.getConnectionTCP().getS().getPort());
					System.out.println("------------------------------------------");
					new Thread(new MenuRequest(client)).start();
				}
				Thread.sleep(1);
			} catch (InterruptedException e) {
			}
		}
	}
}
