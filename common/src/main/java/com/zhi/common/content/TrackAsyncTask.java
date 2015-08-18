/*
 * Copyright [2015] [zhi]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.zhi.common.content;

import com.zhi.common.util.Lists;

import android.annotation.TargetApi;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Message;

import java.util.List;
import java.util.concurrent.Executor;

/**
 * This class has the same usage with {@link android.os.AsyncTask}, but has an extra features:
 * <em>-Bulk cancellation of multiple tasks</em>.
 *
 * @see android.os.AsyncTask
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public abstract class TrackAsyncTask<Params, Progress, Result> extends AsyncTask<Params, Progress, Result> {
    private static final int MESSAGE_DELAY_PARALLEL_RUNNABLE = 0X01;
    private static final int MESSAGE_DELAY_SERIAL_RUNNABLE = 0X02;
    private static final InternalHandler sHandler = new InternalHandler();
    /**
     * Record the current working tasks.
     */
    private final Tracker mTracker;

    public TrackAsyncTask() {
        this(null);
    }

    public TrackAsyncTask(Tracker tracker) {
        mTracker = tracker;
    }

    /**
     * Execute the runnable in serial order. <em>For simple use, use
     * {@link AsyncHandler#post(Runnable)} instead. </em>
     *
     * @param runnable A {@link Runnable} to be executed.
     * @see TrackAsyncTask#executeParallel(Object...)
     * @see TrackAsyncTask#executeSerial(Object...)
     * @see TrackAsyncTask#cancelAndExecuteParallel(Object...)
     * @see TrackAsyncTask#cancelAndExecuteSerial(Object...)
     * @see TrackAsyncTask#executeParallel(Runnable)
     * @see TrackAsyncTask#executeParallel(Runnable, long)
     * @see TrackAsyncTask#executeSerial(Runnable, long)
     * @see AsyncHandler#post(Runnable)
     */
    public static void executeSerial(Runnable runnable) {
        execute(AsyncTask.SERIAL_EXECUTOR, runnable);
    }

    /**
     * Execute the runnable in parallel order. <em>For simple use, use
     * {@link AsyncHandler#post(Runnable)} instead. </em>
     *
     * @param runnable A {@link Runnable} to be executed.
     * @see TrackAsyncTask#executeParallel(Object...)
     * @see TrackAsyncTask#executeSerial(Object...)
     * @see TrackAsyncTask#cancelAndExecuteParallel(Object...)
     * @see TrackAsyncTask#cancelAndExecuteSerial(Object...)
     * @see TrackAsyncTask#executeSerial(Runnable)
     * @see TrackAsyncTask#executeParallel(Runnable, long)
     * @see TrackAsyncTask#executeSerial(Runnable, long)
     */
    public static void executeParallel(Runnable runnable) {
        execute(AsyncTask.THREAD_POOL_EXECUTOR, runnable);
    }

    /**
     * Execute the runnable in parallel order with delayed time milliseconds.
     * <em>For simple use, use
     * {@link AsyncHandler#postDelayed(Runnable, long)} instead. </em>
     *
     * @param runnable    A {@link Runnable} to be executed.
     * @param delayMillis Time in milliseconds before the runnable been
     *                    executed, but without accuracy.
     * @see TrackAsyncTask#executeParallel(Object...)
     * @see TrackAsyncTask#executeSerial(Object...)
     * @see TrackAsyncTask#cancelAndExecuteParallel(Object...)
     * @see TrackAsyncTask#cancelAndExecuteSerial(Object...)
     * @see TrackAsyncTask#executeSerial(Runnable)
     * @see TrackAsyncTask#executeSerial(Runnable, long)
     * @see AsyncHandler#postDelayed(Runnable, long)
     */
    public static void executeParallel(Runnable runnable, long delayMillis) {
        final Message msg = sHandler.obtainMessage(MESSAGE_DELAY_PARALLEL_RUNNABLE, runnable);
        sHandler.sendMessageDelayed(msg, delayMillis);
    }

    /**
     * Execute the runnable in serial order with delayed time milliseconds.
     * <em>For simple use, use
     * {@link AsyncHandler#postDelayed(Runnable, long)} instead. </em>
     *
     * @param runnable    A {@link Runnable} to be executed.
     * @param delayMillis Time in milliseconds before the runnable been executed, but without accuracy.
     * @see TrackAsyncTask#executeParallel(Object...)
     * @see TrackAsyncTask#executeSerial(Object...)
     * @see TrackAsyncTask#cancelAndExecuteParallel(Object...)
     * @see TrackAsyncTask#cancelAndExecuteSerial(Object...)
     * @see TrackAsyncTask#executeParallel(Runnable)
     * @see TrackAsyncTask#executeParallel(Runnable, long)
     * @see AsyncHandler#postDelayed(Runnable, long)
     */
    public static void executeSerial(Runnable runnable, long delayMillis) {
        final Message msg = sHandler.obtainMessage(MESSAGE_DELAY_SERIAL_RUNNABLE, runnable);
        sHandler.sendMessageDelayed(msg, delayMillis);
    }

    private static void execute(Executor executor, Runnable runnable) {
        executor.execute(runnable);
    }

    private void registerSelf() {
        if (mTracker != null) {
            mTracker.add(this);
        }
    }

    private void unregisterSelf() {
        if (mTracker != null) {
            mTracker.remove(this);
        }
    }

    @Override
    protected void onCancelled(Result result) {
        super.onCancelled(result);
        unregisterSelf();
    }

    @Override
    protected final void onPostExecute(Result result) {
        super.onPostExecute(result);
        unregisterSelf();
        if (isCancelled()) {
            onCancelled(result);
        } else {
            onSuccess(result);
        }
    }

    /**
     * Runs on the UI thread after {@link #doInBackground}.
     * <p>
     * This method won't be invoked if the task was cancelled, even if the
     * cancel task operation is called after {@link #doInBackground}.
     * </p>
     *
     * @param result The result of the operation computed by
     *               {@link #doInBackground}.
     * @see #onPreExecute
     * @see #doInBackground
     * @see #onCancelled(Object)
     */
    protected void onSuccess(Result result) {
    }

    /**
     * Executes the task with the specified parameters in parallel order with
     * other tasks. The task returns itself (this) so that the caller can keep a
     * reference to it. *
     * <p/>
     * This method must be invoked on the UI thread.
     *
     * @param params The parameters of the task.
     * @return This instance of DoveAsyncTask.
     * @throws IllegalStateException If {@link #getStatus()} returns either
     *                               {@link android.os.AsyncTask.Status#RUNNING} or
     *                               {@link android.os.AsyncTask.Status#FINISHED}.
     * @see TrackAsyncTask#executeSerial(Object...)
     * @see TrackAsyncTask#cancelAndExecuteParallel(Object...)
     * @see TrackAsyncTask#cancelAndExecuteSerial(Object...)
     * @see TrackAsyncTask#executeParallel(Runnable)
     * @see TrackAsyncTask#executeSerial(Runnable)
     * @see TrackAsyncTask#executeParallel(Runnable, long)
     * @see TrackAsyncTask#executeSerial(Runnable, long)
     */
    public final TrackAsyncTask<Params, Progress, Result> executeParallel(Params... params) {
        return execute(AsyncTask.THREAD_POOL_EXECUTOR, false, params);
    }

    /**
     * Executes the task with the specified parameters in parallel order with
     * other tasks, and cancel all other existed same task(
     * <em>Instanced with the same class</em>) if they are running. The task
     * returns itself (this) so that the caller can keep a reference to it. *
     * <p/>
     * This method must be invoked on the UI thread.
     *
     * @param params The parameters of the task.
     * @return This instance of DoveAsyncTask.
     * @throws IllegalStateException If {@link #getStatus()} returns either
     *                               {@link android.os.AsyncTask.Status#RUNNING} or
     *                               {@link android.os.AsyncTask.Status#FINISHED}.
     * @see TrackAsyncTask#executeParallel(Object...)
     * @see TrackAsyncTask#executeSerial(Object...)
     * @see TrackAsyncTask#cancelAndExecuteSerial(Object...)
     * @see TrackAsyncTask#executeParallel(Runnable)
     * @see TrackAsyncTask#executeSerial(Runnable)
     * @see TrackAsyncTask#executeParallel(Runnable, long)
     * @see TrackAsyncTask#executeSerial(Runnable, long)
     */
    public final TrackAsyncTask<Params, Progress, Result> cancelAndExecuteParallel(Params... params) {
        return execute(AsyncTask.THREAD_POOL_EXECUTOR, true, params);
    }

    /**
     * Executes the task with the specified parameters in serial order with
     * other tasks. The task returns itself (this) so that the caller can keep a
     * reference to it. *
     * <p/>
     * This method must be invoked on the UI thread.
     *
     * @param params The parameters of the task.
     * @return This instance of DoveAsyncTask.
     * @throws IllegalStateException If {@link #getStatus()} returns either
     *                               {@link android.os.AsyncTask.Status#RUNNING} or
     *                               {@link android.os.AsyncTask.Status#FINISHED}.
     * @see TrackAsyncTask#executeParallel(Object...)
     * @see TrackAsyncTask#cancelAndExecuteParallel(Object...)
     * @see TrackAsyncTask#cancelAndExecuteSerial(Object...)
     * @see TrackAsyncTask#executeParallel(Runnable)
     * @see TrackAsyncTask#executeSerial(Runnable)
     * @see TrackAsyncTask#executeParallel(Runnable, long)
     * @see TrackAsyncTask#executeSerial(Runnable, long)
     */
    public final TrackAsyncTask<Params, Progress, Result> executeSerial(Params... params) {
        return execute(AsyncTask.SERIAL_EXECUTOR, false, params);
    }

    /**
     * Executes the task with the specified parameters in serial order with
     * other tasks, and cancel all other existed same task(
     * <em>Instanced with the same class</em>) if they are running. The task
     * returns itself (this) so that the caller can keep a reference to it. *
     * <p/>
     * This method must be invoked on the UI thread.
     *
     * @param params The parameters of the task.
     * @return This instance of DoveAsyncTask.
     * @throws IllegalStateException If {@link #getStatus()} returns either
     *                               {@link android.os.AsyncTask.Status#RUNNING} or
     *                               {@link android.os.AsyncTask.Status#FINISHED}.
     * @see TrackAsyncTask#executeParallel(Object...)
     * @see TrackAsyncTask#executeSerial(Object...)
     * @see TrackAsyncTask#cancelAndExecuteParallel(Object...)
     * @see TrackAsyncTask#executeParallel(Runnable)
     * @see TrackAsyncTask#executeSerial(Runnable)
     * @see TrackAsyncTask#executeParallel(Runnable, long)
     * @see TrackAsyncTask#executeSerial(Runnable, long)
     */
    public final TrackAsyncTask<Params, Progress, Result> cancelAndExecuteSerial(Params... params) {
        return execute(AsyncTask.SERIAL_EXECUTOR, true, params);
    }

    private TrackAsyncTask<Params, Progress, Result> execute(Executor executor, boolean interrupt, Params... params) {
        if (interrupt) {
            if (mTracker == null) {
                throw new IllegalStateException();
            } else {
                mTracker.interrupt();
            }
        }
        // Here, register itself into tracker. And unregister itself in
        // onCancelled method or after tasks finished.
        registerSelf();
        executeOnExecutor(executor, params);
        return this;
    }

    /**
     * A collection that's used to record the working {@link TrackAsyncTask}. Use can
     * invoke {@link TrackAsyncTask.Tracker#interrupt()} to cancel all the background working task.
     */
    public static class Tracker {
        /**
         * Ordered list that's used to record the working DoveAsyncTask.
         */
        private final List<TrackAsyncTask<?, ?, ?>> mTasks = Lists.newLinkedList();

        /**
         * Cancel all the task in the tracker.
         */
        public void interrupt() {
            synchronized (mTasks) {
                for (TrackAsyncTask<?, ?, ?> task : mTasks) {
                    task.cancel(true);
                }
                mTasks.clear();
            }
        }

        /**
         * Add a {@link TrackAsyncTask} object to the end of the Tracker Tasks List.
         *
         * @param task the task to add.
         */
        private void add(TrackAsyncTask<?, ?, ?> task) {
            synchronized (mTasks) {
                mTasks.add(task);
            }
        }

        /**
         * Removes the first occurrence of the specified object from this List.
         *
         * @param task the task to remove.
         */
        private void remove(TrackAsyncTask<?, ?, ?> task) {
            synchronized (mTasks) {
                mTasks.remove(task);
            }
        }
    }

    private static class InternalHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            final Runnable runnable = (Runnable) msg.obj;
            switch (msg.what) {
                case MESSAGE_DELAY_PARALLEL_RUNNABLE:
                    execute(THREAD_POOL_EXECUTOR, runnable);
                    break;

                case MESSAGE_DELAY_SERIAL_RUNNABLE:
                    execute(SERIAL_EXECUTOR, runnable);
                    break;
            }
        }
    }
}
