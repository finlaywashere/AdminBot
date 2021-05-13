package xyz.finlaym.adminbot.action.message.command.commands.permissions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xyz.finlaym.adminbot.action.message.command.Command;
import xyz.finlaym.adminbot.action.message.command.CommandInfo;
import xyz.finlaym.adminbot.action.message.command.CommandResponse;
import xyz.finlaym.adminbot.action.permission.Group;
import xyz.finlaym.adminbot.action.permission.GroupIdentifier;
import xyz.finlaym.adminbot.action.permission.Permission;
import xyz.finlaym.adminbot.storage.config.PermissionsConfig;
import xyz.finlaym.adminbot.utils.MathUtils;

public class ModifyPermissionsRawCommand extends Command {

	private static final Logger logger = LoggerFactory.getLogger(ModifyPermissionsRawCommand.class);
	
	public ModifyPermissionsRawCommand() {
		super("modifypermission", "command.modifypermission", "-modifypermission <action> <id type> <id> <permission>", "Modify raw permission values for a user/group");
	}

	@Override
	public CommandResponse execute(CommandInfo info) {
		String[] command = info.getCommand();
		if(command.length != 5) {
			return new CommandResponse("Incorrect usage of command!\nUsage: -modifypermission <action> <id type> <id> permission",true);
		}
		if(!MathUtils.isLong(command[3])) {
			return new CommandResponse("Error: id must be a group/user id!",true);
		}
		GroupIdentifier identifier = null;
		switch(command[2].toLowerCase()) {
		case "role":
			identifier = new GroupIdentifier(Group.TYPE_ROLE, Long.valueOf(command[3]));
			break;
		case "user":
			identifier = new GroupIdentifier(Group.TYPE_USER, Long.valueOf(command[3]));
			break;
		default:
			return new CommandResponse("Error: Invalid id type\nValid id types are: role, user",true);
		}
		PermissionsConfig pConfig = info.getHandler().getBot().getPermissionsConfig();
		switch(command[1].toLowerCase()) {
		case "add":
			try {
				if(pConfig.getGroupPerms(info.getGid(), identifier) == null) {
					pConfig.loadGroupPermissions(info.getGid(),identifier);
				}
			} catch (Exception e) {
				logger.error("Failed to load permissions in modify permissions raw command", e);
				return new CommandResponse("Critical Error: Failed to load permissions!",true);
			}
			pConfig.addGroupPermission(info.getGid(), identifier, new Permission(command[4]));
			try {
				pConfig.saveGroupPermissions(info.getGid(), identifier);
			} catch (Exception e) {
				logger.error("Failed to save permissions in modify permissions raw command", e);
				return new CommandResponse("Critical Error: Failed to save permissions!",true);
			}
			break;
		case "remove":
			try {
				if(pConfig.getGroupPerms(info.getGid(), identifier) == null) {
					pConfig.loadGroupPermissions(info.getGid(),identifier);
				}
			} catch (Exception e) {
				logger.error("Failed to load permissions in modify permissions raw command", e);
				return new CommandResponse("Critical Error: Failed to load permissions!",true);
			}
			pConfig.removeGroupPermission(info.getGid(), identifier, new Permission(command[4]));
			try {
				pConfig.saveGroupPermissions(info.getGid(), identifier);
			} catch (Exception e) {
				logger.error("Failed to save permissions in modify permissions raw command", e);
				return new CommandResponse("Critical Error: Failed to save permissions!",true);
			}
			break;
		default:
			return new CommandResponse("Error: Invalid action\nValid actions are: add, remove",true);
		}
		return new CommandResponse("Successfully modified permission structure!");
	}
}
