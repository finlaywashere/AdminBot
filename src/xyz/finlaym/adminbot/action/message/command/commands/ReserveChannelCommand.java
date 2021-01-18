package xyz.finlaym.adminbot.action.message.command.commands;

import java.util.ArrayList;
import java.util.List;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.PermissionOverride;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.managers.ChannelManager;
import xyz.finlaym.adminbot.action.message.command.Command;
import xyz.finlaym.adminbot.action.message.command.CommandHandler;
import xyz.finlaym.adminbot.action.reserve.ChannelState;
import xyz.finlaym.adminbot.action.reserve.PermissionState;

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
		List<PermissionOverride> overrides = vc.getPermissionOverrides();
		List<PermissionState> states = new ArrayList<PermissionState>();
		for(PermissionOverride o : overrides) {
			states.add(new PermissionState(o.getAllowed(), o.getDenied(), o.getPermissionHolder()));
		}
		handler.getBot().getReservationManager().getStates().put(vc.getIdLong(), new ChannelState(states));
		ChannelManager manager = vc.getManager();
		for(PermissionOverride p : vc.getPermissionOverrides()) {
			manager.removePermissionOverride(p.getPermissionHolder());
		}
		
		ArrayList<Permission> denyPerms = new ArrayList<Permission>();
		denyPerms.add(Permission.VOICE_CONNECT);
		denyPerms.add(Permission.VOICE_SPEAK);
		manager.putPermissionOverride(channel.getGuild().getPublicRole(), new ArrayList<Permission>(), denyPerms);
		ArrayList<Permission> perms = new ArrayList<Permission>();
		perms.add(Permission.VOICE_CONNECT);
		perms.add(Permission.VOICE_SPEAK);
		for(User u : message.getMentionedUsers()) {
			manager.putPermissionOverride(channel.getGuild().getMember(u), perms, new ArrayList<Permission>());
		}
		manager.queue();
		channel.sendMessage("Successfully reserved channel!").queue();
	}
}
