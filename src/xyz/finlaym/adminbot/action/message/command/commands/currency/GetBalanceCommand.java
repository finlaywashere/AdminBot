package xyz.finlaym.adminbot.action.message.command.commands.currency;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.dv8tion.jda.api.entities.Member;
import xyz.finlaym.adminbot.action.message.command.Command;
import xyz.finlaym.adminbot.action.message.command.CommandHandler;
import xyz.finlaym.adminbot.action.message.command.CommandInfo;
import xyz.finlaym.adminbot.action.message.command.CommandResponse;
import xyz.finlaym.adminbot.action.permission.PermissionDeclaration;
import xyz.finlaym.adminbot.storage.config.CurrencyConfig;
import xyz.finlaym.adminbot.storage.config.ServerConfig;

public class GetBalanceCommand extends Command{

	private static final Logger logger = LoggerFactory.getLogger(GetBalanceCommand.class);

	public GetBalanceCommand() {
		super("getbalance", "command.getbalance", "-getbalance [user tag]", "Gets a user's balance", ServerConfig.CURRENCY_FLAG, 
				new PermissionDeclaration("command.getbalance.others", "Allows a user to get another user's balance"));
	}

	@Override
	public CommandResponse execute(CommandInfo info) {
		CommandHandler handler = info.getHandler();
		CurrencyConfig cConfig = handler.getBot().getCurrencyConfig();
		ServerConfig sConfig = handler.getBot().getServerConfig();
		String currencySuffix = sConfig.getCurrencySuffix(info.getGid());
		if(info.getMemberMentions().size() > 0) {
			try {
				if(handler.getBot().getPermissionsConfig().checkPermission(info.getGuild(), info.getSender(), "command.getbalance.others")) {
					Member m = info.getMemberMentions().get(0);
					int balance = cConfig.getCurrency(info.getGid(), m.getIdLong());
					if(balance == 0) {
						cConfig.loadCurrency(info.getGid(),m.getIdLong());
						balance = cConfig.getCurrency(info.getGid(), m.getIdLong());
					}
					return new CommandResponse("User's balance is "+balance+currencySuffix+"!");
				}else {
					return new CommandResponse("Error: Insufficient permissions to view another user's balance",true);
				}
			} catch (Exception e) {
				logger.error("Failed to check permissions in get balance command", e);
				return new CommandResponse("Critical Error: Failed to check permissions!", true);
			}
		}
		int balance = cConfig.getCurrency(info.getGid(),info.getUid());
		if(balance == 0) {
			try {
				cConfig.loadCurrency(info.getGid(),info.getUid());
			} catch (Exception e) {
				logger.error("Failed to load currency from database in get balance command", e);
				return new CommandResponse("Critical Error: Failed to load currency from database!",true);
			}
			balance = cConfig.getCurrency(info.getGid(), info.getUid());
		}
		return new CommandResponse("Your balance is "+balance+currencySuffix+"!");
	}
}
