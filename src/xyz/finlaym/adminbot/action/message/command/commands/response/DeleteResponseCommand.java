package xyz.finlaym.adminbot.action.message.command.commands.response;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import xyz.finlaym.adminbot.action.message.command.Command;
import xyz.finlaym.adminbot.action.message.command.CommandHandler;
import xyz.finlaym.adminbot.storage.config.ServerConfig;
import xyz.finlaym.adminbot.utils.MathUtils;

public class DeleteResponseCommand extends Command{

	public DeleteResponseCommand() {
		super("deleteresponse","command.deleteresponse","-deleteresponse <id>","Deletes a trigger + response from the bot");
	}

	@Override
	public void execute(Member member, TextChannel channel, String[] command, CommandHandler handler, Message message, boolean silence) {
		if(command.length < 2) {
			channel.sendMessage("Usage: "+usage).queue();
			return;
		}
		if(!MathUtils.isInt(command[1])) {
			channel.sendMessage("Usage: "+usage).queue();
			return;
		}
		int id = Integer.valueOf(command[1])-1;
		long gid = channel.getGuild().getIdLong();
		ServerConfig sConfig = handler.getBot().getServerConfig();
		if(sConfig.getResponses(gid) == null) {
			try {
				sConfig.loadConfig(gid);
			} catch (Exception e) {
				e.printStackTrace();
				channel.sendMessage("Error: Failed to load database!").queue();
				return;
			}
			if(sConfig.getResponses(gid) == null) {
				channel.sendMessage("This guild has no custom responses!").queue();
				return;
			}
		}
		if(id < 0 || id >= sConfig.getResponses(gid).size()) {
			channel.sendMessage("Error: Id is less than 1 or greater than the number of responses!\nUse `-listresponses` to find an id to delete").queue();
			return;
		}
		sConfig.getResponses(gid).remove(id);
		try {
			sConfig.saveConfig(gid);
		} catch (Exception e) {
			e.printStackTrace();
			channel.sendMessage("Error: Failed to save changes to database!").queue();
			return;
		}
		if(!silence)
			channel.sendMessage("Successfully removed response!").queue();
		if(silence)
			message.delete().queue();
	}
}
