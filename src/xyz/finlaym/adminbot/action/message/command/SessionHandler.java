package xyz.finlaym.adminbot.action.message.command;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import xyz.finlaym.adminbot.action.session.HistoryElement;
import xyz.finlaym.adminbot.action.session.Session;
import xyz.finlaym.adminbot.storage.config.SessionConfig;

public class SessionHandler {
	private CommandHandler cHandler;
	public SessionHandler(CommandHandler cHandler) {
		this.cHandler = cHandler;
	}
	public void handleCommand(Member member, TextChannel channel, Message message, String[] command, boolean silenced) {
		SessionConfig config = cHandler.getBot().getSessionConfig();
		long gid = channel.getGuild().getIdLong();
		long uid = member.getIdLong();
		Session session = config.getSession(gid, uid);
		if(session == null)
			return;
		session.getHistory().add(new HistoryElement(channel, message));
		for(int i = 0; i < command.length; i++) {
			for(String key : session.getVariables().keySet()) {
				command[i] = command[i].replaceAll("\\$"+key, session.getVariables().get(key));
			}
		}
	}
}
