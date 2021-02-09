package xyz.finlaym.adminbot.action.message.command.commands;

import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import xyz.finlaym.adminbot.action.message.command.Command;
import xyz.finlaym.adminbot.action.message.command.CommandHandler;

public class ReserveChannelCommand extends Command{
	
	public ReserveChannelCommand() {
		super("reserve", "command.reserve", "-reserve <@users...>", "Makes the current voice channel only accessible by a set of users");
	}

	@Override
	public void execute(Member member, TextChannel channel, String[] command, CommandHandler handler, Message message) {
		GuildVoiceState voiceState = member.getVoiceState();
		if(voiceState.getChannel() == null) {
			channel.sendMessage("You must be in a voice channel to reserve it!").queue();
			return;
		}
		VoiceChannel vc = member.getVoiceState().getChannel();
		handler.getBot().getReservationManager().addReservation(vc,message.getMentionedUsers());
		channel.sendMessage("Successfully reserved channel!").queue();
	}
}
