package color;

import java.util.ArrayList;
import java.util.List;

public class Deck {

	private List<Card> deck;

	private void populateDeck() {
		Suit[] suit = Suit.values();
		Value[] value = Value.values();

		for (Suit s : suit) {
			for (Value v : value) {
				if (v.getValue() == 11)
					deck.add(new Card("Jack", s, v));
				else if (v.getValue() == 12)
					deck.add(new Card("Queen", s, v));
				else if (v.getValue() == 13) {
					deck.add(new Card("King", s, v));
				} else if (v.getValue() == 14) {
					deck.add(new Card("Ace", s, v));
				} else {
					deck.add(new Card(Integer.toString(v.getValue()), s, v));
				}
			}
		}
	}

	public Deck() {
		deck = new ArrayList<>();
		populateDeck();
	}

	public List<Card> getDeck() {
		return this.deck;
	}

}
