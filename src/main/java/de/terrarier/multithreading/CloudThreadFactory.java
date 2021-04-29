package de.terrarier.multithreading;

import java.util.concurrent.ThreadFactory;

public final class CloudThreadFactory implements ThreadFactory {
	
	private int threadId;
	private final String name;
	
	public CloudThreadFactory(String threadName) {
		this.name = threadName;
	}
		
	@Override
	public Thread newThread(Runnable runnable) {
		return new Thread(runnable, name + "-" + ++threadId);
	}

}
