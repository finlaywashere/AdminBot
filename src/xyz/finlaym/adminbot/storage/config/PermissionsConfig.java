package xyz.finlaym.adminbot.storage.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import xyz.finlaym.adminbot.action.permission.Permission;
import xyz.finlaym.adminbot.storage.DBInterface;

public class PermissionsConfig {
	private DBInterface dbInterface;
	private Map<Long,Map<Long,List<Permission>>> userPerms;

	public PermissionsConfig(DBInterface dbInterface) {
		this.dbInterface = dbInterface;
		this.userPerms = new HashMap<Long,Map<Long,List<Permission>>>();
	}
	
	public List<Permission> getUserPerms(long gid, long id) throws Exception{
		Map<Long,List<Permission>> p2 = userPerms.get(gid);
		if(p2 == null) {
			loadPermissions(gid, id);
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
	public void addPermission(long gid, long id, Permission perm) {
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
	public void removePermission(long gid, long id, Permission perm) {
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
	public void loadPermissions(long gid, long id) throws Exception{
		dbInterface.loadPermissions(gid, id,this);
	}
	public void savePermissions(long gid, long id) throws Exception{
		dbInterface.savePermissions(gid, id,this);
	}
	public boolean hasAdmin(Member m) {
		for(Role r : m.getRoles()) {
			if(r.hasPermission(net.dv8tion.jda.api.Permission.ADMINISTRATOR) || m.isOwner())
				return true;
		}
		return false;
	}
	public boolean checkPermission(long gid, Member m, String permission) throws Exception {
		return checkPermission(gid, m.getIdLong(), permission, hasAdmin(m));
	}
	public boolean checkPermission(long gid, long user, String permission, boolean admin) throws Exception {
		return checkPermission(gid, user, permission) | admin;
	}
	public boolean checkPermission(long gid, long user, String permission) throws Exception {
		if(user != 0) {
			if(checkPermission(gid, 0, permission)) // Check default permissions for guild
				return true;
		}
		List<Permission> perms = getUserPerms(gid,user);
		if(perms == null || perms.size() == 0)
			return false;
		for(Permission p : perms) {
			if(p.checkPermission(permission))
				return true;
		}
		return false;
	}
}
