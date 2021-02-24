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

public class RemovePermissionCommand extends Command{
	
	public RemovePermissionCommand() {
		super("removepermission", "command.removepermission", "-removepermission <role/user> <permission...>", "Removes a permission from a user/role");
	}

	@Override
	public void execute(Member member, TextChannel channel, String[] command, CommandHandler handler, Message message, boolean silence) {
		PermissionsConfig pConfig = handler.getBot().getPermissionsConfig();
		GroupIdentifier id = null;
		if(message.getMentionedRoles().size() == 1) {
			Role r = message.getMentionedRoles().get(0);
			id = new GroupIdentifier(Group.TYPE_ROLE, r.getIdLong());
		}else if(message.getMentionedMembers().size() == 1){
			Member m = message.getMentionedMembers().get(0);
			id = new GroupIdentifier(Group.TYPE_USER, m.getIdLong());
		}
		if(id == null){
			channel.sendMessage("You must mention one role/user to remove permissions from!").queue();
			return;
		}
		try {
			if(pConfig.getGroupPerms(channel.getGuild().getIdLong(), id) == null) {
				pConfig.loadGroupPermissions(channel.getGuild().getIdLong(),id);
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		for(int i = 2; i < command.length; i++) {
			pConfig.removeGroupPermission(channel.getGuild().getIdLong(), id, new Permission(command[i]));
		}
		try {
			pConfig.saveGroupPermissions(channel.getGuild().getIdLong(), id);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(!silence)
			channel.sendMessage("Successfully removed permission(s) from users/roles!").queue();
		if(silence)
			message.delete().queue();
	}
}
