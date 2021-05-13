package xyz.finlaym.adminbot.action.message.command;

public class CommandResponse {
	private String message;
	private boolean failure = false;
	public CommandResponse(String message) {
		this.message = message;
	}
	public CommandResponse(String message, boolean failure) {
		this.message = message;
		this.failure = failure;
	}
	public String getMessage() {
		return message;
	}
	public boolean isFailure() {
		return failure;
	}
}
