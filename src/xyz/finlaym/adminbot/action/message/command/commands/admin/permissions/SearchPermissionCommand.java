package xyz.finlaym.adminbot.action.message.command.commands.admin.permissions;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import xyz.finlaym.adminbot.action.message.command.Command;
import xyz.finlaym.adminbot.action.message.command.CommandHandler;
import xyz.finlaym.adminbot.action.permission.PermissionDeclaration;

public class SearchPermissionCommand extends Command{

	public SearchPermissionCommand() {
		super("searchpermission", "command.searchpermission", "-searchpermission [search]", "Searches for a certain permission node/all nodes if blank and displays information about it");
	}

	@Override
	public void execute(Member member, TextChannel channel, String[] command, CommandHandler handler, Message message, boolean silence) {
		String search = "";
		for(int i = 1; i < command.length; i++) {
			search += command[i]+" ";
		}
		search = search.trim().toLowerCase();
		
		String s = "Permission:\n\n";
		
		for(Command c : handler.getCommands()) {
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
		channel.sendMessage(s).queue();
		if(silence)
			message.delete().queue();
	}
}
