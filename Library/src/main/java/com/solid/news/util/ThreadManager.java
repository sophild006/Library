//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.solid.news.util;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy;

public class ThreadManager {
    static final String TAG = "ThreadManager";

    private ThreadManager() {
    }

    private static ExecutorService getExecutor() {
        return ThreadManager.ExecutorHolder.mExecutor;
    }

    private static ExecutorService getSingleExecutor() {
        return ThreadManager.SingleExecutorHolder.mSingleExecutor;
    }

    private static ScheduledExecutorService getScheduledExecutor() {
        return ThreadManager.ScheduledExecutorHolder.mScheduledExecutor;
    }

    public static final void execute(Runnable task) {
        getExecutor().execute(task);
    }

    public static final <T> Future<T> submit(Runnable task, T result) {
        return getExecutor().submit(task, result);
    }

    public static final Future<?> submit(Runnable task) {
        return getExecutor().submit(task);
    }

    public static final <T> Future<T> submit(Callable<T> task) {
        return getExecutor().submit(task);
    }

    public static final ScheduledFuture<?> schedule(Runnable task, long delay) {
        return getScheduledExecutor().schedule(task, delay, TimeUnit.MILLISECONDS);
    }

    public static final <T> ScheduledFuture<T> schedule(Callable<T> task, long delay) {
        return getScheduledExecutor().schedule(task, delay, TimeUnit.MILLISECONDS);
    }

    public static final ScheduledFuture<?> scheduleAtFixedRate(Runnable task, long initialDelay, long period) {
        return getScheduledExecutor().scheduleAtFixedRate(task, initialDelay, period, TimeUnit.MILLISECONDS);
    }

    public static ScheduledFuture<?> scheduleWithFixedDelay(Runnable task, long initialDelay, long delay) {
        return getScheduledExecutor().scheduleWithFixedDelay(task, initialDelay, delay, TimeUnit.MILLISECONDS);
    }

    public static void executeInSingle(Runnable task) {
        getSingleExecutor().execute(task);
    }

    public static <T> Future<T> submitInSingle(Runnable task, T result) {
        return getSingleExecutor().submit(task, result);
    }

    public static Future<?> submitInSingle(Runnable task) {
        return getSingleExecutor().submit(task);
    }

    public static <T> Future<T> submitInSingle(Callable<T> task) {
        return getSingleExecutor().submit(task);
    }

    public static Executor newSerialExecutor() {
        return new ThreadManager.SerialExecutor();
    }

    public static void executeInBackground(Runnable task) {
        if(Thread.currentThread() == Looper.getMainLooper().getThread()) {
            execute(task);
        } else {
            task.run();
        }

    }

    public static void executeInDbWriteThread(Runnable task) {
        executeInSingle(task);
    }

    private static Handler getTimerThreadHandler() {
        return ThreadManager.TimerHandlerThreadHolder.mHandler;
    }

    public static void postDelayed(Runnable r, long delayMillis) {
        getTimerThreadHandler().postDelayed(r, delayMillis);
    }

    public static void postDelayedAndRemoveBefore(Runnable r, long delayMillis) {
        getTimerThreadHandler().removeCallbacks(r);
        getTimerThreadHandler().postDelayed(r, delayMillis);
    }

    public static void postAtTime(Runnable r, long uptimeMillis) {
        getTimerThreadHandler().postAtTime(r, uptimeMillis);
    }

    private static Handler getEventThreadHandler() {
        return ThreadManager.EventHandlerThreadHolder.mHandler;
    }

    public static void runInEventThread(Runnable r) {
        getEventThreadHandler().post(r);
    }

    private static class EventHandlerThreadHolder {
        private static HandlerThread mHandlerThread = new HandlerThread("event-thread");
        private static Handler mHandler;

        private EventHandlerThreadHolder() {
        }

        static {
            mHandlerThread.start();
            mHandler = new Handler(mHandlerThread.getLooper());
        }
    }

    private static class TimerHandlerThreadHolder {
        private static HandlerThread mHandlerThread = new HandlerThread("globle_timer");
        private static Handler mHandler;

        private TimerHandlerThreadHolder() {
        }

        static {
            mHandlerThread.start();
            mHandler = new Handler(mHandlerThread.getLooper());
        }
    }

    private static class SerialExecutor implements Executor {
        final Queue<Runnable> mTasks;
        Runnable mActive;

        private SerialExecutor() {
            this.mTasks = new LinkedList();
        }

        public synchronized void execute(final Runnable r) {
            this.mTasks.offer(new Runnable() {
                public void run() {
                    try {
                        r.run();
                    } finally {
                        SerialExecutor.this.scheduleNext();
                    }

                }
            });
            if(this.mActive == null) {
                this.scheduleNext();
            }

        }

        protected synchronized void scheduleNext() {
            if((this.mActive = (Runnable)this.mTasks.poll()) != null) {
                ThreadManager.getExecutor().execute(this.mActive);
            }

        }
    }

    private static class ScheduledExecutorHolder {
        private static ScheduledExecutorService mScheduledExecutor = Executors.newSingleThreadScheduledExecutor();

        private ScheduledExecutorHolder() {
        }
    }

    private static class SingleExecutorHolder {
        private static ExecutorService mSingleExecutor = Executors.newSingleThreadExecutor();

        private SingleExecutorHolder() {
        }
    }

    private static class ExecutorHolder {
        private static ExecutorService mExecutor;

        private ExecutorHolder() {
        }

        static {
            mExecutor = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors(), 64, 2L, TimeUnit.SECONDS, new SynchronousQueue(), new CallerRunsPolicy());
        }
    }
}
