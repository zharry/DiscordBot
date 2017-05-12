import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.util.ArrayList;

import org.json.JSONObject;

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
				returnMessage = "\nBot Commands:\n" 
						+ DiscordBot.BOT_PREFIX + "help: Displays this message\n"
						+ DiscordBot.BOT_PREFIX + "ping: Pings the bot\n";
				returnMessage += "\nParagon Bot Commands:\n"
						+ DiscordBot.BOT_PREFIX + "elo [username]: Username's ELO, W/L and KDA according to Agora.gg\n";
				break;
			default:
				returnMessage = "Sorry '" + message + "', is not a command.\nSee " + DiscordBot.BOT_PREFIX + "help for a list of commands.";

			}
			e.getChannel()
					.sendMessage(/* e.getAuthor().getAsMention() + " " + */returnMessage).queue();
		}
	}

	public String getElo(String username) throws Exception {

		// Search for User
		URLConnection agora = new URL("https://api.agora.gg/players/search/" + username).openConnection();
		agora.setRequestProperty("User-Agent",
				"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
		agora.connect();
		BufferedReader r = new BufferedReader(new InputStreamReader(agora.getInputStream(), Charset.forName("UTF-8")));
		String fetchUserIdProc, fetchUserId = "";
		while ((fetchUserIdProc = r.readLine()) != null)
			fetchUserId += fetchUserIdProc;
		r.close();
		JSONObject json = new JSONObject(fetchUserId);
		try {
			json = new JSONObject(json.get("data").toString().substring(1, json.get("data").toString().length() - 1));
		} catch (Exception e) {
			return "User " + username + " not found!";
		}
		String ID = json.get("id").toString();

		// Return User Information
		URLConnection player = new URL("https://api.agora.gg/players/" + ID).openConnection();
		player.setRequestProperty("User-Agent",
				"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
		player.connect();
		BufferedReader in = new BufferedReader(
				new InputStreamReader(player.getInputStream(), Charset.forName("UTF-8")));
		String fetchUserDataProc, fetchUserData = "";
		while ((fetchUserDataProc = in.readLine()) != null)
			fetchUserData += fetchUserDataProc;
		r.close();

		ArrayList<String> list = new ArrayList<String>();
		JSONObject data = new JSONObject(fetchUserData);
		data = new JSONObject(
				"{" + data.get("data").toString().substring(1, data.get("data").toString().length() - 1) + "}");
		if (data.get("privacyEnabled").toString().equals("true")) {
			return "User " + username + "'s profile is hidden!";
		}
		String stats = fetchUserData;
		int i = stats.indexOf("\"mode\":4");
		int eloLoc = stats.indexOf("\"elo\":", i);
		int eloLocEnd = stats.indexOf(",\"", eloLoc);
		int gamesLoc = stats.indexOf("\"gamesPlayed\":", i);
		int gamesLocEnd = stats.indexOf(",\"", gamesLoc);
		int winsLoc = stats.indexOf("\"wins\":", i);
		int winsLocEnd = stats.indexOf(",\"", winsLoc);
		int killsLoc = stats.indexOf("\"kills\":", i);
		int killsLocEnd = stats.indexOf(",\"", killsLoc);
		int deathsLoc = stats.indexOf("\"deaths\":", i);
		int deathsLocEnd = stats.indexOf(",\"", deathsLoc);
		int assistsLoc = stats.indexOf("\"assists\":", i);
		int assistsLocEnd = stats.indexOf(",\"", assistsLoc);

		DecimalFormat format = new DecimalFormat("#.00");
		double elo = Double.parseDouble(stats.substring(eloLoc + 6, eloLocEnd));
		double games = Double.parseDouble(stats.substring(gamesLoc + 14, gamesLocEnd));
		double wins = Double.parseDouble(stats.substring(winsLoc + 7, winsLocEnd));
		double wl = (wins / games) * 100;
		double kills = Double.parseDouble(stats.substring(killsLoc + 8, killsLocEnd));
		double deaths = Double.parseDouble(stats.substring(deathsLoc + 9, deathsLocEnd));
		double assists = Double.parseDouble(stats.substring(assistsLoc + 10, assistsLocEnd));
		double kda = (kills + assists) / deaths;

		return username + "\nELO: " + format.format(elo) + "\nW/L: " + format.format(wl) + "%\nKDA: "
				+ format.format(kda);
	}

	public String[] splitCommand(String command) {
		String[] proc = command.split(" ");
		String cmd = proc[0];
		String args = command.replaceFirst(cmd + " ", "");
		return new String[] { cmd, args };
	}

}