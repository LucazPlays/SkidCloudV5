package de.terrarier.logging;

import de.terrarier.Wrapper;
import de.terrarier.command.CommandParser;
import org.jline.reader.LineReader;

import java.text.SimpleDateFormat;

public final class Logger {

	// public static boolean useAnsi;
	private static final SimpleDateFormat FORMAT = new SimpleDateFormat("d.MM.yy | HH:mm:ss");
	private static final String USER = System.getProperty("user.name").replace('-', ' ');
	private final CommandParser parser = new CommandParser();
	private static final String PROMPT_START = "[" + Color.RED;
	private static final String PROMPT_END = Color.RESET + "] TerraCloud" + Color.DARK_GRAY + "@" + Color.GRAY + USER + Color.RED + "=>" + Color.GRAY + " ";
	private static String currentTime;
	private static long lastUpdate;

	public void start() {
		final LineReader reader = Wrapper.getInstance().getReader();
		String line;
		try {
			while (true) {
				while ((line = reader.readLine(PROMPT_START + getCurrentTime() + PROMPT_END)) != null) {
				if (!line.trim().isEmpty()) {
						try {
							parser.parseCommand(line);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static String getCurrentTime() {
		final long current = System.currentTimeMillis();
		if(lastUpdate / 1000 != current / 1000) {
			lastUpdate = current;
			return currentTime = FORMAT.format(current);
		}
		return currentTime;
	}
	
	public static void log(String message, LogType type) {
		final String toPrint = type.getPrefix() + message + Color.RESET;
		try {
			Wrapper.getInstance().getReader().printAbove(toPrint);
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

	public CommandParser getParser() {
		return parser;
	}

}
