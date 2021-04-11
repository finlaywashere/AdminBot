package xyz.finlaym.adminbot.storage.config;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import xyz.finlaym.adminbot.Bot;
import xyz.finlaym.adminbot.action.reserve.PermissionState;
import xyz.finlaym.adminbot.action.reserve.ReservationManager;
import xyz.finlaym.adminbot.action.reserve.ReservationState;
import xyz.finlaym.adminbot.storage.DBInterface;
import xyz.finlaym.adminbot.utils.LoggerHelper;

public class ReservationConfig {
	
	private static final Logger logger = LoggerFactory.getLogger(ReservationConfig.class);
	
	private Map<Long,Map<Long,ReservationState>> reservations;
	private DBInterface dbInterface;
	private Bot bot;
	
	public ReservationConfig(DBInterface dbInterface, Bot bot) throws Exception {
		this.bot = bot;
		this.dbInterface = dbInterface;
		this.reservations = new HashMap<Long,Map<Long,ReservationState>>();
	}
	public void prune(ReservationManager rManager, JDA jda) throws Exception {
		dbInterface.getReservationConfig(this, bot); // Load old reservations
		for(long gid : reservations.keySet()) {
			for(long vid : reservations.get(gid).keySet()) {
				ReservationState state = reservations.get(gid).get(vid);
				VoiceChannel channel = jda.getVoiceChannelById(vid);
				rManager.removeReservation(channel);
				TextChannel textChannel = jda.getTextChannelById(state.getCid());
				textChannel.sendMessage("AdminBot experienced a service interruption while channel was reserved, unreserving \""+channel.getName()+"\"!").queue();
				reservations.get(gid).remove(vid);
				LoggerHelper.log(logger, "Successfully unreserved channel \""+channel.getName()+"\" and sent notice to \"#"+textChannel.getName()+"\" due to outage");
			}
			reservations.remove(gid);
		}
		dbInterface.deleteReservations();
	}
	
	public void addReservationDB(long gid, long vid, long uid, PermissionState[] state) {
		Map<Long,ReservationState> map = reservations.get(gid);
		if(map == null) {
			map = new HashMap<Long,ReservationState>();
		}
		map.put(vid, new ReservationState(gid, vid, uid, state));
		reservations.put(gid, map);
	}
	public void addReservation(ReservationState state) throws Exception {
		addReservationDB(state.getGid(), state.getVid(), state.getCid(), state.getState());
		dbInterface.addReservation(state);
	}
	public void removeReservation(long gid, long vid) throws Exception {
		Map<Long,ReservationState> map = reservations.get(gid);
		if(map == null)
			return;
		map.remove(vid);
		dbInterface.removeReservation(gid,vid);
	}
	public ReservationState getReservation(long gid, long vid) {
		Map<Long,ReservationState> map = reservations.get(gid);
		if(map == null)
			return null;
		return map.get(vid);
	}
}
