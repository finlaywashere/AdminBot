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
import xyz.finlaym.adminbot.action.permission.RoleGroup;
import xyz.finlaym.adminbot.storage.DBInterface;

public class PermissionsConfig {
	private DBInterface dbInterface;
	private Map<Long,Map<Long,List<Permission>>> userPerms;
	private Map<Long,Map<GroupIdentifier,List<Permission>>> groupPerms;

	public PermissionsConfig(DBInterface dbInterface) {
		this.dbInterface = dbInterface;
		this.userPerms = new HashMap<Long,Map<Long,List<Permission>>>();
	}
	public List<Permission> getGroupPerms(long gid, GroupIdentifier group) throws Exception{
		Map<GroupIdentifier,List<Permission>> p2 = groupPerms.get(gid);
		if(p2 == null) {
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
	
	public List<Permission> getUserPerms(long gid, long id) throws Exception{
		Map<Long,List<Permission>> p2 = userPerms.get(gid);
		if(p2 == null) {
			loadUserPermissions(gid, id);
			p2 = userPerms.get(gid);
			if(p2 == null)
				return null;
		}
		return p2.get(id);
	}
	public void setUserPerms(long gid, long id, List<Permission> perms) {
		Map<Long,List<Permission>> p2 = userPerms.get(gid);
		if(p2 == null)
			p2 = new HashMap<Long,List<Permission>>();
		p2.put(id, perms);
		userPerms.put(gid, p2);
	}
	public void addUserPermission(long gid, long id, Permission perm) {
		Map<Long,List<Permission>> p2 = userPerms.get(gid);
		if(p2 == null)
			p2 = new HashMap<Long,List<Permission>>();
		List<Permission> perms = p2.get(id);
		if(perms == null)
			perms = new ArrayList<Permission>();
		perms.add(perm);
		p2.put(id, perms);
		userPerms.put(gid, p2);
	}
	public void removeUserPermission(long gid, long id, Permission perm) {
		Map<Long,List<Permission>> p2 = userPerms.get(gid);
		if(p2 == null)
			return;
		List<Permission> perms = p2.get(id);
		if(perms == null)
			return;
		perms.remove(perm);
		p2.put(id, perms);
		userPerms.put(gid, p2);
	}
	public void loadGroupPermissions(long gid, GroupIdentifier g) throws Exception{
		dbInterface.loadGroupPermissions(gid, g,this);
	}
	public void saveGroupPermissions(long gid, GroupIdentifier g) throws Exception{
		dbInterface.saveGroupPermissions(gid, g,this);
	}
	public void loadUserPermissions(long gid, long id) throws Exception{
		dbInterface.loadUserPermissions(gid, id,this);
	}
	public void saveUserPermissions(long gid, long id) throws Exception{
		dbInterface.saveUserPermissions(gid, id,this);
	}
	public boolean hasAdmin(Member m) {
		for(Role r : m.getRoles()) {
			if(r.hasPermission(net.dv8tion.jda.api.Permission.ADMINISTRATOR) || m.isOwner())
				return true;
		}
		return false;
	}
	public boolean checkPermission(Guild guild, Member m, String permission) throws Exception {
		return checkPermission(guild, m.getIdLong(), permission, hasAdmin(m));
	}
	public boolean checkPermission(Guild guild, long user, String permission, boolean admin) throws Exception {
		return checkPermission(guild, user, permission) | admin;
	}
	public boolean checkPermission(Guild guild, long user, String permission) throws Exception {
		List<Permission> perms = getUserPerms(guild.getIdLong(),user);
		Map<GroupIdentifier, List<Permission>> permsMap = groupPerms.get(guild.getIdLong());
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
				if(m.getIdLong() == user) {
					perms.addAll(permsMap.get(identifier));
					break;
				}
			}
		}
		if(perms == null || perms.size() == 0)
			return false;
		for(Permission p : perms) {
			if(p.checkPermission(permission))
				return true;
		}
		return false;
	}
}
