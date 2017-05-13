import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class BotListener extends ListenerAdapter {

	@Override
	public void onMessageReceived(MessageReceivedEvent e) {

		String message = e.getMessage().getRawContent().toLowerCase();
		if (message.startsWith(DiscordBot.BOT_PREFIX)) {
			message = message.substring(1);
			String cmd = splitCommand(message)[0], args = splitCommand(message)[1];

			String returnMessage = "";
			switch (cmd) {
			case "prefix":
				DiscordBot.BOT_PREFIX = args.charAt(0) + "";
				returnMessage = "Prefix changed to " + args.charAt(0) + ".";
				DiscordBot.jda.getPresence()
						.setGame(Game.of(DiscordBot.PLAYING_GAME + " | " + DiscordBot.BOT_PREFIX + "help"));
				break;
			case "ping":
				returnMessage = "Pong!";
				break;
			case "debug":
				returnMessage = "Command: " + cmd + ", Arguments: " + args;
				break;
			case "elo":
				try {
					returnMessage = getElo(args);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				break;
			case "help":
				returnMessage = "\nBot Commands:\n" + DiscordBot.BOT_PREFIX + "help: Displays this message\n"
						+ DiscordBot.BOT_PREFIX + "ping: Pings the bot\n" + DiscordBot.BOT_PREFIX
						+ "prefix [prefix]: Changes the bot prefix\n";
				returnMessage += "\nParagon Bot Commands:\n" + DiscordBot.BOT_PREFIX
						+ "elo [username]: Username's ELO, W/L and KDA according to Agora.gg\n";
				break;
			default:
				returnMessage = "Sorry '" + message + "', is not a command.\nSee " + DiscordBot.BOT_PREFIX
						+ "help for a list of commands.";

			}
			e.getChannel()
					.sendMessage(/* e.getAuthor().getAsMention() + " " + */returnMessage).queue();
		}
	}

	public String getElo(String username) throws Exception {

		String message = "```\n" + username + "\n--------\n";

		try {
			// Search for User
			URLConnection agora = new URL("https://api.agora.gg/players/search/" + username).openConnection();
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
				JSONArray json = new JSONArray(new JSONObject(fetchUserId).getJSONArray("data").toString());
				for (int i = 0; i < json.length(); i++) {
					Map<String, Object> idsMap = toMap((JSONObject) json.get(i));
					int playerID = Integer.parseInt(idsMap.get("id").toString());

					// Search for Player ID Data
					URLConnection agoraPlayers = new URL("https://api.agora.gg/players/" + playerID).openConnection();
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
					JSONArray data = new JSONObject(fetchUserData).getJSONObject("data").getJSONArray("stats");
					if (new JSONObject(fetchUserData).getJSONObject("data").get("privacyEnabled").toString()
							.equals("true")) {
						message += "User's profile is hidden.\n--------\n";
						found = true;
					} else {
						// Find Player's PVP Stats
						for (int j = 0; j < data.length(); j++) {
							Map<String, Object> statsMap = toMap((JSONObject) data.get(j));
							if (Integer.parseInt(statsMap.get("mode").toString()) == 4) {
								found = true;

								// Calculate Player Info
								double elo = Double.parseDouble(statsMap.get("elo").toString());
								double wins = Double.parseDouble(statsMap.get("wins").toString());
								double games = Double.parseDouble(statsMap.get("gamesPlayed").toString());
								double wl = games == 0 ? 0 : wins / games;
								double kills = Double.parseDouble(statsMap.get("kills").toString());
								double deaths = Double.parseDouble(statsMap.get("deaths").toString());
								double assists = Double.parseDouble(statsMap.get("assists").toString());
								double kda = deaths == 0 ? 0 : (kills + assists) / deaths;

								DecimalFormat format = new DecimalFormat("#.00");
								message += "ELO: " + format.format(elo) + "\nW/L: " + format.format(wl * 100)
										+ "%\nKDA: " + format.format(kda) + "\n--------\n";
							}
						}
					}
				}
				if (!found)
					message += "No PVP Games on record.\n--------\n";
			} catch (Exception e) {
				message += "User not found.\n--------\n";
			}
		} catch (Exception e) {
			message += "Something went wrong, please try again.\n--------\n";
		}
		return message + "```";
	}

	public String[] splitCommand(String command) {
		String[] proc = command.split(" ");
		String cmd = proc[0];
		String args = command.replaceFirst(cmd + " ", "");
		return new String[] { cmd, args };
	}

	public static Map<String, Object> toMap(JSONObject object) {
		Map<String, Object> map = new HashMap<String, Object>();

		Iterator<String> keysItr = object.keySet().iterator();
		while (keysItr.hasNext()) {
			String key = keysItr.next();
			Object value = object.get(key);

			if (value instanceof JSONArray) {
				value = toList((JSONArray) value);
			}

			else if (value instanceof JSONObject) {
				value = toMap((JSONObject) value);
			}
			map.put(key, value);
		}
		return map;
	}

	public static List<Object> toList(JSONArray array) {
		List<Object> list = new ArrayList<Object>();
		for (int i = 0; i < array.length(); i++) {
			Object value = array.get(i);
			if (value instanceof JSONArray) {
				value = toList((JSONArray) value);
			}

			else if (value instanceof JSONObject) {
				value = toMap((JSONObject) value);
			}
			list.add(value);
		}
		return list;
	}

}