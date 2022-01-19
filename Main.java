package Server;

import Server.ServeurFTP;

public class Main {

	public static void main(String[] args) {
		ServeurFTP server = new ServeurFTP(1080);
		server.listen();

	}

}
