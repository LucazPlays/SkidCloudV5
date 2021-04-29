package de.terrarier.terracloud;

import de.terrarier.terracloud.logging.LogType;
import de.terrarier.terracloud.logging.Logger;

public class Bootstrap {
	
	private Bootstrap() {}
	
	public static void main(String[] args) {
		final String javaVersion = System.getProperty("java.version");
		if(!isJavaVersionAtLeast(javaVersion)) {
			Logger.log("You are using a legacy version of Java(" + javaVersion + "). Please update Java at least to Java 8(1.8.0_171)!", LogType.ERROR);
			shutdown();
			return;
		}
		final Runtime runtime = Runtime.getRuntime();
		final long maxMemory = runtime.maxMemory();
		final long totalMemory = runtime.totalMemory();
		if(maxMemory < 256 * 1024 * 1024) {
			Logger.log("Max allocated memory is less than 256MB (" + (maxMemory / 1024 / 1024) + "MB)", LogType.CRITICAL);
			shutdown();
			return;
		}
		if(totalMemory < 256 * 1024 * 1024) {
			Logger.log("Total allocated memory is less than 256MB (" + (totalMemory / 1024 / 1024) + "MB)", LogType.CRITICAL);
			shutdown();
			return;
		}
		new Master().start();
		while (true);
	}

	// Source: https://stackoverflow.com/questions/51196713/check-if-a-java-version-is-greater-than-a-certain-iteration-in-java
	private static boolean isJavaVersionAtLeast(String current) {
		return "1.8.0_171".compareTo(current) <= 0;
	}

	public static void shutdown() {
		Logger.log("Shutting down in 10 seconds...", LogType.WARN);
		try {
			Thread.sleep(10000L);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.exit(0);
	}

}
