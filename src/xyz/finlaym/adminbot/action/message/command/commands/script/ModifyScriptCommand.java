package xyz.finlaym.adminbot.action.message.command.commands.script;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xyz.finlaym.adminbot.action.message.command.Command;
import xyz.finlaym.adminbot.action.message.command.CommandInfo;
import xyz.finlaym.adminbot.action.message.command.CommandResponse;
import xyz.finlaym.adminbot.action.script.Script;
import xyz.finlaym.adminbot.storage.config.ScriptConfig;
import xyz.finlaym.adminbot.utils.MathUtils;

public class ModifyScriptCommand extends Command{

	private static final Logger logger = LoggerFactory.getLogger(ModifyScriptCommand.class);
	
	public ModifyScriptCommand() {
		super("modifyscript", "command.modifyscript", "-modifyscript <add,remove,list> <name> <command/index>", "Edits a script");	
	}
	@Override
	public CommandResponse execute(CommandInfo info) {
		if(info.getCommand().length < 3)
			return new CommandResponse("Usage: "+usage,true);
		String command = info.getCommand()[1];
		String name = info.getCommand()[2];
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
		int s = -1;
		for(int i = 0; i < scripts.size(); i++) {
			if(scripts.get(i).getName().equalsIgnoreCase(name)) {
				s = i;
				break;
			}
		}
		if(s == -1)
			return new CommandResponse("Invalid script name!",true);
		if(command.equalsIgnoreCase("add")) {
			String c = "";
			for(int i = 3; i < info.getCommand().length; i++) {
				c += " "+info.getCommand()[i];
			}
			if(c.length() > 0)
				c = c.substring(1);
			scripts.get(s).getCommands().add(c);
			sConfig.setScripts(info.getGid(), scripts);
			try {
				sConfig.saveConfig(info.getGid());
			} catch (Exception e) {
				logger.error("Failed to save script config", e);
				return new CommandResponse("Critical Error: Failed to save script configuration!",true);
			}
			return new CommandResponse("Successfully added command to script!");
		}else if(command.equalsIgnoreCase("remove")) {
			if(!MathUtils.isInt(info.getCommand()[3]))
				return new CommandResponse("Usage: "+usage,true);
			int index = Integer.valueOf(info.getCommand()[3]) - 1;
			if(index < 0 || index > scripts.get(s).getCommands().size())
				return new CommandResponse("Command index out of bounds!");
			scripts.get(s).getCommands().remove(index);
			sConfig.setScripts(info.getGid(), scripts);
			try {
				sConfig.saveConfig(info.getGid());
			} catch (Exception e) {
				logger.error("Failed to save script config", e);
				return new CommandResponse("Critical Error: Failed to save script configuration!",true);
			}
			return new CommandResponse("Successfully removed command from script!");
		}else if(command.equalsIgnoreCase("list")) {
			Script sc = scripts.get(s);
			String output = "```";
			for(int i = 0; i < sc.getCommands().size(); i++) {
				output += (i+1)+" "+sc.getCommands().get(i)+"\n";
			}
			if(sc.getCommands().size() == 0)
				output += "<empty>";
			output += "```";
			return new CommandResponse(output);
		}else {
			return new CommandResponse("Usage: "+usage,true);
		}
	}

}
