package xyz.finlaym.adminbot.action.message.command.commands.alias;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xyz.finlaym.adminbot.action.alias.Alias;
import xyz.finlaym.adminbot.action.message.command.Command;
import xyz.finlaym.adminbot.action.message.command.CommandInfo;
import xyz.finlaym.adminbot.action.message.command.CommandResponse;
import xyz.finlaym.adminbot.storage.config.ServerConfig;

public class ListAliasesCommand extends Command{
	
	private static final Logger logger = LoggerFactory.getLogger(ListAliasesCommand.class);

	public ListAliasesCommand() {
		super("listaliases", "command.listaliases", "-listaliases", "Shows the server's aliases");
	}

	@Override
	public CommandResponse execute(CommandInfo info) {
		ServerConfig sConfig = info.getHandler().getBot().getServerConfig();
		List<Alias> aliases = sConfig.getAliases(info.getGid());
		if(aliases == null || aliases.size() == 0) {
			try {
				sConfig.loadConfig(info.getGid());
			} catch (Exception e) {
				logger.error("Failed to load server configuration for guild",e);
				return new CommandResponse("Critical database error: Failed to load server configuration from database!",true);
			}
			aliases = sConfig.getAliases(info.getGid());
		}
		String response = "Id\tOriginal\tAlias\n\n";
		for(int i = 0; i < aliases.size(); i++) {
			Alias a = aliases.get(i);
			response += (i + 1) + "\t" + a.getOriginalValue() + "\t" + a.getNewValue() + "\n";
		}
		return new CommandResponse(response,false,true);
	}

}
