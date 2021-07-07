package xyz.finlaym.adminbot.action.message.command.commands.role;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import xyz.finlaym.adminbot.action.message.command.Command;
import xyz.finlaym.adminbot.action.message.command.CommandInfo;
import xyz.finlaym.adminbot.action.message.command.CommandResponse;
import xyz.finlaym.adminbot.utils.MathUtils;

public class ListRolesCommand extends Command{

	public ListRolesCommand() {
		super("listroles", "command.listroles", "-listroles [user/tag", "Lists the name of all the roles a given user has");
	}

	@Override
	public CommandResponse execute(CommandInfo info) {
		Member m = null;
		if(info.getCommand().length > 1) {
			// I would add a permission to view other user's roles except discord already makes this information public
			// Another user was passed as an arguments passed
			if(info.getMemberMentions().size() > 0) {
				// Mentions tag
				m = info.getMemberMentions().get(0);
			}else {
				// Mentions id
				if(!MathUtils.isLong(info.getCommand()[1]))
					return new CommandResponse("Error: Invalid user tag!", true);
				m = info.getChannel().getGuild().retrieveMemberById(Long.valueOf(info.getCommand()[1])).complete();
			}
			
		}else {
			m = info.getSender();
		}
		String roles = "```";
		for(Role r : m.getRoles()) {
			roles += r.getName()+"\n";
		}
		roles += "```";
		return new CommandResponse(roles);
	}
}
