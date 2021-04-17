package xyz.finlaym.adminbot.action.message.command.commands;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import xyz.finlaym.adminbot.action.message.command.Command;
import xyz.finlaym.adminbot.action.message.command.CommandHandler;

public class ReserveChannelCommand extends Command{
	
	private static final Logger logger = LoggerFactory.getLogger(ReserveChannelCommand.class);

	public ReserveChannelCommand() {
		super("reserve", "command.reserve", "-reserve <@users...>", "Makes the current voice channel only accessible by a set of users");
	}

	@Override
	public void execute(Member member, TextChannel channel, String[] command, CommandHandler handler, Message message, boolean silence) {
		GuildVoiceState voiceState = member.getVoiceState();
		if(voiceState.getChannel() == null) {
			channel.sendMessage("You must be in a voice channel to reserve it!").queue();
			return;
		}
		VoiceChannel vc = member.getVoiceState().getChannel();
		try {
			handler.getBot().getReservationManager().addReservation(vc,message.getMentionedUsers(),channel);
		} catch (Exception e) {
			logger.error("Failed to add channel reservation in reserve channel command", e);
			channel.sendMessage("Critical error occurred while attempting to reserve channel!").queue();
			return;
		}
		if(!silence)
			channel.sendMessage("Successfully reserved channel!").queue();
		if(silence)
			message.delete().queue();
	}
}
