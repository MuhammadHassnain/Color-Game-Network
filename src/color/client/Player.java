package color.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import color.Card;
import color.Util;

public class Player {
	private String name;
	private Socket socket;
	private final int PORT = 7000; // port of server
	private PrintWriter out;
	private BufferedReader in;

	private List<Card> hand;
	private List<Card> openPile;

	public Player(String name) {
		this.name = name;
		try {
			socket = new Socket("127.0.0.1", PORT);
			out = new PrintWriter(socket.getOutputStream());
			in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		hand = new ArrayList<>();
		openPile = new ArrayList<>();
	}

	public void sendName() {
		out.println(this.name);
		out.flush();
	}

	public void play() {
		String read = new String();
		while (true) {
			try {
				read = in.readLine();
				if (read.startsWith("HAND")) {
					Integer handSize = Integer.parseInt(read.substring(5));
					for (int i = 0; i < handSize; ++i) {
						String sCard = in.readLine();
						hand.add(Util.toCard(sCard));
					}
				} else if (read.startsWith("DUMPCARD5")) {
					Set<Integer> indexOfCardTodump = new HashSet<>();
					List<Card> cardtodump = new ArrayList<>();
					while (indexOfCardTodump.size() < 5) {
						indexOfCardTodump.add(Util.getRandomInRange(0, hand.size()));
					}
					for (Integer i : indexOfCardTodump) {
						cardtodump.add(hand.get(i));
						out.println(i);
						out.flush();
					}
					hand.removeAll(cardtodump);

				} else if (read.startsWith("PILE")) {
					Integer pileSize = Integer.parseInt(read.substring(5));
					for (int i = 0; i < pileSize; ++i) {
						read = in.readLine();
						openPile.add(Util.toCard(read));
					}
					System.out.println(openPile.size());
				} else if (read.startsWith("REPLACECARD")) {
					Integer rand = Util.getRandomInRange(0, this.openPile.size());
					Card toReplace = hand.get(rand);
					hand.remove(toReplace);
			
					out.println(rand);
					out.flush();
					/*Card rep = Util.replaceCard(ret, this.openPile);
					hand.add(rep);
					*/
					Integer i = Util.getRandomInRange(0, openPile.size());
					Card withReplace = openPile.get(i);
					
					hand.add(withReplace);
					openPile.remove(withReplace);
					openPile.add(toReplace);
					
					out.println(i);
					out.flush();
				} else if (read.startsWith("SCORE")) {
					Integer score = Integer.parseInt(read.substring(6));
					System.out.println(score);
				} else if (read.startsWith("END")) {
					Integer score = Integer.parseInt(read.substring(4));
					if (score == 1) {
						System.out.println("Winner");
					} else {
						System.out.println("Loss the game");
					}
					break;
				}

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {
		Player player = new Player("Hasdssnain");
		player.sendName();
		player.play();
	}
}
