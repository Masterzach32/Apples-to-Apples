package net.masterzach32.cardgame.cards;

import org.json.simple.JSONObject;

public class BlackCard extends Card {
	
	private int numOfBlanks;
	
	public BlackCard(int numOfBlanks, String set, String text) {
		super(set);
		this.type = BLACK_CARD;
		this.text = text;
		this.numOfBlanks = numOfBlanks;
	}
	
	public int getNumOfBlanks() {
		return numOfBlanks;
	}
	
	@SuppressWarnings("unchecked")
	public String toString() {
		JSONObject card = new JSONObject();
		card.put("text", text);
		card.put("numOfBlanks", numOfBlanks);
		card.put("cardSet", cardSet);
		card.put("cardType", type);
		return card.toJSONString();
	}
}