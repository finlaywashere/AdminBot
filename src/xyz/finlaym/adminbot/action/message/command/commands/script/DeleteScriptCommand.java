package xyz.finlaym.adminbot.action.message.command.commands.script;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xyz.finlaym.adminbot.action.message.command.Command;
import xyz.finlaym.adminbot.action.message.command.CommandInfo;
import xyz.finlaym.adminbot.action.message.command.CommandResponse;
import xyz.finlaym.adminbot.action.script.Script;
import xyz.finlaym.adminbot.storage.config.ScriptConfig;

public class DeleteScriptCommand extends Command{

	private static final Logger logger = LoggerFactory.getLogger(DeleteScriptCommand.class);
	
	public DeleteScriptCommand() {
		super("deletescript", "command.deletescript", "-deletescript <name>", "Deletes a script permanently");
	}

	@Override
	public CommandResponse execute(CommandInfo info) {
		if(info.getCommand().length != 2)
			return new CommandResponse("Usage: "+usage,true);
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
		if(scripts == null || scripts.size() == 0)
			return new CommandResponse("Script not found!",true);
		
		String name = info.getCommand()[1];
		int index = -1;
		for(int i = 0; i < scripts.size(); i++) {
			Script s = scripts.get(i);
			if(s.getName().equalsIgnoreCase(name)) {
				index = i;
			}
		}
		if(index == -1)
			return new CommandResponse("Script not found!",true);
		scripts.remove(index);
		sConfig.setScripts(info.getGid(), scripts);
		try {
			sConfig.saveConfig(info.getGid());
		} catch (Exception e) {
			logger.error("Failed to save script config", e);
			return new CommandResponse("Critical Error: Failed to save script configuration!",true);
		}
		return new CommandResponse("Successfully deleted script!");
	}
}
