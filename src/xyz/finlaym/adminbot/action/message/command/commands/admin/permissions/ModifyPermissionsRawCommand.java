package xyz.finlaym.adminbot.action.message.command.commands.admin.permissions;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import xyz.finlaym.adminbot.action.message.command.Command;
import xyz.finlaym.adminbot.action.message.command.CommandHandler;
import xyz.finlaym.adminbot.action.permission.Group;
import xyz.finlaym.adminbot.action.permission.GroupIdentifier;
import xyz.finlaym.adminbot.action.permission.Permission;
import xyz.finlaym.adminbot.storage.config.PermissionsConfig;

public class ModifyPermissionsRawCommand extends Command {

	public ModifyPermissionsRawCommand() {
		super("modifypermission", "command.modifypermission", "-modifypermission <action> <id type> <id> permission", "Modify raw permission values for a user/group");
	}

	@Override
	public void execute(Member member, TextChannel channel, String[] command, CommandHandler handler, Message message) {
		if(command.length != 5) {
			channel.sendMessage("Incorrect usage of command!\nUsage: -modifypermission <action> <id type> <id> permission").queue();
			return;
		}
		if(!isLong(command[3])) {
			channel.sendMessage("Error: id must be a group/user id!").queue();
			return;
		}
		GroupIdentifier identifier = null;
		switch(command[2].toLowerCase()) {
		case "role":
			identifier = new GroupIdentifier(Group.TYPE_ROLE, Long.valueOf(command[3]));
			break;
		case "user":
			identifier = new GroupIdentifier(Group.TYPE_NONGROUP, Long.valueOf(command[3]));
			break;
		default:
			channel.sendMessage("Error: Invalid id type\nValid id types are: role, user");
			return;
		}
		PermissionsConfig pConfig = handler.getBot().getPermissionsConfig();
		switch(command[1].toLowerCase()) {
		case "add":
			if(identifier.getType() != Group.TYPE_NONGROUP) {
				try {
					if(pConfig.getGroupPerms(channel.getGuild().getIdLong(), identifier) == null) {
						pConfig.loadGroupPermissions(channel.getGuild().getIdLong(),identifier);
					}
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				pConfig.addGroupPermission(channel.getGuild().getIdLong(), identifier, new Permission(command[4]));
				try {
					pConfig.saveGroupPermissions(channel.getGuild().getIdLong(), identifier);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}else {
				try {
					if(pConfig.getUserPerms(channel.getGuild().getIdLong(), identifier.getIdentifier()) == null) {
						pConfig.loadUserPermissions(channel.getGuild().getIdLong(),identifier.getIdentifier());
					}
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				pConfig.addUserPermission(channel.getGuild().getIdLong(), identifier.getIdentifier(), new Permission(command[4]));
				try {
					pConfig.saveUserPermissions(channel.getGuild().getIdLong(), identifier.getIdentifier());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			break;
		case "remove":
			if(identifier.getType() != Group.TYPE_NONGROUP) {
				pConfig.removeGroupPermission(channel.getGuild().getIdLong(), identifier, new Permission(command[4]));
				try {
					pConfig.saveGroupPermissions(channel.getGuild().getIdLong(), identifier);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}else {
				pConfig.removeUserPermission(channel.getGuild().getIdLong(), identifier.getIdentifier(), new Permission(command[4]));
				try {
					pConfig.saveUserPermissions(channel.getGuild().getIdLong(), identifier.getIdentifier());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			break;
		default:
			channel.sendMessage("Error: Invalid action\nValid actions are: add, remove").queue();
			return;
		}
	}
	@SuppressWarnings("unused")
	private static boolean isLong(String s) {
		try {
			long l = Long.valueOf(s);
			return true;
		}catch(NumberFormatException e) {
			return false;
		}
	}
}
