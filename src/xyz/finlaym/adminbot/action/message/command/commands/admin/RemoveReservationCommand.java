package xyz.finlaym.adminbot.action.message.command.commands.admin;

import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import xyz.finlaym.adminbot.action.message.command.Command;
import xyz.finlaym.adminbot.action.message.command.CommandHandler;

public class RemoveReservationCommand extends Command{

	public RemoveReservationCommand() {
		super("unreserve", "command.unreserve", "-unreserve [id]", "Removes a reservation on either the channel you are in or a channel from its ID");
	}

	@Override
	public void execute(Member member, TextChannel channel, String[] command, CommandHandler handler, Message message) {
		VoiceChannel vc;
		if(command.length > 1) {
			vc = channel.getGuild().getVoiceChannelById(Long.valueOf(command[1]));
		}else {
			GuildVoiceState voiceState = member.getVoiceState();
			if(voiceState.getChannel() == null) {
				channel.sendMessage("You must be in a voice channel to un reserve it, alternatively specify a channels ID!").queue();
				return;
			}
			vc = member.getVoiceState().getChannel();
		}
		handler.getBot().getReservationManager().removeReservation(vc);
		channel.sendMessage("Successfully removed a reservation on channel \""+vc.getName()+"\"!").queue();
	}
}
