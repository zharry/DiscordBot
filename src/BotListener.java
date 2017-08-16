import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.math.BigDecimal;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.util.ArrayList;

import javax.json.Json;
import javax.json.stream.JsonParser;
import javax.json.stream.JsonParser.Event;

import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class BotListener extends ListenerAdapter {

	@Override
	public void onMessageReceived(MessageReceivedEvent e) {

		String message = e.getMessage().getRawContent().toLowerCase();
		if (message.startsWith(ParaBot.BOT_PREFIX)) {
			message = message.substring(1);
			String cmd = splitCommand(message)[0], args = splitCommand(message)[1];

			String returnMessage = "";
			switch (cmd) {
			case "prefix":
				ParaBot.BOT_PREFIX = args.charAt(0) + "";
				returnMessage = "Prefix changed to " + args.charAt(0) + ".";
				ParaBot.jda.getPresence().setGame(Game.of(ParaBot.PLAYING_GAME + " | " + ParaBot.BOT_PREFIX + "help"));
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
				returnMessage = "```\nParagon Bot (Version " + ParaBot.VERSION + ")\n\nBot Commands:\n"
						+ ParaBot.BOT_PREFIX + "help: Displays this message\n" + ParaBot.BOT_PREFIX
						+ "ping: Pings the bot\n" + ParaBot.BOT_PREFIX + "prefix [prefix]: Changes the bot prefix\n";
				returnMessage += "\nAgora.gg Bot Commands:\n" + ParaBot.BOT_PREFIX
						+ "elo [username]: Username's ELO, W/L and KDA\n```";
				break;
			default:
				returnMessage = "Sorry '" + message + "', is not a command.\nSee " + ParaBot.BOT_PREFIX
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
			URLConnection agora = new URL("https://api.agora.gg/v1/players/search?name=" + username).openConnection();
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
					URLConnection agoraPlayers = new URL("https://api.agora.gg/v1/players/" + playerID)
							.openConnection();
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

						DecimalFormat format = new DecimalFormat("#.00");
						message += "ELO: " + format.format(elo) + "\nW/L: " + format.format(wl * 100) + "%\nKDA: "
								+ format.format(kda) + "\n--------\n";
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

	public String[] splitCommand(String command) {
		String[] proc = command.split(" ");
		String cmd = proc[0];
		String args = command.replaceFirst(cmd + " ", "");
		return new String[] { cmd, args };
	}

	public ArrayList<String> getKeyVals(String json, String wantKey) {
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
			}
		}

		parser.close();
		return vals;
	}
}