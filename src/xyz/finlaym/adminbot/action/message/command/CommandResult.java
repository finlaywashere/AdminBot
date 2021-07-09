package xyz.finlaym.adminbot.action.message.command;

public class CommandResult {
	private int code;
	private CommandState state;
	private CommandResponse response;
	public CommandResult(int code, CommandState state, CommandResponse response) {
		this.code = code;
		this.state = state;
		this.response = response;
	}
	public int getCode() {
		return code;
	}
	public CommandState getState() {
		return state;
	}
	public CommandResponse getResponse() {
		return response;
	}
}
