package de.terrarier.logging;

public enum LogType {
	
	NONE(""), INFO("[Info] "),
	WARN(Color.GRAY + "[" + Color.YELLOW + "Warning" + Color.GRAY + "] " + Color.YELLOW),
	CRITICAL(Color.GRAY + "[" + Color.RED + "Critical" + Color.GRAY + "] " + Color.RED),
	ERROR(Color.DARK_RED + "[Error] ");
	
	private final String prefix;
	
	LogType(String prefix) {
		this.prefix = prefix;
	}
	
	public String getPrefix() {
		return prefix;
	}

}
