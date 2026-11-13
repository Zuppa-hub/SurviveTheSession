package it.unicam.cs.mpgc.rpg127295.engine.timer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Delayed;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Scheduler for the tests: it records the periodic tasks instead of scheduling them, and runs
 * them only when the test calls {@link #tick()}. No thread waits for real time.
 */
public final class ManualScheduler extends ScheduledThreadPoolExecutor {

    private final List<Task> tasks = new ArrayList<>();

    public ManualScheduler() {
        super(1);
    }

    @Override
    public ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit) {
        Task task = new Task(command, unit.toMillis(initialDelay), unit.toMillis(period));
        tasks.add(task);
        return task;
    }

    /** Lets one period pass: runs every task not cancelled. */
    public void tick() {
        List.copyOf(tasks).stream()
                .filter(task -> !task.isCancelled())
                .forEach(task -> task.command.run());
    }

    public void tick(int periods) {
        for (int period = 0; period < periods; period++) {
            tick();
        }
    }

    /** Runs a task even if cancelled, as if it had already started when it was cancelled. */
    public void runTask(int index) {
        tasks.get(index).command.run();
    }

    /** @return the number of tasks scheduled and not cancelled */
    public long activeTasks() {
        return tasks.stream().filter(task -> !task.isCancelled()).count();
    }

    /** @return the initial delay and the period of the last task, in milliseconds */
    public List<Long> lastDelayAndPeriod() {
        Task last = tasks.getLast();
        return List.of(last.initialDelayMillis, last.periodMillis);
    }

    /** A recorded task, cancellable like a real one. */
    private static final class Task implements ScheduledFuture<Object> {

        private final Runnable command;
        private final long initialDelayMillis;
        private final long periodMillis;
        private boolean cancelled;

        private Task(Runnable command, long initialDelayMillis, long periodMillis) {
            this.command = command;
            this.initialDelayMillis = initialDelayMillis;
            this.periodMillis = periodMillis;
        }

        @Override
        public boolean cancel(boolean mayInterruptIfRunning) {
            cancelled = true;
            return true;
        }

        @Override
        public boolean isCancelled() {
            return cancelled;
        }

        @Override
        public boolean isDone() {
            return cancelled;
        }

        @Override
        public long getDelay(TimeUnit unit) {
            return unit.convert(periodMillis, TimeUnit.MILLISECONDS);
        }

        @Override
        public int compareTo(Delayed other) {
            return Long.compare(getDelay(TimeUnit.MILLISECONDS), other.getDelay(TimeUnit.MILLISECONDS));
        }

        @Override
        public Object get() {
            throw new UnsupportedOperationException("A periodic task has no result");
        }

        @Override
        public Object get(long timeout, TimeUnit unit) {
            throw new UnsupportedOperationException("A periodic task has no result");
        }
    }
}
