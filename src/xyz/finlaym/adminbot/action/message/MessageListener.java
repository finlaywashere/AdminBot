package xyz.finlaym.adminbot.action.message;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.MessageUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import xyz.finlaym.adminbot.Bot;
import xyz.finlaym.adminbot.action.message.command.CommandHandler;
import xyz.finlaym.adminbot.action.message.currency.CurrencyHandler;
import xyz.finlaym.adminbot.action.message.response.ResponseHandler;
import xyz.finlaym.adminbot.action.message.swear.SwearHandler;
import xyz.finlaym.adminbot.storage.config.PermissionsConfig;
import xyz.finlaym.adminbot.utils.LoggerHelper;

public class MessageListener extends ListenerAdapter{
	
	private static final Logger logger = LoggerFactory.getLogger(MessageListener.class);
	
	private Bot bot;
	private CurrencyHandler lHandler;
	private SwearHandler sHandler;
	private CommandHandler cHandler;
	private ResponseHandler rHandler;

	public MessageListener(Bot bot) {
		this.bot = bot;
		this.rHandler = new ResponseHandler(bot);
		this.lHandler = new CurrencyHandler(bot);
		this.sHandler = new SwearHandler(bot);
		this.cHandler = new CommandHandler(bot);
	}
	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		try {
			handleMessage(event.getTextChannel(), event.getMember(), event.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	@Override
	public void onMessageUpdate(MessageUpdateEvent event) {
		try {
			handleMessage(event.getTextChannel(), event.getMember(), event.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private void handleMessage(TextChannel channel, Member member, Message message) throws Exception {
		User author = member.getUser();
		if (author.isBot())
			return;
		try {
			lHandler.countMessages(channel.getGuild().getIdLong(),author.getIdLong(),channel,author.getAsTag());
		}catch(Exception e) {
			e.printStackTrace();
			logger.error("OwO *flips table* I did an oopsie woopsie and bwoke!");
		}
		try {
			rHandler.handleResponse(channel, member, message);
		}catch(Exception e) {
			e.printStackTrace();
		}
		long gid = channel.getGuild().getIdLong();
		PermissionsConfig pConfig = bot.getPermissionsConfig();
		boolean swearOverride = pConfig.checkPermission(channel.getGuild(), member, "override.swear");
		if(!swearOverride) {
			// User's messages need to be checked for swearing
			boolean swear = sHandler.swearCheck(message.getContentRaw(), gid);
			if(swear) {
				message.delete().queue();
				channel.sendMessage("Swear, you will not!").queue();
				LoggerHelper.log(logger, channel.getGuild(), bot.getServerConfig().getLoggingChannel(gid), member.getUser(), "said a swear word in channel "+channel.getAsMention()+", contents: \""+message.getContentRaw()+"\"",bot.getDBInterface());
			}
		}
		if(message.getContentRaw().startsWith("-")) {
			// Command call
			cHandler.handleCommand(member,channel,message.getContentRaw().substring(1).trim().split(" "),message);
		}
	}
}
