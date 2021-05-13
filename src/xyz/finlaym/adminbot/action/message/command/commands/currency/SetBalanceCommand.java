package xyz.finlaym.adminbot.action.message.command.commands.currency;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xyz.finlaym.adminbot.action.message.command.Command;
import xyz.finlaym.adminbot.action.message.command.CommandInfo;
import xyz.finlaym.adminbot.action.message.command.CommandResponse;
import xyz.finlaym.adminbot.storage.config.CurrencyConfig;
import xyz.finlaym.adminbot.storage.config.ServerConfig;
import xyz.finlaym.adminbot.utils.MathUtils;

public class SetBalanceCommand extends Command{

	private static final Logger logger = LoggerFactory.getLogger(SetBalanceCommand.class);

	public SetBalanceCommand() {
		super("setbalance", "command.setbalance", "-setbalance <user tag> <amount>", "Sets a user's balance", ServerConfig.CURRENCY_FLAG);
	}

	@Override
	public CommandResponse execute(CommandInfo info) {
		String[] command = info.getCommand();
		if(info.getMemberMentions().size() == 0 || command.length < 3) {
			return new CommandResponse("Usage: "+usage, true);
		}
		CurrencyConfig cConfig = info.getHandler().getBot().getCurrencyConfig();
		ServerConfig sConfig = info.getHandler().getBot().getServerConfig();
		if(!MathUtils.isInt(command[2])) {
			return new CommandResponse("User's balance must be an integer!",true);
		}
		int balance = Integer.valueOf(command[2]);
		long gid = info.getGid();
		long id = info.getMemberMentions().get(0).getIdLong();
		cConfig.setCurrency(gid, id, balance);
		try {
			cConfig.saveCurrency(gid, id);
		} catch (Exception e) {
			logger.error("Failed to save currency config in set balance command", e);
			return new CommandResponse("Critical Error: Failed to save balance to database!",true);
		}
		String suffix = sConfig.getCurrencySuffix(gid);
		return new CommandResponse("Successfully set user's balance to "+balance+suffix+"!",true);
	}
}
