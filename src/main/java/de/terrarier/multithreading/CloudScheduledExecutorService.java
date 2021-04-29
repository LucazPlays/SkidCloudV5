package de.terrarier.multithreading;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

public final class CloudScheduledExecutorService {

	private final ScheduledExecutorService scheduledExecutorService;
	private final ExecutorService defaultService;
	private final int schedulerThreads;

	public CloudScheduledExecutorService(ThreadFactory threadFactory, int schedulerThreads) {
		this.schedulerThreads = schedulerThreads;
		scheduledExecutorService = Executors.newScheduledThreadPool(schedulerThreads, threadFactory);
		defaultService = Executors.newCachedThreadPool(threadFactory);
	}

	public ScheduledFuture<?> executeDelayed(Runnable run, long delay, TimeUnit unit) {
		return scheduledExecutorService.schedule(run, delay, unit);
	}

	public ScheduledFuture<?> executeRepeating(Runnable run, long startDelay, long delay, TimeUnit unit) {
		return scheduledExecutorService.scheduleWithFixedDelay(run, startDelay, delay, unit);
	}

	public void shutdown(int timeout, TimeUnit timeoutTimeUnit) {
		scheduledExecutorService.shutdown();
		try {
			if (!scheduledExecutorService.awaitTermination(timeout, timeoutTimeUnit)) {
				scheduledExecutorService.shutdownNow();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
			scheduledExecutorService.shutdownNow();
		}

		defaultService.shutdown();
		try {
			if (!defaultService.awaitTermination(timeout, timeoutTimeUnit)) {
				defaultService.shutdownNow();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
			defaultService.shutdownNow();
		}
	}

	public void fireAsync(Runnable run) {
		defaultService.execute(run);
	}

	public Future<?> executeAsync(Runnable run) {
		return defaultService.submit(run);
	}

	public boolean isShutdown() {
		return scheduledExecutorService.isTerminated() && defaultService.isTerminated();
	}

	public int getSchedulerThreadSize() {
		return this.schedulerThreads;
	}

}