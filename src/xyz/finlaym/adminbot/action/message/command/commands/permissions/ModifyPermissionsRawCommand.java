package xyz.finlaym.adminbot.action.message.command.commands.permissions;

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
import xyz.finlaym.adminbot.storage.config.PermissionsConfig;
import xyz.finlaym.adminbot.utils.MathUtils;

public class ModifyPermissionsRawCommand extends Command {

	private static final Logger logger = LoggerFactory.getLogger(ModifyPermissionsRawCommand.class);
	
	public ModifyPermissionsRawCommand() {
		super("modifypermission", "command.modifypermission", "-modifypermission <action> <id type> <id> <permission>", "Modify raw permission values for a user/group");
	}

	@Override
	public void execute(Member member, TextChannel channel, String[] command, CommandHandler handler, Message message, boolean silence) {
		if(command.length != 5) {
			channel.sendMessage("Incorrect usage of command!\nUsage: -modifypermission <action> <id type> <id> permission").queue();
			return;
		}
		if(!MathUtils.isLong(command[3])) {
			channel.sendMessage("Error: id must be a group/user id!").queue();
			return;
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
			channel.sendMessage("Error: Invalid id type\nValid id types are: role, user");
			return;
		}
		PermissionsConfig pConfig = handler.getBot().getPermissionsConfig();
		switch(command[1].toLowerCase()) {
		case "add":
			try {
				if(pConfig.getGroupPerms(channel.getGuild().getIdLong(), identifier) == null) {
					pConfig.loadGroupPermissions(channel.getGuild().getIdLong(),identifier);
				}
			} catch (Exception e) {
				logger.error("Failed to load permissions in modify permissions raw command", e);
				channel.sendMessage("Critical Error: Failed to load permissions!").queue();
				return;
			}
			pConfig.addGroupPermission(channel.getGuild().getIdLong(), identifier, new Permission(command[4]));
			try {
				pConfig.saveGroupPermissions(channel.getGuild().getIdLong(), identifier);
			} catch (Exception e) {
				logger.error("Failed to save permissions in modify permissions raw command", e);
				channel.sendMessage("Critical Error: Failed to save permissions!").queue();
				return;
			}
			break;
		case "remove":
			try {
				if(pConfig.getGroupPerms(channel.getGuild().getIdLong(), identifier) == null) {
					pConfig.loadGroupPermissions(channel.getGuild().getIdLong(),identifier);
				}
			} catch (Exception e) {
				logger.error("Failed to load permissions in modify permissions raw command", e);
				channel.sendMessage("Critical Error: Failed to load permissions!").queue();
				return;
			}
			pConfig.removeGroupPermission(channel.getGuild().getIdLong(), identifier, new Permission(command[4]));
			try {
				pConfig.saveGroupPermissions(channel.getGuild().getIdLong(), identifier);
			} catch (Exception e) {
				logger.error("Failed to save permissions in modify permissions raw command", e);
				channel.sendMessage("Critical Error: Failed to save permissions!").queue();
				return;
			}
			break;
		default:
			channel.sendMessage("Error: Invalid action\nValid actions are: add, remove").queue();
			return;
		}
		if(!silence)
			channel.sendMessage("Successfully modified permission structure!").queue();
		if(silence)
			message.delete().queue();
	}
}