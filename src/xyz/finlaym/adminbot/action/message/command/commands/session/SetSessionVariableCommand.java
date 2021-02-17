package xyz.finlaym.adminbot.action.message.command.commands.session;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import xyz.finlaym.adminbot.action.message.command.Command;
import xyz.finlaym.adminbot.action.message.command.CommandHandler;
import xyz.finlaym.adminbot.action.session.Session;

public class SetSessionVariableCommand extends Command{

	public SetSessionVariableCommand() {
		super("setvariable", "command.setvariable", "-setvariable <name> <value>", "Sets a variable in a user's current session");
	}

	@Override
	public void execute(Member member, TextChannel channel, String[] command, CommandHandler handler, Message message) {
		if(command.length < 3) {
			channel.sendMessage("Usage: "+usage).queue();
			return;
		}
		long gid = channel.getGuild().getIdLong();
		long uid = member.getIdLong();
		
		Session s = handler.getBot().getSessionConfig().getSession(gid, uid);
		if(s == null) {
			channel.sendMessage("Error: No session!").queue();
			return;
		}
		
		String key = command[1];
		String value = command[2];
		if(command.length > 3) {
			for(int i = 3; i > command.length; i++) {
				value += " "+command[i];
			}
		}
		s.getVariables().put(key, value);
		channel.sendMessage("Successfully set session variable!").queue();
	}
}
