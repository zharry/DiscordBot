import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class BotListener extends ListenerAdapter {

	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		if (event.getMessage().getContent().startsWith(DiscordBot.PREFIX)) {
			if (event.getMessage().getAuthor().getId() != event.getJDA().getSelfUser().getId()) {
				DiscordBot.handleCommand(DiscordBot.parser.parse(event.getMessage().getContent().toLowerCase(), event));
			}
		}
	}
}
