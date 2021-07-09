package xyz.finlaym.adminbot.action.message.command;

import net.dv8tion.jda.api.entities.TextChannel;

public class CommandState {
	private TextChannel outputChannel;
	private boolean silenced;
	private boolean outputScriptFailure= true;
	public CommandState(TextChannel outputChannel, boolean silenced) {
		this.outputChannel = outputChannel;
		this.silenced = silenced;
	}
	public TextChannel getOutputChannel() {
		return outputChannel;
	}
	public void setOutputChannel(TextChannel outputChannel) {
		this.outputChannel = outputChannel;
	}
	public boolean isSilenced() {
		return silenced;
	}
	public void setSilenced(boolean silenced) {
		this.silenced = silenced;
	}
	public boolean isOutputScriptFailure() {
		return outputScriptFailure;
	}
	public void setOutputScriptFailure(boolean outputScriptFailure) {
		this.outputScriptFailure = outputScriptFailure;
	}
}
