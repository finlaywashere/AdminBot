package xyz.finlaym.adminbot.storage.config;

import java.util.HashMap;
import java.util.Map;

import xyz.finlaym.adminbot.action.session.Session;

/**
 * Volatile storage for configuration sessions
 * @author finlay
 *
 */
public class SessionConfig {
	/**
	 * A map to store current configuration sessions
	 * Key: Guild id as long
	 * Value: Map of user id long and session for them
	 */
	private Map<Long,Map<Long,Session>> sessions;

	public SessionConfig() {
		this.sessions = new HashMap<Long,Map<Long,Session>>();
	}
	public Session getSession(long gid, long uid) {
		Map<Long,Session> s2 = sessions.get(gid);
		if(s2 == null)
			return null;
		return s2.get(uid);
	}
	public void setSession(long gid, long uid, Session session) {
		Map<Long,Session> s2 = sessions.get(gid);
		if(s2 == null)
			s2 = new HashMap<Long,Session>();
		
		s2.put(uid, session);
		sessions.put(gid, s2);
	}
}
