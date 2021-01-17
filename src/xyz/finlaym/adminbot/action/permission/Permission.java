package xyz.finlaym.adminbot.action.permission;

public class Permission {
	private String permission;

	public Permission(String permission) {
		this.permission = permission;
	}
	public String getPermission() {
		return permission;
	}
	public boolean checkPermission(String permission2) {
		String[] split1 = permission.split(".");
		String[] split2 = permission.split(".");
		if(split2.length < split1.length)
			return false;
		for(int i = 0; i < split1.length; i++) {
			if(split1[i].equals("*"))
				return true;
			if(!split1[i].equals(split2[i]))
				return false;
		}
		if(split2.length > split1.length)
			return false;
		return true;
	}
	@Override
	public String toString() {
		return permission;
	}
}
