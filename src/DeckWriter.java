

import java.io.*;
import java.util.Scanner;

public class DeckWriter {

	static String addDeck(String deck){
		File f = new File("decks.txt");
		
		try {
			FileWriter fw = new FileWriter(f, true);
			BufferedWriter bf = new BufferedWriter(fw);
			System.out.println("[Write] Decklist opened.");
			bf.write(deck + "\n");
			
			bf.flush();
			bf.close();
			System.out.println("[Write] Decklist updated.");
			return "VLD"; // valid
		} catch(FileNotFoundException e){
			System.out.println("[Write] Decklist not found!");
			return "FNF";
		} catch (IOException e) {
			System.out.println("[Write] IOException!");
			return "IOE";
		}
	}
	//just testing commit stuff
	static String removeDeck(String deck){
		File f = new File("decks.txt");
		
		try {
			//find deck place, and replace with nothing
			

			System.out.println("[Write] Decklist opened.");
			
			String ram = "";
			
			Scanner sc = new Scanner(f);
			String s = ""; //temp string for input proccessing
			while(sc.hasNextLine()){
				s = sc.nextLine();
				if((s.substring(0, s.indexOf(":"))).equals(deck)){
					continue; //not adding the deck to be deleted
				}
				ram += s + "\n";
			}
			sc.close();
			
			ram = ram.substring(0, ram.length() - 1);
			
			PrintWriter flusher = new PrintWriter(f);
			flusher.close();
			
			FileWriter fw = new FileWriter(f, true);
			BufferedWriter bf = new BufferedWriter(fw);
			bf.write(ram);
			bf.flush();
			bf.close();
			System.out.println("[Write] Deck removed.");
			return "VLD"; // valid
		} catch(FileNotFoundException e){
			System.out.println("[Write] Decklist not found!");
			return "FNF"; // decklist not found
		} catch (IOException e) {
			System.out.println("[Write] IOException!");
			return "IOE"; // IO exception
		}
	}
}
