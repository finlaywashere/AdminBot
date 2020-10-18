package xyz.finlaym.adminbot;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDA.Status;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;

public class Bot extends ListenerAdapter {

	private static final File TOKEN_FILE = new File("token.priv");
	private static final File SWEAR_FILE = new File("swear.words");
	private static final File ENABLE_FILE = new File("servers.enabled");

	private static Map<Long,List<String[]>> swearWords = new HashMap<Long,List<String[]>>();
	private static ConfigManager manager;
	private static Lock swearLock = new ReentrantLock();
	private static Map<Long,Integer> currMessageCount = new HashMap<Long,Integer>();
	private static Lock enableLock = new ReentrantLock();
	private static Map<Long,Boolean> enabled = new HashMap<Long,Boolean>();
	private static JDA jda;
	
	public static void main(String[] args) throws Exception {
		Scanner in = new Scanner(TOKEN_FILE);
		String token = in.nextLine();
		in.close();
		manager = new ConfigManager();
		loadServers();
		jda = JDABuilder.createDefault(token).addEventListeners(new Bot()).setAutoReconnect(true).setActivity(Activity.watching("you")).enableIntents(GatewayIntent.GUILD_MEMBERS).build();
		
		while(true) {
			if(jda.getStatus() == Status.ATTEMPTING_TO_RECONNECT) {
				System.out.println("Disconnected from Discord! Forcing reconnect!");
				jda.cancelRequests();
				jda = JDABuilder.createDefault(token).addEventListeners(new Bot()).setAutoReconnect(true).setActivity(Activity.watching("you")).build();
			}
			Thread.sleep(60000);
		}
	}

	public static void loadSwears(long guildid) throws Exception {
		swearLock.lock();
		swearWords = new HashMap<Long,List<String[]>>();
		Scanner in = new Scanner(new File(SWEAR_FILE+"."+guildid));
		List<String[]> swears = new ArrayList<String[]>();
		while (in.hasNextLine()) {
			String swear = in.nextLine().trim().toLowerCase();
			if (swear.isEmpty())
				continue;
			swears.add(swear.split(":"));
		}
		swearWords.put(guildid, swears);
		in.close();
		swearLock.unlock();
	}
	public static void loadServers() throws Exception{
		enableLock.lock();
		enabled.clear();
		if(!ENABLE_FILE.exists()) {
			enableLock.unlock();
			return;
		}
		Scanner in = new Scanner(ENABLE_FILE);
		while(in.hasNextLine()) {
			String s = in.nextLine().trim();
			if(s.length() == 0)
				continue;
			String[] split = s.split(":",2);
			enabled.put(Long.valueOf(split[0]), Boolean.valueOf(split[1]));
		}
		in.close();
		enableLock.unlock();
	}
	public static void addSwear(String s, long guildid) throws Exception{
		swearLock.lock();
		if(!swearWords.containsKey(guildid))
			swearWords.put(guildid, new ArrayList<String[]>());
		swearWords.get(guildid).add(s.split(":"));
		swearLock.unlock();
		File f = new File(SWEAR_FILE+"."+guildid);
		String outS = s;
		if(!f.exists())
			f.createNewFile();
		else
			outS = "\n"+outS;
		PrintWriter out = new PrintWriter(new FileWriter(f,true));
		out.print(outS);
		out.close();
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
		if(!currMessageCount.containsKey(id)) {
			currMessageCount.put(id, 1);
			return;
		}
		enableLock.lock();
		if(!enabled.containsKey(event.getGuild().getIdLong()) || enabled.get(event.getGuild().getIdLong()) == false) {
			enableLock.unlock();
			return;
		}
		enableLock.unlock();
		int messageCount = currMessageCount.get(id);
		messageCount++;
		UserInfo info = manager.infoMap.get(id);
		if(info == null) {
			info = new UserInfo(event.getAuthor().getIdLong(),0);
		}
		if(messageCount >= computeLevelUpLevels(info.getLevel())) {
			messageCount = 0;
			info.setLevel(info.getLevel()+1);
			manager.infoMap.put(id, info);
			try {
				manager.saveInfo();
			} catch (Exception e) {
				e.printStackTrace();
				System.err.println("UwU program did an oopsie woopsie when it twiedd to swave the fwile");
			}
			event.getChannel().sendMessage("Congratulations "+event.getAuthor().getAsMention()+" for leveling up to level "+info.getLevel()+"!").queue();
		}
		currMessageCount.put(id, messageCount);
	}
	
	@Override
	public void onGuildMemberJoin(GuildMemberJoinEvent event) {
		if(!swearWords.containsKey(event.getGuild().getIdLong())) {
			try {
				loadSwears(event.getGuild().getIdLong());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		List<String[]> swears = swearWords.get(event.getGuild().getIdLong());
		for (int i = 0; i < swears.size(); i++) {
			String[] data = swears.get(i);
			if (data[1].equals("u") && event.getUser().getName().contains(data[0])) {
				// Oh No!!! Swear word detected!
				System.out.println("\"" + event.getGuild().getName() + "\": " + event.getMember().getUser().getAsTag() + " joined with a username with a disallowed word! Muting them and sending them a message!");
				PrivateChannel channel = event.getUser().openPrivateChannel().complete();
				channel.sendMessage("Hello! It appears you have a disallowed word in your username to join this server and because of this you have been muted!").queue();
				
				String roleName = (data.length >= 3 ? data[2] : "muted");
				List<Role> roles = event.getGuild().getRolesByName(roleName, true);
				for(Role r : roles) {
					event.getGuild().addRoleToMember(event.getMember(), r).complete();
				}
				continue;
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
			if(!swearWords.containsKey(guildid)) {
				try {
					loadSwears(guildid);
				}catch(Exception e) {
					System.out.println("Failed to load swear word file for server "+event.getGuild().getName()+" with id "+guildid+"! Creating empty one");
					try {
						File f = new File(SWEAR_FILE+"."+guildid);
						f.createNewFile();
						//PrintWriter p = new PrintWriter(new FileWriter(f,true));
						//p.println("true");
						//p.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					swearWords.put(guildid, new ArrayList<String[]>());
				}
			}
			List<String[]> swears = swearWords.get(guildid);
			for (int i = 0; i < swears.size(); i++) {
				if (swears.get(i)[1].equals("m") && message.contains(swears.get(i)[0])) {
					// Oh No!!! Swear word detected!
					event.getMessage().delete().queue();
					System.out.println("\"" + event.getGuild().getName() + "\": " + event.getMember().getUser().getAsTag() + " sent a swear word to channel #" + event.getChannel().getName() + "! They said:\n"+message);
					event.getChannel().sendMessage("Swear, you will not!").queue();
					return;
				}
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
				System.out.println("\"" + event.getGuild().getName() + "\": " + event.getMember().getUser().getAsTag()
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
			}else if(message.startsWith("-addswear") || message.startsWith("-addmswear")) {
				System.out.println("\"" + event.getGuild().getName() + "\": " + event.getMember().getUser().getAsTag()
						+ " added message swear word(s) \""+message.split(" ",2)[1]+"\" in channel #" + event.getChannel().getName()+"\"!");
				String[] swears = message.split(" ",2)[1].split(" ");
				for(String s : swears) {
					try {
						addSwear(s.replaceAll("_", " ")+":m",event.getGuild().getIdLong());
					}catch(Exception e) {
						e.printStackTrace();
						System.err.println("Failed to add swear word to file!");
					}
				}
				return;
			}else if(message.startsWith("-adduswear")) {
				System.out.println("\"" + event.getGuild().getName() + "\": " + event.getMember().getUser().getAsTag()
						+ " added username swear word(s) \""+message.split(" ",2)[1]+"\" in channel #" + event.getChannel().getName()+"\"!");
				String[] swears = message.split(" ",2)[1].split(" ");
				for(String s : swears) {
					try {
						if(s.contains(":")) {
							String[] split = s.split(":",2);
							addSwear(split[0].replaceAll("_", " ")+":u:"+split[1], event.getGuild().getIdLong());
						}else {
							addSwear(s.replaceAll("_", " ")+":u",event.getGuild().getIdLong());
						}
					}catch(Exception e) {
						e.printStackTrace();
						System.err.println("Failed to add swear word to file!");
					}
				}
				return;
			}else if(message.startsWith("-help")) {
				System.out.println("\"" + event.getGuild().getName() + "\": " + event.getMember().getUser().getAsTag()
						+ " requested the help menu in channel "+event.getChannel().getName()+"!");
				String helpMessage = "-help\tShows this message\n"
						+ "-roles <emoji and role names seperated by space>\tCreates a role selection dialogue\n"
						+ "-addmswear <swear words seperated by space>\tAdds a swear word to the list of swears for checking against messages\n"
						+ "-adduswear <swear words seperated by space>\tAdds a swear word to the list of swears for checking against usernames\n"
						+ "Note: For the roles command the emoji name must be the same as the role name, however it is case insensitive and underscores are converted to spaces\n"
						+ "Note: For all commands underscores are converted to spaces unless otherwise specified (emoji names, etc)";
				event.getChannel().sendMessage(helpMessage).queue();
				return;
			}else if(message.equals("-reload")) {
				System.out.println("\"" + event.getGuild().getName() + "\": " + event.getMember().getUser().getAsTag()
						+ " reloaded guild data in channel "+event.getChannel().getName()+"!");
				try {
					loadSwears(event.getGuild().getIdLong());
					loadServers();
				} catch (Exception e) {
					e.printStackTrace();
					event.getChannel().sendMessage("Failed to reload swear word list").queue();
					return;
				}
				event.getChannel().sendMessage("Reloaded swear words!").queue();
			}else if(message.equals("-gid")) {
				System.out.println("\"" + event.getGuild().getName() + "\": " + event.getMember().getUser().getAsTag()
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
