

import java.util.Calendar;
import java.util.HashMap;

import net.dv8tion.jda.core.events.message.*;

public class Command {
	
	/*
	 * TABLE OF CONTENTS;
	 * 1: Misellaneous Commands
	 * 2: Deck System
	 * 3: Queue System
	 * 4: QueueTime System
	 */
	
	
	/*
	 * MISC. COMMANDS
	 */
	void help(MessageReceivedEvent e){
		e.getChannel().sendMessage(
				  "Michael's Discord Bot"
				+ "\n```"
				+ "Bot Commands:\n"
				+ "-help - Displays help\n"
				+ "-ping - Ping the bot\n"
				+ "-pingcount - Check number of pings in the current session\n"
				+ "-qtime [USERNAME] - Check estimated queue time\n"
				+ "\n"
				+ "Deck Commands:\n"
				+ "-deck [HERO] [AUTHOR] [optional DECK NAME] - Searches for a deck.\n"
				+ "-add [HERO] [AUTHOR] [URL] [optional DECK NAME] - Adds a deck to the deck list.\n"
				+ "-remove [HERO] [AUTHOR] [optional DECK NAME] - Removes a deck from the deck list.\n"
				+ "\n"
				+ "Queue Commands:\n"
				+ "-join / -j - Join the current party queue.\n"
				+ "-leave - Leaves the current party queue.\n"
				+ "-party - Mention all the people in the current party queue for a party.\n"
				+ "-queue - List of people in the queue."
				+ "```"
				).complete();
	}
	
	void ping(MessageReceivedEvent e){
		System.out.println("[Ping] Ping Received."); 
		e.getChannel().sendMessage(e.getAuthor().getAsMention() + " GET PINGED").complete();
		System.out.println("[Ping] Message Sent, Ping count updated.");
	}
	
	void pingcount(MessageReceivedEvent e, int pingcount){
		System.out.println("[Ping] Pingcount Recieved.");
		e.getChannel().sendMessage("Pings: " + pingcount).complete();
		System.out.println("[Ping] Message Sent.");
	}
	
	/*
	 * DECK SYSTEM COMMANDS
	 */
	
	void adddeck(MessageReceivedEvent e, String[] args){
		if(args.length < 3 || args.length > 4){ // invalid amount of arguments
			System.out.println("[Deck] Improper syntax.");
			e.getChannel().sendMessage("Improper syntax?\nCommand usage: -add [HERO] [AUTHOR] [URL] [optional DECK NAME]").complete();
			return;
		}
		
		String deckName = args[0] + args[1];
		if(args.length == 4) deckName += args[3];
		
		System.out.println("[Deck] " + args.length + " arguments.");
		deckName = deckName.toLowerCase();
		args[0].toLowerCase();
		
		System.out.print("[Deck] Add Deck method initialized.");
		String deck = "";
		deck += deckName + ":" + args[2];
		
		String s = DeckWriter.addDeck(deck);
		System.out.println("[Deck] Deck Proccessed.");
		
		if(s.equals("VLD")){
			if(args.length != 4) e.getChannel().sendMessage(args[1] + "'s " + args[0].substring(0, 1).toUpperCase() + args[0].substring(1) + " deck added.").complete();
			else e.getChannel().sendMessage(args[1] + "'s " + args[3] + " " + args[0].substring(0, 1).toUpperCase() + args[0].substring(1) + " deck added.").complete();
		}
		else if(s.equals("FNF")){
			e.getChannel().sendMessage("Decklist not found.").complete();
		}
		else if(s.equals("IOE")){
			e.getChannel().sendMessage("IO Exception! Contact Michael/Harry.").complete();
		}
		System.out.println("[Deck] Message sent.");
		
	}
	
	void getdeck(MessageReceivedEvent e, String[] args){
		if(args.length < 2 || args.length > 3){ // invalid amount of arguments
			System.out.println("[Deck] Improper syntax.");
			e.getChannel().sendMessage("Improper syntax?\nCommand usage: -deck [HERO] [AUTHOR] [optional DECK NAME]").complete();
			return;
		}
		
		String deckName = args[0] + args[1];
		if(args.length == 3) deckName += args[2];
		
		System.out.println("[Deck] " + args.length + " arguments.");
		args[0].toLowerCase();
		deckName = deckName.toLowerCase();
		
		System.out.println("[Deck] Get Deck method initialized.");
		
		String out = "";
		
		out = FileReader.getDeck(deckName);
		
		if(out.equals("DNF")){
			System.out.println("[Deck] Deck not found.");
			e.getChannel().sendMessage("Deck not found.").complete();
			return;
		}
		if(out.equals("FNF")){
			System.out.println("[Deck] ERROR: Text file not found!");
			e.getChannel().sendMessage("Deck list not found.").complete();
			return;
		}
		System.out.println("[Deck] Deck found.");
		
		if(args.length != 3){
			e.getChannel().sendMessage(args[1] + "'s " + args[0].substring(0, 1).toUpperCase() + args[0].substring(1) + " deck:\n" + out).complete();
		}
		else{
			e.getChannel().sendMessage(args[1] + "'s " + args[2] + " " + args[0].substring(0, 1).toUpperCase() + args[0].substring(1) + " deck:\n" + out).complete();
		}
		System.out.println("[Deck] Deck sent.");
	}
	
	void removeDeck(MessageReceivedEvent e, String args[]){
		if(args.length < 2 || args.length > 3){ // invalid amount of arguments
			System.out.println("[Deck] Improper syntax.");
			e.getChannel().sendMessage("Improper syntax?\nCommand usage: -remove [HERO] [AUTHOR] [optional DECK NAME]");
			return;
		}
		
		String deckName = args[0] + args[1];
		if(args.length == 3) deckName += args[2];
		
		System.out.println("[Deck] " + args.length + " arguments.");
		args[0].toLowerCase();
		deckName = deckName.toLowerCase();
		
		System.out.println("[Deck] Deck Method initialized.");
		
		String out = DeckWriter.removeDeck(deckName);
		
		switch(out){
		case "VLD": 
			if(args.length != 3) e.getChannel().sendMessage(args[1] + "'s " + args[0].substring(0, 1).toUpperCase() + args[0].substring(1) + " deck removed.").complete();
			else e.getChannel().sendMessage(args[1] + "'s " + args[2] + " " + args[0].substring(0, 1).toUpperCase() + args[0].substring(1) + " deck removed.").complete();
			System.out.println("[Deck] Deck successfully removed.");
			return;
		case "FNF":
			System.out.println("[Deck] ERROR: Decklist not found!");
			e.getChannel().sendMessage("Deck list not found.").complete();
			return;
		case "IOE":
			System.out.println("[Deck] ERROR: IO Exception thrown!");
			e.getChannel().sendMessage("IO Exception! Contact Michael/Harry.").complete();
			return;
		}
	}
	
	/*
	 * QUEUE SYSTEM COMMANDS
	 */
	
	void queue(MessageReceivedEvent e, QueueContainer queue){
		//Xqueue command, checkqueue, drop from queue command, Xcall on queue command, Xqueue flush command
		System.out.println("[Queue] Queue Method initialized.");
		
		for(int i = 0; i < 5; i++){
			if(queue.id[i] == null){
				queue.id[i] = e.getMember().getEffectiveName(); //[TODO] check if user is already in queue
				queue.user[i] = e.getAuthor();
				System.out.println("[Queue] " + "\"" + e.getAuthor().toString() + "\" added to the queue.");
				e.getChannel().sendMessage(e.getAuthor().getAsMention() + " has been added to the queue.\n").complete();
				listQueue(e, queue);
				break;
			}
		}
		if(queue.user[4] != null){
			System.out.println("[Queue] Queue full, calling players.");
			callPlayers(e, queue);
			return;
		}
		//call on players of the queue if queue becomes full
	}
	
	void callPlayers(MessageReceivedEvent e, QueueContainer queue){
		System.out.println("[Queue] Calling players.");
		
		String out = "TEAM MADE:";
		for(int i = 0; i < 5; i++){
			if(queue.id[i] == null){
				break;
			}
			out += "\n" + queue.user[i].getAsMention();
		}
		queue.resetContainer();
		
		e.getChannel().sendMessage(out).complete();
	}
	
	void listQueue(MessageReceivedEvent e, QueueContainer queue){
		System.out.println("[Queue] Listing players in the current queue.");
		
		String out = "Players currently in queue: ```";
		
		for(int i = 0; i < 5; i++){
			if(queue.id[i] == null){
				if(queue.id[0] == null){
					e.getChannel().sendMessage("No one is in queue.").complete();
					return;
				}
				break;
			}
			out += "\n" + queue.id[i];
		}
		out += "```";
		e.getChannel().sendMessage(out).complete();
	}
	
	void leaveQueue(MessageReceivedEvent e, QueueContainer queue){
		
		for(int i = 0; i < 5; i++){
			if(queue.id[i] == null){
				break;
			}
			if(queue.user[i] == e.getAuthor()){
				queue.removeUser(i);
				System.out.println("[Queue] " + e.getMember().getEffectiveName() + " has been removed from the queue.");
				e.getChannel().sendMessage(e.getAuthor().getAsMention() + " has been removed from the queue.").complete();
				return;
			}
		}
		//reaches here only if person not found
		System.out.println("[Queue] User not found in the queue, and therefore was not removed.");
		e.getChannel().sendMessage(e.getAuthor().getAsMention() + " You can't remove what's not there.").complete();
	}
	
	void flushQueue(MessageReceivedEvent e, QueueContainer queue){
		System.out.println(e.getAuthor().getId());
		if(e.getAuthor().getId() == "200612085887926272" || e.getAuthor().getId() == "224343099042693131"){ // hidden command
			return;
		}
		queue.resetContainer();
		System.out.println("[Queue] Queue flushed by " + e.getMember().getEffectiveName());
	}
	
	/*
	 * QUEUETIME COMMANDS
	 */
	
	void queueTimes(MessageReceivedEvent e, String[] args){
		if(args.length > 1){
			e.getChannel().sendMessage("Improper syntax? Check -help!").complete();
		}
		
		int elo;
		
		HashMap<String, String> data = AgoraGG.fetchUserPVPData(args[0]);
		/*
		 * get elo
		 */
		Calendar cal = Calendar.getInstance();
	}
}
