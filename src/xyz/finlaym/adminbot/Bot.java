package xyz.finlaym.adminbot;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class Bot extends ListenerAdapter {

	private static final File TOKEN_FILE = new File("token.priv");
	private static final File SWEAR_FILE = new File("swear.words"); 

	public static void main(String[] args) throws Exception {
		Scanner in = new Scanner(TOKEN_FILE);
		String token = in.nextLine();
		in.close();
		loadSwears();
		JDABuilder.createDefault(token).addEventListeners(new Bot()).build();
	}
	public static void loadSwears() throws Exception{
		swearWords = new ArrayList<String>();
		Scanner in = new Scanner(SWEAR_FILE);
		while(in.hasNextLine()) {
			String swear = in.nextLine().trim().toLowerCase();
			if(swear.isEmpty())
				continue;
			swearWords.add(swear);
		}
		in.close();
	}

	private static List<String> swearWords;

	@Override
	public void onMessageReceived(final MessageReceivedEvent event) {
		if(event.getAuthor().isBot())
			return;
		String message = event.getMessage().getContentRaw().toLowerCase();
		for(String swear : swearWords) {
			if(message.contains(swear)) {
				// Oh No!!! Swear word detected!
				event.getMessage().delete().queue();
				System.out.println("\""+event.getGuild().getName()+"\": "+event.getMember().getUser().getAsTag()+" sent a swear word to channel #"+event.getChannel().getName()+"!");
				event.getChannel().sendMessage("Swear, you will not!").queue();
				return;
			}
		}
		if(message.startsWith("-roles")) {
			Member m = event.getMember();
			boolean admin = false;
			for(Role r : m.getRoles()) {
				if(r.hasPermission(Permission.ADMINISTRATOR)) {
					admin = true;
					break;
				}
			}
			if(!admin) {
				System.out.println("\""+event.getGuild().getName()+"\": "+event.getMember().getUser().getAsTag()+" tried to create a roles menu in channel #"+event.getChannel().getName()+" with insufficient permissions!");
				return;
			}
			final String rawMessage = event.getMessage().getContentRaw();
			System.out.println("\""+event.getGuild().getName()+"\": "+event.getMember().getUser().getAsTag()+" created roles menu in channel #"+event.getChannel().getName()+" with roles \""+rawMessage.split(" ",2)[1]+"\"!");
			MessageBuilder mBuilder = new MessageBuilder("React to be assigned a role!");
			Message m1 = mBuilder.build();
			event.getChannel().sendMessage(m1).queue(m2 -> {
				boolean first = true;  	
				for(String s : rawMessage.split(" ")) {
				 		if(first) {
				 			first = false;
				 			continue;
				 		}
				 		List<Emote> e = event.getGuild().getEmotesByName(s, true);
				 		for(Emote e1 : e)
				 			m2.addReaction(e1).queue();
				}
				});
			event.getMessage().delete().queue();
		}
	}
	@Override
	public void onMessageReactionAdd(MessageReactionAddEvent event) {
		if(event.getMember().getUser().isBot())
			return;
		final MessageReaction reac = event.getReaction();
		Message message = event.getChannel().retrieveMessageById(event.getMessageIdLong()).complete();
		boolean found = false;
		String name = reac.getReactionEmote().getName().replaceAll("_", " ").toLowerCase();
		for(MessageReaction r : message.getReactions()) {
			String name2 = r.getReactionEmote().getName().replaceAll("_", " ").toLowerCase();
			if(name.equals(name2)) {
				boolean bot = r.isSelf();
				if(bot) {
					found = true;
					break;
				}
			}
		}
		if(!found) {
			System.out.println("\""+event.getGuild().getName()+"\": "+event.getMember().getUser().getAsTag()+" tried to react with an unapproved emoji!");
			event.getReaction().removeReaction(event.getUser()).queue();
			return;
		}
		// We know it is a valid role with good permissions 'n' stuff because the bot reacted it too
		for(Role r : event.getGuild().getRolesByName(name, true)) {
			System.out.println("\""+event.getGuild().getName()+"\": "+event.getMember().getUser().getAsTag()+" given role "+r.getName()+"!");
			event.getGuild().addRoleToMember(event.getMember(), r).queue();
		}
	}
}
