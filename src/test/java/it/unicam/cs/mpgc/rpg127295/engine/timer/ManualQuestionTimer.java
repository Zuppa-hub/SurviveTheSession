package it.unicam.cs.mpgc.rpg127295.engine.timer;

import java.util.Objects;

/**
 * Timer for the tests: time passes only when the test calls {@link #advance(int)}, so the
 * tests do not depend on real time.
 */
public final class ManualQuestionTimer implements QuestionTimer {

    private TimerListener listener;
    private int secondsLeft;
    private boolean running;

    @Override
    public void start(int seconds, TimerListener listener) {
        this.listener = Objects.requireNonNull(listener, "Listener must not be null");
        this.secondsLeft = seconds;
        this.running = true;
    }

    @Override
    public void addSeconds(int seconds) {
        secondsLeft += seconds;
    }

    @Override
    public void stop() {
        running = false;
    }

    /**
     * Lets the given seconds pass, one at a time, notifying the listener of each tick and of
     * the expiry. Stops early if the timer is stopped.
     */
    public void advance(int seconds) {
        for (int second = 0; second < seconds && running; second++) {
            tick();
        }
    }

    public boolean isRunning() {
        return running;
    }

    public int secondsLeft() {
        return secondsLeft;
    }

    private void tick() {
        secondsLeft--;
        listener.onTick(secondsLeft);
        if (secondsLeft == 0) {
            running = false;
            listener.onExpired();
        }
    }
}
