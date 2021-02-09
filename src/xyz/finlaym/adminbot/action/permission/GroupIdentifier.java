package xyz.finlaym.adminbot.action.permission;

public class GroupIdentifier {
	private int type;
	private long identifier;
	
	public GroupIdentifier(int type, long identifier) {
		this.type = type;
		this.identifier = identifier;
	}
	public int getType() {
		return type;
	}
	public long getIdentifier() {
		return identifier;
	}
}
