package de.terrarier.terracloud.multithreading;

import java.util.concurrent.*;

public final class CloudScheduledExecutorService {

	private final ScheduledExecutorService scheduledExecutorService;
	private final ExecutorService defaultService;

	public CloudScheduledExecutorService(ThreadFactory threadFactory, int schedulerThreads) {
		scheduledExecutorService = Executors.newScheduledThreadPool(schedulerThreads, threadFactory);
		defaultService = Executors.newCachedThreadPool(threadFactory);
	}

	public ScheduledFuture<?> executeDelayed(Runnable run, long delay, TimeUnit unit) {
		return scheduledExecutorService.schedule(run, delay, unit);
	}

	public ScheduledFuture<?> executeRepeating(Runnable run, long startDelay, long delay, TimeUnit unit) {
		return scheduledExecutorService.scheduleWithFixedDelay(run, startDelay, delay, unit);
	}

	public Future<?> executeAsync(Runnable run) {
		return defaultService.submit(run);
	}

}