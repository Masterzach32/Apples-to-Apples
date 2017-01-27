package net.masterzach32.cardgame.cards;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

import org.json.simple.JSONObject;

import net.masterzach32.cardgame.CardGameServer;
import net.masterzach32.cardgame.JSONHelper;

public class Player extends Deck {
	
	private String name;
	private Socket socket;
	private BufferedReader in;
	private PrintWriter out;
	private int score, lastCardPlayed;
	private boolean cardCzar;
	
	/**
	 * Create a player object without opening a socket
	 */
	public Player() {}
	
	public Player(Socket socket, String name) {
		super();
		this.socket = socket;
		this.name = name;
		this.score = 0;
		this.cardCzar = false;
		lastCardPlayed = -1;
		
		try {
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new PrintWriter(socket.getOutputStream(), true);
			if(!CardGameServer.isServer)
				out.println(name);
			else {
				this.name = in.readLine();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void handFromJSON(JSONObject hand) {
		ArrayList<Card> cards = CardGameServer.cards;
		this.cards.clear();
		for(int i = 0; i < JSONHelper.getInteger(hand, "cards"); i++) 
			this.cards.add(cards.get(JSONHelper.getInteger(hand, "" + i)));
	}
	
	@SuppressWarnings("unchecked")
	public JSONObject handToJSON() {
		JSONObject cards = new JSONObject();
		cards.put("cards", this.cards.size());
		for(int i = 0; i < this.cards.size(); i++)
			cards.put("" + i, this.cards.get(i).getID());
		return cards;
	}
	
	public void incrementScore() {
		score++;
	}
	
	public int getScore() {
		return score;
	}
	
	public void draw(Deck deck) {
		cards.add(deck.draw());
	}
	
	public String getName() {
		return name;
	}
	
	public boolean isCardCzar() {
		return cardCzar;
	}
	
	public void setCardCzar(boolean status) {
		cardCzar = status;
	}
	
	public void setLastCardPlayed(int id) {
		lastCardPlayed = id;
	}
	
	public int lastCardPlayed() {
		return lastCardPlayed;
	}
	
	public Socket getConnection() {
		return socket;
	}
	
	public BufferedReader getInputStream() {
		return in;
	}
	
	public PrintWriter getOutputStream() {
		return out;
	}
	
	public String toString() {
		return getPlayerData().toString();
	}
	
	@SuppressWarnings("unchecked")
	public JSONObject getPlayerData() {
		JSONObject playerData = new JSONObject();
		playerData.put("hand", handToJSON());
		playerData.put("name", name);
		playerData.put("score", score);
		playerData.put("cardCzar", cardCzar);
		return playerData;
	}

	public void updatePlayerData(JSONObject playerData) {
		handFromJSON(JSONHelper.getJSONObject(playerData, "hand"));
		name = JSONHelper.getString(playerData, "name");
		score = JSONHelper.getInteger(playerData, "score");
		cardCzar = JSONHelper.getBoolean(playerData, "cardCzar");
	}
}