import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.Game;

public class ParaBot {

	public static JDA jda;

	public static final String VERSION = "6.1.0";
	
	public static String BOT_TOKEN;
	public static String BOT_PREFIX = ">";
	public static String PLAYING_GAME = "Paragon";

	public static void main(String[] args) {

		BOT_TOKEN = args[0];
		
		System.out.println("Welcome to Harry's Paragon Discord Bot! Version " + VERSION);
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