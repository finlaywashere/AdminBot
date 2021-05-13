package xyz.finlaym.adminbot.action.message.command.commands;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.VoiceChannel;
import xyz.finlaym.adminbot.action.message.command.Command;
import xyz.finlaym.adminbot.action.message.command.CommandHandler;
import xyz.finlaym.adminbot.action.message.command.CommandInfo;
import xyz.finlaym.adminbot.action.message.command.CommandResponse;

public class ReserveChannelCommand extends Command{
	
	private static final Logger logger = LoggerFactory.getLogger(ReserveChannelCommand.class);

	public ReserveChannelCommand() {
		super("reserve", "command.reserve", "-reserve <@users...>", "Makes the current voice channel only accessible by a set of users");
	}

	@Override
	public CommandResponse execute(CommandInfo info) {
		CommandHandler handler = info.getHandler();
		GuildVoiceState voiceState = info.getSender().getVoiceState();
		if(voiceState.getChannel() == null) {
			return new CommandResponse("You must be in a voice channel to reserve it!",true);
		}
		VoiceChannel vc = info.getSender().getVoiceState().getChannel();
		try {
			handler.getBot().getReservationManager().addReservation(vc,info.getMemberMentions(),info.getChannel());
		} catch (Exception e) {
			logger.error("Failed to add channel reservation in reserve channel command", e);
			return new CommandResponse("Critical error occurred while attempting to reserve channel!",true);
		}
		return new CommandResponse("Successfully reserved channel!");
	}
}
