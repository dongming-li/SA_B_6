package com.cs309.nerdsbattle.nerds_battle.battle;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Custom ThreadPoolExecutor that allows for pausing and counting of completed tasks.
 *
 * @author Matthew Kelly
 * Created by Matthew Kelly on 11/5/2017.
 */

public class MyThreadPoolExecutor extends ThreadPoolExecutor {
	
    /**
     * Counter for completed tasks.
     */
    private int tasks_completed = 0;

    /**
     * Variables to enable pausing
     */
    private boolean isPaused = false;
    private ReentrantLock pauseLock = new ReentrantLock();
    private Condition unpaused = pauseLock.newCondition();

    /**
     * MyThreadPoolExecutor Constructor.
     *
     * @param corePoolSize
     * @param maximumPoolSize
     * @param keepAliveTime
     * @param unit
     * @param workQueue
     */
    public MyThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
    }

    /**
     * MyThreadPoolExecutor Constructor.
     *
     * @param corePoolSize
     * @param maximumPoolSize
     * @param keepAliveTime
     * @param unit
     * @param workQueue
     * @param threadFactory
     */
    public MyThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory);
    }

    /**
     * MyThreadPoolExecutor Constructor.
     *
     * @param corePoolSize
     * @param maximumPoolSize
     * @param keepAliveTime
     * @param unit
     * @param workQueue
     * @param handler
     */
    public MyThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, handler);
    }

    /**
     * MyThreadPoolExecutor Constructor.
     *
     * @param corePoolSize
     * @param maximumPoolSize
     * @param keepAliveTime
     * @param unit
     * @param workQueue
     * @param threadFactory
     * @param handler
     */
    public MyThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory, RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
    }

    /**
     * Changes the before execution behavior to allow for pausing.
     *
     * @param thread
     *  Thread that will run the runnable.
     * @param runnable
     *  Runnable to execute.
     */
    protected void beforeExecute(Thread thread, Runnable runnable) {
        super.beforeExecute(thread, runnable);
        pauseLock.lock();
        try {
            while (isPaused) unpaused.await();
        } catch (InterruptedException ie) {
            thread.interrupt();
        } finally {
            pauseLock.unlock();
        }
    }

    /**
     * Changes the after execution behavior to allow for counting of completed tasks.
     *
     * @param runnable
     *  Runnable that was completed.
     * @param throwable
     *  Throwable is any occured during execution.
     */
    @Override
    protected void afterExecute(Runnable runnable, Throwable throwable) {
        super.afterExecute(runnable, throwable);

        tasks_completed++;
    }

    /**
     * Get tasks completed.
     *
     * @return
     *  tasks completed.
     */
    public int getTasks_completed() {
        return tasks_completed;
    }

    /**
     * Pauses the ThreadPoolListener (does not allow more tasks to run, does not stop running tasks).
     */
    public void pause() {
        pauseLock.lock();
        try {
            isPaused = true;
        } finally {
            pauseLock.unlock();
        }
    }

    /**
     * Resumes executing runnables from the queue.
     */
    public void resume() {
        pauseLock.lock();
        try {
            isPaused = false;
            unpaused.signalAll();
        } finally {
            pauseLock.unlock();
        }
    }

}
