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
			try {
				switch (cmd) {
				case "ping":
					returnMessage = "Pong!";
					break;
				case "elo":
					returnMessage = BotCommands.getElo(args);
					break;
				case "stat":
					returnMessage = BotCommands.getStat(args);
					break;
				case "game":
					returnMessage = BotCommands.getCurGame(args);
					break;
				case "help":
					returnMessage = "```\n";
					returnMessage += "Paragon Bot (Version " + ParaBot.VERSION + ")\n";
					returnMessage += "\n";
					returnMessage += "Bot Commands:\n";
					returnMessage += ParaBot.BOT_PREFIX + "help: Displays this message\n";
					returnMessage += ParaBot.BOT_PREFIX + "ping: Pings the bot\n";
					returnMessage += "\n";
					returnMessage += "Agora.gg Bot Commands:\n";
					returnMessage += ParaBot.BOT_PREFIX + "elo [username]: Username's ELO, W/L and KDA\n";
					returnMessage += ParaBot.BOT_PREFIX
							+ "stat [username]: Custom ranking system based on user performance\n";
					returnMessage += ParaBot.BOT_PREFIX
							+ "game [username]: Fetch ELO, W/L and KDA of everyone in user's game\n";
					returnMessage += "```";
					break;
				default:
					returnMessage = "Sorry '" + message + "', is not a command.\nSee " + ParaBot.BOT_PREFIX
							+ "help for a list of commands.";

				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}

			// Send Message
			e.getChannel()
					.sendMessage(/* e.getAuthor().getAsMention() + " " + */returnMessage).queue();
		}
	}

	public String[] splitCommand(String command) {
		String[] proc = command.split(" ");
		String cmd = proc[0];
		String args = command.replaceFirst(cmd + " ", "");
		return new String[] { cmd, args };
	}

}