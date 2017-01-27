package net.masterzach32.cardgame.cards;

public class Card {

	public static final int BLACK_CARD = 0,
							WHITE_CARD = 1;
	
	protected String text, cardSet;
	protected int type, id;
	protected static int numOfCards = 0;
	
	public Card(String set) {
		cardSet = set;
		id = numOfCards++;
	}
	
	public int getType() {
		return type;
	}
	
	public int getID() {
		return id;
	}
	
	public String getText() {
		return text;
	}
}