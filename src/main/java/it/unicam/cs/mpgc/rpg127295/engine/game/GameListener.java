package it.unicam.cs.mpgc.rpg127295.engine.game;

/**
 * Notified when an exam is over and its outcome has been applied to the student. Everything
 * else (stats, transcript, graduation) is read from the game afterwards.
 */
@FunctionalInterface
public interface GameListener {

    void onExamConcluded(ExamReport report);
}
