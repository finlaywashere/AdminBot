package xyz.finlaym.adminbot.action.message.swear;

public class SwearWord {
	private ActivationType type;
	private String word;
	private String muteRole;
	
	public SwearWord(ActivationType type, String word, String muteRole) {
		this.type = type;
		this.word = word;
		this.muteRole = muteRole;
	}
	public SwearWord(String word) {
		this(ActivationType.MESSAGE,word,"muted");
	}
	public ActivationType getType() {
		return type;
	}
	public String getWord() {
		return word;
	}
	public String getMuteRole() {
		return muteRole;
	}
	@Override
	public String toString() {
		return word+":"+ActivationType.toName(type)+":"+muteRole;
	}
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof SwearWord))
			return false;
		SwearWord w = (SwearWord) obj;
		if(w.type != type && type != ActivationType.ANY)
			return false;
		if(!w.word.equals(word))
			return false;
		if(!w.muteRole.equals(muteRole))
			return false;
		return true;
	}
	
	public static SwearWord fromString(String s) {
		String[] split = s.split(":");
		String word = split[0];
		ActivationType type = ActivationType.MESSAGE;
		if(split.length >= 2)
			type = ActivationType.toType(split[1]);
		String role = "muted";
		if(split.length >= 3)
			role = split[2];
		return new SwearWord(type,word,role);
	}
	
	public enum ActivationType{
		USER,
		MESSAGE,
		ANY;
		
		public static ActivationType toType(String name) {
			switch(name) {
			case "u":
				return USER;
			case "m":
				return MESSAGE;
			case "a":
				return ANY;
			default:
				return null;
			}
		}
		public static String toName(ActivationType type) {
			switch(type) {
			case USER:
				return "u";
			case MESSAGE:
				return "m";
			case ANY:
				return "a";
			default:
				break;
			}
			return "null";
		}
	}
}
