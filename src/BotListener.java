import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

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
				returnMessage = " Command: " + cmd + ", Arguments: " + args;
				break;
			case "elo":
				try {
					returnMessage = getElo(args);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				break;
			default:
				returnMessage = " Sorry '" + message + "', is not a command";

			}
			e.getChannel().sendMessage(e.getAuthor().getAsMention() + returnMessage).queue();
		}
	}

	public String getElo(String username) throws Exception {
		
		URLConnection agora = new URL("https://api.agora.gg/players/search/" + username).openConnection();
		
		BufferedReader in = new BufferedReader(new InputStreamReader(agora.openStream()));
		String fetchUserIdProc, fetchUserId = "";
		while ((fetchUserIdProc = in.readLine()) != null)
			fetchUserId += fetchUserIdProc;
		in.close();
		System.out.println(fetchUserId);
		
		return "";
	}

	public String[] splitCommand(String command) {
		String[] proc = command.split(" ");
		String cmd = proc[0];
		String args = command.replaceFirst(cmd + " ", "");
		return new String[] { cmd, args };
	}

}