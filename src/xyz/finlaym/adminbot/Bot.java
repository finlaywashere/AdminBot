package xyz.finlaym.adminbot;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDA.Status;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import xyz.finlaym.adminbot.action.swear.SwearWord;
import xyz.finlaym.adminbot.action.swear.SwearWord.ActivationType;
import xyz.finlaym.adminbot.storage.DBInterface;
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
	
	private static final File TOKEN_FILE = new File("token.priv");
	private static final Logger logger = LoggerFactory.getLogger(Bot.class);
	
	
	private static DBInterface dbInterface;
	
	private static Map<Long,Integer> currMessageCount = new HashMap<Long,Integer>();
	private static JDA jda;
	private static SwearsConfig sConfig;
	private static UserLevelConfig uConfig;
	private static ServerConfig seConfig;
	
	public static void main(String[] args) throws Exception {
		Scanner in = new Scanner(TOKEN_FILE);
		String token = in.nextLine();
		in.close();
		dbInterface = new DBInterface();
		dbInterface.init("adminbot", "bot", "bot");
		jda = JDABuilder.createDefault(token).addEventListeners(new Bot()).
				setAutoReconnect(true).setActivity(Activity.watching("you")).
				enableIntents(GatewayIntent.GUILD_MEMBERS).
				setMemberCachePolicy(MemberCachePolicy.ONLINE).build();
		
		sConfig = new SwearsConfig(dbInterface);
		uConfig = new UserLevelConfig(dbInterface);
		seConfig = new ServerConfig(dbInterface);
		
		while(true) {
			if(jda.getStatus() == Status.ATTEMPTING_TO_RECONNECT) {
				logger.warn("Disconnected from Discord! Forcing reconnect!");
				jda.cancelRequests();
				jda = JDABuilder.createDefault(token).addEventListeners(new Bot()).setAutoReconnect(true).setActivity(Activity.watching("you")).build();
			}
			Thread.sleep(60000);
		}
	}

	public static int computeLevelUpLevels(int currLevel) {
		if(currLevel == 1)
			return 5;
		int required = 5;
		for(int i = 1; i <= currLevel; i++) {
			required += i;
			if(required > 100)
				return 100;
		}
		
		return required;
	}
	public void countMessages(MessageReceivedEvent event) {
		long id = event.getAuthor().getIdLong();
		if(!seConfig.getLevelsEnabled(id))
			return;
		if(!currMessageCount.containsKey(id)) {
			currMessageCount.put(id, 1);
			return;
		}
		int messageCount = currMessageCount.get(id);
		messageCount++;
		int level = uConfig.getUserLevels(id);
		if(messageCount >= computeLevelUpLevels(level)) {
			messageCount = 0;
			uConfig.setUserLevels(id, level+1);
			try {
				uConfig.saveLevels(id);
			} catch (Exception e) {
				logger.error("UwU program did an oopsie woopsie when it twiedd to swave the fwile", e.getCause());
			}
			event.getChannel().sendMessage("Congratulations "+event.getAuthor().getAsMention()+" for leveling up to level "+(level+1)+"!").queue();
		}
		currMessageCount.put(id, messageCount);
	}
	
	@Override
	public void onGuildMemberJoin(GuildMemberJoinEvent event) {
		checkName(event.getGuild(), event.getMember());
	}
	private void checkName(Guild guild, Member member) {
		SwearWord word = sConfig.isSwear(guild.getIdLong(), member.getNickname(), ActivationType.USER);
		if(word != null) {
			logger.info("\"" + guild.getName() + "\": " + member.getUser().getAsMention() + " joined with a username with a disallowed word! Muting them and sending them a message!");
			PrivateChannel channel = member.getUser().openPrivateChannel().complete();
			channel.sendMessage("Hello! It appears you have a disallowed word in your username to join this server and because of this you have been muted!").queue();
			
			String roleName = word.getMuteRole();
			List<Role> roles = guild.getRolesByName(roleName, true);
			for(Role r : roles) {
				guild.addRoleToMember(member, r).complete();
			}
		}
	}
	
	@Override
	public void onMessageReceived(final MessageReceivedEvent event) {
		if (event.getAuthor().isBot())
			return;
		String message = event.getMessage().getContentRaw().toLowerCase();
		Member m = event.getMember();
		boolean admin = isAdmin(m);
		if(!admin) {
			long guildid = event.getGuild().getIdLong();
			SwearWord word = sConfig.isSwear(guildid, message, ActivationType.MESSAGE);
			if(word != null) {
				// Oh No!!! Swear word detected!
				event.getMessage().delete().queue();
				logger.info("\"" + event.getGuild().getName() + "\": " + event.getMember().getUser().getAsTag() + " sent a swear word to channel #" + event.getChannel().getName() + "! They said:\n"+message);
				event.getChannel().sendMessage("Swear, you will not!").queue();
				return;
			}
		}
		try {
			countMessages(event);
		}catch(Exception e) {
			e.printStackTrace();
			System.err.println("OwO *flips table* I did an oopsie woopsie and bwoke!");
		}
		if (message.startsWith("-")) {
			if (!admin) {
				System.out.println("\"" + event.getGuild().getName() + "\": " + event.getMember().getUser().getAsTag() + " tried to execute command in channel #"+ event.getChannel().getName() + " with insufficient permissions! Command: "+event.getMessage().getContentRaw());
				return;
			}
			if (message.startsWith("-roles")) {
				final String rawMessage = event.getMessage().getContentRaw();
				logger.info("\"" + event.getGuild().getName() + "\": " + event.getMember().getUser().getAsTag()
						+ " created roles menu in channel #" + event.getChannel().getName() + " with roles \""
						+ rawMessage.split(" ", 2)[1] + "\"!");
				MessageBuilder mBuilder = new MessageBuilder("React to be assigned a role!");
				Message m1 = mBuilder.build();
				event.getChannel().sendMessage(m1).queue(m2 -> {
					boolean first = true;
					for (String s : rawMessage.split(" ")) {
						if (first) {
							first = false;
							continue;
						}
						List<Emote> e = event.getGuild().getEmotesByName(s, true);
						for (Emote e1 : e)
							m2.addReaction(e1).queue();
					}
				});
				event.getMessage().delete().queue();
				return;
			}else if(message.startsWith("-addswear")) {
				logger.info("\"" + event.getGuild().getName() + "\": " + event.getMember().getUser().getAsTag()
						+ " added message swear word(s) \""+message.split(" ",2)[1]+"\" in channel #" + event.getChannel().getName()+"\"!");
				String[] swears = message.split(" ",2)[1].split(" ");
				for(String s : swears) {
					try {
						sConfig.addSwear(SwearWord.fromString(s.replaceAll("_", " ")),event.getGuild().getIdLong());
						sConfig.saveSwears(event.getGuild().getIdLong());
					}catch(Exception e) {
						e.printStackTrace();
						System.err.println("Failed to add swear word to file!");
					}
				}
				return;
			}else if(message.startsWith("-help")) {
				logger.info("\"" + event.getGuild().getName() + "\": " + event.getMember().getUser().getAsTag()
						+ " requested the help menu in channel "+event.getChannel().getName()+"!");
				String helpMessage = "-help\tShows this message\n"
						+ "-roles <emoji and role names seperated by space>\tCreates a role selection dialogue\n"
						+ "-addswear <swear words seperated by space>\tAdds a swear word to the list of swears for checking against messages/usernames\n"
						+ "Note: For the roles command the emoji name must be the same as the role name, however it is case insensitive and underscores are converted to spaces\n"
						+ "Note: For all commands underscores are converted to spaces unless otherwise specified (emoji names, etc)";
				event.getChannel().sendMessage(helpMessage).queue();
				return;
			}else if(message.equals("-reload")) {
				logger.info("\"" + event.getGuild().getName() + "\": " + event.getMember().getUser().getAsTag()
						+ " reloaded guild data in channel "+event.getChannel().getName()+"!");
				try {
					sConfig.loadSwears(event.getGuild().getIdLong());
				} catch (Exception e) {
					e.printStackTrace();
					event.getChannel().sendMessage("Failed to reload swear word list").queue();
					return;
				}
				event.getChannel().sendMessage("Reloaded swear words!").queue();
			}else if(message.equals("-gid")) {
				logger.info("\"" + event.getGuild().getName() + "\": " + event.getMember().getUser().getAsTag()
						+ " requested the guild's id in channel "+event.getChannel().getName()+"!");
				event.getChannel().sendMessage("This guild's id is "+event.getGuild().getIdLong()).queue();
			}
		}
	}

	private static boolean isAdmin(Member m) {
		boolean admin = false;
		if(m.isOwner())
			return true;
		for (Role r : m.getRoles()) {
			if (r.hasPermission(Permission.ADMINISTRATOR)) {
				admin = true;
				break;
			}
		}
		return admin;
	}

	@Override
	public void onMessageReactionAdd(MessageReactionAddEvent event) {
		if (event.getMember().getUser().isBot())
			return;
		Message m = event.retrieveMessage().complete();
		User u = m.getAuthor();
		if(!u.isBot())
			return;
		if(!m.getContentRaw().startsWith("React to be assigned a role!"))
			return;
		final MessageReaction reac = event.getReaction();
		Message message = event.getChannel().retrieveMessageById(event.getMessageIdLong()).complete();
		boolean found = false;
		String name = reac.getReactionEmote().getName().replaceAll("_", " ").toLowerCase();
		for (MessageReaction r : message.getReactions()) {
			String name2 = r.getReactionEmote().getName().replaceAll("_", " ").toLowerCase();
			if (name.equals(name2)) {
				boolean bot = r.isSelf();
				if (bot) {
					found = true;
					break;
				}
			}
		}
		if (!found) {
			System.out.println("\"" + event.getGuild().getName() + "\": " + event.getMember().getUser().getAsTag()
					+ " tried to react with an unapproved emoji!");
			event.getReaction().removeReaction(event.getUser()).queue();
			return;
		}
		// We know it is a valid role with good permissions 'n' stuff because the bot
		// reacted it too
		for (Role r : event.getGuild().getRolesByName(name, true)) {
			System.out.println("\"" + event.getGuild().getName() + "\": " + event.getMember().getUser().getAsTag()
					+ " given role " + r.getName() + "!");
			event.getGuild().addRoleToMember(event.getMember(), r).queue();
		}
	}
}
