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

public class AddPermissionCommand extends Command{

	public AddPermissionCommand() {
		super("addpermission", "command.addpermission", "-addpermission <role/user> <permission...>", "Gives a user/role a set of permissions");
	}

	@Override
	public void execute(Member member, TextChannel channel, String[] command, CommandHandler handler, Message message) {
		PermissionsConfig pConfig = handler.getBot().getPermissionsConfig();
		if(message.getMentionedRoles().size() == 1) {
			Role r = message.getMentionedRoles().get(0);
			List<Member> members = channel.getGuild().getMembersWithRoles(r);
			for(Member m : members) {
				if(pConfig.getUserPerms(channel.getGuild().getIdLong(), member.getIdLong()) == null) {
					try {
						pConfig.loadPermissions(channel.getGuild().getIdLong(),member.getIdLong());
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
				for(int i = 2; i < command.length; i++) {
					pConfig.addPermission(m.getGuild().getIdLong(), m.getIdLong(), new Permission(command[i]));
				}
				try {
					pConfig.savePermissions(m.getGuild().getIdLong(), m.getIdLong());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}else if(message.getMentionedMembers().size() == 1){
			Member m = message.getMentionedMembers().get(0);
			if(pConfig.getUserPerms(channel.getGuild().getIdLong(), m.getIdLong()) == null) {
				try {
					pConfig.loadPermissions(channel.getGuild().getIdLong(),m.getIdLong());
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
			for(int i = 2; i < command.length; i++) {
				pConfig.addPermission(m.getGuild().getIdLong(), m.getIdLong(), new Permission(command[i]));
			}
			try {
				pConfig.savePermissions(m.getGuild().getIdLong(), m.getIdLong());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else {
			channel.sendMessage("You must mention one role/user to give permissions to!").queue();
			return;
		}
		channel.sendMessage("Successfully added permission(s) to users/roles!").queue();
	}
}
