package xyz.finlaym.adminbot.action.message.command.commands.role;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import xyz.finlaym.adminbot.action.message.command.Command;
import xyz.finlaym.adminbot.action.message.command.CommandInfo;
import xyz.finlaym.adminbot.action.message.command.CommandResponse;
import xyz.finlaym.adminbot.utils.MathUtils;

public class RemoveRoleCommand extends Command{

	public RemoveRoleCommand() {
		super("removerole", "command.removerole", "-removerole <user/tag> <role/tag>", "Removes a role from a user");
	}

	@Override
	public CommandResponse execute(CommandInfo info) {
		if(info.getCommand().length != 3)
			return new CommandResponse("Usage: "+usage, true);
		
		Member m = null;
		if(info.getMemberMentions().size() > 0)
			m = info.getMemberMentions().get(0);
		else {
			if(!MathUtils.isLong(info.getCommand()[1]))
				return new CommandResponse("Usage: "+usage, true);
			else
				m = info.getChannel().getGuild().retrieveMemberById(Long.valueOf(info.getCommand()[1])).complete();
		}
		Role r = null;
		if(info.getRoleMentions().size() > 0)
			r = info.getRoleMentions().get(0);
		else {
			if(!MathUtils.isLong(info.getCommand()[2]))
				return new CommandResponse("Usage: "+usage, true);
			else
				r = info.getChannel().getGuild().getRoleById(Long.valueOf(info.getCommand()[2]));
		}
		info.getChannel().getGuild().removeRoleFromMember(m, r).queue();
		return new CommandResponse("Successfully removed role from member!");
	}

}
