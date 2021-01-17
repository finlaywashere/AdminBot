package xyz.finlaym.adminbot.storage.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import xyz.finlaym.adminbot.action.permission.Permission;
import xyz.finlaym.adminbot.storage.DBInterface;

public class PermissionsConfig {
	private DBInterface dbInterface;
	private Map<Long,Map<Long,List<Permission>>> userPerms;

	public PermissionsConfig(DBInterface dbInterface) {
		this.dbInterface = dbInterface;
		this.userPerms = new HashMap<Long,Map<Long,List<Permission>>>();
	}
	
	public List<Permission> getUserPerms(long gid, long id){
		Map<Long,List<Permission>> p2 = userPerms.get(gid);
		if(p2 == null)
			return null;
		return p2.get(id);
	}
	public void setUserPerms(long gid, long id, List<Permission> perms) {
		Map<Long,List<Permission>> p2 = userPerms.get(gid);
		if(p2 == null)
			p2 = new HashMap<Long,List<Permission>>();
		p2.put(id, perms);
		userPerms.put(gid, p2);
	}
	public void loadPermissions(long gid, long id) throws Exception{
		dbInterface.loadPermissions(gid, id,this);
	}
	public void savePermissions(long gid, long id) throws Exception{
		dbInterface.savePermissions(gid, id,this);
	}
	public boolean checkPermission(long gid, long user, String permission) {
		List<Permission> perms = getUserPerms(gid,user);
		if(perms.size() == 0)
			return false;
		for(Permission p : perms) {
			if(!p.checkPermission(permission))
				return false;
		}
		return true;
	}
}
