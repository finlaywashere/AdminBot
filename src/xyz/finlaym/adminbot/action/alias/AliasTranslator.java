package xyz.finlaym.adminbot.action.alias;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.dv8tion.jda.api.entities.TextChannel;
import xyz.finlaym.adminbot.storage.config.ServerConfig;

public class AliasTranslator {
	private static final Logger logger = LoggerFactory.getLogger(AliasTranslator.class);
	
	private ServerConfig sConfig;

	public AliasTranslator(ServerConfig sConfig) {
		this.sConfig = sConfig;
	}
	public String applyAliases(String input, long gid, TextChannel channel) {
		String result = input;
		int recursionCount = 0;
		List<Alias> aliases = sConfig.getAliases(gid);
		if(aliases == null || aliases.size() == 0) {
			try {
				sConfig.loadConfig(gid);
			} catch (Exception e) {
				logger.error("Failed to load server configuration for guild", e);
				channel.sendMessage("Critical database error: Failed to load server configuration!").queue();
				return null;
			}
			aliases = sConfig.getAliases(gid);
		}
		while(recursionCount < 20) {
			boolean changed = false;
			for(Alias a : aliases) {
				// If the alias' new value is equal to the input then set the result to the translation and then go around to the next recurse 
				if(result.equalsIgnoreCase(a.getNewValue())) {
					result = a.getOriginalValue();
					changed = true;
					break;
				}
			}
			// If nothing has changed then don't waste cpu cycles
			if(!changed)
				break;
			recursionCount++;
		}
		return result;
	}
}
