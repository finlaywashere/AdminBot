package xyz.finlaym.adminbot.action.reserve;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.IPermissionHolder;
import net.dv8tion.jda.api.entities.Member;

public class PermissionState {
	private EnumSet<Permission> allowed;
	private EnumSet<Permission> denied;
	private IPermissionHolder holder;
	public PermissionState(EnumSet<Permission> enumSet, EnumSet<Permission> enumSet2, IPermissionHolder holder) {
		this.allowed = enumSet;
		this.denied = enumSet2;
		this.holder = holder;
	}
	public EnumSet<Permission> getAllowed() {
		return allowed;
	}
	public EnumSet<Permission> getDenied() {
		return denied;
	}
	public IPermissionHolder getHolder() {
		return holder;
	}
	@Override
	public String toString() {
		String s = holder.getGuild().getIdLong()+":"+holder.getIdLong()+":"+(holder instanceof Member)+",";
		for(Permission p : allowed) {
			if(s.endsWith(",")) {
				s += p.toString();
			}else {
				s += ":"+p.toString();
			}
		}
		s += ",";
		for(Permission p : denied) {
			if(s.endsWith(",")) {
				s += p.toString();
			}else {
				s += ":"+p.toString();
			}
		}
		return s;
	}
	public static PermissionState fromString(String s, JDA jda) {
		String[] split = s.split(",",3);
		String[] s1 = split[0].split(":",3);
		long gid = Long.valueOf(s1[0]);
		long uid = Long.valueOf(s1[1]);
		boolean user = Boolean.valueOf(s1[2]);
		IPermissionHolder holder;
		Guild g = jda.getGuildById(gid);
		if(user) {
			holder = g.getMemberById(uid);
		}else {
			holder = g.getRoleById(uid);
		}
		String[] allowed = split[1].split(":");
		List<Permission> allowedP = new ArrayList<Permission>();
		for(int i = 0; i < allowed.length; i++) {
			if(allowed[i].length() > 0)
				allowedP.add(Permission.valueOf(allowed[i]));
		}
		EnumSet<Permission> allowedE = EnumSet.noneOf(Permission.class);
		allowedE.addAll(allowedP);
		String[] denied = split[2].split(":");
		List<Permission> deniedP = new ArrayList<Permission>();
		for(int i = 0; i < denied.length; i++) {
			if(denied[i].length() > 0)
				deniedP.add(Permission.valueOf(denied[i]));
		}
		EnumSet<Permission> deniedE = EnumSet.noneOf(Permission.class);
		deniedE.addAll(deniedP);
		return new PermissionState(allowedE, deniedE, holder);
	}
}
