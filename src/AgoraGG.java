import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.math.BigDecimal;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;

import javax.json.Json;
import javax.json.stream.JsonParser;
import javax.json.stream.JsonParser.Event;

public class AgoraGG {

	/**
	 * @param username
	 *            Username
	 * @return ArrayList<String> ["id"[, "id"]] or {"Error", "User not found"}
	 * @throws Exception
	 */
	public static ArrayList<String> usernameToUserID(String username) throws Exception {

		ArrayList<String> data = new ArrayList<String>();
		try {
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
			data = getKeyVals(fetchUserId, "id");
		} catch (Exception e) {
			data.add("Error");
			data.add("User not found!");
			System.out.println(e);
		}
		return data;

	}

	/**
	 * @param playerID
	 *            Player ID
	 * @return HashMap<String, String> {"elo" => "elo", "wins" => "wins" ,...}
	 *         or {"Error" => "Error Message"}
	 */
	public static HashMap<String, String> fetchUserPVPData(String playerID) {

		HashMap<String, String> data = new HashMap<String, String>();
		try {
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
			in.close();

			// Check Profile Status
			boolean privacy = getKeyVals(fetchUserData, "privacyEnabled").get(0).equals("true") ? true : false;
			if (privacy) {
				data.put("Error", "User's profile is hidden!");
			} else {
				double elo = Double.parseDouble(getKeyVals(fetchUserData, "elo").get(0));
				int wins = Integer.parseInt(getKeyVals(fetchUserData, "wins").get(0));
				int games = Integer.parseInt(getKeyVals(fetchUserData, "gamesPlayed").get(0));
				double wl = games == 0 ? 0 : wins / (double) games;

				int kills = Integer.parseInt(getKeyVals(fetchUserData, "kills").get(0));
				int deaths = Integer.parseInt(getKeyVals(fetchUserData, "deaths").get(0));
				int assists = Integer.parseInt(getKeyVals(fetchUserData, "assists").get(0));
				double kda = deaths == 0 ? 0 : (kills + assists) / (double) deaths;

				data.put("elo", elo + "");
				data.put("wins", wins + "");
				data.put("gamesPlayed", games + "");
				data.put("wl", wl + "");
				data.put("kills", kills + "");
				data.put("deaths", deaths + "");
				data.put("assists", assists + "");
				data.put("kda", kda + "");
			}
		} catch (Exception e) {
			data.put("Error", "No PVP Games on record!");
			System.out.println(e);
		}
		return data;
	}

	/**
	 * @param playerID
	 *            PlayerID
	 * @return ArrayList<HashMap<String, String>> [{"Username" => "Elo",...},
	 *         {"Username" => "Elo",...}] or [{"Error" => "Error Message"}]
	 */
	public static ArrayList<HashMap<String, String>> fetchUserCurrentGame(String playerID) {

		ArrayList<HashMap<String, String>> returnData = new ArrayList<HashMap<String, String>>();
		HashMap<String, String> data2 = new HashMap<String, String>();
		HashMap<String, String> data = new HashMap<String, String>();
		try {
			URLConnection agoraGamesNow = new URL(
					"https://api.agora.gg/v1/games/now/" + URLEncoder.encode(playerID, "UTF-8")).openConnection();
			agoraGamesNow.setRequestProperty("User-Agent",
					"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
			agoraGamesNow.connect();
			BufferedReader in = new BufferedReader(
					new InputStreamReader(agoraGamesNow.getInputStream(), Charset.forName("UTF-8")));
			String fetchGameDataProc, fetchGameData = "";
			while ((fetchGameDataProc = in.readLine()) != null)
				fetchGameData += fetchGameDataProc;
			in.close();

			ArrayList<String> players = getKeyVals(fetchGameData, "name");
			ArrayList<String> elo = getKeyVals(fetchGameData, "totalElo");

			if (players.size() == 10) {
				for (int i = 0; i < 10; i++) {
					if (i < 5)
						data.put(players.get(i), elo.get(i));
					else
						data2.put(players.get(i), elo.get(i));
				}
				returnData.add(data);
				returnData.add(data2);
			} else {
				data.put("Error", "Player is currently not in a game!");
				returnData.add(data);
			}
		} catch (Exception e) {
			data.put("Error", "Something went wrong, check server logs!");
			returnData.add(data);
			System.out.println(e);
		}
		return returnData;
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
