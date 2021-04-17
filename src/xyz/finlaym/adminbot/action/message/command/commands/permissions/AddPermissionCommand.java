package xyz.finlaym.adminbot.action.message.command.commands.permissions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import xyz.finlaym.adminbot.action.message.command.Command;
import xyz.finlaym.adminbot.action.message.command.CommandHandler;
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
	public void execute(Member member, TextChannel channel, String[] command, CommandHandler handler, Message message, boolean silence) {
		PermissionsConfig pConfig = handler.getBot().getPermissionsConfig();
		if(message.getMentionedRoles().size() == 1) {
			Role r = message.getMentionedRoles().get(0);
			GroupIdentifier identifier = new GroupIdentifier(Group.TYPE_ROLE, r.getIdLong());
			try {
				if(pConfig.getGroupPerms(channel.getGuild().getIdLong(), identifier) == null) {
					pConfig.loadGroupPermissions(channel.getGuild().getIdLong(),identifier);
				}
			} catch (Exception e) {
				logger.error("Failed to load permissions in add permission command", e);
				channel.sendMessage("Critical Error: Failed to load permissions!").queue();
				return;
			}
			for(int i = 2; i < command.length; i++) {
				pConfig.addGroupPermission(channel.getGuild().getIdLong(), identifier, new Permission(command[i]));
			}
			try {
				pConfig.saveGroupPermissions(channel.getGuild().getIdLong(), identifier);
			} catch (Exception e) {
				logger.error("Failed to save permissions in add permission command", e);
				channel.sendMessage("Critical Error: Failed to save permissions!").queue();
				return;
			}
		}else if(message.getMentionedMembers().size() == 1){
			Member m = message.getMentionedMembers().get(0);
			GroupIdentifier id = new GroupIdentifier(Group.TYPE_USER, m.getIdLong());
			try {
				if(pConfig.getGroupPerms(channel.getGuild().getIdLong(), id) == null) {
					pConfig.loadGroupPermissions(channel.getGuild().getIdLong(),id);
				}
			} catch (Exception e) {
				logger.error("Failed to load permissions in add permission command", e);
				channel.sendMessage("Critical Error: Failed to load permissions!").queue();
				return;
			}
			for(int i = 2; i < command.length; i++) {
				pConfig.addGroupPermission(m.getGuild().getIdLong(), id, new Permission(command[i]));
			}
			try {
				pConfig.saveGroupPermissions(m.getGuild().getIdLong(), id);
			} catch (Exception e) {
				logger.error("Failed to save permissions in add permission command", e);
				channel.sendMessage("Critical Error: Failed to save permissions!").queue();
				return;
			}
		}else {
			channel.sendMessage("You must mention one role/user to give permissions to!").queue();
			return;
		}
		if(!silence)
			channel.sendMessage("Successfully added permission(s) to users/roles!").queue();
		if(silence)
			message.delete().queue();
	}
}
