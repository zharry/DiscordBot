import java.util.HashMap;

import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;

public class DiscordBot {

	private static JDA jda;
	public static final CommandParser parser = new CommandParser();
	public static final String TOKEN = "MzEyMzQ4NjE5MjE2OTEyMzg1.C_Zxew.kT4SLfnluxZYTbe9-ZTgis8HA4Q";
	public static final String PREFIX = ">";

	public static HashMap<String, Command> commands = new HashMap<String, Command>();

	public static void main(String[] args) {

		// Add Commands
		commands.put("ping", new Ping());

		try {
			// Configure JDA
			jda = new JDABuilder(AccountType.BOT).addEventListener(new BotListener()).setToken(TOKEN).buildBlocking();
			jda.setAutoReconnect(true);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static void handleCommand(CommandParser.CommandContainer cmd) {
		if (commands.containsKey(cmd.invoke)) {
			boolean safe = commands.get(cmd.invoke).called(cmd.args, cmd.event);

			if (safe) {
				commands.get(cmd.invoke).action(cmd.args, cmd.event);
			}
			commands.get(cmd.invoke).executed(safe, cmd.event);
		}
	}

}
