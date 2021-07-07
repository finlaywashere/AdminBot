package xyz.finlaym.adminbot;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import xyz.finlaym.adminbot.action.message.MessageListener;
import xyz.finlaym.adminbot.action.reaction.ReactionListener;
import xyz.finlaym.adminbot.action.reserve.ReservationManager;
import xyz.finlaym.adminbot.action.timer.TimedEventManager;
import xyz.finlaym.adminbot.storage.DBInterface;
import xyz.finlaym.adminbot.storage.config.CurrencyConfig;
import xyz.finlaym.adminbot.storage.config.PermissionsConfig;
import xyz.finlaym.adminbot.storage.config.ReservationConfig;
import xyz.finlaym.adminbot.storage.config.ScriptConfig;
import xyz.finlaym.adminbot.storage.config.ServerConfig;
import xyz.finlaym.adminbot.storage.config.SessionConfig;
import xyz.finlaym.adminbot.storage.config.SwearsConfig;
import xyz.finlaym.adminbot.storage.config.TimedEventConfig;

public class Bot extends ListenerAdapter {

	static {
		try {
			PropertyConfigurator.configure(new FileInputStream(new File("log4j.properties")));
		} catch (FileNotFoundException e) {
			// Cannot replace this with a logger.error as it is the code that configures the logger
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	public static Bot INSTANCE;
	private static final Logger logger = LoggerFactory.getLogger(Bot.class);
	
	
	private DBInterface dbInterface;
	
	private SwearsConfig sConfig;
	private CurrencyConfig cConfig;
	private ServerConfig seConfig;
	private PermissionsConfig pConfig;
	private ReservationManager rManager;
	private SessionConfig sessionConfig;
	private ReservationConfig rConfig;
	private TimedEventManager eManager;
	private ScriptConfig scConfig;
	
	private JDA jda;
	
	public static void main(String[] args) throws Exception {
		INSTANCE = new Bot();
	}
	public Bot() throws Exception{
		String token = System.getenv("TOKEN");
		String dbUser = System.getenv("DBUSER");
		String dbPass = System.getenv("DBPASS");
		dbInterface = new DBInterface();
		dbInterface.init("adminbot", dbUser, dbPass);
		rConfig = new ReservationConfig(dbInterface,this);
		rManager = new ReservationManager(rConfig);
		sConfig = new SwearsConfig(dbInterface);
		cConfig = new CurrencyConfig(dbInterface);
		seConfig = new ServerConfig(dbInterface, this);
		pConfig = new PermissionsConfig(dbInterface);
		sessionConfig = new SessionConfig();
		scConfig = new ScriptConfig(dbInterface); 
		
		TimedEventConfig eConfig= new TimedEventConfig(dbInterface);
		eManager = new TimedEventManager(eConfig);
		
		jda = JDABuilder.createDefault(token).addEventListeners(new MessageListener(this), new ReactionListener(), rManager).
				setAutoReconnect(true).setActivity(Activity.watching("you")).
				enableIntents(GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_VOICE_STATES).
				setChunkingFilter(ChunkingFilter.NONE).
				setMemberCachePolicy(MemberCachePolicy.ALL).build().awaitReady();
		rConfig.prune(rManager,jda);
		
		logger.info("Finished startup!");
	}
	public ScriptConfig getScriptConfig() {
		return scConfig;
	}
	public ServerConfig getServerConfig() {
		return seConfig;
	}
	public SwearsConfig getSwearsConfig() {
		return sConfig;
	}
	public CurrencyConfig getCurrencyConfig() {
		return cConfig;
	}
	public PermissionsConfig getPermissionsConfig() {
		return pConfig;
	}
	public ReservationManager getReservationManager() {
		return rManager;
	}
	public SessionConfig getSessionConfig() {
		return sessionConfig;
	}
	public JDA getJDA() {
		return jda;
	}
	public DBInterface getDBInterface() {
		return dbInterface;
	}
	public TimedEventManager getEventManager() {
		return eManager;
	}
}
