package xyz.finlaym.adminbot.action.message.command.commands.session;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import xyz.finlaym.adminbot.action.message.command.Command;
import xyz.finlaym.adminbot.action.message.command.CommandHandler;
import xyz.finlaym.adminbot.action.permission.PermissionDeclaration;
import xyz.finlaym.adminbot.action.session.HistoryElement;
import xyz.finlaym.adminbot.action.session.Session;
import xyz.finlaym.adminbot.utils.MathUtils;

public class ViewHistoryCommand extends Command{

	public ViewHistoryCommand() {
		super("viewhistory", "command.viewhistory", "-viewhistory [max] [tag or id]", "Shows a user their history or someone else's",
				new PermissionDeclaration("command.viewhistory.others", "Allows a user to view someone else's command history"));
	}

	@Override
	public void execute(Member member, TextChannel channel, String[] command, CommandHandler handler, Message message) {
		int max = 20;
		long gid = channel.getGuild().getIdLong();
		long uid = member.getIdLong();
		if(command.length > 1) {
			if(!MathUtils.isInt(command[1])) {
				channel.sendMessage("Error: Max number of history elements must be an integer!").queue();
				return;
			}
			max = Integer.valueOf(command[1]);
			if(command.length > 2) {
				try {
					if(!handler.getBot().getPermissionsConfig().checkPermission(channel.getGuild(), member, "command.viewhistory.others")) {
						channel.sendMessage("Error: Insufficient permissions to execute command!").queue();
						return;
					}else {
						if(message.getMentionedUsers().size() > 0) {
							uid = message.getMentionedUsers().get(0).getIdLong();
						}else {
							if(!MathUtils.isLong(command[2])) {
								channel.sendMessage("Error: User id must be a number!").queue();
								return;
							}
							uid = Long.valueOf(command[2]);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
					channel.sendMessage("Critical Error: Failed to check user's permissions!").queue();
					return;
				}
			}
		}
		Session s = handler.getBot().getSessionConfig().getSession(gid, uid);
		if(s == null) {
			channel.sendMessage("Error: No session!").queue();
			return;
		}
		String m = "User's command history:\n\n";
		for(int i = max-1; i >= 0; i--) {
			if(i >= s.getHistory().size())
				continue;
			HistoryElement elem = s.getHistory().get(i);
			m += "`"+elem.getMessage().getContentRaw()+"`\n";
		}
		channel.sendMessage(m).queue();
	}
}
