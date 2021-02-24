package xyz.finlaym.adminbot.action.message.command.commands.admin;

import java.util.List;

import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import xyz.finlaym.adminbot.action.message.command.Command;
import xyz.finlaym.adminbot.action.message.command.CommandHandler;

public class RolesMenuCommand extends Command{

	public RolesMenuCommand() {
		super("roles", "command.roles", "-roles <role1> [role2...]", "Creates a dialog for users to select a role");
		
	}

	@Override
	public void execute(Member member, TextChannel channel, String[] command, CommandHandler handler, Message message, boolean silence) {
		MessageBuilder mBuilder = new MessageBuilder("React to be assigned a role!");
		Message m1 = mBuilder.build();
		channel.sendMessage(m1).queue(m2 -> {
			for (int i = 1; i < command.length; i++) {
				List<Emote> e = channel.getGuild().getEmotesByName(command[i].replaceAll("_", " "), true);
				for (Emote e1 : e)
					m2.addReaction(e1).queue();
			}
		});
		message.delete().queue();
		if(silence)
			message.delete().queue();
	}

}
