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
import xyz.finlaym.adminbot.action.message.level.LevelHandler;
import xyz.finlaym.adminbot.action.message.swear.SwearHandler;
import xyz.finlaym.adminbot.storage.config.PermissionsConfig;
import xyz.finlaym.adminbot.utils.LoggerHelper;

public class MessageListener extends ListenerAdapter{
	
	private static final Logger logger = LoggerFactory.getLogger(MessageListener.class);
	
	private Bot bot;
	private LevelHandler lHandler;
	private SwearHandler sHandler;
	private CommandHandler cHandler;

	public MessageListener(Bot bot) {
		this.bot = bot;
		this.lHandler = new LevelHandler(bot);
		this.sHandler = new SwearHandler(bot);
		this.cHandler = new CommandHandler(bot);
	}
	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		handleMessage(event.getAuthor(), event.getTextChannel(), event.getMember(), event.getMessage());
	}
	@Override
	public void onMessageUpdate(MessageUpdateEvent event) {
		handleMessage(event.getAuthor(), event.getTextChannel(), event.getMember(), event.getMessage());
	}
	private void handleMessage(User author, TextChannel channel, Member member, Message message) {
		if (author.isBot())
			return;
		try {
			lHandler.countMessages(channel.getGuild().getIdLong(),author.getIdLong(),channel,author.getAsTag());
		}catch(Exception e) {
			e.printStackTrace();
			logger.error("OwO *flips table* I did an oopsie woopsie and bwoke!");
		}
		long gid = channel.getGuild().getIdLong();
		PermissionsConfig pConfig = bot.getPermissionsConfig();
		boolean swearOverride = pConfig.checkPermission(gid, member, "override.swear");
		if(!swearOverride) {
			// User's messages need to be checked for swearing
			boolean swear = sHandler.swearCheck(message.getContentRaw(), gid);
			if(swear) {
				message.delete().queue();
				channel.sendMessage("Swear, you will not!").queue();
				LoggerHelper.log(logger, channel.getGuild(),channel, author, "said a swear word in channel \""+channel.getName()+"\", contents: \""+message.getContentRaw()+"\"");
			}
		}
		if(message.getContentRaw().startsWith("-")) {
			// Command call
			cHandler.handleCommand(member,channel,message.getContentRaw().substring(1).trim().split(" "),message);
		}
	}
}
