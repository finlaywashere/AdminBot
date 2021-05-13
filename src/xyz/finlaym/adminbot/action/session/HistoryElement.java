package xyz.finlaym.adminbot.action.session;

import net.dv8tion.jda.api.entities.TextChannel;

public class HistoryElement {
	private TextChannel channel;
	private String message;
	public HistoryElement(TextChannel channel, String message) {
		this.channel = channel;
		this.message = message;
	}
	public TextChannel getChannel() {
		return channel;
	}
	public String getMessage() {
		return message;
	}
}
