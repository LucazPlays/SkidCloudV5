package de.terrarier.terracloud.logging;

import de.terrarier.terracloud.Master;
import de.terrarier.terracloud.command.CommandParser;
import org.jline.reader.LineReader;

import java.text.SimpleDateFormat;

public final class Logger {

	private static final SimpleDateFormat FORMAT = new SimpleDateFormat("d.MM.yy | HH:mm:ss");
	private static final String USER = System.getProperty("user.name").replace('-', ' ');
	private final CommandParser parser = new CommandParser();
	private static final String PROMPT_START = "[" + Color.RED;
	private static final String PROMPT_END = Color.RESET + "] TerraCloud" + Color.DARK_GRAY + "@" + Color.GRAY + USER + Color.RED + "=>" + Color.GRAY + " ";
	private static String currentTime;
	private static long lastUpdate;
	private static boolean initialized;

	public void start() {
		final LineReader reader = Master.getInstance().getReader();
		String line;
		initialized = true;
		while (true) {
			try {
				while ((line = reader.readLine(PROMPT_START + getCurrentTime() + PROMPT_END)) != null) {
					if (!line.trim().isEmpty()) {
						try {
							parser.parseCommand(line);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
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
		try {
			if(initialized) {
				final String toPrint = Color.RESET + type.getPrefix() + message + Color.RESET;
				Master.getInstance().getReader().printAbove(toPrint);
			}else {
				System.out.println("[" + type.name() + "] " + message);
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

	public CommandParser getParser() {
		return parser;
	}

}
