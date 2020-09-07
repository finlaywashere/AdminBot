package xyz.finlaym.adminbot;

public class UserInfo {
	private long id;
	private int level;
	public UserInfo(long id, int level) {
		this.id = id;
		this.level = level;
	}
	public UserInfo(String s) {
		String[] split = s.split(",");
		this.id = Long.valueOf(split[0]);
		this.level = Integer.valueOf(split[1]);
	}
	@Override
	public String toString() {
		return id+","+level;
	}
	public long getId() {
		return id;
	}
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
}
