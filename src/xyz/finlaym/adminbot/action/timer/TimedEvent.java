package xyz.finlaym.adminbot.action.timer;

public class TimedEvent {
	private long uid;
	private long gid;
	private long runDate;
	private long repeatInterval;
	private long repeatCount;
	private long type;
	private String extra;
	public TimedEvent(long uid, long gid, long runDate, long repeatInterval, long repeatCount, long type, String extra) {
		this.uid = uid;
		this.gid = gid;
		this.runDate = runDate;
		this.repeatInterval = repeatInterval;
		this.repeatCount = repeatCount;
		this.type = type;
		this.extra = extra;
	}
	public long getUid() {
		return uid;
	}
	public void setUid(long uid) {
		this.uid = uid;
	}
	public long getGid() {
		return gid;
	}
	public void setGid(long gid) {
		this.gid = gid;
	}
	public long getRunDate() {
		return runDate;
	}
	public void setRunDate(long runDate) {
		this.runDate = runDate;
	}
	public long getRepeatInterval() {
		return repeatInterval;
	}
	public void setRepeatInterval(long repeatInterval) {
		this.repeatInterval = repeatInterval;
	}
	public long getRepeatCount() {
		return repeatCount;
	}
	public void setRepeatCount(long repeatCount) {
		this.repeatCount = repeatCount;
	}
	public long getType() {
		return type;
	}
	public void setType(long type) {
		this.type = type;
	}
	public String getExtra() {
		return extra;
	}
	public void setExtra(String extra) {
		this.extra = extra;
	}
}
