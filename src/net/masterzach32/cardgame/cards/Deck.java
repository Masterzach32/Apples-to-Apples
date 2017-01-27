package net.masterzach32.cardgame.cards;

import java.util.ArrayList;

public class Deck {

	protected ArrayList<Card> cards;
	
	public Deck() {
		cards = new ArrayList<Card>();
	}
	
	public void addCard(Card card) {
		cards.add(card);
	}
	
	/**
	 * Returns the top card of this deck and removes it from the deck
	 * @return
	 */
	public Card draw() {
		return cards.remove(0);
	}
	
	/**
	 * Returns the card at the given index
	 * @param index
	 * @return
	 */
	public Card getCard(int index) {
		return cards.get(index);
	}
	
	/**
	 * Returns the card at the given index and removes it from this deck
	 * @param index
	 * @return
	 */
	public Card removeCard(int index) {
		return cards.remove(index);
	}
	
	/**
	 * Remove all cards from this deck
	 */
	public void clearDeck() {
		cards.clear();
	}
	
	/**
	 * Shuffle the deck n times
	 * @param times
	 */
	public void shuffle(int n) {
		for(int i = 0; i < n; i++)
			shuffle();
	}
	
	private void shuffle() {
		for(int i = cards.size()-1; i > 0; i--) {
			int r = (int) (Math.random() * i);
			Card temp = cards.get(i);
			cards.set(i, cards.get(r));
			cards.set(r, temp);
		}
	}
	
	/**
	 * Returns the size of this deck
	 * @return
	 */
	public int size() {
		return cards.size();
	}
	
	public String writeOutCards() {
		String str = "";
		for(int i = 0; i < cards.size(); i++) {
			Card card = cards.get(i);
			str += i + ": " + card.getText() + "\n";
		}
		return str;
	}
}