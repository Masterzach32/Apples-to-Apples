package net.masterzach32.cardgame;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;

import net.masterzach32.cardgame.cards.*;

/**
 * Apples to Apples, APCS Programming Project 6
 * Server driver class. Can handle multiple games at once
 * 
 * @author Zach Kozar
 * @version 3/5/16
 */
public class CardGameServer {
	
	public static final int GAME_SIZE = 4;

	public static ArrayList<Card> cards;
	public static ArrayList<CardGameLobby> games;
	
	private static ServerSocket serverSocket;
	
	public static boolean isServer = false;
	
	public static void main(String[] args) {
		isServer = true;
		cards = CardLoader.loadAllCards();
		games = new ArrayList<>();
		
		try {
			serverSocket = new ServerSocket(25565);
			while(true) {
				ArrayList<Player> players = new ArrayList<>();
				while(players.size() < GAME_SIZE) {
					players.add(new Player(serverSocket.accept(), ""));
					System.out.println("New player joined: " + players.get(players.size()-1).getName() + " Players needed to start: " + (GAME_SIZE-players.size()));
				}
				games.add(new CardGameLobby(players));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}