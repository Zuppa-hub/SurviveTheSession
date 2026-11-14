package it.unicam.cs.mpgc.rpg127295.engine.timer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

/**
 * Executor for the tests that simulates the event queue of the user interface
 * ({@code Platform.runLater}): tasks wait in a queue until the test calls {@link #runAll()}.
 */
public final class QueuedExecutor implements Executor {

    private final List<Runnable> queue = new ArrayList<>();

    @Override
    public void execute(Runnable task) {
        queue.add(task);
    }

    /** Runs the queued tasks in order, including those queued while running. */
    public void runAll() {
        while (!queue.isEmpty()) {
            queue.removeFirst().run();
        }
    }

    public int size() {
        return queue.size();
    }
}
