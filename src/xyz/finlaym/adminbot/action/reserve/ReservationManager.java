package xyz.finlaym.adminbot.action.reserve;

import java.util.HashMap;
import java.util.Map;

import net.dv8tion.jda.api.entities.PermissionOverride;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.managers.ChannelManager;

public class ReservationManager extends ListenerAdapter{
	
	private Map<Long,ChannelState> states;
	
	public ReservationManager() {
		this.states = new HashMap<Long,ChannelState>();
	}
	
	@Override
	public void onGuildVoiceLeave(GuildVoiceLeaveEvent event) {
		VoiceChannel vc = event.getChannelLeft();
		if(vc.getMembers().size() == 0) {
			// Everyone left the VC
			long id = vc.getIdLong();
			if(states.containsKey(id)) {
				ChannelManager manager = vc.getManager();
				
				for(PermissionOverride p : vc.getPermissionOverrides()) {
					manager.removePermissionOverride(p.getPermissionHolder());
				}
				
				for(PermissionState state : states.get(id).getPrevState()){
					manager.putPermissionOverride(state.getHolder(), state.getAllowed(), state.getDenied());
				}
				
				manager.queue();
				states.remove(id);
			}
		}
	}
	public Map<Long, ChannelState> getStates() {
		return states;
	}
}
