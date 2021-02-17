package xyz.finlaym.adminbot.action.message.command.commands.session;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import xyz.finlaym.adminbot.action.message.command.Command;
import xyz.finlaym.adminbot.action.message.command.CommandHandler;
import xyz.finlaym.adminbot.action.permission.PermissionDeclaration;
import xyz.finlaym.adminbot.utils.MathUtils;

public class DeleteSessionCommand extends Command{

	public DeleteSessionCommand() {
		super("deletesession", "command.deletesession", "-deletesession [tag or id]", "Deletes either your own or someone else's command session", 
				new PermissionDeclaration("command.deletesession.others", "Allows you to delete other people's command sessions"));
	}

	@Override
	public void execute(Member member, TextChannel channel, String[] command, CommandHandler handler, Message message) {
		if(message.getMentionedUsers().size() > 0 && command.length > 1) {
			long id;
			if(message.getMentionedUsers().size() > 0)
				id = message.getMentionedUsers().get(0).getIdLong();
			else {
				if(!MathUtils.isLong(command[1])) {
					channel.sendMessage("Error: User id must be a number!").queue();
					return;
				}
				id = Long.valueOf(command[1]);
			}
			try {
				if(handler.getBot().getPermissionsConfig().checkPermission(channel.getGuild(), member, "command.deletesession.others")) {
					handler.getBot().getSessionConfig().setSession(channel.getGuild().getIdLong(), id, null);
				}else {
					channel.sendMessage("Error: Insufficient permissions to execute command!").queue();
					return;
				}
			} catch (Exception e) {
				e.printStackTrace();
				channel.sendMessage("Critical Error: Failed to check user's permissions!").queue();
				return;
			}
		}else {
			handler.getBot().getSessionConfig().setSession(channel.getGuild().getIdLong(), member.getIdLong(), null);
		}
		channel.sendMessage("Successfully deleted session!").queue();
	}
}
