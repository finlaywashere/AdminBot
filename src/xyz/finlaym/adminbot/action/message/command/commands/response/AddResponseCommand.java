package xyz.finlaym.adminbot.action.message.command.commands.response;

import java.util.ArrayList;
import java.util.List;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import xyz.finlaym.adminbot.action.message.command.Command;
import xyz.finlaym.adminbot.action.message.command.CommandHandler;
import xyz.finlaym.adminbot.action.message.response.CustomResponse;

public class AddResponseCommand extends Command {

	public AddResponseCommand() {
		super("addresponse", "command.addresponse", "-addresponse <trigger regex> <response>", "Sends a response every time a message matches a pattern, can substiture $u and $c for user mention and channel mention");
	}
	
	@Override
	public void execute(Member member, TextChannel channel, String[] command, CommandHandler handler, Message message, boolean silence) {
		if(command.length < 3) {
			channel.sendMessage(usage).queue();
			return;
		}
		String trigger = command[1].replaceAll("_", " ");
		String response = "";
		for(int i = 2; i < command.length; i++) {
			response += command[i] + " ";
		}
		List<CustomResponse> currResponses = handler.getBot().getServerConfig().getResponses(channel.getGuild().getIdLong());
		if(currResponses == null)
			currResponses = new ArrayList<CustomResponse>();
		currResponses.add(CustomResponse.fromStringSingle(response.replaceAll(",", "&comma")+","+trigger));
		handler.getBot().getServerConfig().setResponses(channel.getGuild().getIdLong(), currResponses);
		try {
			handler.getBot().getServerConfig().saveConfig(channel.getGuild().getIdLong());
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(!silence)
			channel.sendMessage("Successfully added custom response to database!").queue();
		if(silence)
			message.delete().queue();
	}
}
