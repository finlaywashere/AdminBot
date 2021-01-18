package xyz.finlaym.adminbot;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Scanner;

import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import xyz.finlaym.adminbot.action.message.MessageListener;
import xyz.finlaym.adminbot.action.reaction.ReactionListener;
import xyz.finlaym.adminbot.action.reserve.ReservationManager;
import xyz.finlaym.adminbot.storage.DBInterface;
import xyz.finlaym.adminbot.storage.config.PermissionsConfig;
import xyz.finlaym.adminbot.storage.config.ServerConfig;
import xyz.finlaym.adminbot.storage.config.SwearsConfig;
import xyz.finlaym.adminbot.storage.config.UserLevelConfig;

public class Bot extends ListenerAdapter {

	static {
		try {
			PropertyConfigurator.configure(new FileInputStream(new File("log4j.properties")));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public static Bot INSTANCE;
	
	private static final File TOKEN_FILE = new File("token.priv");
	private static final Logger logger = LoggerFactory.getLogger(Bot.class);
	
	
	private DBInterface dbInterface;
	
	private SwearsConfig sConfig;
	private UserLevelConfig uConfig;
	private ServerConfig seConfig;
	private PermissionsConfig pConfig;
	private ReservationManager rManager;
	
	public static void main(String[] args) throws Exception {
		INSTANCE = new Bot();
	}
	public Bot() throws Exception{
		Scanner in = new Scanner(TOKEN_FILE);
		String token = in.nextLine();
		in.close();
		dbInterface = new DBInterface();
		dbInterface.init("adminbot", "bot", "bot");
		rManager = new ReservationManager();
		JDABuilder.createDefault(token).addEventListeners(new MessageListener(this), new ReactionListener(), rManager).
				setAutoReconnect(true).setActivity(Activity.watching("you")).
				enableIntents(GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_VOICE_STATES).
				setChunkingFilter(ChunkingFilter.NONE).
				setMemberCachePolicy(MemberCachePolicy.ALL).build();
		
		sConfig = new SwearsConfig(dbInterface);
		uConfig = new UserLevelConfig(dbInterface);
		seConfig = new ServerConfig(dbInterface);
		pConfig = new PermissionsConfig(dbInterface);
		logger.info("Finished startup!");
	}

	public ServerConfig getServerConfig() {
		return seConfig;
	}
	public SwearsConfig getSwearsConfig() {
		return sConfig;
	}
	public UserLevelConfig getUserLevelConfig() {
		return uConfig;
	}
	public PermissionsConfig getPermissionsConfig() {
		return pConfig;
	}
	public ReservationManager getReservationManager() {
		return rManager;
	}
}
