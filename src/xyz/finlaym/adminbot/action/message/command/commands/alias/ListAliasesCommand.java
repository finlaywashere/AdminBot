package xyz.finlaym.adminbot.action.message.command.commands.alias;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import xyz.finlaym.adminbot.action.alias.Alias;
import xyz.finlaym.adminbot.action.message.command.Command;
import xyz.finlaym.adminbot.action.message.command.CommandHandler;
import xyz.finlaym.adminbot.storage.config.ServerConfig;

public class ListAliasesCommand extends Command{
	
	private static final Logger logger = LoggerFactory.getLogger(ListAliasesCommand.class);

	public ListAliasesCommand() {
		super("listaliases", "command.listaliases", "-listaliases", "Shows the server's aliases");
	}

	@Override
	public void execute(Member member, TextChannel channel, String[] command, CommandHandler handler, Message message, boolean silence) {
		long gid = channel.getGuild().getIdLong();
		ServerConfig sConfig = handler.getBot().getServerConfig();
		List<Alias> aliases = sConfig.getAliases(gid);
		if(aliases == null || aliases.size() == 0) {
			try {
				sConfig.loadConfig(gid);
			} catch (Exception e) {
				logger.error("Failed to load server configuration for guild",e);
				channel.sendMessage("Critical database error: Failed to load server configuration from database!").queue();
				return;
			}
			aliases = sConfig.getAliases(gid);
		}
		String response = "Id\tOriginal\tAlias\n\n";
		for(int i = 0; i < aliases.size(); i++) {
			Alias a = aliases.get(i);
			response += (i + 1) + "\t" + a.getOriginalValue() + "\t" + a.getNewValue() + "\n";
		}
		channel.sendMessage(response).queue();
	}

}
