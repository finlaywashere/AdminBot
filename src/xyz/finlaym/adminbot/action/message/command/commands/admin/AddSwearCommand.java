package xyz.finlaym.adminbot.action.message.command.commands.admin;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import xyz.finlaym.adminbot.action.message.command.Command;
import xyz.finlaym.adminbot.action.message.command.CommandHandler;
import xyz.finlaym.adminbot.action.message.swear.SwearWord;
import xyz.finlaym.adminbot.storage.config.SwearsConfig;

public class AddSwearCommand extends Command {

	public AddSwearCommand() {
		super("addswear", "command.addswear", "-addswear <swear1> [swear2...]", "Adds a swear to a server's blacklist");
	}

	@Override
	public void execute(Member member, TextChannel channel, String[] command, CommandHandler handler, Message message) {
		Guild guild = channel.getGuild();
		SwearsConfig sConfig = handler.getBot().getSwearsConfig();
		for(int i = 1; i < command.length; i++) {
			String swear = command[i];
			try {
				sConfig.addSwear(SwearWord.fromString(swear.replaceAll("_", " ")),guild.getIdLong());
				sConfig.saveSwears(guild.getIdLong());
			}catch(Exception e) {
				e.printStackTrace();
				System.err.println("Failed to add swear word to file!");
			}
		}
		channel.sendMessage("Successfully added swears to DB!").queue();
	}
}
