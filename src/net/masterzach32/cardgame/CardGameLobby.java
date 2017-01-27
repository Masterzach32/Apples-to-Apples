package net.masterzach32.cardgame;

import java.io.IOException;
import java.util.ArrayList;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;

import net.masterzach32.cardgame.cards.*;

/**
 * Main game logic class, communicates with clients.
 * 
 * @author Zach Kozar
 */
public class CardGameLobby extends Thread {
	
	public static final int HAND_SIZE = 7, WINNING_SCORE = 7;
	private static int numOfGames = 0;

	private Deck blacks, whites;
	private ArrayList<Player> players;
	
	public CardGameLobby(ArrayList<Player> players) {
		numOfGames++;
		System.out.println("Preparing Lobby " + numOfGames);
		this.players = players;
		// create the two decks from the array of cards
		blacks = new Deck();
		whites = new Deck();
		for(Card card : CardGameServer.cards) {
			if(card instanceof BlackCard)
				blacks.addCard((BlackCard) card);
			else if(card instanceof WhiteCard)
				whites.addCard((WhiteCard) card);
		}
		blacks.shuffle(100);
		whites.shuffle(100);
		System.out.println("Game " + numOfGames + " starting");
		// stars a new thread so the server can create a new game lobby
		start();
	}
	
	@SuppressWarnings("unchecked")
	public void run() {
		players.get(0).setCardCzar(true);
		while(hasWon(players) == null) {
			try {
				JSONObject data = new JSONObject();
				data.put("size", players.size());
				// start new round and draw new cards
				for(int i = 0; i < players.size(); i++) {
					Player player = players.get(i);
					while(player.size() < HAND_SIZE)
						player.draw(whites);
					data.put("" + i, player.getPlayerData());
				}

				// card to be played on
				BlackCard play = (BlackCard) blacks.draw();
				data.put("play", play.getID());

				//System.out.print("Data Packet: " + data);
				// send each player every players data
				for(Player player : players) {
					player.getOutputStream().println(data.toString());
				}
				
				// get player choice
				data = new JSONObject();
				data.put("size", players.size()-1);
				int j = 0;
				for(Player player : players) {
					if(!player.isCardCzar()) {
						JSONObject temp = (JSONObject) JSONValue.parseWithException(player.getInputStream().readLine());
						// play = card index in the Cards ArrayList, num = card index in player hand
						player.setLastCardPlayed(JSONHelper.getInteger(temp, "play"));
						player.removeCard(JSONHelper.getInteger(temp, "num"));
						data.put("" + j, JSONHelper.getInteger(temp, "play"));
						j++;
					}
				}
				
				// send choices
				for(Player player : players)
					player.getOutputStream().println(data.toString());
				
				// get winning card and give a point
				Player winner = null;
				JSONObject temp = null;
				for(Player cardCzar : players) {
					if(cardCzar.isCardCzar()) {
						temp = (JSONObject) JSONValue.parseWithException(cardCzar.getInputStream().readLine());
						cardCzar.setCardCzar(false);
						for(Player player : players) {
							if(cardCzar != player && player.lastCardPlayed() == JSONHelper.getInteger(temp, "winner")) {
								winner = player;
								winner.incrementScore();
							}
						}
					}
				}
				
				// send winning card and restart loop
				data = new JSONObject();
				data.put("winner", JSONHelper.getInteger(temp, "winner"));
				data.put("name", winner.getName());
				data.put("end", !(hasWon(players) == null));
				for(Player player : players) {
					player.getOutputStream().println(data.toString());
				}
				winner.setCardCzar(true);
			} catch (ParseException | IOException e) {
				e.printStackTrace();
			}
		}
		System.out.println("Lobby " + numOfGames + " has ended");
	}
	
	private Player hasWon(ArrayList<Player> players) {
		for(Player player : players) 
			if(player.getScore() >= WINNING_SCORE)
				return player;
		return null;
	}
}