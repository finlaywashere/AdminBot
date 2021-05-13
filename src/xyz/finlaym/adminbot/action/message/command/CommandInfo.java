package xyz.finlaym.adminbot.action.message.command;

import java.util.List;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;

public class CommandInfo {
	private String message;
	private Member sender;
	private TextChannel channel;
	private Guild guild;
	private String[] command;
	private List<Member> mMentions;
	private List<Role> rMentions;
	private List<TextChannel> cMentions;
	private boolean mentionsEveryone;
	private CommandHandler handler;
	
	public CommandInfo(String message, Member sender, TextChannel channel, String[] command, List<Member> mMentions, List<Role> rMentions, List<TextChannel> cMentions, boolean mentionsEveryone, CommandHandler handler) {
		this.message = message;
		this.sender = sender;
		this.channel = channel;
		this.guild = this.channel.getGuild();
		this.command = command;
		this.mMentions = mMentions;
		this.rMentions = rMentions;
		this.cMentions = cMentions;
		this.mentionsEveryone = mentionsEveryone;
		this.handler = handler;
	}
	public String getMessage() {
		return message;
	}
	public Member getSender() {
		return sender;
	}
	public TextChannel getChannel() {
		return channel;
	}
	public Guild getGuild() {
		return guild;
	}
	public String[] getCommand() {
		return command;
	}
	public List<Member> getMemberMentions() {
		return mMentions;
	}
	public List<Role> getRoleMentions() {
		return rMentions;
	}
	public boolean mentionsEveryone() {
		return mentionsEveryone;
	}
	public CommandHandler getHandler() {
		return handler;
	}
	public List<TextChannel> getChannelMentions() {
		return cMentions;
	}
	public long getGid() {
		return guild.getIdLong();
	}
	public long getUid() {
		return sender.getIdLong();
	}
}
