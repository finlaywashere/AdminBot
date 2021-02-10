package xyz.finlaym.adminbot.action.message.response;

import java.util.List;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import xyz.finlaym.adminbot.Bot;

public class ResponseHandler {
	private Bot bot;
	public ResponseHandler(Bot bot) {
		this.bot = bot;
	}
	public void handleResponse(TextChannel channel, Member author, Message message) throws Exception {
		long gid = channel.getGuild().getIdLong();
		List<CustomResponse> responses = bot.getServerConfig().getResponses(gid);
		if(responses == null) {
			bot.getServerConfig().loadConfig(gid);
			responses = bot.getServerConfig().getResponses(gid);
			if(responses == null)
				return;
		}
		for(CustomResponse r : responses) {
			if(message.getContentRaw().matches(r.getTrigger())) {
				channel.sendMessage(replace(r.getResponse(),author,channel,message)).queue();
				return;
			}
		}
	}
	private static String replace(String s, Member author, TextChannel channel, Message message) {
		s = s.replace("$u", author.getAsMention());
		s = s.replace("$c", channel.getAsMention());
		return s;
	}
}
