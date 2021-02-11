package xyz.finlaym.adminbot.action.permission;

public class PermissionDeclaration {
	private String permission;
	private String description;
	
	public PermissionDeclaration(String permission, String description) {
		this.permission = permission;
		this.description = description;
	}
	public String getPermission() {
		return permission;
	}
	public String getDescription() {
		return description;
	}
	@Override
	public String toString() {
		return "\""+permission+"\" : "+description;
	}
}
