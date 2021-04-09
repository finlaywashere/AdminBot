package xyz.finlaym.adminbot.action.message.command.commands.response;

import java.util.List;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import xyz.finlaym.adminbot.action.message.command.Command;
import xyz.finlaym.adminbot.action.message.command.CommandHandler;
import xyz.finlaym.adminbot.action.message.response.CustomResponse;

public class ListResponsesCommand extends Command{

	public ListResponsesCommand() {
		super("listresponses", "command.listresponses", "-listresponses", "Lists all the bot responses in this guild");
	}

	@Override
	public void execute(Member member, TextChannel channel, String[] command, CommandHandler handler, Message message, boolean silence) {
		List<CustomResponse> responses = handler.getBot().getServerConfig().getResponses(channel.getGuild().getIdLong());
		if(responses == null) {
			try {
				handler.getBot().getServerConfig().loadConfig(channel.getGuild().getIdLong());
			} catch (Exception e) {
				e.printStackTrace();
				channel.sendMessage("Error: Failed to load database!").queue();
				return;
			}
			responses = handler.getBot().getServerConfig().getResponses(channel.getGuild().getIdLong());
			if(responses == null) {
				channel.sendMessage("This guild has no custom responses!").queue();
				return;
			}
		}
		String s = "Custom Responses:\nId\tTrigger\t\tResponse\n";
		for(int i = 0; i < responses.size(); i++) {
			CustomResponse r = responses.get(i);
			s += "\n"+(i+1)+"\t"+r.getTrigger()+"\t\t"+r.getResponse().replaceAll("&comma", ",");
		}
		channel.sendMessage(s).queue();
		if(silence)
			message.delete().queue();
	}
}
