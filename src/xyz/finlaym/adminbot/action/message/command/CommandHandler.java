package xyz.finlaym.adminbot.action.message.command;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import xyz.finlaym.adminbot.Bot;
import xyz.finlaym.adminbot.action.alias.AliasTranslator;
import xyz.finlaym.adminbot.action.message.command.commands.EchoCommand;
import xyz.finlaym.adminbot.action.message.command.commands.HelpCommand;
import xyz.finlaym.adminbot.action.message.command.commands.ReserveChannelCommand;
import xyz.finlaym.adminbot.action.message.command.commands.admin.GetFlagsCommand;
import xyz.finlaym.adminbot.action.message.command.commands.admin.ReloadCommand;
import xyz.finlaym.adminbot.action.message.command.commands.admin.RemoveReservationCommand;
import xyz.finlaym.adminbot.action.message.command.commands.admin.SetFlagCommand;
import xyz.finlaym.adminbot.action.message.command.commands.admin.SetLoggingChannelCommand;
import xyz.finlaym.adminbot.action.message.command.commands.alias.DeleteAliasCommand;
import xyz.finlaym.adminbot.action.message.command.commands.alias.ListAliasesCommand;
import xyz.finlaym.adminbot.action.message.command.commands.alias.SetAliasCommand;
import xyz.finlaym.adminbot.action.message.command.commands.currency.GetBalanceCommand;
import xyz.finlaym.adminbot.action.message.command.commands.currency.SetBalanceCommand;
import xyz.finlaym.adminbot.action.message.command.commands.currency.SetCurrencySuffixCommand;
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
	private AliasTranslator aliasTranslator;

	public CommandHandler(Bot bot) {
		this.bot = bot;
		this.sessionHandler = new SessionHandler(this);
		this.aliasTranslator = new AliasTranslator(bot.getServerConfig());
		this.commands = new ArrayList<Command>();
		
		this.commands.add(new HelpCommand());
		this.commands.add(new DebugInfoCommand());
		this.commands.add(new AddSwearCommand());
		this.commands.add(new ReloadCommand());
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
		this.commands.add(new GetBalanceCommand());
		this.commands.add(new SetBalanceCommand());
		this.commands.add(new SetCurrencySuffixCommand());
		this.commands.add(new SetAliasCommand());
		this.commands.add(new ListAliasesCommand());
		this.commands.add(new DeleteAliasCommand());
	}
	public Bot getBot() {
		return bot;
	}
	public List<Command> getCommands() {
		return commands;
	}
	public void handleRestrictedCommand(User user, PrivateChannel channel, Message message) {
		//TODO: Implement restricted commands in private messages
	}
	public void handleCommand(Member member, TextChannel channel, Message message){
		long gid = channel.getGuild().getIdLong();
		String prefix = this.bot.getServerConfig().getPrefix(gid);
		if(!message.getContentRaw().startsWith(prefix))
			return;
		String[] commands = message.getContentRaw().substring(prefix.length()).trim().split("\\|");
		if(commands.length == 0)
			return;
		boolean silenced = false;
		if(commands[0].startsWith("!")) {
			commands[0] = commands[0].substring(1);
			silenced = true;
		}
		CommandResponse response = null;
		for(int i = 0; i < commands.length; i++) {
			commands[i] = commands[i].trim();
			if(response != null) {
				commands[i] += " "+response.getMessage();
			}
			String[] command = commands[i].split(" ");
			try {
				response = runCommand(member,channel,commands[i],command,message.getMentionedMembers(),message.getMentionedRoles(), message.getMentionedChannels(),message.mentionsEveryone());
				if(response == null) {
					if(i == 0)
						return; // Command not found
					else
						channel.sendMessage("Error: Command \""+commands[i]+"\" not found!").queue();
				}
				if(response.isFailure())
					break;
			}catch(Exception e) {
				channel.sendMessage("Error: Failed to execute command!").queue();
				logger.error("Failed to execute command!", e);
				return;
			}
		}
		boolean delete = false;
		if((silenced && response.isFailure()) || !silenced || response.isForce()) {
			if(silenced && response.isForce())
				delete = true;
			String[] newMessage = splitMessage(response.getMessage()); 
			for(String s : newMessage) {
				channel.sendMessage(s).queue();
			}
		}else {
			delete = true;
		}
		if(delete)
			message.delete().queue();
	}
	public String[] splitMessage(String message) {
		String[] messages = new String[message.length()/2000 + ((message.length()%2000) > 0 ? 1 : 0)];
		int curr = 0;
		for(String s : message.split("\n")) {
			if(messages[curr] == null) {
				messages[curr] = s;
				continue;
			}
			if(messages[curr].length() + s.length() > 2000) {
				curr++;
				messages[curr] = s;
				continue;
			}
			messages[curr] += "\n"+s;
		}
		return messages;
	}
	public CommandResponse runCommand(Member member, TextChannel channel, String message, String[] command, List<Member> mMentioned, List<Role> rMentioned, List<TextChannel> cMentioned, boolean mentionsEveryone) throws Exception{
		long gid = channel.getGuild().getIdLong();
		// Handle command before permission checks to ensure there is no way to bypass them
		// This replaces variables, etc in the command with environment variables and this could be used to circumvent permission checks
		// If it is run after the checks
		sessionHandler.handleCommand(member, channel, message, command);
		// Also handle aliases here so that they can't break permission checks
		command[0] = aliasTranslator.applyAliases(command[0],gid,channel);
		if(command[0] == null)
			return new CommandResponse("Error: Alias translation failed",true);
		PermissionsConfig pConfig = bot.getPermissionsConfig();
		for(Command c : commands) {
			if(command[0].equalsIgnoreCase(c.getName())) {
				if(pConfig.checkPermission(channel.getGuild(), member, c.getPermission())) {
					long requiredFlags = c.getRequiredFlags();
					if(requiredFlags == -1 || checkFlags(requiredFlags, bot.getServerConfig().getFlags(gid))) {
						LoggerHelper.log(logger, channel.getGuild(), bot.getServerConfig().getLoggingChannel(gid), member.getUser(), "successfully executed command \""+message+"\" in channel "+channel.getAsMention(), bot.getDBInterface());
						CommandInfo info = new CommandInfo(message, member, channel, command, mMentioned, rMentioned, cMentioned, mentionsEveryone, this);
						CommandResponse response = c.execute(info);
						return response;
					}else {
						LoggerHelper.log(logger, channel.getGuild(), bot.getServerConfig().getLoggingChannel(gid), member.getUser(), "tried to execute disabled command \""+message+"\" in channel "+channel.getAsMention(), bot.getDBInterface());
						return new CommandResponse("Error: That command is not enabled on this server!",true);
					}
				}else {
					LoggerHelper.log(logger, channel.getGuild(), bot.getServerConfig().getLoggingChannel(gid), member.getUser(), "tried to execute command with insufficient permissions\""+message+"\" in channel "+channel.getAsMention(), bot.getDBInterface());
					return new CommandResponse("Error: Insufficient permissions to execute command!",true);
				}
			}
		}
		return null; // Return null to indicate command not found
	}
	private boolean checkFlags(long actual, long expected) {
		long and = actual & expected;
		return and > 0;
	}
}
