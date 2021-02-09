package xyz.finlaym.adminbot.action.permission;

import java.util.List;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;

public class RoleGroup extends Group {
	private List<Member> members;
	public RoleGroup(long roleId, Guild guild) {
		super(new GroupIdentifier(Group.TYPE_ROLE, roleId));
		Role r = guild.getRoleById(roleId);
		this.members = guild.getMembersWithRoles(r);
	}
	
	@Override
	public List<Member> getMembers() {
		return members;
	}

}
