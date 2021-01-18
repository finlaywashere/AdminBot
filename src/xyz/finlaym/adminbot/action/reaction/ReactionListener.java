package xyz.finlaym.adminbot.action.reaction;

import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class ReactionListener extends ListenerAdapter{
	@Override
	public void onMessageReactionAdd(MessageReactionAddEvent event) {
		if (event.getMember().getUser().isBot())
			return;
		event.retrieveMessage().queue(message -> {
			User u = message.getAuthor();
			if(!u.isBot())
				return;
			if(!message.getContentRaw().startsWith("React to be assigned a role!"))
				return;
			final MessageReaction reac = event.getReaction();
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
		});
	}
}
