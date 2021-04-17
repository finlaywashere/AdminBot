package xyz.finlaym.adminbot.action.message.command.commands.currency;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import xyz.finlaym.adminbot.action.message.command.Command;
import xyz.finlaym.adminbot.action.message.command.CommandHandler;
import xyz.finlaym.adminbot.storage.config.ServerConfig;

public class SetCurrencySuffix extends Command{

	public SetCurrencySuffix() {
		super("setcurrencysuffix", "command.setcurrencysuffix", "-setcurrencysuffix <$,etc>", "Sets what currency is used by this guild", ServerConfig.CURRENCY_FLAG);
	}

	@Override
	public void execute(Member member, TextChannel channel, String[] command, CommandHandler handler, Message message, boolean silence) {
		if(command.length < 2) {
			channel.sendMessage("Usage: "+usage).queue();
			return;
		}
		String suffix = "";
		for(int i = 1; i < command.length; i++) {
			if(i == 1) {
				suffix += command[i];
			}else {
				suffix += " "+command[i];
			}
		}
		ServerConfig sConfig = handler.getBot().getServerConfig();
		long gid = channel.getGuild().getIdLong();
		sConfig.setCurrencySuffix(gid, suffix);
		try {
			sConfig.saveConfig(gid);
		} catch (Exception e) {
			e.printStackTrace();
			channel.sendMessage("Critical Error: Failed to save currency suffix to database!").queue();
			return;
		}
		if(!silence)
			channel.sendMessage("Successfully set currency suffix to \""+suffix+"\"").queue();
		if(silence)
			message.delete().queue();
	}
}
