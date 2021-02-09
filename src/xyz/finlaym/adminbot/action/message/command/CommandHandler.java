package xyz.finlaym.adminbot.action.message.command;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import xyz.finlaym.adminbot.Bot;
import xyz.finlaym.adminbot.action.message.command.commands.HelpCommand;
import xyz.finlaym.adminbot.action.message.command.commands.ReserveChannelCommand;
import xyz.finlaym.adminbot.action.message.command.commands.admin.AddSwearCommand;
import xyz.finlaym.adminbot.action.message.command.commands.admin.ReloadCommand;
import xyz.finlaym.adminbot.action.message.command.commands.admin.RemoveReservationCommand;
import xyz.finlaym.adminbot.action.message.command.commands.admin.RolesMenuCommand;
import xyz.finlaym.adminbot.action.message.command.commands.admin.permissions.AddPermissionCommand;
import xyz.finlaym.adminbot.action.message.command.commands.admin.permissions.ModifyPermissionsRawCommand;
import xyz.finlaym.adminbot.action.message.command.commands.admin.permissions.RemovePermissionCommand;
import xyz.finlaym.adminbot.action.message.command.commands.debug.GuildInfoCommand;
import xyz.finlaym.adminbot.storage.config.PermissionsConfig;
import xyz.finlaym.adminbot.utils.LoggerHelper;

public class CommandHandler {
	
	private static final Logger logger = LoggerFactory.getLogger(CommandHandler.class);
	
	private Bot bot;
	private List<Command> commands;

	public CommandHandler(Bot bot) {
		this.bot = bot;
		this.commands = new ArrayList<Command>();
		
		this.commands.add(new HelpCommand());
		this.commands.add(new GuildInfoCommand());
		this.commands.add(new AddSwearCommand());
		this.commands.add(new ReloadCommand());
		this.commands.add(new RolesMenuCommand());
		this.commands.add(new ReserveChannelCommand());	
		this.commands.add(new AddPermissionCommand());
		this.commands.add(new RemovePermissionCommand());
		this.commands.add(new RemoveReservationCommand());
		this.commands.add(new ModifyPermissionsRawCommand());
	}
	public Bot getBot() {
		return bot;
	}
	public List<Command> getCommands() {
		return commands;
	}
	public void handleCommand(Member member, TextChannel channel, String[] command, Message message) throws Exception {
		if(command.length == 0)
			return;
		PermissionsConfig pConfig = bot.getPermissionsConfig();
		for(Command c : commands) {
			if(command[0].equalsIgnoreCase(c.getName())) {
				if(pConfig.checkPermission(channel.getGuild(), member, c.getPermission())) {
					LoggerHelper.log(logger, channel.getGuild(), channel, member.getUser(), "successfully executed command \""+message.getContentRaw()+"\" in channel \""+channel.getName()+"\"");
					c.execute(member, channel, command, this, message);
					return;
				}else {
					LoggerHelper.log(logger, channel.getGuild(), channel, member.getUser(), "tried to execute command \""+message.getContentRaw()+"\" in channel \""+channel.getName()+"\"");
					channel.sendMessage("Error: Insufficient permissions to execute command!").queue();
					return;
				}
			}
		}
	}
}
