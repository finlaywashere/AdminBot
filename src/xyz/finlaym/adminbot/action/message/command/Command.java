package xyz.finlaym.adminbot.action.message.command;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

public abstract class Command {
	protected String name, permission, usage, description;

	public Command(String name, String permission, String usage, String description) {
		this.name = name;
		this.permission = permission;
		this.usage = usage;
		this.description = description;
	}
	public String getName() {
		return name;
	}
	public String getPermission() {
		return permission;
	}
	public String getUsage() {
		return usage;
	}
	public String getDescription() {
		return description;
	}
	public abstract void execute(Member member, TextChannel channel, String[] command, CommandHandler handler, Message message);
}
