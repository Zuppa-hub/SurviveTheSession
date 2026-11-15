package it.unicam.cs.mpgc.rpg127295.engine.game;

/**
 * Notified when an exam is over and its outcome has been applied to the student. Everything
 * else (stats, transcript, graduation) is read from the game afterwards.
 */
@FunctionalInterface
public interface GameListener {

    /**
     * Called once for every exam that ends, passed or not, after transcript, reward, levels
     * and energy have been updated; listeners are called in the order they were added.
     */
    void onExamConcluded(ExamReport report);
}
