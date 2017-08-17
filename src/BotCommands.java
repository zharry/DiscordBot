import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.math.BigDecimal;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.util.ArrayList;

import javax.json.Json;
import javax.json.stream.JsonParser;
import javax.json.stream.JsonParser.Event;

public class BotCommands {

	public static String getElo(String username, boolean stat) throws Exception {

		String message = "```\n" + username + "\n--------\n";

		try {
			// Search for User
			URLConnection agora = new URL(
					"https://api.agora.gg/v1/players/search?name=" + URLEncoder.encode(username, "UTF-8"))
							.openConnection();
			agora.setRequestProperty("User-Agent",
					"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
			agora.connect();
			BufferedReader r = new BufferedReader(
					new InputStreamReader(agora.getInputStream(), Charset.forName("UTF-8")));
			String fetchUserIdProc, fetchUserId = "";
			while ((fetchUserIdProc = r.readLine()) != null)
				fetchUserId += fetchUserIdProc;
			r.close();

			// Locate Player ID
			boolean found = false;
			try {
				ArrayList<String> ids = getKeyVals(fetchUserId, "id");

				for (int i = 0; i < ids.size(); i++) {
					String playerID = ids.get(i);

					// Search for Player ID Data
					URLConnection agoraPlayers = new URL(
							"https://api.agora.gg/v1/players/" + URLEncoder.encode(playerID, "UTF-8")).openConnection();
					agoraPlayers.setRequestProperty("User-Agent",
							"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
					agoraPlayers.connect();
					BufferedReader in = new BufferedReader(
							new InputStreamReader(agoraPlayers.getInputStream(), Charset.forName("UTF-8")));
					String fetchUserDataProc, fetchUserData = "";
					while ((fetchUserDataProc = in.readLine()) != null)
						fetchUserData += fetchUserDataProc;
					r.close();

					// Check Profile Status
					boolean privacy = getKeyVals(fetchUserData, "privacyEnabled").get(0).equals("true") ? true : false;
					if (privacy) {
						message += "User's profile is hidden.\n--------\n";
					} else {
						// Find Player's PVP Stats
						double elo = Double.parseDouble(getKeyVals(fetchUserData, "elo").get(0));
						int wins = Integer.parseInt(getKeyVals(fetchUserData, "wins").get(0));
						int games = Integer.parseInt(getKeyVals(fetchUserData, "gamesPlayed").get(0));
						double wl = games == 0 ? 0 : wins / (double) games;

						int kills = Integer.parseInt(getKeyVals(fetchUserData, "kills").get(0));
						int deaths = Integer.parseInt(getKeyVals(fetchUserData, "deaths").get(0));
						int assists = Integer.parseInt(getKeyVals(fetchUserData, "assists").get(0));
						double kda = deaths == 0 ? 0 : (kills + assists) / (double) deaths;

						if (!stat) {
							DecimalFormat format = new DecimalFormat("#.00");
							message += "ELO: " + format.format(elo) + "\n";
							message += "W/L: " + format.format(wl * 100) + "%\n";
							message += "KDA: " + format.format(kda) + "\n--------\n";
						} else {
							int score = 0;
							String rank = "";

							score += Math.floor(wl * 100) - 50;
							score += Math.floor((kda - 2) / 0.15);
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
							message += "Stat: " + score + "\n--------\n";
						}
					}
					found = true;

				}
				if (!found)
					message += "No PVP Games on record.\n--------\n";

			} catch (Exception e) {
				message += "User not found.\n--------\n";
			}
		} catch (Exception e) {
			message += "Something went wrong, please try again.\n--------\nError: ";
			message += e + "\n--------\n";
		}
		return message + "```";
	}

	public static ArrayList<String> getKeyVals(String json, String wantKey) {
		final JsonParser parser = Json.createParser(new StringReader(json));
		ArrayList<String> vals = new ArrayList<String>();

		String key = null;
		boolean addNext = false;
		while (parser.hasNext()) {
			final Event event = parser.next();
			switch (event) {
			case KEY_NAME:
				key = parser.getString();
				if (key.equals(wantKey)) {
					addNext = true;
				}
				break;
			case VALUE_STRING:
				String string = parser.getString();
				if (addNext)
					vals.add(string);
				addNext = false;
				break;
			case VALUE_NUMBER:
				BigDecimal number = parser.getBigDecimal();
				if (addNext)
					vals.add(number + "");
				addNext = false;
				break;
			case VALUE_TRUE:
				if (addNext)
					vals.add("true");
				addNext = false;
				break;
			case VALUE_FALSE:
				if (addNext)
					vals.add("false");
				addNext = false;
				break;
			default:
				break;
			}
		}

		parser.close();
		return vals;
	}

}
