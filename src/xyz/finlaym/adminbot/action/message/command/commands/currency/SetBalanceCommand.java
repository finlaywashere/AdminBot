package xyz.finlaym.adminbot.action.message.command.commands.currency;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import xyz.finlaym.adminbot.action.message.command.Command;
import xyz.finlaym.adminbot.action.message.command.CommandHandler;
import xyz.finlaym.adminbot.storage.config.CurrencyConfig;
import xyz.finlaym.adminbot.storage.config.ServerConfig;
import xyz.finlaym.adminbot.utils.MathUtils;

public class SetBalanceCommand extends Command{

	public SetBalanceCommand() {
		super("setbalance", "command.setbalance", "-setbalance <user tag> <amount>", "Sets a user's balance", ServerConfig.CURRENCY_FLAG);
	}

	@Override
	public void execute(Member member, TextChannel channel, String[] command, CommandHandler handler, Message message, boolean silence) {
		if(message.getMentionedUsers().size() == 0 || command.length < 3) {
			channel.sendMessage("Usage: "+usage).queue();
			return;
		}
		CurrencyConfig cConfig = handler.getBot().getCurrencyConfig();
		ServerConfig sConfig = handler.getBot().getServerConfig();
		if(!MathUtils.isInt(command[2])) {
			channel.sendMessage("User's balance must be an integer!").queue();
			return;
		}
		int balance = Integer.valueOf(command[2]);
		long gid = channel.getGuild().getIdLong();
		long id = message.getMentionedUsers().get(0).getIdLong();
		cConfig.setCurrency(gid, id, balance);
		try {
			cConfig.saveCurrency(gid, id);
		} catch (Exception e) {
			e.printStackTrace();
			channel.sendMessage("Critical Error: Failed to save balance to database!").queue();
			return;
		}
		if(!silence) {
			String suffix = sConfig.getCurrencySuffix(gid);
			channel.sendMessage("Successfully set user's balance to "+balance+suffix+"!").queue();
		}
		if(silence)
			message.delete().queue();
	}
}
