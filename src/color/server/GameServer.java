package color.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;


import color.Game;
import color.Player;

public class GameServer {
	private static final int PORT = 7000;  //PORT AT WHICH GAME SERVER LISTEN
	private ServerSocket welcomeSocket ; //welcome socket of server
	public static volatile ArrayList<Player> playerJoined;  //store joined player in list 
	private static final int  waitTime = 15; //main server wait for given sec for player joining  
	private Game game;
	public GameServer() {
		try {
			welcomeSocket = new ServerSocket(PORT);
			playerJoined = new ArrayList<>();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(-1);
		}
	}
	public void run() {
		try {
			joinPlayer joinplayer = new joinPlayer(welcomeSocket);
			Thread t = new Thread(joinplayer);
			t.start();
			Thread.sleep(1000*waitTime);
			joinplayer.isAlive = false;
			try {
				welcomeSocket.close();
			} catch (IOException e) {}
			
			if(playerJoined.size()<2) {
				throw new IllegalArgumentException("No of player is incorrect");
			}
			game= new Game(GameServer.playerJoined.size(), 1);
			game.InitiateGame(GameServer.playerJoined);
			game.setGameColor();
			game.distributeCard();
			game.play();
			game.announceWinner();
		} catch (InterruptedException e) {
				e.printStackTrace();
		}
	}
	public void close() {
		//close welcome socket
		if(welcomeSocket!=null) {
			try {
				welcomeSocket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		//close socket of player
		for (Player player : playerJoined) {
			if(player.getSocket()!=null) {
				try {
					player.getSocket().close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	public static void main(String[] args) {
		GameServer gameServer = new GameServer();
		gameServer.run();
		gameServer.close();
	}
	

}
