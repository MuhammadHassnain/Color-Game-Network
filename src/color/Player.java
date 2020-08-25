package color;

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

public class Player implements Runnable {
	private final Object replaceLock = new Object();
	private boolean myturn = false;
	private String name;
	private List<Card> hand;
	private Game game; /// game player is playing
	private int score;

	private Socket socket;// socket at which player will listen

	///
	private PrintWriter out;
	private BufferedReader in;


	public Socket getSocket() {
		return socket;
	}

	private void sharePile() {
		// send msg to client that a pile will be sent
		out.println("PILE " + Game.openPile.size());
		out.flush();
		// wait for client so
		for (Card card : Game.openPile) {
			out.println(card.toString());
			out.flush();
		}
	}

	private void shareHand() {
		out.println("HAND " + this.hand.size());
		out.flush();

		for (Card card : hand) {
			out.println(card.toString());
			out.flush();
		}
	}

	public void setSocket(Socket socket, BufferedReader reader, PrintWriter writer) {
		this.socket = socket;
		in = reader;
		out = writer;

	}

	public int getScore() {
		return score;
	}

	public void calculateScore() {
		for (int i = 0; i < hand.size(); ++i) {
			if (hand.get(i).getSuit() == Game.colorOfGame) {
				score += hand.get(i).getValue().value;
			}
			score += hand.get(i).getValue().value;
		}
		out.println("SCORE " + this.score);
		out.flush();
	}

	public Player(String name) {
		setName(name);
		hand = new ArrayList<>();
	}

	public void setGame(Game game) {
		this.game = game;
	}

	public Game getGame() {
		return game;
	}

	public String getName() {
		return name;

	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 
	 * @param card recive a card and puts it in hand
	 * @return if card is jack the return true otherwise false
	 */

	public boolean receiveCard(Card card) {
		if (hand == null)
			hand = new ArrayList<>();
		hand.add(card);
		if (card.getValue() == Value.Jack) {
			return true;
		}
		return false;
	}

	/**
	 * this funtion will make the hand empty
	 * 
	 */
	public void makeHandEmpty() {
		hand = null;
	}

	public void endGame(String msg) {
		out.println("END " + msg);
		out.flush();
	}

	public void setColorOfGame() {
		Suit[] suit = Suit.values();
		int rand = Util.getRandomInRange(0, suit.length);
		Game.colorOfGame = suit[rand];
	}

	public void showHand() {
		try {
			Game.logBufWriter.write(this.name + " Showing Hand---\n\n");

			for (Card c : hand) {
				Game.logBufWriter.write(c.toString() + "\n");
			}
			Game.logBufWriter.write("\n\n");
			Game.logBufWriter.flush();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void run() {

		try {

			this.shareHand();

			Game.logBufWriter.write("\n\n\n" + this.name + " Turn---\n\n\n");
			List<Card> cardtodump = new ArrayList<>();

			/** ask client to dump 5 card **/

			out.println("DUMPCARD5");
			out.flush();

			Game.logBufWriter.write(this.name + " Dump Following Card(s) in open pile----\n");
			for (int i = 0; i < 5; ++i) {
				String s = in.readLine();
				
				Util.dumpCard(hand.get(Integer.parseInt(s)));
				Game.logBufWriter.write(hand.get(i).toString() + "\n");
				cardtodump.add(hand.get(Integer.parseInt(s)));
			}
			Game.logBufWriter.flush();
			hand.removeAll(cardtodump);
			System.out.println(hand.size());
			game.NotifyGame(); // not

		} catch (Exception e) {
			e.printStackTrace();
		}

		synchronized (replaceLock) {
			try {
				while (!myturn)
					replaceLock.wait();
				/**
				 * code for replacing card in pile
				 */
				// ask client to receive open pile
				this.sharePile();

				Game.logBufWriter.write("\n\n---" + this.getName() + " replacing card in open pile---\n\n");
				for (int i = 0; i < 2; ++i) {

					// ask client to replace card
					out.println("REPLACECARD");
					out.flush();
					String toReplacei = in.readLine();
					String withReplacei = in.readLine();
					
					Card toReplace = hand.get(Integer.parseInt(toReplacei));
					Card withReplace = Game.openPile.get(Integer.parseInt(withReplacei));
					
					hand.remove(toReplace);
					Game.openPile.add(toReplace);
					hand.add(withReplace);
					Game.openPile.remove(withReplace);
					
					Game.logBufWriter.write(toReplace.toString() + " replaced with " + withReplace.toString() + "\n");
				}
				Game.logBufWriter.flush();
				game.notifyPlayerHasReplaced();

			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	public void notifyToReplace() {
		synchronized (replaceLock) {
			myturn = true;
			replaceLock.notify();
		}
	}
}
