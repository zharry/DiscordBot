import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.Game;

public class DiscordBot {

	public static JDA jda;

	public static final int VERSION = 2;
	public static final String BOT_TOKEN = "MzEyMzQ4NjE5MjE2OTEyMzg1.C_aGcQ.lWJV2QDFO9ZxjuAdQKtmzDFIHbY";
	
	public static String BOT_PREFIX = ">";
	public static String PLAYING_GAME = "Paragon";

	public static void main(String[] args) {

		System.out.println("Welcome to Harry's Discord Bot! Version " + VERSION);
		System.out.println("Prefix is " + BOT_PREFIX);

		try {
			jda = new JDABuilder(AccountType.BOT).addEventListener(new BotListener()).setToken(BOT_TOKEN)
					.buildBlocking();
			jda.getPresence().setGame(Game.of(PLAYING_GAME + " | " + BOT_PREFIX + "help"));

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}