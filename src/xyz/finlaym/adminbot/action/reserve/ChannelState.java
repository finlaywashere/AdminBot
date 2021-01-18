package xyz.finlaym.adminbot.action.reserve;

import java.util.List;

public class ChannelState{
	private List<PermissionState> prevState;

	public ChannelState(List<PermissionState> prevState) {
		this.prevState = prevState;
	}
	public List<PermissionState> getPrevState() {
		return prevState;
	}
}