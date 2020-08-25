package color;

import java.io.Serializable;

enum Suit {
	Hearts, Spades, Diamond, Club;

	public static Suit getSuit(String s) {
		if (s.equals(Hearts.toString()))
			return Suit.Hearts;
		if (s.equals(Spades.toString()))
			return Suit.Spades;
		if (s.equals(Diamond.toString()))
			return Suit.Diamond;
		if (s.equals(Club.toString()))
			return Suit.Club;
		return null;
	}
}

enum Value {
	Ace(14), King(13), Queen(12), Jack(11), Two(2), Three(3), Four(4), Five(5), Six(6), Seven(7), Eight(8), Nine(9),
	Ten(10);
	int value;

	Value(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	public static Value getValue(String s) {
		if (s.equals(Ace.toString()))
			return Value.Ace;
		if (s.equals(King.toString()))
			return Value.King;
		if (s.equals(Queen.toString()))
			return Value.Queen;
		if (s.equals(Jack.toString()))
			return Value.Jack;
		if (s.equals(Two.toString()))
			return Value.Two;
		if (s.equals(Three.toString()))
			return Value.Three;
		if (s.equals(Four.toString()))
			return Value.Four;
		if (s.equals(Five.toString()))
			return Value.Five;
		if (s.equals(Six.toString()))
			return Value.Six;
		if (s.equals(Seven.toString()))
			return Value.Seven;
		if (s.equals(Eight.toString()))
			return Value.Eight;
		if (s.equals(Nine.toString()))
			return Value.Nine;
		if (s.equals(Ten.toString()))
			return Value.Ten;
		return null;
	}

}

public class Card implements Comparable<Card>,Serializable {
	private String name;
	private Suit suit;
	private Value value;

	public Card(String name, Suit suit, Value value) {
		this.name = name;
		this.suit = suit;
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Suit getSuit() {
		return suit;
	}

	public void setSuit(Suit suit) {
		this.suit = suit;
	}

	public Value getValue() {
		return value;
	}

	public void setValue(Value value) {
		this.value = value;
	}

	@Override
	public int compareTo(Card o) {
		int cmp = this.suit.compareTo(o.getSuit());
		if (cmp != 0) {
			return cmp;
		}
		return this.value.compareTo(o.getValue());
	}

	
	

	@Override
	public String toString() {
		return "[" + name + ", " + suit + ", " + value + "]";
	}

}
