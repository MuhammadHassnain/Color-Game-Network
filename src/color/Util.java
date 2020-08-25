package color;

import java.util.List;
import java.util.Random;
import java.util.StringTokenizer;

public class Util {
	/**
	 * 
	 * @param min lower limit and it is included
	 * @param max upper limit and it is excluded
	 * @return random number between min and max
	 */
	public static int getRandomInRange(int min,int max) {
		Random random = new Random();
		int randno = min + random.nextInt(max-min);
		return randno;
	}
	
	public synchronized static void dumpCard(Card c) {
		synchronized (Game.pileLock) {
			Game.openPile.add(c);
		}
	}
	public synchronized static Card replaceCard(Card c,List<Card> openPile) {
		Integer i = getRandomInRange(0, openPile.size());
		Card ret = openPile.get(i);
		openPile.remove(ret);
		openPile.add(c);
		return ret;
	}
	
	public static Card toCard(String sCard) {
		StringTokenizer tokens = new StringTokenizer(sCard, ",");
		String name = tokens.nextToken().substring(1);
		String suit = tokens.nextToken().substring(1);
		String value = tokens.nextToken();
		value = value.substring(1, value.length()-1);
		Suit s = Suit.getSuit(suit);
		Value v = Value.getValue(value);
		Card c = new Card(name, s, v);
		return c;
	}

}
