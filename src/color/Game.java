package color;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;

public class Game {
	private int noOfPlayer, noOfdecks;
	public boolean next = false;
	private List<Deck> deck;
	private List<Player> player;
	private List<Card> pile;
	private List<Integer> score;
	public static Suit colorOfGame;// suit which is selected as color of game
	public static List<Card> openPile = new ArrayList<>();// open pile for game;

	public static final Object pileLock = new Object(); // lock for open pile
	public final Object gamePlayLock = new Object(); // lock for changing turn of player
	public final Object gamePlayLock1 = new Object(); // lock for changing turn of player

	public static FileWriter logWriter;
	public static BufferedWriter logBufWriter;
	public void InitiateGame(ArrayList<Player> player) {
		// adding player
		if (player.size() != noOfPlayer) {
			throw new IllegalArgumentException("No of player is invalid!");
		}
		this.player = player;
		
		for (Player player2 : this.player) {
			player2.setGame(this);
		}
		// adding deck
		for (int i = 0; i < noOfdecks; ++i) {
			deck.add(new Deck());
			try {
				Game.logBufWriter.write("Deck " + (i + 1) + " has been added to game!\n");
				Game.logBufWriter.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		// making pile of deck
		for (int i = 0; i < deck.size(); ++i) {
			pile.addAll(deck.get(i).getDeck());
		}
		score = new ArrayList<>();
		// shuffle the pile
		Collections.shuffle(pile);
		try {
			Game.logBufWriter.write("\n\n\n------Shuffled Pile--------\n\n");
			for (int i = 0; i < pile.size(); ++i)
				Game.logBufWriter.write(pile.get(i).toString());
			Game.logBufWriter.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public Game(int noOfPlayer, int noOfdecks) {
		try {
			logWriter = new FileWriter("log.txt");
			logBufWriter = new BufferedWriter(logWriter);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		setNoOfPlayer(noOfPlayer);
		setNoOfdecks(noOfdecks);

		deck = new ArrayList<>();
		player = new ArrayList<>();
		pile = new ArrayList<>();

	}

	/**
	 * funtion distribute card one by one to each player whenever a player recieve
	 * jack of any suit it will ask player to set game color and make hand of player
	 * empty.
	 */
	public void setGameColor() {
		try {
			Game.logBufWriter.write("\n\n\n---Setting color of game---\n\n\n" + "\n");
			Game.logBufWriter.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for (int i = 0; i < pile.size(); ++i) {
			// select player who will receive card
			int reciverPlayer = i % player.size();
			try {
				Game.logBufWriter.write(
						"Player:" + player.get(reciverPlayer).getName() + " has received:" + pile.get(i).toString());
				Game.logBufWriter.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (player.get(reciverPlayer).receiveCard(pile.get(i))) {// jack recived by the player

				player.get(reciverPlayer).setColorOfGame();// player will set game color
				Collections.swap(player, 0, reciverPlayer);
				// and shuffle the pile
				try {
					Game.logBufWriter.write("\n\nPlayer:" + player.get(reciverPlayer).getName()
							+ " setting game color to :" + Game.colorOfGame + "\n");
					Game.logBufWriter.flush();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Collections.shuffle(pile);
				try {
					Game.logBufWriter.write("\n\n---Again Shuffling Card---\n\n");
					for (int j = 0; j < pile.size(); ++j) {
						Game.logBufWriter.write(pile.get(j).toString()+"\n");
					}
					Game.logBufWriter.flush();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			}
		}

		try {
			Game.logBufWriter.write("\n\n\n---Making Players hand empty---\n\n\n");

			for (Player p : player) {
				Game.logBufWriter.write("Make hand empty of Player:" + p.getName() + "\n");
				p.makeHandEmpty();
			}
			Game.logBufWriter.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void distributeCard() {
		try {
			Game.logBufWriter.write("\n\n\nAgain Distributing Card----\n\n\n");
			for (int i = 0; i < pile.size(); ++i) {
				int reciverplayer = i % player.size();
				player.get(reciverplayer).receiveCard(pile.get(i));
				Game.logBufWriter.write(
						"Player:" + player.get(reciverplayer).getName() + " has received:" + pile.get(i).toString()+"\n");
			}
			Game.logBufWriter.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	//funciton to share pile to client side player

	//function to share player hand to client player
	public void play() {
		List<Thread> thread = new ArrayList<>();
		for (Player p : player) {
			thread.add(new Thread(p));
		}
		for (Thread t : thread) {
			t.start();
			try {
				synchronized (gamePlayLock) {
					gamePlayLock.wait();
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		for (Player p : player) {
			p.notifyToReplace();
			synchronized (gamePlayLock1) {
				try {
					while (!next)
						gamePlayLock1.wait();
					next = false;
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public int getNoOfPlayer() {
		return noOfPlayer;
	}

	public void setNoOfPlayer(int noOfPlayer) {
		if (noOfPlayer >= 2 && noOfPlayer <= 4) {
			this.noOfPlayer = noOfPlayer;
			try {
				Game.logBufWriter.write("Player in game:" + this.noOfPlayer + "\n");
				Game.logBufWriter.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else
			throw new IllegalArgumentException("No of player is invalid!");
	}

	public int getNoOfdecks() {
		return noOfdecks;
	}

	public void setNoOfdecks(int noOfdecks) {
		if (noOfdecks >= 1 && noOfdecks <= 4) {
			this.noOfdecks = noOfdecks;
			try {
				Game.logBufWriter.write("Deck in game:" + this.noOfdecks + "\n");
				Game.logBufWriter.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else
			throw new IllegalArgumentException("No of Deck is invalid!");
	}

	public void NotifyGame() {
		synchronized (gamePlayLock) {
			gamePlayLock.notify();
		}
	}

	public void notifyPlayerHasReplaced() {
		synchronized (gamePlayLock1) {
			next = true;
			gamePlayLock1.notify();
		}

	}

	public void announceWinner() {
		try {
			BufferedWriter scoreWrite = new BufferedWriter(new FileWriter(new File("score.txt")));
			for (Player p : player) {
				p.calculateScore();
				scoreWrite.write(p.getName() + "\'s Score -> " + p.getScore() + "\n");
				p.showHand();
				score.add(p.getScore());
			}

			int winner = -1, j = 0;
			int max = -99999;
			for (Integer i : score) {
				if (i > max) {
					max = i;
					winner = j;
				}
				j++;
			}
			int i = 0;
			for(Player p:player) {
				if(i==winner)
					p.endGame("1");
				else
					p.endGame("0");
				++i;
			}
			scoreWrite.write("\n\nWinner is " + player.get(winner).getName());
			scoreWrite.flush();
			scoreWrite.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	

}
