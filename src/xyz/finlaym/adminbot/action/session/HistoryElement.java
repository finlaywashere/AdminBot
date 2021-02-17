package xyz.finlaym.adminbot.action.session;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

public class HistoryElement {
	private TextChannel channel;
	private Message message;
	public HistoryElement(TextChannel channel, Message message) {
		this.channel = channel;
		this.message = message;
	}
	public TextChannel getChannel() {
		return channel;
	}
	public Message getMessage() {
		return message;
	}
}
