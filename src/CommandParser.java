import java.util.ArrayList;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class CommandParser {

	public CommandContainer parse(String raw, MessageReceivedEvent event) {
		ArrayList<String> command = new ArrayList<String>();

		String cmd = raw.replace(DiscordBot.PREFIX, "");
		String[] split = cmd.split(" ");
		for (String s : split)
			command.add(s);

		String invoke = command.get(0);
		String[] args = new String[command.size() - 1];

		return new CommandContainer(raw, cmd, split, invoke, args, event);
	}

	public class CommandContainer {
		public final String raw, cmd, invoke;
		public final String[] split, args;
		public final MessageReceivedEvent event;

		public CommandContainer(String raw, String cmd, String[] split, String invoke, String[] args,
				MessageReceivedEvent event) {
			this.raw = raw;
			this.cmd = cmd;
			this.split = split;
			this.invoke = invoke;
			this.args = args;
			this.event = event;
		}
	}

}
