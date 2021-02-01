package xyz.finlaym.adminbot.action.permission;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Permission {
	private static final Logger logger = LoggerFactory.getLogger(Permission.class);
	
	private String permission;

	public Permission(String permission) {
		this.permission = permission;
	}
	public String getPermission() {
		return permission;
	}
	public boolean checkPermission(String permission2) {
		String[] split1 = permission.split("\\.");
		String[] split2 = permission2.split("\\.");
		if(split2.length < split1.length) {
			logger.trace("Rejected permission for reason: permission2 length less than permission : "+permission+" : "+permission2);
			return false;
		}
		for(int i = 0; i < split1.length; i++) {
			if(split1[i].equals("*")) {
				logger.trace("Accepted permission for reason: permission contains * in checked field : "+permission+" : "+permission2);
				return true;
			}
			if(!split1[i].equals(split2[i])) {
				logger.trace("Rejected permission for reason: permission2 not equal to permission at index "+i+" : "+permission+" : "+permission2);
				return false;
			}
		}
		if(split2.length > split1.length) {
			logger.trace("Rejected permission for reason: permission2 length greater than permission : "+permission+" : "+permission2);
			return false;
		}
		logger.trace("Accepted permission for reason: all other checks passed : "+permission+" : "+permission2);
		return true;
	}
	@Override
	public String toString() {
		return permission;
	}
}
