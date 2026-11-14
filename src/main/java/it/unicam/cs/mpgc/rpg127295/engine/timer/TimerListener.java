package it.unicam.cs.mpgc.rpg127295.engine.timer;

/** Notifications of a {@link QuestionTimer}. */
public interface TimerListener {

    /** Called every second with the seconds left, 0 included. */
    void onTick(int secondsLeft);

    /** Called once, when the time is over. */
    void onExpired();
}
