package it.unicam.cs.mpgc.rpg127295.engine.timer;

/**
 * The countdown of a question. The engine depends on this interface only: the real timer runs
 * on its own thread (issue #23), the tests use a timer moved by hand.
 * <p>
 * Contract: {@link #start} replaces any countdown in progress; after {@link #stop()} the
 * listener is no longer notified; {@code stop()} can be called at any time, even twice.
 */
public interface QuestionTimer {

    /** Starts counting down the given seconds, notifying the listener every second. */
    void start(int seconds, TimerListener listener);

    /** Adds seconds to the countdown in progress. */
    void addSeconds(int seconds);

    void stop();
}
