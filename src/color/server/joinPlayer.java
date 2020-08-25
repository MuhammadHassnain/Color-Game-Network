package color.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import color.Player;

public class joinPlayer implements Runnable {
	ServerSocket socket;
	public volatile boolean isAlive = true;

	public joinPlayer(ServerSocket socket) {
		this.socket = socket;
	}

	@Override
	public void run() {
		do {
			try {
				Socket ss = this.socket.accept();

				BufferedReader reader = new BufferedReader(new InputStreamReader(ss.getInputStream()));
				PrintWriter writer = new PrintWriter(ss.getOutputStream());
				String name = reader.readLine();

				System.out.println(name + "---Joined");
				Player player = new Player(name);
				GameServer.playerJoined.add(player);
				player.setSocket(ss, reader, writer);
				; // socket at which player listen
				ss = null;

			} catch (IOException e) {
				// TODO Auto-generated catch block
			//	e.printStackTrace();
			}

		} while ((GameServer.playerJoined.size() < 4 && isAlive));
	}

}
