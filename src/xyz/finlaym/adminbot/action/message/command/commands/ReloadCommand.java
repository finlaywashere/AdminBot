package xyz.finlaym.adminbot.action.message.command.commands;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import xyz.finlaym.adminbot.action.message.command.Command;
import xyz.finlaym.adminbot.action.message.command.CommandHandler;
import xyz.finlaym.adminbot.storage.config.SwearsConfig;

public class ReloadCommand extends Command{

	public ReloadCommand() {
		super("reload", "command.reload", "-reload", "Reloads the configuration for a guild");
		
	}

	@Override
	public void execute(Member member, TextChannel channel, String[] command, CommandHandler handler, Message message) {
		SwearsConfig sConfig = handler.getBot().getSwearsConfig();
		try {
			sConfig.loadSwears(channel.getGuild().getIdLong());
		} catch (Exception e) {
			e.printStackTrace();
			channel.sendMessage("Failed to reload swear word list").queue();
			return;
		}
		channel.sendMessage("Reloaded swear words!").queue();
	}

}
