package it.unicam.cs.mpgc.rpg127295.engine.battle;

import java.util.OptionalInt;

/**
 * Notifications from a battle to its observers, such as the user interface (Observer). The
 * engine calls the listeners and never the other way round.
 * <p>
 * Every method does nothing by default, so a listener implements only the notifications it
 * needs (ISP): the game, for example, only listens to the end of the battle.
 */
public interface BattleListener {

    /** @param secondsToAnswer the time to answer, or empty if there is no timer */
    default void onQuestionPresented(PresentedQuestion question, OptionalInt secondsToAnswer) {
    }

    default void onAnswerEvaluated(boolean correct, BattleState state) {
    }

    default void onTimeTick(int secondsLeft) {
    }

    /** Called after an item or an ability: the state shows what changed. */
    default void onEffectApplied(BattleState state) {
    }

    default void onBattleEnded(BattleOutcome outcome) {
    }
}
