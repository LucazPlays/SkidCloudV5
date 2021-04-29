package de.terrarier.multithreading;

import java.io.IOException;

public final class ProcessManager {
	
	public static Process executeAndGetProcess(ProcessBuilder builder) {
		return executeProcessInternally(builder);
	}
	
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
			//internalProcess.destroy();
			if(internalProcess != null) {
				internalProcess.destroyForcibly();
			}
			if(!internalExecuted) {
				return executeAndDestroyProcess(builder);
			}
			return true;
		}
	}
	
	@SuppressWarnings("finally")
	private static Process executeProcessInternally(ProcessBuilder builder) {
		boolean internalExecuted = false;
		Process internalProcess = null;
		try {
			internalProcess = builder.start();
			internalExecuted = true;
		} catch (IOException e) {
			e.printStackTrace();
			internalExecuted = false;
		}finally {
			if(!internalExecuted) {
				return executeProcessInternally(builder);
			}
			return internalProcess;
		}
	}

}
