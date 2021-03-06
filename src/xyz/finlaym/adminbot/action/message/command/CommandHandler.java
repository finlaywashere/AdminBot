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
import xyz.finlaym.adminbot.action.message.command.commands.helper.CheckArgumentsCommand;
import xyz.finlaym.adminbot.action.message.command.commands.helper.SetStateCommand;
import xyz.finlaym.adminbot.action.message.command.commands.permissions.AddPermissionCommand;
import xyz.finlaym.adminbot.action.message.command.commands.permissions.ListPermissionsCommand;
import xyz.finlaym.adminbot.action.message.command.commands.permissions.ModifyPermissionsRawCommand;
import xyz.finlaym.adminbot.action.message.command.commands.permissions.RemovePermissionCommand;
import xyz.finlaym.adminbot.action.message.command.commands.permissions.SearchPermissionCommand;
import xyz.finlaym.adminbot.action.message.command.commands.response.AddResponseCommand;
import xyz.finlaym.adminbot.action.message.command.commands.response.DeleteResponseCommand;
import xyz.finlaym.adminbot.action.message.command.commands.response.ListResponsesCommand;
import xyz.finlaym.adminbot.action.message.command.commands.role.AddRoleCommand;
import xyz.finlaym.adminbot.action.message.command.commands.role.ListRolesCommand;
import xyz.finlaym.adminbot.action.message.command.commands.role.RemoveRoleCommand;
import xyz.finlaym.adminbot.action.message.command.commands.script.AddScriptCommand;
import xyz.finlaym.adminbot.action.message.command.commands.script.DeleteScriptCommand;
import xyz.finlaym.adminbot.action.message.command.commands.script.ListScriptsCommand;
import xyz.finlaym.adminbot.action.message.command.commands.script.ModifyScriptCommand;
import xyz.finlaym.adminbot.action.message.command.commands.session.DeleteSessionCommand;
import xyz.finlaym.adminbot.action.message.command.commands.session.SetSessionVariableCommand;
import xyz.finlaym.adminbot.action.message.command.commands.session.StartSessionCommand;
import xyz.finlaym.adminbot.action.message.command.commands.session.ViewHistoryCommand;
import xyz.finlaym.adminbot.action.message.command.commands.swear.AddSwearCommand;
import xyz.finlaym.adminbot.action.message.command.commands.swear.DeleteSwearCommand;
import xyz.finlaym.adminbot.action.message.command.commands.swear.ListSwearsCommand;
import xyz.finlaym.adminbot.action.script.Script;
import xyz.finlaym.adminbot.storage.config.PermissionsConfig;
import xyz.finlaym.adminbot.storage.config.ScriptConfig;
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
		this.commands.add(new SetStateCommand());
		this.commands.add(new ListRolesCommand());
		this.commands.add(new AddRoleCommand());
		this.commands.add(new RemoveRoleCommand());
		this.commands.add(new ListScriptsCommand());
		this.commands.add(new AddScriptCommand());
		this.commands.add(new DeleteScriptCommand());
		this.commands.add(new ModifyScriptCommand());
		this.commands.add(new CheckArgumentsCommand());
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
		CommandResult delete = parseCommand(member, channel, message.getContentRaw(),message.getMentionedMembers(),message.getMentionedRoles(),message.getMentionedChannels(),message.mentionsEveryone());
		if(delete.getCode() == 1)
			message.delete().queue();
	}
	public CommandResult parseCommand(Member member, TextChannel channel, String message, List<Member> mMentioned, List<Role> rMentioned, List<TextChannel> cMentioned, boolean mentionsEveryone) {
		String prefix = this.bot.getServerConfig().getPrefix(channel.getGuild().getIdLong());
		if(!message.startsWith(prefix))
			return new CommandResult(2,null,null);
		String[] commands = message.substring(prefix.length()).trim().split("\\||\\&");
		if(commands.length == 0)
			return new CommandResult(2,null,null);
		boolean silenced = false;
		if(commands[0].startsWith("!")) {
			commands[0] = commands[0].substring(1);
			silenced = true;
		}
		CommandState state = new CommandState(channel, silenced);
		return parseCommand(member, channel, message, mMentioned, rMentioned, cMentioned, mentionsEveryone,state);
	}
	public CommandResult parseCommand(Member member, TextChannel channel, String message, List<Member> mMentioned, List<Role> rMentioned, List<TextChannel> cMentioned, boolean mentionsEveryone, CommandState state) {
		long gid = channel.getGuild().getIdLong();
		String prefix = this.bot.getServerConfig().getPrefix(gid);
		if(!message.startsWith(prefix))
			return new CommandResult(2,null,null);
		String[] commands = message.substring(prefix.length()).trim().split("\\||\\&");
		if(commands.length == 0)
			return new CommandResult(2,null,null);
		boolean silenced = false;
		if(commands[0].startsWith("!")) {
			commands[0] = commands[0].substring(1);
			silenced = true;
		}
		state.setSilenced(silenced);
		CommandResponse response = null;
		int charIndex = 0;
		for(int i = 0; i < commands.length; i++) {
			char character = message.charAt(charIndex);
			charIndex += commands[i].length() + 1;
			commands[i] = commands[i].trim();
			if(response != null && character == '|') {
				commands[i] += " "+response.getMessage();
			}
			String[] command = commands[i].trim().split(" ");
			try {
				response = runCommand(member,channel,commands[i],command,mMentioned,rMentioned, cMentioned,mentionsEveryone,state);
				if(response == null) {
					if(i == 0)
						return new CommandResult(2,state,response); // Command not found
					else {
						state.getOutputChannel().sendMessage("Error: Command \""+commands[i]+"\" not found!").queue();
						return new CommandResult(2,state,response);
					}
				}
				if(response.getState() != null) {
					state = response.getState();
				}
				if(i == commands.length-1 || message.charAt(charIndex) == '&') {
					if((state.isSilenced() && response.isFailure()) || !state.isSilenced() || response.isForce()) {
						String[] newMessage = splitMessage(response.getMessage()); 
						for(String s : newMessage) {
							state.getOutputChannel().sendMessage(s).queue();
						}
					}
				}
			}catch(Exception e) {
				state.getOutputChannel().sendMessage("Error: Failed to execute command!").queue();
				logger.error("Failed to execute command!", e);
				return new CommandResult(2,state,response);
			}
		}
		if(response.isFailure())
			return new CommandResult(2,state,response);
		boolean delete = false;
		if((silenced && response.isFailure()) || !silenced || response.isForce()) {
			if(silenced && response.isForce())
				delete = true;
		}else {
			delete = true;
		}
		if(delete)
			return new CommandResult(1,state,response);
		else
			return new CommandResult(0,state,response);
	}
	public String[] splitMessage(String message) {
		if(message.length() == 0)
			return new String[0];
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
	public CommandResponse runCommand(Member member, TextChannel channel, String message, String[] command, List<Member> mMentioned, List<Role> rMentioned, List<TextChannel> cMentioned, boolean mentionsEveryone, CommandState state) throws Exception{
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
						CommandInfo info = new CommandInfo(message, member, channel, command, mMentioned, rMentioned, cMentioned, mentionsEveryone, this, state);
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
		ScriptConfig sConfig = bot.getScriptConfig();
		List<Script> scripts = sConfig.getScripts(gid);
		if(scripts == null) {
			try {
				sConfig.loadConfig(gid);
			}catch(Exception e) {
				logger.error("Failed to load script config!",e);
				return new CommandResponse("Critical Error: Failed to load script config!",true);
			}
			scripts = sConfig.getScripts(gid);
		}
		String prefix = this.bot.getServerConfig().getPrefix(gid);
		if(scripts != null) {
			for(Script s : scripts) {
				if(s.getName().equalsIgnoreCase(command[0])) {
					if(pConfig.checkPermission(channel.getGuild(), member, "script."+s.getName())) {
						LoggerHelper.log(logger, channel.getGuild(), bot.getServerConfig().getLoggingChannel(gid), member.getUser(), "successfully executed script \""+message+"\" in channel "+channel.getAsMention(), bot.getDBInterface());
						for(int i = 0; i < s.getCommands().size(); i++) {
							String rawCommand = s.getCommands().get(i);
							String c = prefix+rawCommand;
							// Go in reverse order so that $11 doesn't act like $1(1)
							for(int i1 = command.length-1; i1 >= 1; i1--) {
								c = c.replaceAll("\\$"+i1, command[i1]);
							}
							CommandResult response;
							if(i == 0)
								response = parseCommand(member, channel, c, mMentioned, rMentioned, cMentioned, mentionsEveryone,state);
							else
								response = parseCommand(member, channel, c, new ArrayList<Member>(), new ArrayList<Role>(), new ArrayList<TextChannel>(), false,state);
							if(response.getCode() > 1) {
								String output2 = "";
								if(response.getState().isOutputScriptFailure()) {
									output2 = "Script error at command "+(i+1)+"!";
								}
								return new CommandResponse(output2,true);
							}
						}
						return new CommandResponse("",false);
					}else {
						LoggerHelper.log(logger, channel.getGuild(), bot.getServerConfig().getLoggingChannel(gid), member.getUser(), "tried to execute script with insufficient permissions\""+message+"\" in channel "+channel.getAsMention(), bot.getDBInterface());
						return new CommandResponse("Error: Insufficient permissions to execute script!",true);
					}
				}
			}
		}
		return new CommandResponse("Command not found!");
	}
	private boolean checkFlags(long actual, long expected) {
		long and = actual & expected;
		return and > 0;
	}
}
