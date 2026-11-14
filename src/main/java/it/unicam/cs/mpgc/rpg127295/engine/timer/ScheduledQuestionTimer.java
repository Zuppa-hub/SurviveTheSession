package it.unicam.cs.mpgc.rpg127295.engine.timer;

import static it.unicam.cs.mpgc.rpg127295.model.Preconditions.requirePositive;

import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * The real countdown: a periodic task on a {@link ScheduledExecutorService} removes one second
 * at a time, while the user interface may add seconds (Coffee) from its own thread.
 * <ul>
 *   <li><b>Notifications through an {@link Executor}</b>: the application passes
 *       {@code Platform::runLater}, so the listener (the battle) always runs on the user
 *       interface thread and never needs to be thread-safe. They are handed over outside the
 *       lock: calling external code while holding a lock risks a deadlock.</li>
 *   <li><b>Shared state under one lock</b>: seconds left, scheduled task, listener and
 *       generation change together, so {@code synchronized} protects all of them. An
 *       {@code AtomicInteger} would make the counter atomic, but not the invariant across the
 *       fields. Without the lock, {@code secondsLeft--} (read, compute, write) running while
 *       {@link #addSeconds(int)} writes could lose the added seconds: the race condition of
 *       the shared counter.</li>
 *   <li><b>Old notifications dropped</b>: every {@link #start} and {@link #stop()} starts a new
 *       generation. Each tick and each notification remembers its generation and is dropped
 *       if it is no longer the current one, so an expiry already queued when the player
 *       answers never counts against the next question.</li>
 * </ul>
 * The scheduler is not owned: whoever creates it shuts it down.
 */
public final class ScheduledQuestionTimer implements QuestionTimer {

    private static final long TICK_SECONDS = 1;

    private final ScheduledExecutorService scheduler;
    private final Executor notifier;
    private final Object lock = new Object();

    private int generation;
    private int secondsLeft;
    private TimerListener listener;
    private ScheduledFuture<?> countdown;

    /**
     * @param scheduler runs the countdown; a single thread is enough
     * @param notifier  delivers the notifications, such as {@code Platform::runLater}
     */
    public ScheduledQuestionTimer(ScheduledExecutorService scheduler, Executor notifier) {
        this.scheduler = Objects.requireNonNull(scheduler, "Scheduler must not be null");
        this.notifier = Objects.requireNonNull(notifier, "Notifier must not be null");
    }

    /** @throws IllegalArgumentException if the seconds are not positive */
    @Override
    public void start(int seconds, TimerListener listener) {
        requirePositive(seconds, "Seconds");
        Objects.requireNonNull(listener, "Listener must not be null");
        synchronized (lock) {
            cancelCountdown();
            this.secondsLeft = seconds;
            this.listener = listener;
            int current = generation;
            countdown = scheduler.scheduleAtFixedRate(() -> tick(current), TICK_SECONDS, TICK_SECONDS,
                    TimeUnit.SECONDS);
        }
    }

    /**
     * Adds seconds to the countdown in progress; ignored if there is none.
     *
     * @throws IllegalArgumentException if the seconds are not positive
     */
    @Override
    public void addSeconds(int seconds) {
        requirePositive(seconds, "Seconds");
        synchronized (lock) {
            if (countdown != null) {
                secondsLeft += seconds;
            }
        }
    }

    @Override
    public void stop() {
        synchronized (lock) {
            cancelCountdown();
        }
    }

    /** Called with the lock held. */
    private void cancelCountdown() {
        generation++;
        if (countdown != null) {
            countdown.cancel(false);
            countdown = null;
        }
    }

    private void tick(int tickGeneration) {
        TimerListener target;
        int left;
        synchronized (lock) {
            if (tickGeneration != generation) {
                return;
            }
            secondsLeft--;
            left = secondsLeft;
            target = listener;
            if (left == 0) {
                countdown.cancel(false);
                countdown = null;
            }
        }
        deliver(tickGeneration, () -> target.onTick(left));
        if (left == 0) {
            deliver(tickGeneration, target::onExpired);
        }
    }

    private void deliver(int notificationGeneration, Runnable notification) {
        notifier.execute(() -> {
            if (isCurrent(notificationGeneration)) {
                notification.run();
            }
        });
    }

    private boolean isCurrent(int notificationGeneration) {
        synchronized (lock) {
            return notificationGeneration == generation;
        }
    }
}
