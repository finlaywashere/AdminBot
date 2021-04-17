package xyz.finlaym.adminbot.action.message.command.commands.currency;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import xyz.finlaym.adminbot.action.message.command.Command;
import xyz.finlaym.adminbot.action.message.command.CommandHandler;
import xyz.finlaym.adminbot.action.permission.PermissionDeclaration;
import xyz.finlaym.adminbot.storage.config.CurrencyConfig;
import xyz.finlaym.adminbot.storage.config.ServerConfig;

public class GetBalanceCommand extends Command{

	public GetBalanceCommand() {
		super("getbalance", "command.getbalance", "-getbalance [user tag]", "Gets a user's balance", ServerConfig.CURRENCY_FLAG, 
				new PermissionDeclaration("command.getbalance.others", "Allows a user to get another user's balance"));
	}

	@Override
	public void execute(Member member, TextChannel channel, String[] command, CommandHandler handler, Message message, boolean silence) {
		CurrencyConfig cConfig = handler.getBot().getCurrencyConfig();
		ServerConfig sConfig = handler.getBot().getServerConfig();
		String currencySuffix = sConfig.getCurrencySuffix(channel.getGuild().getIdLong());
		long gid = channel.getGuild().getIdLong();
		if(message.getMentionedUsers().size() > 0) {
			try {
				if(handler.getBot().getPermissionsConfig().checkPermission(channel.getGuild(), member, "command.getbalance.others")) {
					User u = message.getMentionedUsers().get(0);
					int balance = cConfig.getCurrency(gid, u.getIdLong());
					if(balance == 0) {
						cConfig.loadCurrency(gid,u.getIdLong());
						balance = cConfig.getCurrency(gid, u.getIdLong());
					}
					channel.sendMessage("User's balance is "+balance+currencySuffix+"!").queue();
				}else {
					channel.sendMessage("Error: Insufficient permissions to view another user's balance").queue();
				}
			} catch (Exception e) {
				e.printStackTrace();
				channel.sendMessage("Critical Error: Failed to check permissions!").queue();
				return;
			}
		}
		int balance = cConfig.getCurrency(gid,member.getIdLong());
		if(balance == 0) {
			try {
				cConfig.loadCurrency(gid,member.getIdLong());
			} catch (Exception e) {
				e.printStackTrace();
				channel.sendMessage("Critical Error: Failed to load currency from database!").queue();
				return;
			}
			balance = cConfig.getCurrency(gid, member.getIdLong());
		}
		channel.sendMessage("Your balance is "+balance+currencySuffix+"!").queue();
		if(silence)
			message.delete().queue();
	}
}
