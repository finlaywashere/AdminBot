package xyz.finlaym.adminbot.action.reserve;

import java.util.EnumSet;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.IPermissionHolder;

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
	
}
