package xyz.finlaym.adminbot.action.message.command.commands;

import java.util.List;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import xyz.finlaym.adminbot.action.message.command.Command;
import xyz.finlaym.adminbot.action.message.command.CommandHandler;
import xyz.finlaym.adminbot.action.permission.Permission;
import xyz.finlaym.adminbot.storage.config.PermissionsConfig;

public class RemovePermissionCommand extends Command{
	
	public RemovePermissionCommand() {
		super("removepermission", "command.removepermission", "-removepermission <role/user> <permission...>", "Removes a permission from a user/role");
	}

	@Override
	public void execute(Member member, TextChannel channel, String[] command, CommandHandler handler, Message message) {
		PermissionsConfig pConfig = handler.getBot().getPermissionsConfig();
		if(message.getMentionedRoles().size() == 1) {
			Role r = message.getMentionedRoles().get(0);
			List<Member> members = channel.getGuild().getMembersWithRoles(r);
			for(Member m : members) {
				for(int i = 2; i < command.length; i++) {
					pConfig.removePermission(m.getGuild().getIdLong(), m.getIdLong(), new Permission(command[i]));
				}
				try {
					pConfig.savePermissions(m.getGuild().getIdLong(), m.getIdLong());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}else if(message.getMentionedMembers().size() == 1){
			Member m = message.getMentionedMembers().get(0);
			for(int i = 2; i < command.length; i++) {
				pConfig.removePermission(m.getGuild().getIdLong(), m.getIdLong(), new Permission(command[i]));
			}
			try {
				pConfig.savePermissions(m.getGuild().getIdLong(), m.getIdLong());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else {
			channel.sendMessage("You must mention one role/user to remove permissions from!").queue();
			return;
		}
		channel.sendMessage("Successfully removed permission(s) from users/roles!").queue();
	}
}
