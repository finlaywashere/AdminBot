package xyz.finlaym.adminbot.action.message.command.commands.permissions;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xyz.finlaym.adminbot.action.message.command.Command;
import xyz.finlaym.adminbot.action.message.command.CommandHandler;
import xyz.finlaym.adminbot.action.message.command.CommandInfo;
import xyz.finlaym.adminbot.action.message.command.CommandResponse;
import xyz.finlaym.adminbot.action.permission.Group;
import xyz.finlaym.adminbot.action.permission.GroupIdentifier;
import xyz.finlaym.adminbot.action.permission.Permission;
import xyz.finlaym.adminbot.action.permission.PermissionDeclaration;
import xyz.finlaym.adminbot.storage.config.PermissionsConfig;

public class ListPermissionsCommand extends Command{
	
	private static final Logger logger = LoggerFactory.getLogger(ListPermissionsCommand.class);

	public ListPermissionsCommand() {
		super("listpermissions", "command.listpermissions", "-listpermissions [user/group tag]", "Displays the permissions of a user or group", 
				new PermissionDeclaration("permission.view.self", "Allows a user to view their own permissions"),
				new PermissionDeclaration("permission.view.others.role.<id>", "Allows a user to view a role's permissions"),
				new PermissionDeclaration("permission.view.others.user.<id>", "Allows a user to view another user's permissions"));
	}

	@Override
	public CommandResponse execute(CommandInfo info) {
		CommandHandler handler = info.getHandler();
		String[] command = info.getCommand();
		PermissionsConfig pConfig = handler.getBot().getPermissionsConfig();
		if(command.length == 1) {
			try {
				if(pConfig.checkPermission(info.getGuild(), info.getSender(), "permission.view.self")) {
					List<Permission> perms = pConfig.getEffectivePermissions(info.getGuild(), info.getSender());
					
					String s = "```";
					if(pConfig.hasAdmin(info.getSender()))
						s += "*\n";
					if(perms != null && perms.size() > 0) {
						for(Permission p : perms) {
							s += p+"\n";
						}
					}else {
						s += "\nNone";
					}
					s += "```";
					return new CommandResponse(s);
				}else {
					return new CommandResponse("Error: Insufficient permissions to view your permissions!",true);
				}
			} catch (Exception e) {
				logger.error("Failed to check permissions in list permissions command", e);
				return new CommandResponse("Critical Error: Failed to check permissions!",true);
			}
		}else {
			if(info.getRoleMentions().size() == 1) {
				long id = info.getRoleMentions().get(0).getIdLong();
				try {
					if(pConfig.checkPermission(info.getGuild(), info.getSender(), "permission.view.others.role."+id)) {
						List<Permission> perms = pConfig.getGroupPerms(info.getGid(), new GroupIdentifier(Group.TYPE_ROLE, id));
						String s = "```";
						if(pConfig.hasAdmin(info.getRoleMentions().get(0)))
							s += "*\n";
						if(perms != null && perms.size() > 0) {
							for(Permission p : perms) {
								s += p+"\n";
							}
						}else {
							s += "\nNone";
						}
						s += "```";
						return new CommandResponse(s,false,true);
					}else {
						return new CommandResponse("Error: Insufficient permissions to view role's permissions!",true);
					}
				} catch (Exception e) {
					logger.error("Failed to check permissions in list permissions command", e);
					return new CommandResponse("Critical Error: Failed to check permissions!",true);
				}
			}else if(info.getMemberMentions().size() == 1) {
				long id = info.getMemberMentions().get(0).getIdLong();
				try {
					if(pConfig.checkPermission(info.getGuild(), info.getSender(), "permission.view.others.user."+id)) {
						List<Permission> perms = pConfig.getEffectivePermissions(info.getGuild(), info.getMemberMentions().get(0));
						String s = "```";
						if(pConfig.hasAdmin(info.getMemberMentions().get(0)))
							s += "*\n";
						if(perms != null && perms.size() > 0) {
							for(Permission p : perms) {
								s += p+"\n";
							}
						}else {
							s += "\nNone";
						}
						s += "```";
						return new CommandResponse(s,false,true);
					}else {
						return new CommandResponse("Error: Insufficient permissions to view user's permissions!",true);
					}
				} catch (Exception e) {
					logger.error("Failed to check permissions in list permissions command", e);
					return new CommandResponse("Critical Error: Failed to check permissions!",true);
				}
			}else {
				return new CommandResponse("Usage: "+usage,true);
			}
		}
	}
}
