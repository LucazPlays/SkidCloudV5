package de.terrarier.terracloud.utils;

import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public final class ShutdownController {

    private final ThreadPoolExecutor pool;

    public ShutdownController(int coreThreads, int maxThreads, long keepAliveTime) {
        pool = new ThreadPoolExecutor(coreThreads, maxThreads,
                keepAliveTime, TimeUnit.MILLISECONDS,
                new SynchronousQueue<>());
    }

    public void process(Process process, Runnable finishTask, long timeOut) {
        pool.execute(() -> {
            try {
                process.waitFor(timeOut, TimeUnit.MILLISECONDS);
                process.destroy();
                finishTask.run();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

}
