package xyz.finlaym.adminbot.action.message.command.commands.script;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xyz.finlaym.adminbot.action.message.command.Command;
import xyz.finlaym.adminbot.action.message.command.CommandInfo;
import xyz.finlaym.adminbot.action.message.command.CommandResponse;
import xyz.finlaym.adminbot.action.script.Script;
import xyz.finlaym.adminbot.storage.config.ScriptConfig;

public class AddScriptCommand extends Command{
	
	private static final Logger logger = LoggerFactory.getLogger(AddScriptCommand.class);

	public AddScriptCommand() {
		super("addscript", "command.addscript", "-addscript <name>", "Creates a new script");
	}

	@Override
	public CommandResponse execute(CommandInfo info) {
		if(info.getCommand().length != 2)
			return new CommandResponse("Usage: "+usage, true);
		String name = info.getCommand()[1];
		ScriptConfig sConfig = info.getHandler().getBot().getScriptConfig();
		List<Script> scripts = sConfig.getScripts(info.getGid());
		if(scripts == null) {
			try {
				sConfig.loadConfig(info.getGid());
			} catch (Exception e) {
				logger.error("Failed to load script config", e);
				return new CommandResponse("Critical Error: Failed to load script configuration!",true);
			}
			scripts = sConfig.getScripts(info.getGid());
		}
		if(scripts == null)
			scripts = new ArrayList<Script>();
		for(Script s : scripts) {
			if(s.getName().equalsIgnoreCase(name)) {
				return new CommandResponse("All scripts must have a unique name!",true);
			}
		}
		scripts.add(new Script("", name));
		sConfig.setScripts(info.getGid(), scripts);
		try {
			sConfig.saveConfig(info.getGid());
		} catch (Exception e) {
			logger.error("Failed to save script config", e);
			return new CommandResponse("Critical Error: Failed to save script configuration!",true);
		}
		return new CommandResponse("Successfully created script!");
	}
}
