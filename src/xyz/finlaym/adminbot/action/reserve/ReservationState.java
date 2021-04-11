package xyz.finlaym.adminbot.action.reserve;

public class ReservationState {
	/**
	 * The id of the guild
	 */
	private long gid;
	/**
	 * The id of the reserved voice channel
	 */
	private long vid;
	/**
	 * The id of the text channel where the command to reserve was sent
	 */
	private long cid;
	private PermissionState[] state;
	public ReservationState(long gid, long vid, long cid, PermissionState[] state) {
		this.gid = gid;
		this.vid = vid;
		this.cid = cid;
		this.state = state;
	}
	public long getGid() {
		return gid;
	}
	public long getVid() {
		return vid;
	}
	public long getCid() {
		return cid;
	}
	public PermissionState[] getState() {
		return state;
	}
}
