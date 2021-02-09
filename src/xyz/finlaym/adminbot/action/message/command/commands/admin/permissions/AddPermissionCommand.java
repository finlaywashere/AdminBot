package xyz.finlaym.adminbot.action.message.command.commands.admin.permissions;

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

	public AddPermissionCommand() {
		super("addpermission", "command.addpermission", "-addpermission <role/user> <permission...>", "Gives a user/role a set of permissions");
	}

	@Override
	public void execute(Member member, TextChannel channel, String[] command, CommandHandler handler, Message message) {
		PermissionsConfig pConfig = handler.getBot().getPermissionsConfig();
		if(message.getMentionedRoles().size() == 1) {
			Role r = message.getMentionedRoles().get(0);
			GroupIdentifier identifier = new GroupIdentifier(Group.TYPE_ROLE, r.getIdLong());
			try {
				if(pConfig.getGroupPerms(channel.getGuild().getIdLong(), identifier) == null) {
					pConfig.loadGroupPermissions(channel.getGuild().getIdLong(),identifier);
				}
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			for(int i = 2; i < command.length; i++) {
				pConfig.addGroupPermission(channel.getGuild().getIdLong(), identifier, new Permission(command[i]));
			}
			try {
				pConfig.saveGroupPermissions(channel.getGuild().getIdLong(), identifier);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else if(message.getMentionedMembers().size() == 1){
			Member m = message.getMentionedMembers().get(0);
			try {
				if(pConfig.getUserPerms(channel.getGuild().getIdLong(), m.getIdLong()) == null) {
					pConfig.loadUserPermissions(channel.getGuild().getIdLong(),m.getIdLong());
				}
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			for(int i = 2; i < command.length; i++) {
				pConfig.addUserPermission(m.getGuild().getIdLong(), m.getIdLong(), new Permission(command[i]));
			}
			try {
				pConfig.saveUserPermissions(m.getGuild().getIdLong(), m.getIdLong());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else {
			channel.sendMessage("You must mention one role/user to give permissions to!").queue();
			return;
		}
		channel.sendMessage("Successfully added permission(s) to users/roles!").queue();
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
