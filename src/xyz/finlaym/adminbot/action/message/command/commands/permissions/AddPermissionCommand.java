package xyz.finlaym.adminbot.action.message.command.commands.permissions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.dv8tion.jda.api.entities.Member;
import xyz.finlaym.adminbot.action.message.command.Command;
import xyz.finlaym.adminbot.action.message.command.CommandHandler;
import xyz.finlaym.adminbot.action.message.command.CommandInfo;
import xyz.finlaym.adminbot.action.message.command.CommandResponse;
import xyz.finlaym.adminbot.action.permission.Group;
import xyz.finlaym.adminbot.action.permission.GroupIdentifier;
import xyz.finlaym.adminbot.action.permission.Permission;
import xyz.finlaym.adminbot.storage.config.PermissionsConfig;

public class AddPermissionCommand extends Command{

	private static final Logger logger = LoggerFactory.getLogger(AddPermissionCommand.class);
	
	public AddPermissionCommand() {
		super("addpermission", "command.addpermission", "-addpermission <role/user> <permission...>", "Gives a user/role a set of permissions");
	}

	@Override
	public CommandResponse execute(CommandInfo info) {
		CommandHandler handler = info.getHandler();
		String[] command = info.getCommand();
		PermissionsConfig pConfig = handler.getBot().getPermissionsConfig();
		if(info.getRoleMentions().size() == 1 || info.mentionsEveryone()) {
			GroupIdentifier identifier;
			if(!info.mentionsEveryone())
				identifier = new GroupIdentifier(Group.TYPE_ROLE, info.getRoleMentions().get(0).getIdLong());
			else
				identifier = new GroupIdentifier(Group.TYPE_ROLE, 0);
			try {
				if(pConfig.getGroupPerms(info.getGid(), identifier) == null) {
					pConfig.loadGroupPermissions(info.getGid(),identifier);
				}
			} catch (Exception e) {
				logger.error("Failed to load permissions in add permission command", e);
				return new CommandResponse("Critical Error: Failed to load permissions!",true);
			}
			for(int i = 2; i < command.length; i++) {
				pConfig.addGroupPermission(info.getGid(), identifier, new Permission(command[i]));
			}
			try {
				pConfig.saveGroupPermissions(info.getGid(), identifier);
			} catch (Exception e) {
				logger.error("Failed to save permissions in add permission command", e);
				return new CommandResponse("Critical Error: Failed to save permissions!",true);
			}
		}else if(info.getMemberMentions().size() == 1){
			Member m = info.getMemberMentions().get(0);
			GroupIdentifier id = new GroupIdentifier(Group.TYPE_USER, m.getIdLong());
			try {
				if(pConfig.getGroupPerms(info.getGid(), id) == null) {
					pConfig.loadGroupPermissions(info.getGid(),id);
				}
			} catch (Exception e) {
				logger.error("Failed to load permissions in add permission command", e);
				return new CommandResponse("Critical Error: Failed to load permissions!",true);
			}
			for(int i = 2; i < command.length; i++) {
				pConfig.addGroupPermission(m.getGuild().getIdLong(), id, new Permission(command[i]));
			}
			try {
				pConfig.saveGroupPermissions(m.getGuild().getIdLong(), id);
			} catch (Exception e) {
				logger.error("Failed to save permissions in add permission command", e);
				return new CommandResponse("Critical Error: Failed to save permissions!", true);
			}
		}else {
			return new CommandResponse("You must mention one role/user to give permissions to!",true);
		}
		return new CommandResponse("Successfully added permission(s) to users/roles!");
	}
}
