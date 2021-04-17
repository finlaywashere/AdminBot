package xyz.finlaym.adminbot.action.message.command;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import xyz.finlaym.adminbot.action.permission.PermissionDeclaration;

public abstract class Command {
	protected String name, permission, usage, description;
	private PermissionDeclaration[] effectedPermissions = new PermissionDeclaration[0];
	private long requiredFlags = -1;

	public Command(String name, String permission, String usage, String description) {
		this.name = name;
		this.permission = permission;
		this.usage = usage;
		this.description = description;
	}
	public Command(String name, String permission, String usage, String description, long requiredFlags) {
		this(name,permission,usage,description);
		this.requiredFlags = requiredFlags;
	}
	public Command(String name, String permission, String usage, String description, PermissionDeclaration... effectedPermissions) {
		this(name,permission,usage,description);
		this.effectedPermissions = effectedPermissions;
	}
	public Command(String name, String permission, String usage, String description, long requiredFlags, PermissionDeclaration... effectedPermissions) {
		this(name,permission,usage,description, effectedPermissions);
		this.requiredFlags = requiredFlags;
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
	public PermissionDeclaration[] getEffectedPermissions() {
		return effectedPermissions;
	}
	public long getRequiredFlags() {
		return requiredFlags;
	}
	public abstract void execute(Member member, TextChannel channel, String[] command, CommandHandler handler, Message message, boolean silence);
}
