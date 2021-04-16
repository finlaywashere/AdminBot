package xyz.finlaym.adminbot.action.message.command;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import xyz.finlaym.adminbot.Bot;
import xyz.finlaym.adminbot.action.message.command.commands.EchoCommand;
import xyz.finlaym.adminbot.action.message.command.commands.HelpCommand;
import xyz.finlaym.adminbot.action.message.command.commands.ReserveChannelCommand;
import xyz.finlaym.adminbot.action.message.command.commands.admin.GetFlagsCommand;
import xyz.finlaym.adminbot.action.message.command.commands.admin.ReloadCommand;
import xyz.finlaym.adminbot.action.message.command.commands.admin.RemoveReservationCommand;
import xyz.finlaym.adminbot.action.message.command.commands.admin.RolesMenuCommand;
import xyz.finlaym.adminbot.action.message.command.commands.admin.SetFlagCommand;
import xyz.finlaym.adminbot.action.message.command.commands.admin.SetLoggingChannelCommand;
import xyz.finlaym.adminbot.action.message.command.commands.debug.DebugInfoCommand;
import xyz.finlaym.adminbot.action.message.command.commands.permissions.AddPermissionCommand;
import xyz.finlaym.adminbot.action.message.command.commands.permissions.ListPermissionsCommand;
import xyz.finlaym.adminbot.action.message.command.commands.permissions.ModifyPermissionsRawCommand;
import xyz.finlaym.adminbot.action.message.command.commands.permissions.RemovePermissionCommand;
import xyz.finlaym.adminbot.action.message.command.commands.permissions.SearchPermissionCommand;
import xyz.finlaym.adminbot.action.message.command.commands.response.AddResponseCommand;
import xyz.finlaym.adminbot.action.message.command.commands.response.DeleteResponseCommand;
import xyz.finlaym.adminbot.action.message.command.commands.response.ListResponsesCommand;
import xyz.finlaym.adminbot.action.message.command.commands.session.DeleteSessionCommand;
import xyz.finlaym.adminbot.action.message.command.commands.session.SetSessionVariableCommand;
import xyz.finlaym.adminbot.action.message.command.commands.session.StartSessionCommand;
import xyz.finlaym.adminbot.action.message.command.commands.session.ViewHistoryCommand;
import xyz.finlaym.adminbot.action.message.command.commands.swear.AddSwearCommand;
import xyz.finlaym.adminbot.action.message.command.commands.swear.DeleteSwearCommand;
import xyz.finlaym.adminbot.action.message.command.commands.swear.ListSwearsCommand;
import xyz.finlaym.adminbot.storage.config.PermissionsConfig;
import xyz.finlaym.adminbot.utils.LoggerHelper;

public class CommandHandler {
	
	private static final Logger logger = LoggerFactory.getLogger(CommandHandler.class);
	
	private Bot bot;
	private List<Command> commands;
	private SessionHandler sessionHandler;

	public CommandHandler(Bot bot) {
		this.bot = bot;
		this.sessionHandler = new SessionHandler(this);
		this.commands = new ArrayList<Command>();
		
		this.commands.add(new HelpCommand());
		this.commands.add(new DebugInfoCommand());
		this.commands.add(new AddSwearCommand());
		this.commands.add(new ReloadCommand());
		this.commands.add(new RolesMenuCommand());
		this.commands.add(new ReserveChannelCommand());	
		this.commands.add(new AddPermissionCommand());
		this.commands.add(new RemovePermissionCommand());
		this.commands.add(new RemoveReservationCommand());
		this.commands.add(new ModifyPermissionsRawCommand());
		this.commands.add(new AddResponseCommand());
		this.commands.add(new ListPermissionsCommand());
		this.commands.add(new SearchPermissionCommand());
		this.commands.add(new StartSessionCommand());
		this.commands.add(new DeleteSessionCommand());
		this.commands.add(new ViewHistoryCommand());
		this.commands.add(new SetSessionVariableCommand());
		this.commands.add(new EchoCommand());
		this.commands.add(new ListResponsesCommand());
		this.commands.add(new DeleteResponseCommand());
		this.commands.add(new DeleteSwearCommand());
		this.commands.add(new ListSwearsCommand());
		this.commands.add(new SetFlagCommand());
		this.commands.add(new GetFlagsCommand());
		this.commands.add(new SetLoggingChannelCommand());
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
		boolean silenced = false;
		if(command[0].startsWith("!")) {
			command[0] = command[0].substring(1);
			silenced = true;
		}
		// Handle command before permission checks to ensure theres no way to bypass them
		sessionHandler.handleCommand(member, channel, message, command, silenced);
		PermissionsConfig pConfig = bot.getPermissionsConfig();
		for(Command c : commands) {
			if(command[0].equalsIgnoreCase(c.getName())) {
				if(pConfig.checkPermission(channel.getGuild(), member, c.getPermission())) {
					LoggerHelper.log(logger, channel.getGuild(), bot.getServerConfig().getLoggingChannel(channel.getGuild().getIdLong()), member.getUser(), "successfully executed command \""+message.getContentRaw()+"\" in channel \""+channel.getName()+"\"", bot.getDBInterface());
					c.execute(member, channel, command, this, message, silenced);
					return;
				}else {
					LoggerHelper.log(logger, channel.getGuild(), bot.getServerConfig().getLoggingChannel(channel.getGuild().getIdLong()), member.getUser(), "tried to execute command \""+message.getContentRaw()+"\" in channel \""+channel.getName()+"\"", bot.getDBInterface());
					channel.sendMessage("Error: Insufficient permissions to execute command!").queue();
					return;
				}
			}
		}
	}
}
