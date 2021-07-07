package xyz.finlaym.adminbot.action.message.command.commands.script;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xyz.finlaym.adminbot.action.message.command.Command;
import xyz.finlaym.adminbot.action.message.command.CommandInfo;
import xyz.finlaym.adminbot.action.message.command.CommandResponse;
import xyz.finlaym.adminbot.action.script.Script;
import xyz.finlaym.adminbot.storage.config.ScriptConfig;

public class ListScriptsCommand extends Command{
	
	private static final Logger logger = LoggerFactory.getLogger(ListScriptsCommand.class);

	public ListScriptsCommand() {
		super("listscripts", "command.listscripts", "-listscripts", "Lists all of the scripts that you have access to");
	}

	@Override
	public CommandResponse execute(CommandInfo info) {
		ScriptConfig sConfig = info.getHandler().getBot().getScriptConfig();
		long gid = info.getGid();
		List<Script> scripts = sConfig.getScripts(gid);
		if(scripts == null) {
			try {
				sConfig.loadConfig(gid);
			} catch (Exception e) {
				logger.error("Failed to load script config", e);
				return new CommandResponse("Critical Error: Failed to load script configuration!",true);
			}
			scripts = sConfig.getScripts(gid);
		}
		if(scripts == null || scripts.size() == 0) {
			return new CommandResponse("```<None>```");
		}else {
			String output = "```";
			for(Script s : scripts) {
				output += "-"+s.getName()+"\n";
			}
			output += "```";
			return new CommandResponse(output);
		}
	}
}
