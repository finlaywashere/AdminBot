package xyz.finlaym.adminbot.action.message.command.commands.debug;

import java.util.List;
import java.util.Map;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import xyz.finlaym.adminbot.action.message.command.Command;
import xyz.finlaym.adminbot.action.message.command.CommandHandler;
import xyz.finlaym.adminbot.action.permission.GroupIdentifier;
import xyz.finlaym.adminbot.action.permission.Permission;

public class DebugInfoCommand extends Command{

	public DebugInfoCommand() {
		super("info", "command.debuginfo", "-info", "Shows information about a guild");
	}

	@Override
	public void execute(Member member, TextChannel channel, String[] command, CommandHandler handler, Message message) {
		String s = "Guild ID: `"+channel.getGuild().getIdLong()+"`\n";
		s += "Channel ID: `"+channel.getIdLong()+"`\n";
		s += "Sender ID: `"+member.getIdLong()+"`\n";
		Map<GroupIdentifier,List<Permission>> perms = handler.getBot().getPermissionsConfig().getGroupPerms().get(channel.getGuild().getIdLong());
		if(perms != null)
			s += "# Of Loaded Permission Groups: `"+perms.size()+"`";
		
		channel.sendMessage(s).queue();
	}

}
