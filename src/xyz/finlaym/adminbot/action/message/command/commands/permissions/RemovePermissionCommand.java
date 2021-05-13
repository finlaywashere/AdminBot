package xyz.finlaym.adminbot.action.message.command.commands.permissions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import xyz.finlaym.adminbot.action.message.command.Command;
import xyz.finlaym.adminbot.action.message.command.CommandInfo;
import xyz.finlaym.adminbot.action.message.command.CommandResponse;
import xyz.finlaym.adminbot.action.permission.Group;
import xyz.finlaym.adminbot.action.permission.GroupIdentifier;
import xyz.finlaym.adminbot.action.permission.Permission;
import xyz.finlaym.adminbot.storage.config.PermissionsConfig;

public class RemovePermissionCommand extends Command{
		
	private static final Logger logger = LoggerFactory.getLogger(RemovePermissionCommand.class);

	public RemovePermissionCommand() {
		super("removepermission", "command.removepermission", "-removepermission <role/user> <permission...>", "Removes a permission from a user/role");
	}

	@Override
	public CommandResponse execute(CommandInfo info) {
		PermissionsConfig pConfig = info.getHandler().getBot().getPermissionsConfig();
		GroupIdentifier id = null;
		if(info.getRoleMentions().size() == 1) {
			Role r = info.getRoleMentions().get(0);
			id = new GroupIdentifier(Group.TYPE_ROLE, r.getIdLong());
		}else if(info.getMemberMentions().size() == 1){
			Member m = info.getMemberMentions().get(0);
			id = new GroupIdentifier(Group.TYPE_USER, m.getIdLong());
		}
		if(id == null){
			return new CommandResponse("You must mention one role/user to remove permissions from!",true);
		}
		try {
			if(pConfig.getGroupPerms(info.getGid(), id) == null) {
				pConfig.loadGroupPermissions(info.getGid(),id);
			}
		} catch (Exception e) {
			logger.error("Failed to load permissions in remove permission command", e);
			return new CommandResponse("Critical error: Failed to load permissions from database!",true);
		}
		String[] command = info.getCommand();
		for(int i = 2; i < command.length; i++) {
			pConfig.removeGroupPermission(info.getGid(), id, new Permission(command[i]));
		}
		try {
			pConfig.saveGroupPermissions(info.getGid(), id);
		} catch (Exception e) {
			logger.error("Failed to save permissions in remove permission command", e);
			return new CommandResponse("Critical error: Failed to save permissions to database!",true);
		}
		return new CommandResponse("Successfully removed permission(s) from users/roles!");
	}
}
