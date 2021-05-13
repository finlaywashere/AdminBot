package xyz.finlaym.adminbot.action.message.command;

public class CommandResponse {
	private String message;
	private boolean failure;
	private boolean force;
	private CommandState state;
	
	public CommandResponse(String message) {
		this(message,false,false,null);
	}
	public CommandResponse(String message, boolean failure) {
		this(message,failure,false,null);
	}
	public CommandResponse(String message, boolean failure, boolean force) {
		this(message,failure,force,null);
	}
	public CommandResponse(String message, boolean failure, boolean force, CommandState state) {
		this.message = message;
		this.failure = failure;
		this.force = force;
		this.state = state;
	}
	public String getMessage() {
		return message;
	}
	public boolean isFailure() {
		return failure;
	}
	public boolean isForce() {
		return force;
	}
	public CommandState getState() {
		return state;
	}
}
