package net.masterzach32.cardgame.cards;

import org.json.simple.JSONObject;

public class WhiteCard extends Card {
	
	private boolean isBlank;
	
	public WhiteCard(boolean isBlank, String set, String text) {
		super(set);
		this.type = WHITE_CARD;
		this.text = text;
		this.isBlank = isBlank;
	}
	
	public boolean isBlank() {
		return isBlank;
	}
	
	@SuppressWarnings("unchecked")
	public String toString() {
		JSONObject card = new JSONObject();
		card.put("text", text);
		card.put("isBlank", isBlank);
		card.put("cardSet", cardSet);
		card.put("cardType", type);
		return card.toJSONString();
	}
}