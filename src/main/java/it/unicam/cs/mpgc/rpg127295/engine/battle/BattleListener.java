package it.unicam.cs.mpgc.rpg127295.engine.battle;

import java.util.OptionalInt;

/**
 * Notifications from a battle to its observers, such as the user interface (Observer). The
 * engine calls the listeners and never the other way round.
 * <p>
 * Every method does nothing by default, so a listener implements only the notifications it
 * needs: the game, for example, only listens to the end of the battle. This is a compromise on
 * ISP (every listener still depends on all the methods), accepted because there are only two
 * clients.
 */
public interface BattleListener {

    /** @param secondsToAnswer the time to answer, or empty if there is no timer */
    default void onQuestionPresented(PresentedQuestion question, OptionalInt secondsToAnswer) {
    }

    /**
     * Called after every answer, an expired time included (as a wrong answer), before the next
     * question is presented or the battle ends.
     *
     * @param state the state after the answer
     */
    default void onAnswerEvaluated(boolean correct, BattleState state) {
    }

    /** Called every second while a timed question waits for an answer. */
    default void onTimeTick(int secondsLeft) {
    }

    /** Called after an item or an ability: the state shows what changed. */
    default void onEffectApplied(BattleState state) {
    }

    /** Called once, when the battle ends for any reason; no notification follows. */
    default void onBattleEnded(BattleOutcome outcome) {
    }
}
