package xyz.finlaym.adminbot.action.message.command.commands.permissions;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import xyz.finlaym.adminbot.action.message.command.Command;
import xyz.finlaym.adminbot.action.message.command.CommandHandler;
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
	public void execute(Member member, TextChannel channel, String[] command, CommandHandler handler, Message message, boolean silence) {
		PermissionsConfig pConfig = handler.getBot().getPermissionsConfig();
		if(command.length == 1) {
			try {
				if(pConfig.checkPermission(channel.getGuild(), member, "permission.view.self")) {
					List<Permission> perms = pConfig.getEffectivePermissions(channel.getGuild(), member);
					
					String s = "User is admin: "+pConfig.hasAdmin(member)+"\nPermissions:\n```";
					if(perms != null) {
						for(Permission p : perms) {
							s += "\n"+p;
						}
					}else {
						s += "\nNone";
					}
					s += "```";
					channel.sendMessage(s).queue();
				}else {
					channel.sendMessage("Error: Insufficient permissions to view your permissions!").queue();
				}
			} catch (Exception e) {
				logger.error("Failed to check permissions in list permissions command", e);
				channel.sendMessage("Critical Error: Failed to check permissions!").queue();
				return;
			}
		}else {
			if(message.getMentionedRoles().size() == 1) {
				long id = message.getMentionedRoles().get(0).getIdLong();
				try {
					if(pConfig.checkPermission(channel.getGuild(), member, "permission.view.others.role."+id)) {
						List<Permission> perms = pConfig.getGroupPerms(channel.getGuild().getIdLong(), new GroupIdentifier(Group.TYPE_ROLE, id));
						String s = "Role has admin: "+pConfig.hasAdmin(message.getMentionedRoles().get(0))+"\nPermissions:\n```";
						if(perms != null) {
							for(Permission p : perms) {
								s += "\n"+p;
							}
						}else {
							s += "\nNone";
						}
						s += "```";
						channel.sendMessage(s).queue();
					}else {
						channel.sendMessage("Error: Insufficient permissions to view role's permissions!").queue();
					}
				} catch (Exception e) {
					logger.error("Failed to check permissions in list permissions command", e);
					channel.sendMessage("Critical Error: Failed to check permissions!").queue();
					return;
				}
			}else if(message.getMentionedMembers().size() == 1) {
				long id = message.getMentionedMembers().get(0).getIdLong();
				try {
					if(pConfig.checkPermission(channel.getGuild(), member, "permission.view.others.user."+id)) {
						List<Permission> perms = pConfig.getEffectivePermissions(channel.getGuild(), message.getMentionedMembers().get(0));
						String s = "User is admin: "+pConfig.hasAdmin(message.getMentionedMembers().get(0))+"\nPermissions:\n```";
						if(perms != null) {
							for(Permission p : perms) {
								s += "\n"+p;
							}
						}else {
							s += "\nNone";
						}
						s += "```";
						channel.sendMessage(s).queue();
					}else {
						channel.sendMessage("Error: Insufficient permissions to view user's permissions!").queue();
					}
				} catch (Exception e) {
					logger.error("Failed to check permissions in list permissions command", e);
					channel.sendMessage("Critical Error: Failed to check permissions!").queue();
					return;
				}
			}else {
				channel.sendMessage("Usage: "+usage);
				return;
			}
			if(silence)
				message.delete().queue();
		}
	}
}
