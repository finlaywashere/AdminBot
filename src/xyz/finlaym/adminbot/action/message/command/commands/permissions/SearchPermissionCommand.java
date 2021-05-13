package xyz.finlaym.adminbot.action.message.command.commands.permissions;

import xyz.finlaym.adminbot.action.message.command.Command;
import xyz.finlaym.adminbot.action.message.command.CommandInfo;
import xyz.finlaym.adminbot.action.message.command.CommandResponse;
import xyz.finlaym.adminbot.action.permission.PermissionDeclaration;

public class SearchPermissionCommand extends Command{

	public SearchPermissionCommand() {
		super("searchpermission", "command.searchpermission", "-searchpermission [search]", "Searches for a certain permission node/all nodes if blank and displays information about it");
	}

	@Override
	public CommandResponse execute(CommandInfo info) {
		String[] command = info.getCommand();
		String search = "";
		for(int i = 1; i < command.length; i++) {
			search += command[i]+" ";
		}
		search = search.trim().toLowerCase();
		
		String s = "Permission:\n\n";
		
		for(Command c : info.getHandler().getCommands()) {
			String s2 = "-"+c.getName()+" : "+c.getPermission()+" : "+c.getDescription()+"\n";
			int count = 0;
			for(PermissionDeclaration d : c.getEffectedPermissions()) {
				if(!d.getPermission().toLowerCase().contains(search) && !d.getDescription().toLowerCase().contains(search))
					continue;
				count++;
				s2 += "\t`"+d.getPermission()+" : "+d.getDescription()+"`\n";
			}
			if(count > 0 || search.isEmpty()) {
				s += s2;
			}
		}
		return new CommandResponse(s,false,true);
	}
}
