package xyz.finlaym.adminbot.action.reserve;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.PermissionOverride;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.managers.ChannelManager;
import xyz.finlaym.adminbot.storage.config.ReservationConfig;

public class ReservationManager extends ListenerAdapter{
	
	private static final Logger logger = LoggerFactory.getLogger(ReservationManager.class);
	
	private ReservationConfig rConfig;
	public ReservationManager(ReservationConfig rConfig) {
		this.rConfig = rConfig;
	}
	
	@Override
	public void onGuildVoiceLeave(GuildVoiceLeaveEvent event) {
		VoiceChannel vc = event.getChannelLeft();
		if(vc.getMembers().size() == 0) {
			// Everyone left the VC
			try {
				removeReservation(vc);
			} catch (Exception e) {
				logger.error("Error in removing reservation", e);
			}
		}
	}
	public void removeReservation(VoiceChannel vc) throws Exception {
		ReservationState state = rConfig.getReservation(vc.getGuild().getIdLong(), vc.getIdLong());
		if(state == null)
			return;
		
		ChannelManager manager = vc.getManager();
			
		for(PermissionOverride p : vc.getPermissionOverrides()) {
			manager.removePermissionOverride(p.getPermissionHolder());
		}
		
		for(PermissionState perm : state.getState()){
			manager.putPermissionOverride(perm.getHolder(), perm.getAllowed(), perm.getDenied());
		}
		manager.queue();
		rConfig.removeReservation(vc.getGuild().getIdLong(), vc.getIdLong());
	}
	public void addReservation(VoiceChannel vc, List<Member> users, TextChannel channel) throws Exception {
		ReservationState state = rConfig.getReservation(vc.getGuild().getIdLong(), vc.getIdLong());
		if(state != null)
			return;
		
		List<PermissionOverride> overrides = vc.getPermissionOverrides();
		PermissionState[] statesL = new PermissionState[overrides.size()];
		for(int i = 0; i < overrides.size(); i++) {
			PermissionOverride o = overrides.get(i);
			statesL[i] = new PermissionState(o.getAllowed(), o.getDenied(), o.getPermissionHolder());
		}
		state = new ReservationState(vc.getGuild().getIdLong(), vc.getIdLong(), channel.getIdLong(), statesL);
		rConfig.addReservation(state);
		ChannelManager manager = vc.getManager();
		for(PermissionOverride p : vc.getPermissionOverrides()) {
			manager.removePermissionOverride(p.getPermissionHolder());
		}
		
		ArrayList<Permission> denyPerms = new ArrayList<Permission>();
		denyPerms.add(Permission.VOICE_CONNECT);
		denyPerms.add(Permission.VOICE_SPEAK);
		manager.putPermissionOverride(vc.getGuild().getPublicRole(), new ArrayList<Permission>(), denyPerms);
		ArrayList<Permission> perms = new ArrayList<Permission>();
		perms.add(Permission.VOICE_CONNECT);
		perms.add(Permission.VOICE_SPEAK);
		for(Member u : users) {
			manager.putPermissionOverride(u, perms, new ArrayList<Permission>());
		}
		manager.queue();
	}
}
