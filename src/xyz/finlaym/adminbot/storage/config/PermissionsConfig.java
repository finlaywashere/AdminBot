package xyz.finlaym.adminbot.storage.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import xyz.finlaym.adminbot.action.permission.Group;
import xyz.finlaym.adminbot.action.permission.GroupIdentifier;
import xyz.finlaym.adminbot.action.permission.Permission;
import xyz.finlaym.adminbot.storage.DBInterface;

public class PermissionsConfig {
	private DBInterface dbInterface;
	/**
	 * A map of permissions for all groups the bot manages
	 * Key: Guild id as long
	 * Value: Map of group identifiers to group permissions
	 */
	private Map<Long,Map<GroupIdentifier,List<Permission>>> groupPerms;

	public PermissionsConfig(DBInterface dbInterface) {
		this.dbInterface = dbInterface;
		this.groupPerms = new HashMap<Long,Map<GroupIdentifier,List<Permission>>>();
	}
	public List<Permission> getGroupPerms(long gid, GroupIdentifier group) throws Exception{
		Map<GroupIdentifier,List<Permission>> p2 = groupPerms.get(gid);
		if(p2 == null || !p2.containsKey(group)) {
			loadGroupPermissions(gid, group);
			p2 = groupPerms.get(gid);
			if(p2 == null)
				return null;
		}
		return p2.get(group);
	}
	public void setGroupPerms(long gid, GroupIdentifier group, List<Permission> perms) throws Exception{
		Map<GroupIdentifier,List<Permission>> p2 = groupPerms.get(gid);
		if(p2 == null)
			p2 = new HashMap<GroupIdentifier,List<Permission>>();
		p2.put(group, perms);
		groupPerms.put(gid, p2);
	}
	public void addGroupPermission(long gid, GroupIdentifier group, Permission perm) {
		Map<GroupIdentifier,List<Permission>> p2 = groupPerms.get(gid);
		if(p2 == null)
			p2 = new HashMap<GroupIdentifier,List<Permission>>();
		List<Permission> perms = p2.get(group);
		if(perms == null)
			perms = new ArrayList<Permission>();
		if(perms.contains(perm))
			return;
		perms.add(perm);
		p2.put(group, perms);
		groupPerms.put(gid, p2);
	}
	public void removeGroupPermission(long gid, GroupIdentifier group, Permission perm) {
		Map<GroupIdentifier,List<Permission>> p2 = groupPerms.get(gid);
		if(p2 == null)
			return;
		List<Permission> perms = p2.get(group);
		if(perms == null)
			return;
		perms.remove(perm);
		p2.put(group, perms);
		groupPerms.put(gid, p2);
	}
	
	public void loadGroupPermissions(long gid, GroupIdentifier g) throws Exception{
		dbInterface.loadGroupPermissions(gid, g,this);
	}
	public void saveGroupPermissions(long gid, GroupIdentifier g) throws Exception{
		dbInterface.saveGroupPermissions(gid, g,this);
	}
	public boolean hasAdmin(Member m) {
		List<Role> roles = m.getRoles();
		for(Role r : roles) {
			if(r.hasPermission(net.dv8tion.jda.api.Permission.ADMINISTRATOR) || m.isOwner())
				return true;
		}
		return false;
	}
	public boolean hasAdmin(Role r) {
		return r.hasPermission(net.dv8tion.jda.api.Permission.ADMINISTRATOR);
	}
	public List<Permission> getEffectivePermissions(Guild guild, Member user) throws Exception{
		List<Permission> perms = new ArrayList<Permission>();
		List<Permission> u = getGroupPerms(guild.getIdLong(),new GroupIdentifier(Group.TYPE_USER, user.getIdLong()));
		if(u != null)
			perms.addAll(u);
		List<Permission> everyone = getGroupPerms(guild.getIdLong(), new GroupIdentifier(Group.TYPE_ROLE, 0));
		if(everyone != null)
			perms.addAll(everyone);
		Map<GroupIdentifier, List<Permission>> permsMap = groupPerms.get(guild.getIdLong());
		for(Role r : user.getRoles()) {
			GroupIdentifier identifier = new GroupIdentifier(Group.TYPE_ROLE,r.getIdLong());
			if(permsMap == null || !permsMap.containsKey(identifier)) {
				loadGroupPermissions(guild.getIdLong(), identifier);
				permsMap = groupPerms.get(guild.getIdLong());
				if(permsMap == null || !permsMap.containsKey(identifier))
					continue;
			}
			List<Permission> gPerms = permsMap.get(identifier);
			if(gPerms == null)
				continue;
			perms.addAll(gPerms);
		}
		
		// This code is commented until support for role independent groups is added
		/*if(permsMap != null) {
			for(GroupIdentifier identifier : permsMap.keySet()) {
				Group g = null;
				switch(identifier.getType()) {
				case Group.TYPE_ROLE:
					g = new RoleGroup(identifier.getIdentifier(), guild);
				}
				if(g == null)
					continue;
				List<Member> members = g.getMembers();
				for(Member m : members) {
					if(m.getIdLong() == user.getIdLong()) {
						if(perms == null)
							perms = new ArrayList<Permission>();
						perms.addAll(permsMap.get(identifier));
						break;
					}
				}
			}
		}*/
		return perms;
	}
	
	public Map<Long, Map<GroupIdentifier, List<Permission>>> getGroupPerms() {
		return groupPerms;
	}
	public boolean checkPermission(Guild guild, Member user, String permission, boolean admin) throws Exception {
		return checkPermission(guild, user, permission) | admin;
	}
	public boolean checkPermission(Guild guild, Member user, String permission) throws Exception {
		if(hasAdmin(user))
			return true;
		List<Permission> perms = getEffectivePermissions(guild, user);
		if(perms == null || perms.size() == 0)
			return false;
		for(Permission p : perms) {
			if(p.checkPermission(permission))
				return true;
		}
		return false;
	}
}
