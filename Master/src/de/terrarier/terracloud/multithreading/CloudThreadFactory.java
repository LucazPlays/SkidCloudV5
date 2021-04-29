package de.terrarier.terracloud.multithreading;

import java.util.concurrent.ThreadFactory;

public final class CloudThreadFactory implements ThreadFactory {

	private final String name;
	private int threadId;
	
	public CloudThreadFactory(String threadName) {
		this.name = threadName;
	}
		
	@Override
	public Thread newThread(Runnable runnable) {
		return new Thread(runnable, name + "-" + ++threadId);
	}

}
