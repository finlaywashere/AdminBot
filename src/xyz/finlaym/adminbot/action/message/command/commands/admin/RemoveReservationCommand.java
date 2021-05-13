package xyz.finlaym.adminbot.action.message.command.commands.admin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.VoiceChannel;
import xyz.finlaym.adminbot.action.message.command.Command;
import xyz.finlaym.adminbot.action.message.command.CommandInfo;
import xyz.finlaym.adminbot.action.message.command.CommandResponse;
import xyz.finlaym.adminbot.utils.MathUtils;

public class RemoveReservationCommand extends Command{

	private static final Logger logger = LoggerFactory.getLogger(RemoveReservationCommand.class);
	
	public RemoveReservationCommand() {
		super("unreserve", "command.unreserve", "-unreserve [id]", "Removes a reservation on either the channel you are in or a channel from its ID");
	}

	@Override
	public CommandResponse execute(CommandInfo info) {
		VoiceChannel vc;
		String[] command = info.getCommand();
		if(command.length > 1) {
			if(!MathUtils.isLong(command[1])) {
				return new CommandResponse("Channel id must be a number!",true);
			}
			vc = info.getGuild().getVoiceChannelById(Long.valueOf(command[1]));
		}else {
			GuildVoiceState voiceState = info.getSender().getVoiceState();
			if(voiceState.getChannel() == null) {
				return new CommandResponse("You must be in a voice channel to un reserve it, alternatively specify a channels ID!",true);
			}
			vc = info.getSender().getVoiceState().getChannel();
		}
		try {
			info.getHandler().getBot().getReservationManager().removeReservation(vc);
		}catch(Exception e) {
			logger.error("Error in removing reservation command",e);
			return new CommandResponse("Critical error occurred while attempting to remove reservation!",true);
		}
		return new CommandResponse("Successfully removed a reservation on channel \""+vc.getName()+"\"!");
	}
}
