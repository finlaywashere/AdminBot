package xyz.finlaym.adminbot.action.message.command.commands.swear;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import xyz.finlaym.adminbot.action.message.command.Command;
import xyz.finlaym.adminbot.action.message.command.CommandHandler;
import xyz.finlaym.adminbot.action.message.swear.SwearWord;

public class ListSwearsCommand extends Command{

	private static final Logger logger = LoggerFactory.getLogger(ListSwearsCommand.class);
	
	public ListSwearsCommand() {
		super("listswears", "command.listswears", "-listswears", "Displays all of the blacklisted words on this guild");
	}

	@Override
	public void execute(Member member, TextChannel channel, String[] command, CommandHandler handler, Message message, boolean silence) {
		String s = "Id\t\tTrigger\t\tType\t\tRole";
		List<SwearWord> swears = handler.getBot().getSwearsConfig().getSwears(channel.getGuild().getIdLong());
		if(swears == null) {
			try {
				handler.getBot().getSwearsConfig().loadSwears(channel.getGuild().getIdLong());
			} catch (Exception e) {
				logger.error("Failed to load server config in list swears command", e);
				channel.sendMessage("Error loading swear words from database!").queue();
				return;
			}
			swears = handler.getBot().getSwearsConfig().getSwears(channel.getGuild().getIdLong());
			if(swears == null) {
				channel.sendMessage("This guild has no blacklisted words!").queue();
				return;
			}
		}
		for(int i = 0; i < swears.size(); i++) {
			SwearWord swear = swears.get(i);
			s += "\n"+(i+1)+"\t\t"+swear.getWord()+"\t\t"+swear.getType()+"\t\t"+swear.getMuteRole();
		}
		channel.sendMessage(s).queue();
		if(silence)
			message.delete().queue();
	}

}
