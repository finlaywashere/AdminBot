package xyz.finlaym.adminbot.action.reserve;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.PermissionOverride;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.managers.ChannelManager;

public class ReservationManager extends ListenerAdapter{
	
	private Map<Long,Map<Long,ChannelState>> states;
	
	public ReservationManager() {
		this.states = new HashMap<Long,Map<Long,ChannelState>>();
	}
	
	@Override
	public void onGuildVoiceLeave(GuildVoiceLeaveEvent event) {
		VoiceChannel vc = event.getChannelLeft();
		if(vc.getMembers().size() == 0) {
			// Everyone left the VC
			removeReservation(vc);
		}
	}
	public void removeReservation(VoiceChannel vc) {
		if(!states.containsKey(vc.getGuild().getIdLong()))
			return;
		Map<Long,ChannelState> gStates = states.get(vc.getGuild().getIdLong());
		long id = vc.getIdLong();
		if(!gStates.containsKey(id)) 
			return;
		ChannelManager manager = vc.getManager();
			
		for(PermissionOverride p : vc.getPermissionOverrides()) {
			manager.removePermissionOverride(p.getPermissionHolder());
		}
		
		for(PermissionState state : gStates.get(id).getPrevState()){
			manager.putPermissionOverride(state.getHolder(), state.getAllowed(), state.getDenied());
		}
		manager.queue();
		gStates.remove(id);
		states.put(id, gStates);
	}
	public void addReservation(VoiceChannel vc, List<User> users) {
		Map<Long,ChannelState> gStates = states.get(vc.getGuild().getIdLong());
		if(gStates != null && gStates.containsKey(vc.getIdLong()))
			return;
		if(gStates == null)
			gStates = new HashMap<Long,ChannelState>();
		List<PermissionOverride> overrides = vc.getPermissionOverrides();
		List<PermissionState> statesL = new ArrayList<PermissionState>();
		for(PermissionOverride o : overrides) {
			statesL.add(new PermissionState(o.getAllowed(), o.getDenied(), o.getPermissionHolder()));
		}
		gStates.put(vc.getIdLong(), new ChannelState(statesL));
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
		for(User u : users) {
			manager.putPermissionOverride(vc.getGuild().getMember(u), perms, new ArrayList<Permission>());
		}
		manager.queue();
		states.put(vc.getGuild().getIdLong(),gStates);
	}
	public Map<Long,Map<Long, ChannelState>> getStates() {
		return states;
	}
}
