package it.unicam.cs.mpgc.rpg127295.engine.battle;

import java.util.OptionalInt;

/**
 * Notifications from a battle to its observers, such as the user interface (Observer). The
 * engine calls the listeners and never the other way round.
 */
public interface BattleListener {

    /** @param secondsToAnswer the time to answer, or empty if there is no timer */
    void onQuestionPresented(PresentedQuestion question, OptionalInt secondsToAnswer);

    void onAnswerEvaluated(boolean correct, BattleState state);

    void onTimeTick(int secondsLeft);

    void onBattleEnded(BattleOutcome outcome);
}
