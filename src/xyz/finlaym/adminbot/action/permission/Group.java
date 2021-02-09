package xyz.finlaym.adminbot.action.permission;

import java.util.List;

import net.dv8tion.jda.api.entities.Member;

public abstract class Group {
	public static final int TYPE_ROLE = 1;
	public static final int TYPE_USER = 2;
	
	protected GroupIdentifier identifier;
	public Group(GroupIdentifier identifier) {
		this.identifier = identifier;
	}
	public GroupIdentifier getIdentifier() {
		return identifier;
	}

	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof Group))
			return false;
		Group g = (Group) obj;
		if(g.getIdentifier().getType() == identifier.getType() && g.getIdentifier().getIdentifier() == identifier.getIdentifier())
			return true;
		return false;
	}
	
	public abstract List<Member> getMembers();
}
