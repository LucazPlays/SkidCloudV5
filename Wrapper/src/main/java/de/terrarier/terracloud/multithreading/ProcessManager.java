package de.terrarier.terracloud.multithreading;

import java.io.IOException;

public final class ProcessManager {
	
	@SuppressWarnings("finally")
	public static boolean executeAndDestroyProcess(ProcessBuilder builder) {
		boolean internalExecuted = false;
		Process internalProcess = null;
		try {
			internalProcess = builder.start();
			internalExecuted = true;
		} catch (IOException e) {
			e.printStackTrace();
			internalExecuted = false;
		}finally {
			if(internalProcess != null) {
				internalProcess.destroyForcibly();
			}
			return !internalExecuted && executeAndDestroyProcess(builder);
		}
	}

}
