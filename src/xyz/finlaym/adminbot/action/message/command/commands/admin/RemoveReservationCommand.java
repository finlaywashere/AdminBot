package xyz.finlaym.adminbot.action.message.command.commands.admin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import xyz.finlaym.adminbot.action.message.command.Command;
import xyz.finlaym.adminbot.action.message.command.CommandHandler;
import xyz.finlaym.adminbot.utils.MathUtils;

public class RemoveReservationCommand extends Command{

	private static final Logger logger = LoggerFactory.getLogger(RemoveReservationCommand.class);
	
	public RemoveReservationCommand() {
		super("unreserve", "command.unreserve", "-unreserve [id]", "Removes a reservation on either the channel you are in or a channel from its ID");
	}

	@Override
	public void execute(Member member, TextChannel channel, String[] command, CommandHandler handler, Message message, boolean silence) {
		VoiceChannel vc;
		if(command.length > 1) {
			if(!MathUtils.isLong(command[1])) {
				channel.sendMessage("Channel id must be a number!").queue();
				return;
			}
			vc = channel.getGuild().getVoiceChannelById(Long.valueOf(command[1]));
		}else {
			GuildVoiceState voiceState = member.getVoiceState();
			if(voiceState.getChannel() == null) {
				channel.sendMessage("You must be in a voice channel to un reserve it, alternatively specify a channels ID!").queue();
				return;
			}
			vc = member.getVoiceState().getChannel();
		}
		try {
			handler.getBot().getReservationManager().removeReservation(vc);
		}catch(Exception e) {
			logger.error("Error in removing reservation command",e);
			channel.sendMessage("Critical error occurred while attempting to remove reservation!").queue();
			return;
		}
		if(!silence)
			channel.sendMessage("Successfully removed a reservation on channel \""+vc.getName()+"\"!").queue();
		if(silence)
			message.delete().queue();
	}
}
