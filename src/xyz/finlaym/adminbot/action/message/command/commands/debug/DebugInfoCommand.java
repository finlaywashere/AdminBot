package xyz.finlaym.adminbot.action.message.command.commands.debug;

import java.util.List;
import java.util.Map;

import xyz.finlaym.adminbot.action.message.command.Command;
import xyz.finlaym.adminbot.action.message.command.CommandInfo;
import xyz.finlaym.adminbot.action.message.command.CommandResponse;
import xyz.finlaym.adminbot.action.permission.GroupIdentifier;
import xyz.finlaym.adminbot.action.permission.Permission;

public class DebugInfoCommand extends Command{

	public DebugInfoCommand() {
		super("info", "command.debuginfo", "-info", "Shows information about a guild");
	}

	@Override
	public CommandResponse execute(CommandInfo info) {
		String s = "Guild ID: `"+info.getGid()+"`\n";
		s += "Channel ID: `"+info.getChannel().getIdLong()+"`\n";
		s += "Sender ID: `"+info.getUid()+"`\n";
		Map<GroupIdentifier,List<Permission>> perms = info.getHandler().getBot().getPermissionsConfig().getGroupPerms().get(info.getGid());
		if(perms != null)
			s += "# Of Loaded Permission Groups: `"+perms.size()+"`";
		
		return new CommandResponse(s);
	}

}
