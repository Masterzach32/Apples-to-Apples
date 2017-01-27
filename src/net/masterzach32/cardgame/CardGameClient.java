package net.masterzach32.cardgame;

import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;

import net.masterzach32.cardgame.cards.*;

/**
 * Apples to Apples, APCS Programming Project 6
 * Client driver class, receives data from the
 * server, displays it to the player and gets 
 * input from the player.
 * 
 * @author Zach Kozar
 */
public class CardGameClient {
	
	@SuppressWarnings("unchecked")
	public static void main(String[] args) {
		CardGameServer.cards = CardLoader.loadAllCards();
		Scanner scan = new Scanner(System.in);
		Player player = null;
		String name, ip;
		
		System.out.print("Server IP: ");
		ip = scan.next();
		System.out.print("Name: ");
		name = scan.next();
		try {
			player = new Player(new Socket(ip, 25565), name);
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Waiting for other players...");
		int round = 0;
		while(true) {
			try {
				round++;
				JSONObject data;
				data = (JSONObject) JSONValue.parseWithException(player.getInputStream().readLine());
				System.out.println();
				System.out.println("------------------------------------------------------");
				System.out.println("Round " + round);
				System.out.println();
				//System.out.println("Received Data: " + data);
				for(int i = 0; i < JSONHelper.getInteger(data, "size"); i++) {
					Player other = new Player();
					other.updatePlayerData(JSONHelper.getJSONObject(data, "" + i));
					System.out.println(other.getName() + ": " + other.getScore() + " point(s)");
					if(other.getName().equals(player.getName()))
						player.updatePlayerData(JSONHelper.getJSONObject(data, "" + i));
				}
				
				if(!player.isCardCzar()) {
					System.out.println();
					System.out.println("Your hand:");
					System.out.println(player.writeOutCards());
					System.out.println("Card to play on:");
					System.out.println(CardGameServer.cards.get(JSONHelper.getInteger(data, "play")).getText());
					// send card to play
					data = new JSONObject();
					int play = -1;
					boolean isValid = false;
					do {
						System.out.print("Enter the number of the card you would like to play: ");
						play = scan.nextInt();
						if(play >= 0 && play < player.size())
							isValid = true;
					} while (!isValid);
					System.out.println();
					data.put("play", player.removeCard(play).getID());
					data.put("num", play);
					player.getOutputStream().println(data.toString());
					System.out.print("Waiting for other players...");
				} else {
					System.out.println();
					System.out.println("Card to play on:");
					System.out.println(CardGameServer.cards.get(JSONHelper.getInteger(data, "play")).getText());	
					System.out.println();
					System.out.println("Its your turn to choose a winning card.");
					System.out.println("Waiting for other players...");
					System.out.println();
				}
				
				// receive played cards
				data = (JSONObject) JSONValue.parseWithException(player.getInputStream().readLine());
				Deck played = new Deck();
				System.out.println("Played cards:");
				for(int i = 0; i < JSONHelper.getInteger(data, "size"); i++) {
					System.out.println(i + ": " + CardGameServer.cards.get(JSONHelper.getInteger(data, "" + i)).getText());
					played.addCard(CardGameServer.cards.get(JSONHelper.getInteger(data, "" + i)));
				}
				System.out.println();
				
				// choose winning card
				data = new JSONObject();
				if(player.isCardCzar()) {
					int winningCard = -1;
					boolean isValid = false;
					do {
						System.out.print("Enter the number of the card that you think best fits the card to play on: ");
						winningCard = scan.nextInt();
						if(winningCard >= 0 && winningCard < CardGameServer.GAME_SIZE-1)
							isValid = true;
					} while (!isValid);
					data.put("winner", played.getCard(winningCard).getID());
					player.getOutputStream().println(data.toString());
				} else {
					System.out.println("Waiting for player to select winning card...");
				}
				
				data = (JSONObject) JSONValue.parseWithException(player.getInputStream().readLine());
				System.out.println();
				System.out.println("Winning Card: " + CardGameServer.cards.get(JSONHelper.getInteger(data, "winner")).getText());
				System.out.println("Winning Player: " + JSONHelper.getString(data, "name"));
				
				if(JSONHelper.getBoolean(data, "end")) {
					System.out.println();
					System.out.println(JSONHelper.getString(data, "name") + " has won the game!");
					scan.close();
					break;
				}
			} catch (IOException | ParseException e) {
				e.printStackTrace();
			}
		}
	}
}