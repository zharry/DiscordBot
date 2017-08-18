import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class BotCommands {

	public static String getElo(String username) throws Exception {

		String message = "```\n" + username + "\n--------\n";

		// Locate Player ID
		ArrayList<String> ids = AgoraGG.usernameToUserID(username);

		if (ids.get(0).equals("Error")) {
			message += ids.get(1);
		} else {
			for (int i = 0; i < ids.size(); i++) {
				String playerID = ids.get(i);

				// Search for Player Data
				HashMap<String, String> data = AgoraGG.fetchUserPVPData(playerID);
				if (!data.containsKey("Error")) {
					DecimalFormat format = new DecimalFormat("#.00");
					message += "ELO: " + format.format(Double.parseDouble(data.get("elo"))) + "\n";
					message += "W/L: " + format.format(Double.parseDouble(data.get("wl")) * 100) + "%\n";
					message += "KDA: " + format.format(Double.parseDouble(data.get("kda")));
				} else
					message += data.get("Error");
				if (i != ids.size() - 1)
					message += "\n--------\n";
			}
		}

		return message + "\n--------\n```";
	}

	public static String getStat(String username) throws Exception {

		String message = "```\n" + username + "\n--------\n";

		// Locate Player ID
		ArrayList<String> ids = AgoraGG.usernameToUserID(username);

		if (ids.get(0).equals("Error")) {
			message += ids.get(1);
		} else {
			for (int i = 0; i < ids.size(); i++) {
				String playerID = ids.get(i);

				// Search for Player Data
				HashMap<String, String> data = AgoraGG.fetchUserPVPData(playerID);
				if (!data.containsKey("Error")) {
					int score = 0;
					String rank = "";

					score += Math.floor(Double.parseDouble(data.get("wl")) * 100) - 50;
					score += Math.floor((Double.parseDouble(data.get("kda")) - 2) / 0.15);

					double elo = Double.parseDouble(data.get("elo"));
					if (elo < 1300)
						score += -5;
					else if (elo < 1400)
						score += -1;
					else if (elo < 1500)
						score += 0;
					else if (elo < 1600)
						score += 2;
					else if (elo < 1700)
						score += 5;
					else if (elo < 2000)
						score += 10;
					else
						score += 20;

					if (score < 0)
						rank = "Boosted";
					else if (score < 5)
						rank = "Trade";
					else if (score < 10)
						rank = "Average";
					else if (score < 20)
						rank = "Good";
					else
						rank = "Excellent";

					message += "Rank: " + rank + "\n";
					message += "Stat: " + score + "";
				} else
					message += data.get("Error");

				if (i != ids.size() - 1)
					message += "";
			}
		}

		return message + "\n--------\n```";
	}

	public static String getCurGame(String username) throws Exception {

		String message = "```\n" + username + "\n--------\n";

		// Locate Player ID
		ArrayList<String> ids = AgoraGG.usernameToUserID(username);

		if (ids.get(0).equals("Error")) {
			message += ids.get(1);
		} else {
			for (int i = 0; i < ids.size(); i++) {
				String playerID = ids.get(i);

				// Get for current game for player
				ArrayList<HashMap<String, String>> data = AgoraGG.fetchUserCurrentGame(playerID);
				if (!data.get(0).containsKey("Error")) {
					for (int a = 0; a < 2; a++) {
			    		message += "\n";
			    		message += "Team " + (a + 1) + ":\n";
					    Iterator it = data.get(a).entrySet().iterator();
					    while (it.hasNext()) {
					        Map.Entry pair = (Map.Entry)it.next();
					        String user = pair.getKey() + "";
							DecimalFormat format = new DecimalFormat("#.00");
							message += user + ": " + format.format(Double.parseDouble(pair.getValue() + "")) +"\n";
					        it.remove();
					    }
					}
				} else
					message += data.get(0).get("Error");
			}
		}
		return message + "\n--------\n```";
	}
}
