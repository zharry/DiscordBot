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
			default:
				returnMessage = " Sorry '" + message + "', is not a command";

			}
			e.getChannel().sendMessage(e.getAuthor().getAsMention() + returnMessage).queue();
		}
	}

	public String[] splitCommand(String command) {
		String[] proc = command.split(" ");
		String cmd = proc[0];
		String args = command.replaceFirst(cmd + " ", "");
		return new String[] { cmd, args };
	}

}