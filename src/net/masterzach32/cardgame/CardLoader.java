package net.masterzach32.cardgame;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;

import net.masterzach32.cardgame.cards.BlackCard;
import net.masterzach32.cardgame.cards.Card;
import net.masterzach32.cardgame.cards.WhiteCard;

public class CardLoader {
	
	/**
	 * Don't let someone make an instance of this class.
	 */
	private CardLoader() {}
	
	public static ArrayList<Card> loadAllCards() {
		ArrayList<Card> cards = new ArrayList<Card>();
		File cardSets = new File("card_sets/");
		if(!cardSets.exists())
			cardSets.mkdirs();
		File[] sets = cardSets.listFiles();
		for(File set : sets) {
			if(set.isDirectory() && !set.getName().substring(0, 1).equals("_")) {
				try {
					JSONObject setInfo = (JSONObject) JSONValue.parseWithException(readFile(new File(set.toString() + "/info.json")));
					System.out.println("Loading Card Set: " + set.toString() + " " + JSONHelper.getString(setInfo, "name"));
					loadCards(new File(set.toString() + "/whites.json"), cards, true, JSONHelper.getString(setInfo, "name"));
					loadCards(new File(set.toString() + "/blacks.json"), cards, false, JSONHelper.getString(setInfo, "name"));
				} catch (ParseException e) {
					e.printStackTrace();
				}
			} else if(set.getName().substring(0, 1).equals("_"))
				System.out.println("Ignoring: " + set.toString());
		}
		System.out.println("All Cards Loaded.");
		return cards;
	}
	
	private static ArrayList<Card> loadCards(File file, ArrayList<Card> cards, boolean type, String set) {
		JSONObject data;
		try {
			data = (JSONObject) JSONValue.parseWithException(readFile(file));
			for(int i = 0; i < JSONHelper.getInteger(data, "size"); i++) {
				JSONObject card = JSONHelper.getJSONObject(data, "" + i);
				if(type) cards.add(new WhiteCard(false, set, JSONHelper.getString(card, "text")));
				else cards.add(new BlackCard(JSONHelper.getInteger(card, "numOfBlanks"), set, JSONHelper.getString(card, "text")));
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return cards;
	}
	
	private static String readFile(File file) {
		RandomAccessFile fin = null;
		String path = file.toString();
		byte[] buffer = null;
		
		try {
			fin = new RandomAccessFile(path, "r"); // "r" = open file for reading only
			buffer = new byte[(int) fin.length()];
			fin.readFully(buffer);
			fin.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return new String(buffer);
	}
}